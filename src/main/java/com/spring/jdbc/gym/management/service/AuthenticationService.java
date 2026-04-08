package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.UserDao;
import com.spring.jdbc.gym.management.model.User;
import com.spring.jdbc.gym.management.model.filter.UserFilter;
import com.spring.jdbc.gym.management.utils.AESUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticationService {
    private final UserDao userDao;
    private final AESUtil aesUtil;

    public AuthenticationService(UserDao userDao, AESUtil aesUtil) {
        this.userDao = userDao;
        this.aesUtil = aesUtil;
    }

    public Map<String, Object> validateUser(User user) throws Exception {
        List<User> users = userDao.getUserList(new UserFilter(aesUtil.encrypt(user.getPassword()), user.getEmail()));
        if (users.isEmpty()) {
            throw new Exception("User Not Found");
        }
        User foundUser = users.get(0);
        Map<String, Object> map = new HashMap<>();
        map.put("user", foundUser);
        map.put("token", createToken(foundUser.getEmail(), foundUser.getRole().getDescription()));
        return map;
    }

    public String createToken(String email, String role) throws Exception {
        String timestamp = String.valueOf(new Date().getTime());
        String res = email + "|" + role + "|" + timestamp;
        return aesUtil.encrypt(res);
    }

//    public User validateTokenAndReturnUser(String token) throws Exception {
//        if (token == null || token.isEmpty()) {
//            return null;
//        }
//
//        // Split the data to retrieve the email, password, and timestamp
//        User foundUser = getCurrentUser(token);
//        if (foundUser == null) {
//            return null;
//        }
//        String[] parts = token.split(":");
//        long timestamp = Long.parseLong(AESUtil.decrypt(parts[2]));
//
//        // Check if the token is still valid based on the timestamp
//        long currentTime = new Date().getTime();
//        if ((currentTime - timestamp) <= TOKEN_VALIDITY_IN_MILLISECONDS) {
//            return foundUser;
//        } else {
//            return null;
//        }
//    }

//    public boolean validateToken(String token, User currentUser) throws Exception {
//        if (currentUser == null) {
//            return false;
//        }
//        String[] parts = token.split(":");
//        long timestamp = Long.parseLong(AESUtil.decrypt(parts[2]));
//
//        // Check if the token is still valid based on the timestamp
//        long currentTime = new Date().getTime();
//        return (currentTime - timestamp) <= TOKEN_VALIDITY_IN_MILLISECONDS;
//    }

//    public User getCurrentUser(String token) throws Exception {
//        String[] parts = token.split(":");
//        if (parts.length != 3) {
//            throw new IllegalArgumentException("Invalid token format");
//        }
//
//        String email = aesUtil.decrypt(parts[0]);
//        String password = aesUtil.decrypt(parts[1]);
//        return userDao.getUserList(new UserFilter(password, email)).get(0);
//    }
}
