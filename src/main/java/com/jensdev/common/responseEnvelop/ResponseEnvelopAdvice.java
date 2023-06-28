package com.jensdev.common.responseEnvelop;

import com.jensdev.common.responseEnvelop.ResponseEnvelop;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@RequiredArgsConstructor
public class ResponseEnvelopAdvice implements ResponseBodyAdvice<Object> {

    // this class packs the response body into a canonical response format

    private final HttpServletRequest httpServletRequest;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return httpServletRequest.getRequestURI().startsWith("/api") || httpServletRequest.getRequestURI().startsWith("/auth");
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(
            @Nullable Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {

        ServletServerHttpResponse servletServerHttpResponse = (ServletServerHttpResponse) response;
        int status = servletServerHttpResponse.getServletResponse().getStatus();

        if (status == HttpStatus.OK.value() || status == HttpStatus.CREATED.value()) {
            if (body instanceof String) {
                // cannot encapsulate string
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return "{\n" +
                        "\t\"data\": \"" + body + "\",\n" +
                        "\t\"message\": \"success\"\n" +
                        "}";
            }
            return new ResponseEnvelop(body, "success");
        } else {
            return new ResponseEnvelop(body, "failed");
        }
    }

}
