package com.example.rqchallenge.error;

import org.springframework.http.ResponseEntity;

public class ResponseEntityBuilder {
    public static ResponseEntity<Object> build(ApiError apiError){
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
