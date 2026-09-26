package com.loanmanagement.dao;

import com.loanmanagement.model.Customer;

import java.util.List;

public interface CustomerDao {
    void addCustomer(Customer customer);
    Customer getCustomerById(int customerId);
    Customer getCustomerByUserId(int userId);
    List<Customer> getAllCustomers();
    void updateCustomer(Customer customer);
    void deleteCustomer(int customerId);
}
