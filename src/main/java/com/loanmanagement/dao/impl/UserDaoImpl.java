package com.loanmanagement.dao.impl;

import com.loanmanagement.dao.UserDao;
import com.loanmanagement.exception.DataAccessException;
import com.loanmanagement.model.RecordStatus;
import com.loanmanagement.model.Role;
import com.loanmanagement.model.User;
import com.loanmanagement.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl implements UserDao {

    // ---------- READ ----------

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

    @Override
    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DataAccessException("findById failed for " + userId, e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;

        } catch (SQLException e) {
            throw new DataAccessException("findAll failed", e);
        }
    }

    @Override
    public List<User> findByRole(Role role) {
        String sql = "SELECT * FROM users WHERE role = ? ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, role.name());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
            return users;

        } catch (SQLException e) {
            throw new DataAccessException("findByRole failed for " + role, e);
        }
    }

    @Override
    public List<User> searchByUsername(String partial) {
        String sql = "SELECT * FROM users WHERE username LIKE ? ORDER BY user_id";
        List<User> users = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + partial + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
            return users;

        } catch (SQLException e) {
            throw new DataAccessException("searchByUsername failed for " + partial, e);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("existsByUsername failed for " + username, e);
        }
    }

    // ---------- CREATE ----------

    @Override
    public int insert(User user) {
        String sql = "INSERT INTO users (username, password_hash, role, status) VALUES (?,?,?,?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getRole().name());
            ps.setString(4, user.getStatus().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    user.setUserId(newId);
                    return newId;
                }
                throw new DataAccessException("Insert returned no generated key", null);
            }
        } catch (SQLException e) {
            throw new DataAccessException("insert failed for " + user.getUsername(), e);
        }
    }

    // ---------- UPDATE ----------

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET username = ?, role = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getRole().name());
            ps.setInt(3, user.getUserId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new DataAccessException("update failed for " + user.getUserId(), e);
        }
    }

    // ---------- DELETE (soft) ----------

    @Override
    public boolean updateStatus(int userId, RecordStatus status) {
        String sql = "UPDATE users SET status = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, userId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new DataAccessException("updateStatus failed for " + userId, e);
        }
    }

    @Override
    public boolean updatePassword(int userId, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, passwordHash);
            ps.setInt(2, userId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new DataAccessException("updatePassword failed for " + userId, e);
        }
    }

    // ---------- row -> object ----------

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
}