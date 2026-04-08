package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.annotation.RequiresRole;
import com.spring.jdbc.gym.management.utils.AESUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Date;

import static com.spring.jdbc.gym.management.model.enums.CONSTANT_VARIABLES.TOKEN_VALIDITY_IN_MILLISECONDS;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
    private final AESUtil aesUtil;

    public SecurityInterceptor(AESUtil aesUtil) {
        this.aesUtil = aesUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        // check if its Controller method
        if (!(handler instanceof HandlerMethod)) return true;
        HandlerMethod method = (HandlerMethod) handler;

        // check if method has annotation
        RequiresRole annotation = method.getMethodAnnotation(RequiresRole.class);
        if(annotation == null) return true;

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        try {
            String token = authHeader.substring(7);
            String payload = aesUtil.decrypt(token);
            String[] parts = payload.split("\\|");

            // Check if the token is still valid based on the timestamp
            long timestamp = Long.parseLong(parts[2]);
            long currentTime = new Date().getTime();
            if ((currentTime - timestamp) > TOKEN_VALIDITY_IN_MILLISECONDS) {
                response.setStatus(403);
                return false;
            }
            String userRole = parts[1];
            String[] requiredRoles = annotation.value();

            if(userRole.equalsIgnoreCase("ADMIN") || Arrays.stream(requiredRoles).anyMatch(reqRole -> reqRole.equalsIgnoreCase(userRole))) {
                // Store authenticated user info in request for controllers to use
                request.setAttribute("authenticatedUser", parts[0]);
                request.setAttribute("authenticatedRole", userRole);
                return true;
            }
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }

        response.setStatus(403);
        return false;
    }
}
