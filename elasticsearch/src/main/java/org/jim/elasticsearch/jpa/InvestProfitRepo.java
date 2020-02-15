package org.jim.elasticsearch.jpa;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.jim.elasticsearch.model.InvestProfit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

/**
 * 投资收益的操作
 *
 * @author Jim
 */
@Component
public class InvestProfitRepo {
    @Autowired
    private ElasticsearchTemplate esTemplate;

    private static final String INDEX_NAME = "dev_invest_profit";
    private static final String TYPE_NAME = "invest_profit";
    private static final long SCROLL_TIME = 30_000L;

    public void createIndex() {
        boolean deleted = esTemplate.deleteIndex(InvestProfit.class);
        System.out.println("Deleted index: " + deleted);
        boolean result = esTemplate.createIndex(InvestProfit.class);
        System.out.println("Created index: " + result);
    }

    /**
     * 滚屏分页
     *
     * @param pageNo
     * @param pageSize
     * @param scrollId
     * @return
     */
    public Page<InvestProfit> page(int pageNo, int pageSize, String scrollId) {
        Page<InvestProfit> scroll = null;

        // 使用已经创建的scroll
        if (!StringUtils.isEmpty(scrollId)) {
            try {
                scroll = esTemplate.continueScroll(scrollId, SCROLL_TIME, InvestProfit.class);
            } catch (Exception e) {
                // 已经过期
            }
        }

        // 创建新的scroll
        if (Objects.isNull(scroll)) {
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(matchAllQuery())
                    .withIndices(INDEX_NAME)
                    .withTypes(TYPE_NAME)
                    .withPageable(PageRequest.of(pageNo, pageSize))
                    .build();
            scroll = esTemplate.startScroll(SCROLL_TIME, searchQuery, InvestProfit.class);
        }

        // 如果是最后一页，关闭
        if (scroll.getNumberOfElements() < pageSize) {
            esTemplate.clearScroll(scrollId);
        }

        return scroll;
    }

    /**
     * 求和
     * TODO: 返回的数据类型是double,不是BigDecimal
     *
     * @param owner
     * @param assetCode
     * @return
     */
    public double sum(String owner, String assetCode) {
        SumAggregationBuilder ab = AggregationBuilders.sum("sum").field("assetAmount");
        QueryBuilder qb = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("owner", owner))
                .must(QueryBuilders.matchQuery("assetCode", assetCode));
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(qb)
                .addAggregation(ab)
                .withIndices(INDEX_NAME)
                .withTypes(TYPE_NAME)
                .build();
        double sum = esTemplate.query(searchQuery, r -> {
            InternalSum s = r.getAggregations().get("sum");
            return s.getValue();
        });
        return sum;
    }

}
