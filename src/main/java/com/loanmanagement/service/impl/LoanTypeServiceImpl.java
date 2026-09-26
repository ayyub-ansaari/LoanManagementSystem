package com.loanmanagement.service.impl;

import com.loanmanagement.dao.LoanTypeDao;
import com.loanmanagement.exception.BusinessException;
import com.loanmanagement.exception.NotFoundException;
import com.loanmanagement.exception.ValidationException;
import com.loanmanagement.model.LoanType;
import com.loanmanagement.model.Role;
import com.loanmanagement.service.LoanTypeService;
import com.loanmanagement.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class LoanTypeServiceImpl implements LoanTypeService {

    private static final Logger logger = LoggerFactory.getLogger(LoanTypeServiceImpl.class);

    private final LoanTypeDao loanTypeDao;

    public LoanTypeServiceImpl(LoanTypeDao loanTypeDao) {
        this.loanTypeDao = loanTypeDao;
    }

    @Override
    public void addLoanType(LoanType loanType) {
        requiredAdmin();
        validate(loanType);
        loanTypeDao.addLoanType(loanType);

        logger.info("Loan type created: loanTypeId={}, name={}, rate={}, by userId={}",
                loanType.getLoanTypeId(), loanType.getName(),
                loanType.getInterestRate(), Session.getCurrentUserId());
    }

    @Override
    public LoanType getLoanTypeById(int loanTypeId) {
        requiredAdmin();
        LoanType loanType = loanTypeDao.getLoanTypeById(loanTypeId);
        if (loanType == null) {
            throw new NotFoundException("No loan type with id " + loanTypeId);
        }
        return loanType;
    }

    @Override
    public List<LoanType> getAllLoanTypes() {
        requireLogin();
        return loanTypeDao.getAllLoanTypes();
    }

    @Override
    public void updateLoanType(LoanType loanType) {
        requiredAdmin();
        validate(loanType);
        getLoanTypeById(loanType.getLoanTypeId());
        loanTypeDao.updateLoanType(loanType);

        logger.info("Loan type updated: loanTypeId={}, name={}, rate={}, by userId={}",
                loanType.getLoanTypeId(), loanType.getName(),
                loanType.getInterestRate(), Session.getCurrentUserId());
    }

    @Override
    public void deleteLoanType(int loanTypeId) {
        requiredAdmin();
        getLoanTypeById(loanTypeId);
        loanTypeDao.deleteLoanType(loanTypeId);

        logger.info("Loan type deleted: loanTypeId={}, by userId={}",
                loanTypeId, Session.getCurrentUserId());
    }

    private void validate(LoanType loantype) {
        if (loantype.getName() == null || loantype.getName().isBlank()) {
            throw new ValidationException("Name is required");
        }
        if (loantype.getInterestRate() <= 0) {
            throw new ValidationException("Interest rate shouldn't be zero or negative");
        }
        if (loantype.getMinAmount() <= 0) {
            throw new ValidationException("minAmount must be greater than zero");
        }
        if (loantype.getMaxAmount() < loantype.getMinAmount()) {
            throw new ValidationException("Max amount should be greater than Min amount");
        }
        if (loantype.getMaxTenureMonths() <= 0) {
            throw new ValidationException("Tenure must be atleast 1 month");
        }
        if (loantype.getStatus() == null) {
            throw new ValidationException("Status is required");
        }
    }

    private void requireLogin() {
        if (!Session.isLoggedIn()) {
            throw new BusinessException("You must be logged in to do this");
        }
    }

    private void requiredAdmin() {
        requireLogin();
        if (Session.getRole() != Role.ADMIN) {
            throw new BusinessException("Only an administrator can manage loan types");
        }
    }
}