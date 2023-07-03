package com.jensdev.common.infrastructureException;

import com.jensdev.common.businessException.BusinessException;
import com.jensdev.common.responseEnvelop.ExceptionResponseEntity;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
@Log4j2
public class InfrastructureExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(value = {BusinessException.class})
    ResponseEntity<ExceptionResponseEntity> handleInfrastructureException(InfrastructureException e) {
        log.error(e);
        return ResponseEntity.internalServerError().body(
                new ExceptionResponseEntity("internal server error", new Date())
        );
    }
}
