package com.jensdev.common.authException;

import com.jensdev.common.infrastructureException.ExceptionResponseEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
@Log4j2
public class AuthExceptionHandler {
    @ExceptionHandler(value = {AuthException.class})
    ResponseEntity<ExceptionResponseEntity> handleAuthException(AuthException e) {
        log.info("Auth exception response: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ExceptionResponseEntity(e.getMessage(), new Date())
        );
    }
}
