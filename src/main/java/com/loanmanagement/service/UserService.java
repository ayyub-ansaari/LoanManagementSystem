package com.loanmanagement.service;

import com.loanmanagement.model.User;

import java.util.List;

public interface UserService {

    void addUser(User user, String rawPassword);
    User getUserById(int userId);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(int userId);
}