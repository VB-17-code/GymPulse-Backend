package com.vivek.gympulse.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)

    public Map<String, String> handleRuntime(

            RuntimeException ex

    ){

        Map<String,String> error =

                new HashMap<>();

        error.put(

                "message",

                ex.getMessage()

        );

        return error;

    }

}