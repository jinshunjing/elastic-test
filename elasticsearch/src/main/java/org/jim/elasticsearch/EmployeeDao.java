package org.jim.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.jim.elasticsearch.model.InvestProfit;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class EmployeeDao {
    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private EmployeeRepository repository;

    /**
     * 添加文档
     *
     * @param entity
     * @return
     */
    public int save(Employee entity) {
        Employee result = repository.save(entity);
        if (Objects.isNull(result)) {
            return 0;
        } else {
            entity.setId(result.getId());
            return 1;
        }
    }

    /**
     * 读取文档
     *
     * @param id
     * @return
     */
    public Employee queryById(String id) {
        Optional<Employee> optional = repository.findById(id);
        return (optional.isPresent()) ? optional.get() : null;
    }

    /**
     * 搜索文档
     *
     * @param name
     * @return
     */
    public Iterable<Employee> searchByName(String name) {
        TermQueryBuilder qb = QueryBuilders.termQuery("name", name);
        return repository.search(qb);
    }

    /**
     * 简单分页
     */
    public Page<Employee> pageByCreateTime() {
        Sort sort = new Sort(Sort.Direction.DESC, "createTime");
        Pageable pageable = PageRequest.of(0, 10, sort);
        return repository.findAll(pageable);
    }

    /**
     * 滚屏分页
     */
    public Page<Employee> pageByScroll(int pageNo, int pageSize, String scrollId) {
        Page<Employee> scroll = null;

        // 使用已经创建的scroll
        if (!StringUtils.isEmpty(scrollId)) {
            try {
                scroll = esTemplate.continueScroll(scrollId, 30_000L, Employee.class);
            } catch (Exception e) {
                // 已经过期
            }
        }

        // 创建新的scroll
        if (Objects.isNull(scroll)) {
            SearchQuery searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.matchAllQuery())
                    .withIndices("dev_employee")
                    .withTypes("employee")
                    .withPageable(PageRequest.of(pageNo, pageSize))
                    .build();
            scroll = esTemplate.startScroll(30_000L, searchQuery, Employee.class);
        }

        // 如果是最后一页，关闭
        if (scroll.getNumberOfElements() < pageSize) {
            esTemplate.clearScroll(scrollId);
        }

        return scroll;
    }

    /**
     * 简单聚合
     *
     * @param name
     * @return
     */
    public List<String> aggregateByCreateTime(String name) {
        DateHistogramAggregationBuilder db = AggregationBuilders
                .dateHistogram("hist").field("createTime")
                .dateHistogramInterval(DateHistogramInterval.DAY)
                .format("yyyy-MM-dd");

        QueryBuilder qb = QueryBuilders.termQuery("name", name);

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(qb)
                .addAggregation(db)
                .withIndices("dev_employee")
                .withTypes("employee")
                .build();

        List<String> result = esTemplate.query(searchQuery, r -> {
            InternalDateHistogram d = r.getAggregations().get("hist");

            List<String> list = new ArrayList<>();
            for (InternalDateHistogram.Bucket b : d.getBuckets()) {
                list.add(((DateTime)b.getKey()).getMillis() +
                        "," + b.getKeyAsString()  +
                        "," + b.getDocCount());
            }
            return list;
        });

        return result;
    }
}
