package com.loanmanagement.controller;

import com.loanmanagement.model.LoanApplication;
import com.loanmanagement.service.ApplicationService;
import com.loanmanagement.util.ConsoleUtil;

import java.util.List;

public class ApplicationController {

    public static void manageApplications(ApplicationService service) {
        while (true) {
            System.out.println();
            System.out.println("manage Loan Applications");
            System.out.println("1.View all applications");
            System.out.println("2.Add application");
            System.out.println("3.Review application (approve / reject)");
            System.out.println("4.Delete application");
            System.out.println("0.Back");

            int choice = ConsoleUtil.readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> print(service.getAllApplications());
                    case 2 -> add(service);
                    case 3 -> review(service);
                    case 4 -> delete(service);
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void print(List<LoanApplication> applications) {
        if (applications.isEmpty()) {
            System.out.println("No application found");
            return;
        }
        System.out.println();
        for (LoanApplication application : applications) {
            System.out.println(application);
        }
        System.out.println(applications.size() + " application(s)");
    }

    private static void add(ApplicationService service) {
        LoanApplication application = new LoanApplication();

        application.setCustomerId(ConsoleUtil.readInt("Customer id: "));
        application.setLoanTypeId(ConsoleUtil.readInt("Loan type id: "));
        application.setRequestedAmount(ConsoleUtil.readDouble("Requested amount: "));
        application.setTenureMonths(ConsoleUtil.readInt("Tenure in months: "));
        application.setPurpose(readOptional("Purpose (blank to skip): "));

        service.addApplication(application);
        System.out.println("Created: " + application);
    }

    private static void review(ApplicationService service) {
        int id = ConsoleUtil.readInt("Enter the application id to review: ");
        LoanApplication application = service.getApplicationById(id);

        if (application == null) {
            System.out.println("No application found with id " + id);
            return;
        }

        System.out.println("Current: " + application);

        application.setStatus(askDecision());
        application.setRemarks(readOptional("Remarks (required when rejecting): "));

        service.updateApplication(application);
        System.out.println("Reviewed: " + application);
    }

    private static void delete(ApplicationService service) {
        int id = ConsoleUtil.readInt("Enter the application id to delete: ");
        String confirm = ConsoleUtil.readLine("Type YES to confirm: ");
        if (!confirm.equals("YES")) {
            System.out.println("cancelled.");
            return;
        }
        service.deleteApplication(id);
        System.out.println("Deleted.");
    }

    private static String askDecision() {
        System.out.println("1. APPROVED   2. REJECTED");
        return ConsoleUtil.readInt("Decision: ") == 2 ? "REJECTED" : "APPROVED";
    }

    private static String readOptional(String prompt) {
        String value = ConsoleUtil.readLine(prompt);
        return value.isBlank() ? null : value;
    }
}