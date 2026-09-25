package com.loanmanagement.service.impl;


import com.loanmanagement.dao.UserDao;
import com.loanmanagement.exception.BusinessException;
import com.loanmanagement.exception.ValidationException;
import com.loanmanagement.model.RecordStatus;
import com.loanmanagement.model.User;
import com.loanmanagement.service.AuthService;
import com.loanmanagement.util.Session;

public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;

    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User login(String username, String rawPassword) {

        if (username == null || username.isBlank()
                || rawPassword == null || rawPassword.isBlank()) {
            throw new ValidationException("Username and password are required");
        }

        User user = userDao.findByUsername(username.trim())
                .orElseThrow(() -> new BusinessException("Invalid username or password"));

        if (!user.getPasswordHash().equals(rawPassword)) {
            throw new BusinessException("Invalid username or password");
        }

        if (user.getStatus() != RecordStatus.ACTIVE) {
            throw new BusinessException("Your account is inactive. Contact the administrator.");
        }

        Session.login(user);
        return user;
    }

    @Override
    public void logout() {
        Session.logout();
    }
}