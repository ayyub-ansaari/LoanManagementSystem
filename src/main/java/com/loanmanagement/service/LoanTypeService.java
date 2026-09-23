package com.loanmanagement.service;

import com.loanmanagement.model.LoanType;

public interface LoanTypeService {
    void addLoanType(LoanType loanType);

    LoanType getLoanTypeById(int loanTypeId);

    void updateLoanType(LoanType loanType);

    void deleteLoanType(int loanTypeId);
}
