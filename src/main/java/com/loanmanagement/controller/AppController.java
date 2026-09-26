package com.loanmanagement.controller;

import com.loanmanagement.dao.CustomerDao;
import com.loanmanagement.dao.LoanApplicationDao;
import com.loanmanagement.dao.LoanDao;
import com.loanmanagement.dao.LoanTypeDao;
import com.loanmanagement.dao.UserDao;
import com.loanmanagement.dao.impl.CustomerDaoImpl;
import com.loanmanagement.dao.impl.LoanApplicationDaoImpl;
import com.loanmanagement.dao.impl.LoanDaoImpl;
import com.loanmanagement.dao.impl.LoanTypeDaoImpl;
import com.loanmanagement.dao.impl.UserDaoImpl;
import com.loanmanagement.model.User;
import com.loanmanagement.service.ApplicationService;
import com.loanmanagement.service.AuthService;
import com.loanmanagement.service.CustomerService;
import com.loanmanagement.service.LoanService;
import com.loanmanagement.service.LoanTypeService;
import com.loanmanagement.service.UserService;
import com.loanmanagement.service.impl.ApplicationServiceImpl;
import com.loanmanagement.service.impl.AuthServiceImpl;
import com.loanmanagement.service.impl.CustomerServiceImpl;
import com.loanmanagement.service.impl.LoanServiceImpl;
import com.loanmanagement.service.impl.LoanTypeServiceImpl;
import com.loanmanagement.service.impl.UserServiceImpl;
import com.loanmanagement.util.ConsoleUtil;
import com.loanmanagement.util.Session;

public class AppController {

    public static void main(String[] args) {

        UserDao userDao = new UserDaoImpl();
        LoanTypeDao loanTypeDao = new LoanTypeDaoImpl();
        CustomerDao customerDao = new CustomerDaoImpl();
        LoanApplicationDao applicationDao = new LoanApplicationDaoImpl();
        LoanDao loanDao = new LoanDaoImpl();

        AuthService auth = new AuthServiceImpl(userDao);
        UserService userService = new UserServiceImpl(userDao);
        LoanTypeService loanTypeService = new LoanTypeServiceImpl(loanTypeDao);
        CustomerService customerService = new CustomerServiceImpl(customerDao);
        ApplicationService applicationService = new ApplicationServiceImpl(applicationDao, customerDao);
        LoanService loanService = new LoanServiceImpl(loanDao, applicationDao, loanTypeDao, customerDao);

        System.out.println("=====================================");
        System.out.println("        Loan Management System");
        System.out.println("=====================================");

        while (true) {

            if (!Session.isLoggedIn()) {
                ConsoleUtil.heading("Login");
                String username = ConsoleUtil.readLine("Username (blank to exit): ");

                if (username.isBlank()) {
                    System.out.println("Goodbye.");
                    return;
                }

                String password = ConsoleUtil.readLine("Password: ");

                try {
                    User user = auth.login(username, password);
                    System.out.println("\nWelcome, " + user.getUsername() + " (" + user.getRole() + ")");
                } catch (Exception e) {
                    System.out.println("Login failed: " + e.getMessage());
                }
                continue;
            }

            switch (Session.getRole()) {
                case ADMIN -> adminMenu(auth, userService, loanTypeService,
                        customerService, applicationService, loanService);
                case LOAN_OFFICER -> officerMenu(auth, customerService, applicationService, loanService);
                case CUSTOMER -> CustomerPortalController.menu(auth, loanTypeService,
                        applicationService, loanService, customerService);
            }
        }
    }

    private static void adminMenu(AuthService auth, UserService userService,
                                  LoanTypeService loanTypeService, CustomerService customerService,
                                  ApplicationService applicationService, LoanService loanService) {
        while (Session.isLoggedIn()) {
            ConsoleUtil.heading("Admin Menu");
            System.out.println("1. Manage users");
            System.out.println("2. Manage loan types");
            System.out.println("3. Manage customers");
            System.out.println("4. Manage applications");
            System.out.println("5. Manage loans");
            System.out.println("0. Logout");

            switch (ConsoleUtil.readInt("Choice: ")) {
                case 1 -> AdminController.manageUsers(userService);
                case 2 -> LoanTypeController.manageLoantypes(loanTypeService);
                case 3 -> CustomerController.manageCustomers(customerService);
                case 4 -> ApplicationController.manageApplications(applicationService);
                case 5 -> LoanController.manageLoans(loanService);
                case 0 -> auth.logout();
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void officerMenu(AuthService auth, CustomerService customerService,
                                    ApplicationService applicationService, LoanService loanService) {
        while (Session.isLoggedIn()) {
            ConsoleUtil.heading("Loan Officer Menu");
            System.out.println("1. Manage customers");
            System.out.println("2. Manage applications");
            System.out.println("3. Manage loans");
            System.out.println("0. Logout");

            switch (ConsoleUtil.readInt("Choice: ")) {
                case 1 -> CustomerController.manageCustomers(customerService);
                case 2 -> ApplicationController.manageApplications(applicationService);
                case 3 -> LoanController.manageLoans(loanService);
                case 0 -> auth.logout();
                default -> System.out.println("Invalid choice.");
            }
        }
    }
}