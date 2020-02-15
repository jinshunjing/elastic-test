package org.jim.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * JPA Repository
 */
@Component
public interface EmployeeRepository extends ElasticsearchRepository<Employee, String> {
    /**
     * 自定义的查询接口
     *
     * @param name
     * @return
     */
    Iterable<Employee> findByName(String name);

}
