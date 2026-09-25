package com.loanmanagement.service;

import com.loanmanagement.model.Customer;

import java.util.List;

public interface CustomerService {
    void addCustomer(Customer customer);
    Customer getCustomerById(int customerId);
    List<Customer> getAllCustomers();
    void updateCustomer(Customer customer);
    void deleteCustomer(int customerId);
}
