package com.loanmanagement.service.impl;

import com.loanmanagement.dao.UserDao;
import com.loanmanagement.exception.BusinessException;
import com.loanmanagement.exception.ValidationException;
import com.loanmanagement.model.RecordStatus;
import com.loanmanagement.model.Role;
import com.loanmanagement.model.User;
import com.loanmanagement.service.UserService;
import com.loanmanagement.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void addUser(User user, String rawPassword) {
        requireAdmin();

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new ValidationException("Username is required");
        }
        if (rawPassword == null || rawPassword.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters");
        }
        if (user.getRole() == null) {
            throw new ValidationException("Role is required");
        }
        if (userDao.existsByUsername(user.getUsername())) {
            throw new BusinessException("Username '" + user.getUsername() + "' is already taken");
        }

        user.setPasswordHash(rawPassword);
        user.setStatus(RecordStatus.ACTIVE);

        userDao.insert(user);

        logger.info("User created: userId={}, username={}, role={}, by userId={}",
                user.getUserId(), user.getUsername(), user.getRole(), Session.getCurrentUserId());
    }

    @Override
    public User getUserById(int userId) {
        requireAdmin();
        return userDao.findById(userId)
                .orElseThrow(() -> new BusinessException("No user with id " + userId));
    }

    @Override
    public List<User> getAllUsers() {
        requireAdmin();
        return userDao.findAll();
    }

    @Override
    public void updateUser(User user) {
        requireAdmin();

        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new ValidationException("Username is required");
        }

        boolean ok = userDao.update(user);
        if (!ok) {
            throw new BusinessException("No user with id " + user.getUserId());
        }

        logger.info("User updated: userId={}, username={}, by userId={}",
                user.getUserId(), user.getUsername(), Session.getCurrentUserId());
    }

    @Override
    public void deleteUser(int userId) {
        requireAdmin();

        if (userId == Session.getCurrentUserId()) {
            logger.warn("Self-delete refused: userId={}", userId);
            throw new BusinessException("You cannot delete your own account");
        }

        boolean ok = userDao.delete(userId);
        if (!ok) {
            throw new BusinessException("No user with id " + userId);
        }

        logger.info("User deleted: userId={}, by userId={}", userId, Session.getCurrentUserId());
    }

    private void requireAdmin() {
        if (Session.getRole() != Role.ADMIN) {
            throw new BusinessException("Only an administrator can manage users");
        }
    }
}