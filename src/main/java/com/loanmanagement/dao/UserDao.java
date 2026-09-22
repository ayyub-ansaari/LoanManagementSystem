package com.loanmanagement.dao;

import com.loanmanagement.model.RecordStatus;
import com.loanmanagement.model.User;
import com.loanmanagement.model.Role;
import java.util.List;
import java.util.Optional;

public interface UserDao {

        Optional<User> findByUsername(String name);
        Optional<User> findById(int userId);
        List<User> findAll();
        List<User> findByRole(Role role);
        List<User> searchByUsername(String partial);
        boolean existsByUsername(String username);

        int insert(User user);
        boolean update(User user);
        boolean updateStatus(int userId , RecordStatus status);
        boolean updatePassword(int userId , String passwordHash);
        boolean delete(int userId);

}
