package com.jensdev.common;

import com.jensdev.common.exceptions.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {BusinessException.class})
    ResponseEntity<ExceptionResponseEntity> handleBusinessException(BusinessException e) {
        return ResponseEntity.badRequest().body(
                new ExceptionResponseEntity(e.getMessage(), new Date())
        );
    }
}
