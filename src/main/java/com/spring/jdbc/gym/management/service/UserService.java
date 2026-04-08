package com.spring.jdbc.gym.management.service;

import com.spring.jdbc.gym.management.dao.UserDao;
import com.spring.jdbc.gym.management.exception.CustomException;
import com.spring.jdbc.gym.management.exception.ForbiddenException;
import com.spring.jdbc.gym.management.model.User;
import com.spring.jdbc.gym.management.model.filter.UserFilter;
import com.spring.jdbc.gym.management.utils.AESUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserDao userDao;
    private final AESUtil aesUtil;

    public UserService(UserDao userDao, AESUtil aesUtil) {
        this.userDao = userDao;
        this.aesUtil = aesUtil;
    }

    public List<User> getUsers(UserFilter filter) throws Exception {
        try {
            return userDao.getUserList(filter);
        } catch (Exception e) {
            throw new CustomException();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public User createUser(User user) throws Exception {
        try {
            user.setPassword(aesUtil.encrypt(user.getPassword()));
            User createdUser;
            try {
                createdUser = userDao.createUser(user);
                userDao.createUserBusinesses(createdUser);
                return user;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (ForbiddenException e) {
            throw new ForbiddenException();
        } catch (Exception e) {
            throw new Exception(e);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateUser(User user) throws Exception {
        try {
            userDao.updateUser(user);
        } catch (ForbiddenException e) {
            throw new ForbiddenException();
        } catch (CustomException e) {
            throw new CustomException();
        } catch (Exception e) {
            throw new Exception("An error occurred while updating user");
        }
    }

    public void deleteUser(String userId) throws Exception {
        try {
            userDao.deleteUser(userId);
        } catch (ForbiddenException e) {
            throw new ForbiddenException();
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}