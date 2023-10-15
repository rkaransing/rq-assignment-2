package com.example.rqchallenge.util;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TestUtil class contains all the utility implementation
 * and constants declarations required for running tests
 * of the project.
 */
public class TestUtil {
    public static String DOT = ".";
    public static String ZEROTH_ARRAY_ELEMENT = ".[0].";
    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
