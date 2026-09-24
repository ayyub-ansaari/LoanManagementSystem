package com.loanmanagement.service.impl;

import com.loanmanagement.dao.LoanTypeDao;
import com.loanmanagement.model.LoanType;
import com.loanmanagement.service.LoanTypeService;
import com.loanmanagement.model.Role;

public class LoanTypeServiceImpl implements LoanTypeService {

    private final LoanTypeDao loanTypeDao;

    public LoanTypeServiceImpl(LoanTypeDao loanTypeDao){
        this.loanTypeDao = loanTypeDao;
    }

    @Override
    public void addLoanType(LoanType loanType) {
        requiredAdmin();

    }

    @Override
    public LoanType getLoanTypeById(int loanTypeId) {
        return null;
    }

    @Override
    public void updateLoanType(LoanType loanType) {

    }

    @Override
    public void deleteLoanType(int loanTypeId) {

    }
}
