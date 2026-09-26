package com.loanmanagement.dao.impl;

import com.loanmanagement.dao.CustomerDao;
import com.loanmanagement.exception.DataAccessException;
import com.loanmanagement.model.Customer;
import com.loanmanagement.model.LoanApplication;
import com.loanmanagement.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDaoImpl implements CustomerDao {

    private static final String add_customer =
            "INSERT INTO customers "
                    + "(user_id, full_name, email, phone, dob, address, monthly_income, "
                    + "pan_number, aadhaar_last4, employment_type, account_number, ifsc_code, status) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String get_customer_by_id =
            "SELECT * FROM customers WHERE customer_id = ?";
    private static final String get_customer_by_user_id =
            "SELECT * FROM customers WHERE user_id = ?";

    private static final String get_all_customer =
            "SELECT * FROM customers ORDER BY customer_id";

    private static final String update_customer =
            "UPDATE customers SET full_name = ?, email = ?, phone = ?, dob = ?, address = ?, "
                    + "monthly_income = ?, pan_number = ?, aadhaar_last4 = ?, employment_type = ?, "
                    + "account_number = ?, ifsc_code = ?, status = ? "
                    + "WHERE customer_id = ?";

    private static final String delete_customer =
            "DELETE FROM customers WHERE customer_id = ?";

    @Override
    public void addCustomer(Customer customer) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(add_customer, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, customer.getUserId());
            ps.setString(2, customer.getFullName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setDate(5, customer.getDob() == null ? null : Date.valueOf(customer.getDob()));
            ps.setString(6, customer.getAddress());
            ps.setDouble(7, customer.getMonthlyIncome());
            ps.setString(8, customer.getPanNumber());
            ps.setString(9, customer.getAadhaarLast4());
            ps.setString(10, customer.getEmploymentType() == null
                    ? null : customer.getEmploymentType().name());
            ps.setString(11, customer.getAccountNumber());
            ps.setString(12, customer.getIfscCode());
            ps.setString(13, customer.getStatus().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    customer.setCustomerId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("addCustomer failed : " + e.getMessage(), e);
        }
    }

    @Override
    public Customer getCustomerById(int customerId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(get_customer_by_id)) {

            ps.setInt(1, customerId);
            try (ResultSet result = ps.executeQuery()) {
                return result.next() ? mapRow(result) : null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("getCustomerById failed for : " + customerId, e);
        }
    }
    @Override
    public Customer getCustomerByUserId(int userId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(get_customer_by_user_id)) {

            ps.setInt(1, userId);
            try (ResultSet result = ps.executeQuery()) {
                return result.next() ? mapRow(result) : null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("getCustomerByUserId failed for : " + userId, e);
        }
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(get_all_customer);
             ResultSet result = ps.executeQuery()) {

            while (result.next()) {
                customers.add(mapRow(result));
            }
            return customers;

        } catch (SQLException e) {
            throw new DataAccessException("getAllCustomers failed ", e);
        }
    }

    @Override
    public void updateCustomer(Customer customer) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(update_customer)) {

            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getPhone());
            ps.setDate(4, customer.getDob() == null ? null : Date.valueOf(customer.getDob()));
            ps.setString(5, customer.getAddress());
            ps.setDouble(6, customer.getMonthlyIncome());
            ps.setString(7, customer.getPanNumber());
            ps.setString(8, customer.getAadhaarLast4());
            ps.setString(9, customer.getEmploymentType() == null
                    ? null : customer.getEmploymentType().name());
            ps.setString(10, customer.getAccountNumber());
            ps.setString(11, customer.getIfscCode());
            ps.setString(12, customer.getStatus().name());
            ps.setInt(13, customer.getCustomerId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("updateCustomer failed " + customer.getCustomerId(), e);
        }
    }

    @Override
    public void deleteCustomer(int customerId) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(delete_customer)) {

            ps.setInt(1, customerId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DataAccessException("deleteCustomer is failed:" + customerId, e);
        }
    }

    private Customer mapRow(ResultSet result) throws SQLException {
        Customer customer = new Customer();
        customer.setCustomerId(result.getInt("customer_id"));
        customer.setUserId(result.getInt("user_id"));
        customer.setFullName(result.getString("full_name"));
        customer.setEmail(result.getString("email"));
        customer.setPhone(result.getString("phone"));

        Date d = result.getDate("dob");
        customer.setDob(d == null ? null : d.toLocalDate());

        customer.setAddress(result.getString("address"));
        customer.setMonthlyIncome(result.getDouble("monthly_income"));
        customer.setPanNumber(result.getString("pan_number"));
        customer.setAadhaarLast4(result.getString("aadhaar_last4"));
        customer.setEmploymentType(result.getString("employment_type"));
        customer.setAccountNumber(result.getString("account_number"));
        customer.setIfscCode(result.getString("ifsc_code"));
        customer.setStatus(result.getString("status"));
        return customer;
    }
}