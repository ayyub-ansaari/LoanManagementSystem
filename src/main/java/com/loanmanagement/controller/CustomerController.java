package com.loanmanagement.controller;

import com.loanmanagement.model.Customer;
import com.loanmanagement.service.CustomerService;
import com.loanmanagement.util.ConsoleUtil;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    public static void manageCustomers(CustomerService service) {
        while (true) {
            System.out.println();
            System.out.println("manage Customers");
            System.out.println("1.View all customers");
            System.out.println("2.Add customer");
            System.out.println("3.Edit customer");
            System.out.println("4.Delete customer");
            System.out.println("0.Back");

            int choice = ConsoleUtil.readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> print(service.getAllCustomers());
                    case 2 -> add(service);
                    case 3 -> edit(service);
                    case 4 -> delete(service);
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice");
                }
            } catch (Exception e) {
                logger.error("Operation failed in the customer menu", e);
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void print(List<Customer> customers) {
        if (customers.isEmpty()) {
            System.out.println("No customer found");
            return;
        }
        System.out.println();
        for (Customer customer : customers) {
            System.out.println(customer);
        }
        System.out.println(customers.size() + " customer(s)");
    }

    private static void add(CustomerService service) {
        Customer customer = new Customer();

        customer.setUserId(ConsoleUtil.readInt("Login user id: "));
        customer.setFullName(ConsoleUtil.readLine("Full name: "));
        customer.setEmail(ConsoleUtil.readLine("Email: "));
        customer.setPhone(ConsoleUtil.readLine("Phone (10 digits): "));
        customer.setDob(readDate("Date of birth (yyyy-MM-dd, blank to skip): "));
        customer.setAddress(readOptional("Address (blank to skip): "));
        customer.setMonthlyIncome(ConsoleUtil.readDouble("Monthly income: "));
        customer.setPanNumber(readOptional("PAN (10 characters, blank to skip): "));
        customer.setAadhaarLast4(readOptional("Aadhaar last 4 digits (blank to skip): "));
        customer.setEmploymentType(askEmploymentType());
        customer.setAccountNumber(readOptional("Account number (blank to skip): "));
        customer.setIfscCode(readOptional("IFSC code (blank to skip): "));
        customer.setStatus(askStatus());

        service.addCustomer(customer);
        System.out.println("Created: " + customer);
    }

    private static void edit(CustomerService service) {
        int id = ConsoleUtil.readInt("Enter the customer id to edit: ");
        Customer customer = service.getCustomerById(id);

        if (customer == null) {
            System.out.println("No customer found with id " + id);
            return;
        }

        customer.setFullName(ConsoleUtil.readLine("Full name: "));
        customer.setEmail(ConsoleUtil.readLine("Email: "));
        customer.setPhone(ConsoleUtil.readLine("Phone (10 digits): "));
        customer.setDob(readDate("Date of birth (yyyy-MM-dd, blank to skip): "));
        customer.setAddress(readOptional("Address (blank to skip): "));
        customer.setMonthlyIncome(ConsoleUtil.readDouble("Monthly income: "));
        customer.setPanNumber(readOptional("PAN (10 characters, blank to skip): "));
        customer.setAadhaarLast4(readOptional("Aadhaar last 4 digits (blank to skip): "));
        customer.setEmploymentType(askEmploymentType());
        customer.setAccountNumber(readOptional("Account number (blank to skip): "));
        customer.setIfscCode(readOptional("IFSC code (blank to skip): "));
        customer.setStatus(askStatus());

        service.updateCustomer(customer);
        System.out.println("updated: " + customer);
    }

    private static void delete(CustomerService service) {
        int id = ConsoleUtil.readInt("Enter the customer id to delete: ");
        String confirm = ConsoleUtil.readLine("Type YES to confirm: ");
        if (!confirm.equals("YES")) {
            System.out.println("cancelled.");
            return;
        }
        service.deleteCustomer(id);
        System.out.println("Deleted.");
    }

    private static String readOptional(String prompt) {
        String value = ConsoleUtil.readLine(prompt);
        return value.isBlank() ? null : value;
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            String value = ConsoleUtil.readLine(prompt);
            if (value.isBlank()) {
                return null;
            }
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException e) {
                System.out.println("Use the format yyyy-MM-dd, for example 1996-04-12");
            }
        }
    }

    private static String askEmploymentType() {
        System.out.println("1. SALARIED   2. SELF_EMPLOYED   (blank to skip)");
        String value = ConsoleUtil.readLine("Employment type: ");
        if (value.isBlank()) {
            return null;
        }
        return value.equals("2") ? "SELF_EMPLOYED" : "SALARIED";
    }

    private static String askStatus() {
        System.out.println("1. ACTIVE   2. INACTIVE");
        return ConsoleUtil.readInt("Status: ") == 2 ? "INACTIVE" : "ACTIVE";
    }
}