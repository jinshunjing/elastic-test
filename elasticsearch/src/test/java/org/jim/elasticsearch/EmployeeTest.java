package org.jim.elasticsearch;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.InternalDateHistogram;
import org.elasticsearch.search.aggregations.metrics.sum.InternalSum;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

/**
 * @author Jim
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class EmployeeTest {
    @Autowired
    private ElasticsearchTemplate esTemplate;

    @Autowired
    private EmployeeRepository repository;

    @Before
    public void setUp() throws Exception {
        Thread.sleep(1000);
        System.out.println("==================");
        System.out.println();
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("==================");
        System.out.println();
        Thread.sleep(1000);
    }

    public void createIndex() {
        // 创建索引
        boolean result = esTemplate.deleteIndex(Employee.class);
        System.out.println("Deleted index: " + result);
        result = esTemplate.createIndex(Employee.class);
        System.out.println("Created index: " + result);

        // 创建映射
        result = esTemplate.putMapping(Employee.class);
        System.out.println("Put mapping: " + result);
    }

    @Test
    public void test1() {
        createIndex();
    }

    @Test
    public void test11() {
        // index
        boolean flag = esTemplate.deleteIndex(Employee.class);
        System.out.println("Deleted index: " + flag);
    }

    @Test
    public void test12() {
        // index
        boolean result = esTemplate.createIndex(Employee.class);
        System.out.println("Created index: " + result);
        // mapping
        result = esTemplate.putMapping(Employee.class);
        System.out.println("Put mapping: " + result);
    }

    @Test
    public void test13() {
        // mapping
        boolean result = esTemplate.putMapping(Employee.class);
        System.out.println("Put mapping: " + result);
    }

    @Test
    public void test2() {
        // save
        Employee e = Employee.builder()
                .name("Tom")
                .note("This Is Very Easy")
                .createTime(System.currentTimeMillis() - 60L * 24L * 3600_000L)
                .build();
        e = repository.save(e);
        System.out.println(e);
    }

    @Test
    public void test21() {
        // update
        String id = "SojnE3ABnpRlB2rMA8Vw";
        System.out.println(repository.findById(id).get());

        Employee e = Employee.builder()
                .id(id)
                .name("Tom")
                .note("Rabbit")
                .build();
        e = repository.save(e);
        System.out.println(e);

        System.out.println(repository.findById(id).get());
    }

    @Test
    public void test22() {
        // update
        String id = "UogNFXABnpRlB2rMTsUV";
        Employee e = repository.findById(id).get();
        e.setAssetAmount("232.7");
        e = repository.save(e);
        System.out.println(e);

        id = "U4gRFXABnpRlB2rMecX5";
        e = repository.findById(id).get();
        e.setName("Kin");
        e.setAssetAmount("156.5");
        e = repository.save(e);
        System.out.println(e);
    }

    @Test
    public void test3() {
        // find
        String id = "mqv1AmsBchDJYa9ijF8V";
        Optional<Employee> e = repository.findById(id);
        System.out.println(e.get());
    }

    @Test
    public void test31() {
        // find
        String name = "Jerry";
        repository.findByName(name).forEach(System.out::println);
        name = "jerry";
        repository.findByName(name).forEach(System.out::println);
    }

    @Test
    public void test4() {
        // search
        String term = "Kin";
        Iterable<Employee> iter = repository.search(QueryBuilders.queryStringQuery(term));
        //Iterable<Employee> iter = repository.search(QueryBuilders.termQuery("name", term));
        for (Employee e : iter) {
            System.out.println(e);
        }
    }

    @Test
    public void test5() {
        // sort
        //Sort sort = new Sort(Sort.Direction.ASC, "_id");
        //Sort sort = new Sort(Sort.Direction.ASC, "name");
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");
        Iterable<Employee> iter = repository.findAll(sort);
        for (Employee e : iter) {
            System.out.println(e);
        }
    }

    @Test
    public void test6() {
        // page
        Sort sort = new Sort(Sort.Direction.ASC, "name");
        Pageable pagable = PageRequest.of(0, 10, sort);
        Page<Employee> page = repository.findAll(pagable);
        for (Employee e : page) {
            System.out.println(e);
        }
    }

    @Test
    public void test7() {
        // aggregation
        DateHistogramAggregationBuilder db = AggregationBuilders.dateHistogram("hist").field("createTime")
                .dateHistogramInterval(DateHistogramInterval.DAY).format("yyyy-MM-dd");

        QueryBuilder qb = QueryBuilders.termQuery("name", "Kin");

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(qb)
                .addAggregation(db)
                .withIndices("dev_employee")
                .withTypes("employee")
                .build();

        String result = esTemplate.query(searchQuery, r -> {
            InternalDateHistogram d = r.getAggregations().get("hist");
            for (InternalDateHistogram.Bucket b : d.getBuckets()) {
                System.out.println(((DateTime)b.getKey()).getMillis());
                System.out.println(b.getKeyAsString()  + ", " + b.getDocCount());
            }
            return "pass";
        });

        System.out.println(result);
    }

    @Test
    public void test71() {
        // aggregation
        SumAggregationBuilder ab = AggregationBuilders.sum("sum").field("assetAmount");

        QueryBuilder qb = QueryBuilders.termQuery("name", "Kin");

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(qb)
                .addAggregation(ab)
                .withIndices("dev_employee")
                .withTypes("employee")
                .build();

        double result = esTemplate.query(searchQuery, r -> {
            InternalSum s = r.getAggregations().get("sum");
            return s.getValue();
        });

        System.out.println(result);
    }
}
