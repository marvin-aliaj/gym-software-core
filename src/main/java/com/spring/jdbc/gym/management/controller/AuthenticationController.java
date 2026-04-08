package com.spring.jdbc.gym.management.controller;

import com.spring.jdbc.gym.management.model.User;
import com.spring.jdbc.gym.management.service.AuthenticationService;
import com.spring.jdbc.gym.management.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public AuthenticationController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/sign-in")
    public ResponseEntity<Object> authenticate(@RequestBody User user) {
        try {
            Map<String, Object> response = authenticationService.validateUser(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/sign-up")
    public ResponseEntity<Object> register(@RequestBody User user) {
        try {
            if (user.getRole () == null || !("Client".equalsIgnoreCase (user.getRole().getDescription ()))) {
                return new ResponseEntity<>("You cannot sign-up with a role other than client", HttpStatus.FORBIDDEN);
            }
            User response = userService.createUser(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
