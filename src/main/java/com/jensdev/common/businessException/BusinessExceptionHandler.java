package com.jensdev.common.businessException;

import com.jensdev.common.responseEnvelop.ExceptionResponseEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@Log4j2
public class BusinessExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {BusinessException.class})
    ResponseEntity<ExceptionResponseEntity> handleBusinessException(BusinessException e) {
        log.error(e);
        return ResponseEntity.badRequest().body(
                new ExceptionResponseEntity(e.getMessage(), new Date())
        );
    }
}
