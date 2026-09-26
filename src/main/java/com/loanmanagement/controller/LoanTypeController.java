package com.loanmanagement.controller;

import com.loanmanagement.model.LoanType;
import com.loanmanagement.service.LoanTypeService;
import com.loanmanagement.util.ConsoleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;





public class LoanTypeController {

    private static final Logger logger = LoggerFactory.getLogger(LoanTypeController.class);

    public static void manageLoantypes(LoanTypeService service){
        while(true){
            System.out.println("manage Loan Type");
            System.out.println("1.View all loan types");
            System.out.println("2.Add loan type");
            System.out.println("3.Edit loan type");
            System.out.println("4.Delete loan type");
            System.out.println("0.Back");


            int choice = ConsoleUtil.readInt("Enter your choice: ");

            try{
                switch (choice){
                    case 1 ->print(service.getAllLoanTypes());
                    case 2 ->add(service);
                    case 3 -> edit(service);
                    case 4 -> delete(service);
                    case 0 -> {return; }
                }
            }catch (Exception e){
                logger.error("Operation failed in the loan type menu", e);
                System.out.println("Error" + e.getMessage());
            }
            }

        }
    private static void print(List<LoanType> types){
        if(types.isEmpty()){
            System.out.println("No loan type found");
            return;
        }
        System.out.println();
        for(LoanType loanType : types){
            System.out.println(loanType);
        }
        System.out.println(types.size()+"loan type(s)");
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
    private static void edit (LoanTypeService service){
        int id = ConsoleUtil.readInt("Enter the loan id to edit: ");
        LoanType loanType = service.getLoanTypeById(id);

        loanType.setName(ConsoleUtil.readLine("Name: "));
        loanType.setDescription(ConsoleUtil.readLine("Description: "));
        loanType.setInterestRate(readDouble("Interest rate: "));
        loanType.setMinAmount(readDouble("Min amount: "));
        loanType.setMaxAmount(readDouble("Max amount: "));
        loanType.setMaxTenureMonths(ConsoleUtil.readInt("Max tenure:"));
        loanType.setStatus(askStatus());

        service.updateLoanType(loanType);
        System.out.println("updated: "+loanType);
    }
    private static void delete(LoanTypeService service){
        int id = ConsoleUtil.readInt("Enter the loan id to delete: ");
        String confirm = ConsoleUtil.readLine("Type YES to confirm: ");
        if(!confirm.equals("YES")){
            System.out.println("cancelled.");
            return;
        }
        service.deleteLoanType(id);
        System.out.println("Deleted.");

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
