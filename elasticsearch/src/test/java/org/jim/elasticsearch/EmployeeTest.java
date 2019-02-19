package org.jim.elasticsearch;

import org.elasticsearch.index.query.QueryBuilders;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

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

    @Test
    public void test1() {
        boolean result = esTemplate.createIndex(Employee.class);
        System.out.println("Create index: " + result);
    }

    @Test
    public void test2() {
        Employee e = Employee.builder()
                .name("Jerry")
                .note("This Is Very Easy")
                .build();
        e = repository.save(e);
        System.out.println(e);
        //OoKFAGkB__019Tl4L3Vb
        //O4KQAGkB__019Tl4snUZ
        //PIKRAGkB__019Tl4MXVc
    }

    @Test
    public void test3() {
        String id = "OoKFAGkB__019Tl4L3Vb";
        Optional<Employee> e = repository.findById(id);
        System.out.println(e.get());
    }

    @Test
    public void test4() {
        String term = "Jerry";
        term = "This";
        Iterable<Employee> iter = repository.search(QueryBuilders.queryStringQuery(term));
        for (Employee e : iter) {
            System.out.println(e);
        }
    }
}
