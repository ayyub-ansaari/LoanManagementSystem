package com.loanmanagement.service.impl;

import com.loanmanagement.dao.LoanTypeDao;
import com.loanmanagement.exception.BusinessException;
import com.loanmanagement.exception.ValidationException;
import com.loanmanagement.model.Loan;
import com.loanmanagement.model.LoanType;
import com.loanmanagement.service.LoanTypeService;
import com.loanmanagement.model.Role;
import com.loanmanagement.util.Session;

public class LoanTypeServiceImpl implements LoanTypeService {

    private final LoanTypeDao loanTypeDao;

    public LoanTypeServiceImpl(LoanTypeDao loanTypeDao){
        this.loanTypeDao = loanTypeDao;
    }

    @Override
    public void addLoanType(LoanType loanType) {
        requiredAdmin();
        validate(loanType);
        loanTypeDao.addLoanType(loanType);

    }



    @Override
    public LoanType getLoanTypeById(int loanTypeId) {
        requiredAdmin();
        LoanType loanType = loanTypeDao.getLoanTypeById(loanTypeId);
        if(loanType == null ){
            throw new BusinessException("No loan type with id "+loanTypeId);
        }
        return loanType;
    }

    @Override
    public void updateLoanType(LoanType loanType) {
        requiredAdmin();
        validate(loanType);

        getLoanTypeById(loanType.getLoanTypeId());
        loanTypeDao.updateLoanType(loanType);
    }

    @Override
    public void deleteLoanType(int loanTypeId) {
        requiredAdmin();
        getLoanTypeById(loanTypeId);
        loanTypeDao.deleteLoanType(loanTypeId);

    }
    private void validate(LoanType loantype){
        if(loantype.getName() == null || loantype.getName().isBlank()){
            throw new ValidationException("Name is required");
        }
        if(loantype.getInterestRate() <= 0){
            throw new ValidationException("Interest rate shouldn't be zero or nagative");
        }
        if(loantype.getMinAmount() <=0){
            throw new ValidationException("minAmount must be greater than zero");
        }
        if(loantype.getMaxAmount() < loantype.getMinAmount()){
            throw new ValidationException("Max amount should be greater than Min amount");
        }
        if(loantype.getMaxTenureMonths() <= 0) {
            throw new ValidationException("Tenure must be atleast 1 month");
        }
    }
    private void requiredAdmin() {
        if(Session.getRole() != Role.ADMIN){
            throw new BusinessException("Only an administrator can manage loan types");
        }
    }
}
