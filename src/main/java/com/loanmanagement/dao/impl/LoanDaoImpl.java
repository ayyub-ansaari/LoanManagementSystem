package com.loanmanagement.dao.impl;

import com.loanmanagement.dao.LoanDao;
import com.loanmanagement.exception.DataAccessException;
import com.loanmanagement.model.Loan;
import com.loanmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoanDaoImpl implements LoanDao {

    private static final String add_loan =
            "INSERT INTO loans "
                    + "(application_id, customer_id, loan_type_id, principal_amount, interest_rate, "
                    + "tenure_months, total_payable, outstanding_amount, start_date, created_by) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String get_loan_by_id =
            "SELECT * FROM loans WHERE loan_id = ?";

    private static final String get_loans_by_customer =
            "SELECT * FROM loans WHERE customer_id = ? ORDER BY loan_id";

    private static final String get_all_loans =
            "SELECT * FROM loans ORDER BY loan_id";

    private static final String update_loan =
            "UPDATE loans SET outstanding_amount = ?, status = ? WHERE loan_id = ?";

    private static final String delete_loan =
            "DELETE FROM loans WHERE loan_id = ?";

    @Override
    public void addLoan(Loan loan) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(add_loan, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, loan.getApplicationId());
            ps.setInt(2, loan.getCustomerId());
            ps.setInt(3, loan.getLoanTypeId());
            ps.setDouble(4, loan.getPrincipalAmount());
            ps.setDouble(5, loan.getInterestRate());
            ps.setInt(6, loan.getTenureMonths());
            ps.setDouble(7, loan.getTotalPayable());
            ps.setDouble(8, loan.getOutstandingAmount());
            ps.setDate(9, Date.valueOf(loan.getStartDate()));
            ps.setInt(10, loan.getCreatedBy());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    loan.setLoanId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                throw new DataAccessException("Application " + loan.getApplicationId()
                        + " already has a loan — one application can become only one loan", e);
            }
            throw new DataAccessException(
                    "Could not add loan for application " + loan.getApplicationId()
                            + " : " + e.getMessage(), e);
        }
    }

    @Override
    public Loan getLoanById(int loanId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(get_loan_by_id)) {

            ps.setInt(1, loanId);
            try (ResultSet result = ps.executeQuery()) {
                return result.next() ? mapRow(result) : null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("getLoanById failed for : " + loanId, e);
        }
    }
    @Override
    public List<Loan> getLoansByCustomerId(int customerId) {
        List<Loan> loans = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(get_loans_by_customer)) {

            ps.setInt(1, customerId);
            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    loans.add(mapRow(result));
                }
            }
            return loans;

        } catch (SQLException e) {
            throw new DataAccessException("getLoansByCustomerId failed for : " + customerId, e);
        }
    }

    @Override
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(get_all_loans);
             ResultSet result = ps.executeQuery()) {

            while (result.next()) {
                loans.add(mapRow(result));
            }
            return loans;

        } catch (SQLException e) {
            throw new DataAccessException("getAllLoans failed ", e);
        }
    }



    @Override
    public void deleteLoan(int loanId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(delete_loan)) {

            ps.setInt(1, loanId);
            ps.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new DataAccessException("Loan " + loanId
                        + " cannot be deleted because repayments are recorded against it", e);
            }
            throw new DataAccessException(
                    "deleteLoan failed for " + loanId + " : " + e.getMessage(), e);
        }
    }

    private Loan mapRow(ResultSet result) throws SQLException {
        Loan loan = new Loan();
        loan.setLoanId(result.getInt("loan_id"));
        loan.setApplicationId(result.getInt("application_id"));
        loan.setCustomerId(result.getInt("customer_id"));
        loan.setLoanTypeId(result.getInt("loan_type_id"));
        loan.setPrincipalAmount(result.getDouble("principal_amount"));
        loan.setInterestRate(result.getDouble("interest_rate"));
        loan.setTenureMonths(result.getInt("tenure_months"));
        loan.setTotalPayable(result.getDouble("total_payable"));
        loan.setOutstandingAmount(result.getDouble("outstanding_amount"));
        loan.setStartDate(result.getDate("start_date").toLocalDate());
        loan.setStatus(result.getString("status"));
        loan.setCreatedBy(result.getInt("created_by"));
        return loan;
    }
}