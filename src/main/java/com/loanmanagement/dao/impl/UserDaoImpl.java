package com.loanmanagement.dao.impl;

import com.loanmanagement.exception.DataAccessException;
import com.loanmanagement.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.loanmanagement.dao.UserDao;
import com.loanmanagement.model.RecordStatus;
import com.loanmanagement.model.Role;
import com.loanmanagement.model.User;

import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    @Override
    public Optional<User> findByUsername(String name) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new DataAccessException("findByUsername failed for " + name, e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(Role.valueOf(rs.getString("role")));
        u.setStatus(RecordStatus.valueOf(rs.getString("status")));
        u.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return u;
    }

    @Override
    public Optional<User> findById(int userId) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }

    @Override
    public List<User> findByRole(Role role) {
        return List.of();
    }

    @Override
    public List<User> searchByUsername(String partial) {
        return List.of();
    }

    @Override
    public boolean existsByUsername(String username) {
        return false;
    }

    @Override
    public int insert(User user) {
        return 0;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public boolean updateStatus(int userId, RecordStatus status) {
        return false;
    }

    @Override
    public boolean updatePassword(int userId, String passwordHash) {
        return false;
    }
}
