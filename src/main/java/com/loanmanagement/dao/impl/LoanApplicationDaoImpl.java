package com.loanmanagement.dao.impl;

import com.loanmanagement.dao.LoanApplicationDao;
import com.loanmanagement.exception.DataAccessException;
import com.loanmanagement.model.LoanApplication;
import com.loanmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanApplicationDaoImpl implements LoanApplicationDao {

    private static final String add_application =
            "INSERT INTO loan_applications "
                    + "(customer_id, loan_type_id, requested_amount, tenure_months, purpose) "
                    + "VALUES (?, ?, ?, ?, ?)";

    private static final String get_application_by_id =
            "SELECT * FROM loan_applications WHERE application_id = ?";

    private static final String get_applications_by_customer =
            "SELECT * FROM loan_applications WHERE customer_id = ? ORDER BY application_id";

    private static final String get_all_applications =
            "SELECT * FROM loan_applications ORDER BY application_id";

    private static final String update_application =
            "UPDATE loan_applications SET status = ?, remarks = ?, reviewed_by = ?, reviewed_at = ? "
                    + "WHERE application_id = ?";

    private static final String delete_application =
            "DELETE FROM loan_applications WHERE application_id = ?";

    @Override
    public void addApplication(LoanApplication application) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(add_application, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, application.getCustomerId());
            ps.setInt(2, application.getLoanTypeId());
            ps.setDouble(3, application.getRequestedAmount());
            ps.setInt(4, application.getTenureMonths());
            ps.setString(5, application.getPurpose());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    application.setApplicationId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Could not add application for customer " + application.getCustomerId()
                            + " : " + e.getMessage(), e);
        }
    }

    @Override
    public LoanApplication getApplicationById(int applicationId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(get_application_by_id)) {

            ps.setInt(1, applicationId);
            try (ResultSet result = ps.executeQuery()) {
                return result.next() ? mapRow(result) : null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("getApplicationById failed for : " + applicationId, e);
        }
    }

    @Override
    public List<LoanApplication> getAllApplications() {
        List<LoanApplication> applications = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(get_all_applications);
             ResultSet result = ps.executeQuery()) {

            while (result.next()) {
                applications.add(mapRow(result));
            }
            return applications;

        } catch (SQLException e) {
            throw new DataAccessException("getAllApplications failed ", e);
        }
    }

    @Override
    public List<LoanApplication> getApplicationsByCustomerId(int customerId) {
        List<LoanApplication> applications = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(get_applications_by_customer)) {

            ps.setInt(1, customerId);
            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    applications.add(mapRow(result));
                }
            }
            return applications;

        } catch (SQLException e) {
            throw new DataAccessException(
                    "getApplicationsByCustomerId failed for : " + customerId, e);
        }
    }

    @Override
    public void updateApplication(LoanApplication application) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(update_application)) {

            ps.setString(1, application.getStatus().name());
            ps.setString(2, application.getRemarks());

            if (application.getReviewedBy() > 0) {
                ps.setInt(3, application.getReviewedBy());
            } else {
                ps.setNull(3, Types.INTEGER);
            }

            ps.setTimestamp(4, application.getReviewedAt() == null
                    ? null : Timestamp.valueOf(application.getReviewedAt()));

            ps.setInt(5, application.getApplicationId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException(
                    "updateApplication failed " + application.getApplicationId(), e);
        }
    }

    @Override
    public void deleteApplication(int applicationId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(delete_application)) {

            ps.setInt(1, applicationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new DataAccessException("Application " + applicationId
                        + " cannot be deleted because a loan was created from it", e);
            }
            throw new DataAccessException(
                    "deleteApplication failed for " + applicationId + " : " + e.getMessage(), e);
        }
    }

    private LoanApplication mapRow(ResultSet result) throws SQLException {
        LoanApplication application = new LoanApplication();
        application.setApplicationId(result.getInt("application_id"));
        application.setCustomerId(result.getInt("customer_id"));
        application.setLoanTypeId(result.getInt("loan_type_id"));
        application.setRequestedAmount(result.getDouble("requested_amount"));
        application.setTenureMonths(result.getInt("tenure_months"));
        application.setPurpose(result.getString("purpose"));
        application.setStatus(result.getString("status"));
        application.setRemarks(result.getString("remarks"));
        application.setReviewedBy(result.getInt("reviewed_by"));
        application.setAppliedAt(result.getTimestamp("applied_at").toLocalDateTime());

        Timestamp reviewedAt = result.getTimestamp("reviewed_at");
        application.setReviewedAt(reviewedAt == null ? null : reviewedAt.toLocalDateTime());

        return application;
    }
}