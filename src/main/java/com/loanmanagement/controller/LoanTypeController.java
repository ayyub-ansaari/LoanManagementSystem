package com.loanmanagement.controller;

import com.loanmanagement.model.LoanType;
import com.loanmanagement.service.LoanTypeService;
import com.loanmanagement.util.ConsoleUtil;

import java.util.Scanner;


import static java.lang.Integer.parseInt;

public class LoanTypeController {
    public static void manageLoantypes(LoanTypeService service){
        while(true){
            System.out.println("manage Loan Type");
            System.out.println("1.Add loan type");
            System.out.println("2.Edit loan type");
            System.out.println("3.Delete loan type");


            Scanner sc = new Scanner(System.in);
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            try{
                switch (choice){
                    case 1 -> add(service);
                }
            }catch (Exception e){
                System.out.println("Error" + e.getMessage());
            }
            }

        }
    private static void add(LoanTypeService service){
        LoanType loanType = new LoanType();

        loanType.setName(ConsoleUtil.readLine("Name: "));
        loanType.setDescription(ConsoleUtil.readLine("Description: "));
        loanType.setInterestRate(readDouble("Interest Rate: "));
        loanType.setMinAmount(readDouble("min amount: "));
        loanType.setMaxAmount(readDouble("Max amount: "));
        loanType.setMaxTenureMonths(ConsoleUtil.readInt("Maximum tenure in months: "));
        loanType.setStatus(askStatus());

        service.addLoanType(loanType);
        System.out.println("Created"+loanType);

    }
    private static double readDouble(String prompt) {
        while (true) {
            try {
                return Double.parseDouble(ConsoleUtil.readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number, for example 50000 or 12.50");
            }
        }
    }
    private static String askStatus() {
        System.out.println("1. ACTIVE   2. INACTIVE");
        return ConsoleUtil.readInt("Status: ") == 2 ? "INACTIVE" : "ACTIVE";
    }
}
