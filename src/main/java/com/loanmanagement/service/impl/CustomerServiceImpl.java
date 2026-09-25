package com.loanmanagement.service.impl;

import com.loanmanagement.dao.CustomerDao;
import com.loanmanagement.exception.BusinessException;
import com.loanmanagement.exception.NotFoundException;
import com.loanmanagement.exception.ValidationException;
import com.loanmanagement.model.Customer;
import com.loanmanagement.model.Role;
import com.loanmanagement.service.CustomerService;
import com.loanmanagement.util.Session;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerDao customerDao;

    public CustomerServiceImpl(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public void addCustomer(Customer customer) {
        requireStaff();
        validate(customer);
        customerDao.addCustomer(customer);
    }

    @Override
    public Customer getCustomerById(int customerId) {
        requireStaff();
        return customerDao.getCustomerById(customerId);
    }

    @Override
    public List<Customer> getAllCustomers() {
        requireStaff();
        return customerDao.getAllCustomers();
    }

    @Override
    public void updateCustomer(Customer customer) {
        requireStaff();
        validate(customer);
        if (customerDao.getCustomerById(customer.getCustomerId()) == null) {
            throw new NotFoundException("No customer found with id " + customer.getCustomerId());
        }
        customerDao.updateCustomer(customer);
    }

    @Override
    public void deleteCustomer(int customerId) {
        requireStaff();
        if (customerDao.getCustomerById(customerId) == null) {
            throw new NotFoundException("No customer found with id " + customerId);
        }
        customerDao.deleteCustomer(customerId);
    }

    private void requireStaff() {
        if (!Session.isLoggedIn()) {
            throw new BusinessException("You must be logged in to do this");
        }
        Role role = Session.getRole();
        if (role != Role.ADMIN && role != Role.LOAN_OFFICER) {
            throw new BusinessException("Only an admin or a loan officer can manage customers");
        }
    }

    private void validate(Customer customer) {
        if (customer == null) {
            throw new ValidationException("Customer details are missing");
        }
        if (customer.getUserId() <= 0) {
            throw new ValidationException("A valid login user id is required");
        }
        if (customer.getFullName() == null || customer.getFullName().isBlank()) {
            throw new ValidationException("Full name cannot be empty");
        }
        if (customer.getEmail() == null || !customer.getEmail().contains("@")) {
            throw new ValidationException("A valid email address is required");
        }
        if (customer.getPhone() == null || !customer.getPhone().matches("\\d{10}")) {
            throw new ValidationException("Phone number must be exactly 10 digits");
        }
        if (customer.getMonthlyIncome() <= 0) {
            throw new ValidationException("Monthly income must be greater than zero");
        }
        if (customer.getStatus() == null) {
            throw new ValidationException("Status is required");
        }

        String pan = customer.getPanNumber();
        if (pan != null && !pan.isBlank() && pan.length() != 10) {
            throw new ValidationException("PAN must be exactly 10 characters");
        }

        String aadhaar = customer.getAadhaarLast4();
        if (aadhaar != null && !aadhaar.isBlank() && !aadhaar.matches("\\d{4}")) {
            throw new ValidationException("Aadhaar last 4 must be exactly 4 digits");
        }
    }
}