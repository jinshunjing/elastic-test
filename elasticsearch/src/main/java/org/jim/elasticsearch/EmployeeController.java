package org.jim.elasticsearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("EmpCtl")
@RequestMapping("/emp")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeRepository repository;

    @GetMapping("save")
    public String save() {
        Employee e = Employee.builder()
                .name("Jerry")
                .build();
        Employee e2 = repository.save(e);
        log.info("Saved {}", e2);
        return "success";
    }

}
