package com.loanmanagement.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.loanmanagement.dao.UserDao;
import com.loanmanagement.exception.BusinessException;
import com.loanmanagement.exception.ValidationException;
import com.loanmanagement.model.RecordStatus;
import com.loanmanagement.model.User;
import com.loanmanagement.service.AuthService;
import com.loanmanagement.util.Session;

public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);


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
                .orElseThrow(() -> {logger.warn("Login failed: unknown user = {}", username);
                   return new BusinessException("Invalid username or password");
                });

        if (!user.getPasswordHash().equals(rawPassword)) {
            logger.warn("Login failed: wrong password for username={}", username);
            throw new BusinessException("Invalid username or password");
        }

        if (user.getStatus() != RecordStatus.ACTIVE) {
            logger.warn("Login refused: username={} is {}", username, user.getStatus());
            throw new BusinessException("Your account is inactive. Contact the administrator.");
        }

        Session.login(user);
        logger.info("Login refused: username={}, role{}" , username ,user.getRole());
        return user;
    }

    @Override
    public void logout() {
        if (Session.isLoggedIn()) {
            logger.info("Logout: username={}", Session.getCurrentUser().getUsername());
        }
        Session.logout();
    }
}