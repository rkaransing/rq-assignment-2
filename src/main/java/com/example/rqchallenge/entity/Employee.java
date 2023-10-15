package com.example.rqchallenge.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Comparator;

@Data
public class Employee {

    @JsonProperty("id")
    Long id;

    @JsonProperty("employee_name")
    String employeeName;

    @JsonProperty("employee_salary")
    int employeeSalary;

    @JsonProperty("employee_age")
    int employeeAge;

    @JsonProperty("profile_image")
    String profileImage;

    public static Comparator<Employee> employeeSalaryDescendingComparator() {
        return (e1, e2) -> Integer.compare(e2.getEmployeeSalary(), e1.getEmployeeSalary());
    }

    public static Comparator<Employee> employeeSalaryAcsendingComparator() {
        return Comparator.comparingInt(Employee::getEmployeeSalary);
    }
}

