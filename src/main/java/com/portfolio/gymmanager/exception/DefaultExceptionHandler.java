package com.portfolio.gymmanager.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.util.NoSuchElementException;

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({AuthenticationException.class})
    @ResponseBody
    public ResponseEntity<RestError> handleAuthenticationException(Exception ex){
        RestError re = new RestError(
                HttpStatus.UNAUTHORIZED.toString(),
                "Authentication failed"
                );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(re);
    }

    @ExceptionHandler({NoSuchElementException.class})
    @ResponseBody
    public ResponseEntity<RestError> handleNoSuchElementException(Exception ex){
        RestError re = new RestError(
                HttpStatus.NOT_FOUND.toString(),
                "Object not found"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(re);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseBody
    public ResponseEntity<RestError> handleConstraintViolationException(Exception ex){
        RestError re = new RestError(
                HttpStatus.UNPROCESSABLE_ENTITY.toString(),
                "Invalid input"
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(re);
    }

    @ExceptionHandler({PSQLException.class})
    @ResponseBody
    public ResponseEntity<RestError> handlePSQLException(Exception ex){
        RestError re = new RestError(
                HttpStatus.CONFLICT.toString(),
                "Item could not be created"
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(re);
    }

    @ExceptionHandler({NullPointerException.class})
    @ResponseBody
    public ResponseEntity<RestError> handleNullPointerException(Exception ex){
        RestError re = new RestError(
                HttpStatus.UNPROCESSABLE_ENTITY.toString(),
                "Data missing"
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(re);
    }

    @ExceptionHandler({ExpiredJwtException.class})
    @ResponseBody ResponseEntity<RestError> handleExpiredJwtException(Exception ex){
        RestError re = new RestError(
                HttpStatus.FORBIDDEN.toString(),
                "Jwt expired"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(re);
    }

    @ExceptionHandler({RuntimeException.class})
    @ResponseBody ResponseEntity<RestError> handleRuntimeException (Exception ex){
        RestError re = new RestError(
                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
                "Something went wrong"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(re);
    }
}
