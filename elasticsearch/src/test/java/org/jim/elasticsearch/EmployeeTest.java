package org.jim.elasticsearch;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EmployeeTest {

    @Autowired
    private ElasticsearchTemplate esTemplate;

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
}
