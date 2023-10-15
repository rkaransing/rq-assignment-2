package com.example.rqchallenge;

import com.example.rqchallenge.employees.IEmployeeController;
import com.example.rqchallenge.entity.Employee;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.example.rqchallenge.util.Constants.*;
import static com.example.rqchallenge.util.TestUtil.*;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(IEmployeeController.class)
@ContextConfiguration(classes = RqChallengeApplication.class)
class RqChallengeApplicationTests {

    /*
     * I have kept print() method deliberately in some functions for
     * debugging purpose but if it starts eating much space we can
     * stop printing the output of the tests
     * */

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testCreateEmployee() throws Exception {
        Employee employee = new Employee();
        employee.setEmployeeName("Test Employee Name");
        employee.setEmployeeAge(23);
        employee.setEmployeeSalary(45000);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/")
                        .content(asJsonString(employee))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    void testGetAllEmployees() throws Exception {
        int allEmployeeSize = 24;
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(DOLLAR, hasSize(allEmployeeSize)));
    }

    @Test
    void testGetEmployeeById() throws Exception {
        int employeeId = 4;
        int expectedAge = 22;
        int expectedSalary = 433060;
        String expectedName = "Cedric Kelly";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/" + employeeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(DOLLAR + DOT + EMPLOYEE_NAME, is(expectedName)))
                .andExpect(jsonPath(DOLLAR + DOT + EMPLOYEE_SALARY, is(expectedSalary)))
                .andExpect(jsonPath(DOLLAR + DOT + EMPLOYEE_AGE, is(expectedAge)));
    }

    @Test
    void testGetTop10HighestSalariedEmployees() throws Exception {
        int expectedSize = 10;
        int expectedSalary = 725000;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/topTenHighestEarningEmployeeNames")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(DOLLAR, hasSize(expectedSize)))
                .andExpect(jsonPath(DOLLAR + ZEROTH_ARRAY_ELEMENT + EMPLOYEE_SALARY, is(expectedSalary)));
    }

    @Test
    void testGetHighestSalariedEmployee() throws Exception {
        int expectedSalary = 725000;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/highestSalary")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(result -> result.equals(expectedSalary));
    }

    @Test
    void testSearchEmployeeByName() throws Exception {
        String searchString = "Paul";
        String expectedName = "Paul Byrd";
        int expectedAge = 64;

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/search/" + searchString)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(DOLLAR + ZEROTH_ARRAY_ELEMENT + EMPLOYEE_NAME, is(expectedName)))
                .andExpect(jsonPath(DOLLAR + ZEROTH_ARRAY_ELEMENT + EMPLOYEE_AGE, is(expectedAge)));
    }
}
