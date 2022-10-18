package com.atguigu.gulimall.search;

import com.atguigu.gulimall.search.vo.SearchParam;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTests {

    @Autowired
    JestClient jestClient;

    @Test
    public void contextLoads() throws IOException {

        Index build = new Index.Builder(new User("aaaaa","bbbbb",30)).index("user").type("info").id("1").build();


        DocumentResult execute = jestClient.execute(build);

        System.out.println("保存完成....");

    }

    @Test
    public void search() throws IOException {
        SearchParam searchParam = new SearchParam();
        searchParam.setKeyword("白色");

        String s = bulidDSL(searchParam);

        System.out.println(s);


    }

    private static String bulidDSL(SearchParam param) {
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
@NoArgsConstructor
@AllArgsConstructor
@Data
class User{
    private String username;
    private String email;
    private Integer age;
}
