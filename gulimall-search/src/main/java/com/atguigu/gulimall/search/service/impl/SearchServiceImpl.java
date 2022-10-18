package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.commons.es.EsSkuVo;
import com.atguigu.gulimall.search.service.SearchService;
import com.atguigu.gulimall.search.utils.SearchResultPromotion;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResponse;
import com.atguigu.gulimall.search.vo.SearchResponseAttrVo;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.ChildrenAggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import io.searchbox.core.search.aggregation.TermsAggregation;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    JestClient jestClient;

    /**
     * @param param 前端传来的检索条件
     */
    @Override
    public SearchResponse search(SearchParam param) {

        String query = bulidDSL(param);//DSL sentence
        //生成dsl语句

        //创建检索动作
        Search search = new Search.Builder(query)
                .addIndex(Constant.ES_SPU_INDEX)
                .addType(Constant.ES_SPU_TYPE)
                .build();

        SearchResponse response = null;

        try {
            //返回结果
            SearchResult execute = jestClient.execute(search);

            //转换结果vo给前端
            response = buildResult(execute);

            //封装页面
            if(response!=null){
                response.setPageNum(param.getPageNum());
                response.setPageSize(param.getPageSize());
            }


        } catch (IOException e) {

        }

        return response;

    }

    private SearchResponse buildResult(SearchResult execute) {
        SearchResponse searchResponse = new SearchResponse();
        List<SearchResult.Hit<EsSkuVo, Void>> hits = execute.getHits(EsSkuVo.class);
        List<EsSkuVo> EsSkuVos = new ArrayList<>();

        //便利获取 EsSkuVos
        hits.forEach(hit->{
            EsSkuVo source = hit.source;
            Map<String, List<String>> highlight = hit.highlight;
            source.setName(highlight.get("name").get(0));
            EsSkuVos.add(source);
        });
        searchResponse.setProducts(EsSkuVos);

        //total quantity
        searchResponse.setTotal(new SearchResultPromotion(execute).getTotal());

        //quire all aggs-------------------------------------
        MetricAggregation aggregations = execute.getAggregations();
        // 1st
        ChildrenAggregation attr_agg = aggregations.getChildrenAggregation("attr_agg");
        TermsAggregation attrId_agg = attr_agg.getTermsAggregation("attrId_agg");
        //获取到桶
        List<TermsAggregation.Entry> buckets = attrId_agg.getBuckets();

        ArrayList<SearchResponseAttrVo> attrs = new ArrayList<>();

        buckets.forEach(buk->{
            //SHUXING ID
            String attrId = buk.getKey();
            TermsAggregation attrName_agg = buk.getTermsAggregation("attrName_agg");
            List<TermsAggregation.Entry> attrName_aggBuckets = attrName_agg.getBuckets();
            //NAME
            String attrName = attrName_aggBuckets.get(0).getKey();

            TermsAggregation attrValue_agg = buk.getTermsAggregation("attrValue_agg");

            ArrayList<String> attrValueKeys = new ArrayList<>();
            attrValue_agg.getBuckets().forEach(attrValue->{
                String attrValueKey = attrValue.getKey();
                attrValueKeys.add(attrValueKey);
            });

            //构造vo
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            searchResponseAttrVo.setName(attrName);
            searchResponseAttrVo.setProductAttributeId(Long.valueOf(attrId));
            searchResponseAttrVo.setValue(attrValueKeys);

            attrs.add(searchResponseAttrVo);

        });

        searchResponse.setAttrs(attrs);





        //2nd
        SearchResponseAttrVo brand = new SearchResponseAttrVo();
        TermsAggregation brand_agg = aggregations.getTermsAggregation("brandId_agg");

        List<String> brandList = new ArrayList<>();

        brand_agg.getBuckets().forEach(it->{
            String brandId = it.getKey();
            TermsAggregation brandName_agg = it.getTermsAggregation("brandName_agg");
            String brandName = brandName_agg.getBuckets().get(0).getKey();

            HashMap<String, String> b = new HashMap<>();
            b.put("id",brandId);
            b.put("name",brandName);
            String s = JSON.toJSONString(b);

            brandList.add(s);
        });

        brand.setName("品牌");
        brand.setValue(brandList);
        searchResponse.setBrand(brand);
        //3rd
        SearchResponseAttrVo cateLog = new SearchResponseAttrVo();
        TermsAggregation cateLog_agg = aggregations.getTermsAggregation("cateLog_agg");

        List<String> cateList = new ArrayList<>();

        cateLog_agg.getBuckets().forEach(it->{
            String cateId = it.getKey();
            TermsAggregation cateName_agg = it.getTermsAggregation("cateName_agg");
            String cateName = cateName_agg.getBuckets().get(0).getKey();

            HashMap<String, String> b = new HashMap<>();
            b.put("id",cateId);
            b.put("name",cateName);
            String s = JSON.toJSONString(b);

            cateList.add(s);
        });

        cateLog.setName("分类");
        cateLog.setValue(cateList);

        searchResponse.setCatelog(cateLog);


        return searchResponse;
    }

    private String bulidDSL(SearchParam param) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询和过滤
        //bool
        BoolQueryBuilder bool = new BoolQueryBuilder();
        //查看是否有构造match
        if (!StringUtils.isEmpty(param.getKeyword())) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("name", param.getKeyword());
            bool.must(matchQueryBuilder);
        }

        //构造过滤条件
        //1.传brandId
        if (param.getBrand() != null && param.getBrand().length > 0) {
            TermsQueryBuilder brandTerms = new TermsQueryBuilder("brandId", param.getBrand());
            bool.filter(brandTerms);
        }
        //2.3级分类
        if (param.getCatelog3() != null && param.getCatelog3().length > 0) {
            TermsQueryBuilder brandTerms = new TermsQueryBuilder("productCategoryId", param.getCatelog3());
            bool.filter(brandTerms);
        }
        //3.价格区间
        if (param.getPriceFrom() != null || param.getPriceTo() != null) {
            RangeQueryBuilder price =
                    new RangeQueryBuilder("price");
            if (param.getPriceFrom() != null) {
                price.gte(param.getPriceFrom());
            }
            if (param.getPriceTo() != null) {
                price.lte(param.getPriceTo());
            }
            bool.filter(price);
        }
        //按照属性过滤
        if (param.getProps() != null && param.getProps().length > 0) {
            for (String prop : param.getProps()) {
                //便利每个属性 2:win7-win10-win95
                String[] split = prop.split(":");
                if (split != null && split.length == 2) {
                    String attrId = split[0];
                    String[] attrValues = split[1].split("-");
                    //nested 里的query
                    BoolQueryBuilder qb = new BoolQueryBuilder();

                    TermQueryBuilder attrName = new TermQueryBuilder("attrValueList.productAttributeId", attrId);
                    qb.must(attrName);
                    TermsQueryBuilder attrValue = new TermsQueryBuilder("attrValueList.value", attrValues);
                    qb.must(attrValue);

                    NestedQueryBuilder nestedQueryBuilder =
                            new NestedQueryBuilder("attrValueList", qb, ScoreMode.None);

                    bool.filter(nestedQueryBuilder);
                }
            }
        }


        searchSourceBuilder.query(bool);

        //分页
        searchSourceBuilder.from((param.getPageNum() - 1) * param.getPageSize());
        searchSourceBuilder.size(param.getPageSize());
        //高亮
        if (!StringUtils.isEmpty(param.getKeyword())) {
            HighlightBuilder highlighter = new HighlightBuilder();
            highlighter.field("name")
                    .preTags("<b style='color:red'>")
                    .postTags("</b>");
            searchSourceBuilder.highlighter(highlighter);
        }

        //排序
        if (!StringUtils.isEmpty(param.getOrder())) {
            String order = param.getOrder();
            String[] orderS = order.split(":");
            if (orderS != null && orderS.length == 2) {
                SortOrder orders = orderS[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
                if (orderS[0] == "0") {
                    searchSourceBuilder.sort("_score", orders);
                } else if (orderS[0] == "1") {
                    searchSourceBuilder.sort("sale", orders);
                } else {
                    searchSourceBuilder.sort("price", orders);
                }
            }
        }

        //聚合

        //大局和
        NestedAggregationBuilder attrAgg =
                new NestedAggregationBuilder("attr_agg", "attrValueList");
        //子聚合
        TermsAggregationBuilder attr_id = new TermsAggregationBuilder("attrId_agg");
        attr_id.field("attrValueList.productAttributeId");

        //子聚合里的聚合
        //第一个
        TermsAggregationBuilder attrName_agg = new TermsAggregationBuilder("attrName_agg");
        attrName_agg.field("attrValueList.name");
        attr_id.subAggregation(attrName_agg);
        //第二个
        TermsAggregationBuilder attrValue_agg = new TermsAggregationBuilder("attrValue_agg");
        attrValue_agg.field("attrValueList.value");
        attr_id.subAggregation(attrValue_agg);

        //最终聚合一起
        attrAgg.subAggregation(attr_id);

        searchSourceBuilder.aggregation(attrAgg);

        //品牌aggs
        TermsAggregationBuilder brandAgg = new TermsAggregationBuilder("brandId_agg");
        brandAgg.field("brandId");

        TermsAggregationBuilder brandNameAgg = new TermsAggregationBuilder("brandName_agg");
        brandNameAgg.field("brandName");

        brandAgg.subAggregation(brandNameAgg);

        searchSourceBuilder.aggregation(brandAgg);


        //分类aggs
        TermsAggregationBuilder categoAgg = new TermsAggregationBuilder("cateLog_agg");
        categoAgg.field("productCategoryId");

        TermsAggregationBuilder categoNameAgg = new TermsAggregationBuilder("cateName_agg");
        categoNameAgg.field("productCategoryName");

        categoAgg.subAggregation(categoNameAgg);


        searchSourceBuilder.aggregation(categoAgg);
        return searchSourceBuilder.toString();
    }
}
