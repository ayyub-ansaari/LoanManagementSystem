package com.loanmanagement.dao;

import com.loanmanagement.model.Customer;

import java.util.List;

public interface CustomerDao {
    void addCustomer(Customer customer);
    Customer getCustomerById(int customerId);
    List<Customer> getAllCustomers();
    void updateCustomer(Customer customer);
    void deleteCustomer(int customerId);
}
