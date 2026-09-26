package com.loanmanagement.dao;

import com.loanmanagement.model.Loan;

import java.util.List;

public interface LoanDao {
    void addLoan(Loan loan);

    Loan getLoanById(int loanId);

    List<Loan> getLoansByCustomerId(int customerId);

    List<Loan> getAllLoans();

    void deleteLoan(int loanId);
}
