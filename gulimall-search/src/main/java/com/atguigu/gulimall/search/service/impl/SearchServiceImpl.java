package com.atguigu.gulimall.search.service.impl;

import com.atguigu.gulimall.commons.bean.Constant;
import com.atguigu.gulimall.search.service.SearchService;
import com.atguigu.gulimall.search.vo.SearchParam;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    JestClient jestClient;

    /**
     * @param param 前端传来的检索条件
     */
    @Override
    public void search(SearchParam param) {

        String query = bulidDSL(param);//DSL sentence
        //生成dsl语句


        //创建检索动作
        Search search = new Search.Builder(query)
                .addIndex(Constant.ES_SPU_INDEX)
                .addType(Constant.ES_SPU_TYPE)
                .build();

        try {
            //返回结果
            SearchResult execute = jestClient.execute(search);

            //转换结果vo给前端


        } catch (IOException e) {

        }

    }

    private String bulidDSL(SearchParam param) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //查询和过滤

        //分页
        searchSourceBuilder.from((param.getPageNum() - 1) * param.getPageSize());
        searchSourceBuilder.size(param.getPageSize());
        //高亮
        if (!StringUtils.isEmpty(param.getKeyword())) {
            searchSourceBuilder.highlighter()
                    .field("name")
                    .preTags("<b style='color:red'>")
                    .postTags("</b>");
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
        NestedAggregationBuilder allAgg =
                new NestedAggregationBuilder("attr_agg","attrValueList");
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
        allAgg.subAggregation(attr_id);


        searchSourceBuilder.aggregation(allAgg);
        return null;
    }
}
