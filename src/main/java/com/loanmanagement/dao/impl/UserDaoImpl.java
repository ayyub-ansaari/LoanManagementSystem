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

    // ---------- SQL QUERIES ----------

    private static final String find_by_user_name =
            "SELECT * FROM users WHERE username = ?";

    private static final String find_by_id =
            "SELECT * FROM users WHERE user_id = ?";

    private static final String find_all_query =
            "SELECT * FROM users ORDER BY user_id";

    private static final String exist_by_username =
            "SELECT 1 FROM users WHERE username = ?";

    private static final String insert_sqlQuery =
            "INSERT INTO users (username, password_hash, role, status) VALUES (?,?,?,?)";

    private static final String update_query =
            "UPDATE users SET username = ?, role = ? WHERE user_id = ?";

    private static final String delete_query =
            "DELETE FROM users WHERE user_id = ?";


    // ---------- FIND BY USERNAME ----------

    @Override
    public Optional<User> findByUsername(String name) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(find_by_user_name)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(mapRow(rs))
                        : Optional.empty();
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                    "findByUsername failed for " + name, e);
        }
    }


    // ---------- FIND BY ID ----------

    @Override
    public Optional<User> findById(int userId) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(find_by_id)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next()
                        ? Optional.of(mapRow(rs))
                        : Optional.empty();
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                    "findById failed for " + userId, e);
        }
    }


    // ---------- FIND ALL ----------

    @Override
    public List<User> findAll() {

        List<User> users = new ArrayList<>();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(find_all_query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }

            return users;

        } catch (SQLException e) {
            throw new DataAccessException("findAll failed", e);
        }
    }


    // ---------- EXISTS BY USERNAME ----------

    @Override
    public boolean existsByUsername(String username) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(exist_by_username)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                    "existsByUsername failed for " + username, e);
        }
    }


    // ---------- CREATE ----------

    @Override
    public int insert(User user) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     insert_sqlQuery, Statement.RETURN_GENERATED_KEYS)) {

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

                throw new DataAccessException(
                        "Insert returned no generated key", null);
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                    "insert failed for " + user.getUsername(), e);
        }
    }


    // ---------- UPDATE ----------

    @Override
    public boolean update(User user) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(update_query)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getRole().name());
            ps.setInt(3, user.getUserId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new DataAccessException(
                    "update failed for " + user.getUserId(), e);
        }
    }


    // ---------- DELETE ----------

    @Override
    public boolean delete(int userId) {

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(delete_query)) {

            ps.setInt(1, userId);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Cannot delete user " + userId
                            + " — they have related records "
                            + "(customer, loan or repayment)", e);
        }
    }


    // ---------- ROW → OBJECT ----------

    private User mapRow(ResultSet rs) throws SQLException {

        User u = new User();

        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(Role.valueOf(rs.getString("role")));
        u.setStatus(RecordStatus.valueOf(rs.getString("status")));
        u.setCreatedAt(
                rs.getTimestamp("created_at").toLocalDateTime()
        );

        return u;
    }
}