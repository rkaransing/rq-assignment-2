package com.example.rqchallenge.employees;

import com.example.rqchallenge.entity.Employee;
import com.example.rqchallenge.entity.ResponseStatus;
import com.example.rqchallenge.exceptions.ResourceNotFoundException;
import com.example.rqchallenge.util.EmployeeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * The EmployeeControllerImpl contains implementation
 * of all rest apis defined in the IEmployeeController.
 */
@Component
public class EmployeeControllerImpl implements IEmployeeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeControllerImpl.class);

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() throws IOException {
        return new ResponseEntity<>(EmployeeUtil.getAllEmployeesList(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) throws IOException {
        List<Employee> employeeList = EmployeeUtil.getAllEmployeesList()
                .stream()
                .filter(employee -> employee.getEmployeeName()
                                            .matches("(?i).*" + searchString + ".*"))
                .collect(Collectors.toList());

        return new ResponseEntity<>(employeeList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) throws IOException {
        Employee employee = EmployeeUtil.getEmployeeById(id);
        return new ResponseEntity<>(employee, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() throws IOException {
        List<Employee> employeeList = EmployeeUtil.getAllEmployeesList();

        if (!employeeList.isEmpty()) {
            Integer highestSalary = EmployeeUtil.getAllEmployeesList()
                    .stream()
                    .max(Employee.employeeSalaryAcsendingComparator())
                    .get()
                    .getEmployeeSalary();

            return new ResponseEntity<>(highestSalary, HttpStatus.OK);
        } else {
            LOGGER.error("No employees records found.");
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<List<Employee>> getTopTenHighestEarningEmployeeNames() throws IOException {
        int limit = 10;
        List<Employee> employeeList = EmployeeUtil.getAllEmployeesList()
                .stream()
                .sorted(Employee.employeeSalaryDescendingComparator())
                .limit(limit)
                .collect(Collectors.toList());

        return new ResponseEntity<>(employeeList, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ResponseStatus> createEmployee(Map<String, Object> employeeInput) throws IOException {
        String name = EmployeeUtil.extractName(employeeInput);
        int age = EmployeeUtil.extractAge(employeeInput);
        int salary = EmployeeUtil.extractSalary(employeeInput);

        ResponseStatus status = EmployeeUtil.createEmployee(name, salary, age);
        return new ResponseEntity<>(status, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) throws IOException, ResourceNotFoundException {
        String deletedEmployee = EmployeeUtil.deleteEmployee(id);
        return new ResponseEntity<>(deletedEmployee, HttpStatus.OK);
    }
}
