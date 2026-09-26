package com.loanmanagement.service;

import com.loanmanagement.model.Loan;
import java.util.List;

public interface LoanService {
    void addLoan(int applicationId);
    Loan getLoanById(int loanId);
    List<Loan> getAllLoans();
    void deleteLoan(int loanId);

    List<Loan> getMyLoans();
}