package com.loanmanagement.controller;

import com.loanmanagement.model.Loan;
import com.loanmanagement.service.LoanService;
import com.loanmanagement.util.ConsoleUtil;

import java.util.List;

public class LoanController {

    public static void manageLoans(LoanService service) {
        while (true) {
            System.out.println();
            System.out.println("manage Loans");
            System.out.println("1.View all loans");
            System.out.println("2.Create loan from an approved application");
            System.out.println("3.Record payment (update outstanding)");
            System.out.println("4.Delete loan");
            System.out.println("0.Back");

            int choice = ConsoleUtil.readInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1 -> print(service.getAllLoans());
                    case 2 -> add(service);
                    case 3 -> delete(service);
                    case 0 -> { return; }
                    default -> System.out.println("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void print(List<Loan> loans) {
        if (loans.isEmpty()) {
            System.out.println("No loan found");
            return;
        }
        System.out.println();
        for (Loan loan : loans) {
            System.out.println(loan);
        }
        System.out.println(loans.size() + " loan(s)");
    }

    private static void add(LoanService service) {
        int applicationId = ConsoleUtil.readInt("Approved application id: ");
        service.addLoan(applicationId);
    }


    private static void delete(LoanService service) {
        int id = ConsoleUtil.readInt("Enter the loan id to delete: ");
        String confirm = ConsoleUtil.readLine("Type YES to confirm: ");
        if (!confirm.equals("YES")) {
            System.out.println("cancelled.");
            return;
        }
        service.deleteLoan(id);
        System.out.println("Deleted.");
    }
}