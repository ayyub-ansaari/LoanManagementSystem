package com.loanmanagement.service;

import com.loanmanagement.model.LoanType;

import java.util.List;

public interface LoanTypeService {
    void addLoanType(LoanType loanType);

    LoanType getLoanTypeById(int loanTypeId);

    List<LoanType> getAllLoanTypes();

    void updateLoanType(LoanType loanType);

    void deleteLoanType(int loanTypeId);
}
