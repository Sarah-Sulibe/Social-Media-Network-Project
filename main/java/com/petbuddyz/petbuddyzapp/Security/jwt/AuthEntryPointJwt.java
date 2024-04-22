package com.petbuddyz.petbuddyzapp.Security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);
    private static final Logger fileLogger = LoggerFactory.getLogger("security.AuthEntryPointJwt");

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());
       
        fileLogger.error("Unauthorized error: {}", authException.getMessage());

        // Log request details
        logger.info("Request Method: {}", request.getMethod());
        logger.info("Request URL: {}", request.getRequestURL());
        logger.info("Request Path: {}", request.getServletPath());
        logger.info("Request Parameters: {}", request.getParameterMap());
        logger.info("Request Headers: {}", request.getHeaderNames());

        // Log request details to external file
        fileLogger.info("Request Method: {}", request.getMethod());
        fileLogger.info("Request URL: {}", request.getRequestURL());
        fileLogger.info("Request Path: {}", request.getServletPath());
        fileLogger.info("Request Parameters: {}", request.getParameterMap());
        fileLogger.info("Request Headers: {}", request.getHeaderNames());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }

}
