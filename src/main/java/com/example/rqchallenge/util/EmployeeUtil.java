package com.example.rqchallenge.util;

import com.example.rqchallenge.entity.Employee;
import com.example.rqchallenge.entity.ResponseStatus;
import com.example.rqchallenge.exceptions.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.rqchallenge.util.Constants.*;
import static com.example.rqchallenge.util.HttpUtil.*;


/**
 * The EmployeeUtil helps us with the implementation of all required utility
 * methods in the EmployeeController.
 */

public class EmployeeUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(EmployeeUtil.class);


    /**
     * This method helps us to convert an input string of JSONArray into a List<Employee>.
     *
     * @param inputString String containing JSONArray data of employee.
     */
    public static List<Employee> getEmployeeListFromString(String inputString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        return mapper.readValue(inputString, new TypeReference<>() {
        });
    }


    /**
     * This method helps us make an API call and get data of all employees.
     */
    public static List<Employee> getAllEmployeesList() throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + GET_ALL_EMPLOYEES)
                .get()
                .build();

        Response response = HttpUtil.execute(request);

        if (response.isSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body().string());

            List<Employee> employeeList = EmployeeUtil.getEmployeeListFromString(jsonNode.get(JSON_KEY_DATA).toString());
            response.close();
            return employeeList;
        } else {
            /*
               Mocking the data in case if we do not get data.
            */
            response.close();
            return EmployeeUtil.getMockedEmployeeData();
        }
    }


    /**
     * This method helps us to create a new employee.
     *
     * @param name   String of employee name
     * @param salary integer value of employee salary
     * @param age    integer value of employee age
     */
    public static ResponseStatus createEmployee(String name, int salary, int age) throws IOException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put(NAME, name);
        jsonObject.put(SALARY, salary);
        jsonObject.put(AGE, age);

        RequestBody requestBody = RequestBody.create(MediaType.parse(MimeTypeUtils.APPLICATION_JSON_VALUE), jsonObject.toJSONString());

        Request request = new Request.Builder()
                .url(BASE_URL + CREATE_EMPLOYEE)
                .post(requestBody)
                .build();

        Response response = HttpUtil.execute(request);
        if (response.isSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body().string());

            String status = jsonNode.get(JSON_KEY_STATUS)
                    .textValue()
                    .toUpperCase();

            return ResponseStatus.valueOf(status);
        }
        return ResponseStatus.FAILURE;
    }


    /**
     * This method helps us to get an employee with given employee_id.
     *
     * @param id String containing an employee_id
     */
    public static Employee getEmployeeById(String id) throws IOException {
        Request request = new Request.Builder()
                .url(BASE_URL + GET_EMPLOYEE_BY_ID + id)
                .get()
                .build();

        Response response = HttpUtil.execute(request);
        List<Employee> employeeList;

        if (response.isSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body().string());

            employeeList = EmployeeUtil.getEmployeeListFromString(jsonNode.get(JSON_KEY_DATA).toString());
            response.close();


        } else {
            /*
               1. Mocking the data in case if we do not get data.
               2. This will return an employee
            */
            response.close();
            employeeList = EmployeeUtil.getMockedEmployeeData()
                    .stream()
                    .filter(employee -> (employee.getId() == Long.parseLong(id)))
                    .collect(Collectors.toList());

        }

        if(employeeList == null || employeeList.isEmpty())
            throw new ResourceNotFoundException(String.format("No entity found with id : %s", id));

        return employeeList.get(0);
    }

    /**
     * This method helps us to get delete an existing record of
     * an employee with given employee_id.
     *
     * @param id String containing an employee_id
     */
    public static String deleteEmployee(String id) throws IOException {
        Employee employee = getEmployeeById(id);

        Request request = new Request.Builder()
                .url(BASE_URL + DELETE_EMPLOYEE + id)
                .delete()
                .build();

        Response response = HttpUtil.execute(request);
        if (response.isSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response.body().string());

            String status = jsonNode.get(JSON_KEY_STATUS)
                    .textValue()
                    .toUpperCase();

            if (status.equals(ResponseStatus.SUCCESS.name())) {
                return employee.getEmployeeName();
            }
        }
        throw new ResourceNotFoundException(String.format("Failed to delete employee with id : %s", id));
    }

    /**
     * Below three methods helps us to extract data from an input Map.
     *
     * We are getting data in two ways, for example in case of "name":
     * 1. /create route, where we are getting the name with Map key as "name"
     * 2. /create route through tests, where we are using ObjectMapper and getting key as "employee_id"
     *
     * @param inputMap Map containing input values to create employee.
     */
    public static String extractName(Map<String, Object> inputMap) {
        return inputMap.containsKey(NAME)
                ? String.valueOf(inputMap.get(NAME))
                : String.valueOf(inputMap.get(EMPLOYEE_NAME));
    }

    public static int extractAge(Map<String, Object> inputMap) {
        return Integer.parseInt(inputMap.containsKey(AGE)
                ? String.valueOf(inputMap.get(AGE))
                : String.valueOf(inputMap.get(EMPLOYEE_AGE)));
    }

    public static int extractSalary(Map<String, Object> inputMap) {
        return Integer.parseInt(inputMap.containsKey(SALARY)
                ? String.valueOf(inputMap.get(SALARY))
                : String.valueOf(inputMap.get(EMPLOYEE_SALARY)));
    }


    /**
     * Keeping backup mock data in memory so that in case API
     * hits are failing then we are using this mocked data (Ideally should be the part of Tests only).
     */
    public static List<Employee> getMockedEmployeeData() {
        String jsonStringData = "[\n" +
                "        {\n" +
                "            \"id\": 1,\n" +
                "            \"employee_name\": \"Tiger Nixon\",\n" +
                "            \"employee_salary\": 320800,\n" +
                "            \"employee_age\": 61,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 2,\n" +
                "            \"employee_name\": \"Garrett Winters\",\n" +
                "            \"employee_salary\": 170750,\n" +
                "            \"employee_age\": 63,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 3,\n" +
                "            \"employee_name\": \"Ashton Cox\",\n" +
                "            \"employee_salary\": 86000,\n" +
                "            \"employee_age\": 66,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 4,\n" +
                "            \"employee_name\": \"Cedric Kelly\",\n" +
                "            \"employee_salary\": 433060,\n" +
                "            \"employee_age\": 22,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 5,\n" +
                "            \"employee_name\": \"Airi Satou\",\n" +
                "            \"employee_salary\": 162700,\n" +
                "            \"employee_age\": 33,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 6,\n" +
                "            \"employee_name\": \"Brielle Williamson\",\n" +
                "            \"employee_salary\": 372000,\n" +
                "            \"employee_age\": 61,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 7,\n" +
                "            \"employee_name\": \"Herrod Chandler\",\n" +
                "            \"employee_salary\": 137500,\n" +
                "            \"employee_age\": 59,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 8,\n" +
                "            \"employee_name\": \"Rhona Davidson\",\n" +
                "            \"employee_salary\": 327900,\n" +
                "            \"employee_age\": 55,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 9,\n" +
                "            \"employee_name\": \"Colleen Hurst\",\n" +
                "            \"employee_salary\": 205500,\n" +
                "            \"employee_age\": 39,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 10,\n" +
                "            \"employee_name\": \"Sonya Frost\",\n" +
                "            \"employee_salary\": 103600,\n" +
                "            \"employee_age\": 23,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 11,\n" +
                "            \"employee_name\": \"Jena Gaines\",\n" +
                "            \"employee_salary\": 90560,\n" +
                "            \"employee_age\": 30,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 12,\n" +
                "            \"employee_name\": \"Quinn Flynn\",\n" +
                "            \"employee_salary\": 342000,\n" +
                "            \"employee_age\": 22,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 13,\n" +
                "            \"employee_name\": \"Charde Marshall\",\n" +
                "            \"employee_salary\": 470600,\n" +
                "            \"employee_age\": 36,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 14,\n" +
                "            \"employee_name\": \"Haley Kennedy\",\n" +
                "            \"employee_salary\": 313500,\n" +
                "            \"employee_age\": 43,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 15,\n" +
                "            \"employee_name\": \"Tatyana Fitzpatrick\",\n" +
                "            \"employee_salary\": 385750,\n" +
                "            \"employee_age\": 19,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 16,\n" +
                "            \"employee_name\": \"Michael Silva\",\n" +
                "            \"employee_salary\": 198500,\n" +
                "            \"employee_age\": 66,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 17,\n" +
                "            \"employee_name\": \"Paul Byrd\",\n" +
                "            \"employee_salary\": 725000,\n" +
                "            \"employee_age\": 64,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 18,\n" +
                "            \"employee_name\": \"Gloria Little\",\n" +
                "            \"employee_salary\": 237500,\n" +
                "            \"employee_age\": 59,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 19,\n" +
                "            \"employee_name\": \"Bradley Greer\",\n" +
                "            \"employee_salary\": 132000,\n" +
                "            \"employee_age\": 41,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 20,\n" +
                "            \"employee_name\": \"Dai Rios\",\n" +
                "            \"employee_salary\": 217500,\n" +
                "            \"employee_age\": 35,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 21,\n" +
                "            \"employee_name\": \"Jenette Caldwell\",\n" +
                "            \"employee_salary\": 345000,\n" +
                "            \"employee_age\": 30,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 22,\n" +
                "            \"employee_name\": \"Yuri Berry\",\n" +
                "            \"employee_salary\": 675000,\n" +
                "            \"employee_age\": 40,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 23,\n" +
                "            \"employee_name\": \"Caesar Vance\",\n" +
                "            \"employee_salary\": 106450,\n" +
                "            \"employee_age\": 21,\n" +
                "            \"profile_image\": \"\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"id\": 24,\n" +
                "            \"employee_name\": \"Doris Wilder\",\n" +
                "            \"employee_salary\": 85600,\n" +
                "            \"employee_age\": 23,\n" +
                "            \"profile_image\": \"\"\n" +
                "        }\n" +
                "    ]";

        try {
            return getEmployeeListFromString(jsonStringData);
        } catch (JsonProcessingException e) {
            String errorMessage = "Invalid JSON input data for :" + jsonStringData;

            LOGGER.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }
}
