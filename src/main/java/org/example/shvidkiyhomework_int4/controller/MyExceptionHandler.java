package org.example.shvidkiyhomework_int4.controller;

import org.example.shvidkiyhomework_int4.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Viktor Shvidkiy
 */
@RestControllerAdvice
public class MyExceptionHandler {


    @ExceptionHandler
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}
