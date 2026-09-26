package com.loanmanagement.util;

public final class LoanCalculator {

    private LoanCalculator() {
    }

    public static double totalPayable(double principal, double annualRate, int tenureMonths) {
        double years = tenureMonths / 12.0;
        double interest = principal * (annualRate / 100.0) * years;
        return principal + interest;
    }
}
