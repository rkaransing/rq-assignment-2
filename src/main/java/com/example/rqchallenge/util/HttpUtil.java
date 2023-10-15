package com.example.rqchallenge.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * The HttpUtil class contains all the utility implementation
 * and constants needed while making a http requests.
 */
public class HttpUtil {

    public static String BASE_URL = "https://dummy.restapiexample.com/api/v1";
    public static String GET_ALL_EMPLOYEES = "/employees";
    public static String CREATE_EMPLOYEE = "/create";
    public static String DELETE_EMPLOYEE = "/delete/";
    public static String GET_EMPLOYEE_BY_ID = "/employee/";
    public static String EMPTY_STRING = "";

    private static OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();


    public static Response execute(Request request) throws IOException {
        return okHttpClient.newCall(request).execute();
    }
}
