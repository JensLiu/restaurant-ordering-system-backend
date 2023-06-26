package com.jensdev.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jensdev.common.ExceptionResponseEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // this class is used to handle authentication errors in spring security
        // it is called when authentication context is null
        log.info("Unauthorized request: " + request.getRequestURI());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
//        response.getWriter().write(
//                new ObjectMapper().writeValueAsString(
//                        new ExceptionResponseEntity("Unauthorised", new Date())
//                ));
    }
}
