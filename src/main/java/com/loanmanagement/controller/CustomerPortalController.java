package com.loanmanagement.controller;

import com.loanmanagement.model.LoanApplication;
import com.loanmanagement.service.ApplicationService;
import com.loanmanagement.service.AuthService;
import com.loanmanagement.service.CustomerService;
import com.loanmanagement.service.LoanService;
import com.loanmanagement.service.LoanTypeService;
import com.loanmanagement.util.ConsoleUtil;
import com.loanmanagement.util.Session;

import java.util.List;

public class CustomerPortalController {

    public static void menu(AuthService auth, LoanTypeService loanTypeService,
                            ApplicationService applicationService, LoanService loanService,
                            CustomerService customerService) {
        while (Session.isLoggedIn()) {
            ConsoleUtil.heading("Customer Menu");
            System.out.println("1. View available loan types");
            System.out.println("2. Apply for a loan");
            System.out.println("3. My applications");
            System.out.println("4. My loans");
            System.out.println("5. My profile");
            System.out.println("0. Logout");

            try {
                switch (ConsoleUtil.readInt("Choice: ")) {
                    case 1 -> print(loanTypeService.getAllLoanTypes(), "loan type");
                    case 2 -> apply(applicationService);
                    case 3 -> print(applicationService.getMyApplications(), "application");
                    case 4 -> print(loanService.getMyLoans(), "loan");
                    case 5 -> System.out.println(customerService.getMyProfile());
                    case 0 -> auth.logout();
                    default -> System.out.println("Invalid choice.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void apply(ApplicationService service) {
        LoanApplication application = new LoanApplication();

        application.setLoanTypeId(ConsoleUtil.readInt("Loan type id: "));
        application.setRequestedAmount(ConsoleUtil.readDouble("Amount you need: "));
        application.setTenureMonths(ConsoleUtil.readInt("Tenure in months: "));
        application.setPurpose(readOptional("Purpose (blank to skip): "));

        service.addApplication(application);
        System.out.println("Application submitted. It stays PENDING until an officer reviews it.");
    }

    private static void print(List<?> rows, String label) {
        if (rows.isEmpty()) {
            System.out.println("No " + label + " found");
            return;
        }
        System.out.println();
        for (Object row : rows) {
            System.out.println(row);
        }
        System.out.println(rows.size() + " " + label + "(s)");
    }

    private static String readOptional(String prompt) {
        String value = ConsoleUtil.readLine(prompt);
        return value.isBlank() ? null : value;
    }
}