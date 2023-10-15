package com.example.rqchallenge.employees;

import com.example.rqchallenge.entity.Employee;
import com.example.rqchallenge.entity.ResponseStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public interface IEmployeeController {

    @GetMapping()
    ResponseEntity<List<Employee>> getAllEmployees() throws IOException;

    @GetMapping("/search/{searchString}")
    ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) throws IOException;

    @GetMapping("/{id}")
    ResponseEntity<Employee> getEmployeeById(@PathVariable String id) throws IOException;

    @GetMapping("/highestSalary")
    ResponseEntity<Integer> getHighestSalaryOfEmployees() throws IOException;

    @GetMapping("/topTenHighestEarningEmployeeNames")
    ResponseEntity<List<Employee>> getTopTenHighestEarningEmployeeNames() throws IOException;

    @PostMapping()
    ResponseEntity<ResponseStatus> createEmployee(@RequestBody Map<String, Object> employeeInput) throws IOException;

    @DeleteMapping("/{id}")
    ResponseEntity<String> deleteEmployeeById(@PathVariable String id) throws IOException;

}
