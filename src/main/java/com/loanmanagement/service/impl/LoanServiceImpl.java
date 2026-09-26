package com.loanmanagement.service.impl;

import com.loanmanagement.dao.CustomerDao;
import com.loanmanagement.dao.LoanApplicationDao;
import com.loanmanagement.dao.LoanDao;
import com.loanmanagement.dao.LoanTypeDao;
import com.loanmanagement.exception.BusinessException;
import com.loanmanagement.exception.NotFoundException;
import com.loanmanagement.model.Customer;
import com.loanmanagement.model.Loan;
import com.loanmanagement.model.LoanApplication;
import com.loanmanagement.model.LoanApplicationStatus;

import com.loanmanagement.model.LoanType;
import com.loanmanagement.model.Role;
import com.loanmanagement.service.LoanService;

import com.loanmanagement.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class LoanServiceImpl implements LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanServiceImpl.class);


    private final LoanDao loanDao;
    private final LoanApplicationDao applicationDao;
    private final LoanTypeDao loanTypeDao;
    private final CustomerDao customerDao;

    public LoanServiceImpl(LoanDao loanDao, LoanApplicationDao applicationDao,
                           LoanTypeDao loanTypeDao, CustomerDao customerDao) {
        this.loanDao = loanDao;
        this.applicationDao = applicationDao;
        this.loanTypeDao = loanTypeDao;
        this.customerDao = customerDao;
    }

    @Override
    public void addLoan(int applicationId) {
        requireStaff();

        LoanApplication application = applicationDao.getApplicationById(applicationId);

        if (application == null) {
            throw new NotFoundException("No application found with id " + applicationId);
        }

        if (application.getStatus() != LoanApplicationStatus.APPROVED) {
            logger.warn("Loan creation refused: applicationId={} is {}",
                    applicationId, application.getStatus());
            throw new BusinessException("Application " + applicationId + " is "
                    + application.getStatus()
                    + " — only an APPROVED application can become a loan");
        }

        LoanType loanType = loanTypeDao.getLoanTypeById(application.getLoanTypeId());

        if (loanType == null) {
            throw new NotFoundException("Loan type " + application.getLoanTypeId() + " no longer exists");
        }

        double principal = application.getRequestedAmount();

        Loan loan = new Loan();
        loan.setApplicationId(application.getApplicationId());
        loan.setCustomerId(application.getCustomerId());
        loan.setLoanTypeId(application.getLoanTypeId());
        loan.setPrincipalAmount(principal);
        loan.setInterestRate(loanType.getInterestRate());
        loan.setTenureMonths(loanType.getMaxTenureMonths());
        loan.setTotalPayable(principal);
        loan.setOutstandingAmount(principal);
        loan.setStartDate(LocalDate.now());
        loan.setCreatedBy(Session.getCurrentUserId());

        loanDao.addLoan(loan);
        logger.info("Loan created: loanId={}, applicationId={}, customerId={}, "
                        + "principal={}, rate={}, tenure={} months, by userId={}",
                loan.getLoanId(), applicationId, loan.getCustomerId(),
                principal, loanType.getInterestRate(), loanType.getMaxTenureMonths(), Session.getCurrentUserId());

        System.out.println("Loan created: " + loan);
    }

    @Override
    public Loan getLoanById(int loanId) {
        requireStaff();
        return loanDao.getLoanById(loanId);
    }

    @Override
    public List<Loan> getAllLoans() {
        requireStaff();
        return loanDao.getAllLoans();
    }


    @Override
    public void deleteLoan(int loanId) {
        requireStaff();

        if (loanDao.getLoanById(loanId) == null) {
            throw new NotFoundException("No loan found with id " + loanId);
        }

        loanDao.deleteLoan(loanId);
        logger.info("Loan deleted: loanId={}, by userId={}", loanId, Session.getCurrentUserId());

    }

    @Override
    public List<Loan> getMyLoans() {
        requireCustomer();
        return loanDao.getLoansByCustomerId(currentCustomerId());
    }

    private void requireStaff() {
        if (!Session.isLoggedIn()) {
            throw new BusinessException("You must be logged in to do this");
        }
        Role role = Session.getRole();
        if (role != Role.ADMIN && role != Role.LOAN_OFFICER) {
            throw new BusinessException("Only an admin or a loan officer can manage loans");
        }
    }

    private void requireCustomer() {
        if (!Session.isLoggedIn()) {
            throw new BusinessException("You must be logged in to do this");
        }
        if (Session.getRole() != Role.CUSTOMER) {
            throw new BusinessException("Only a customer can view their own loans");
        }
    }

    private int currentCustomerId() {
        Customer customer = customerDao.getCustomerByUserId(Session.getCurrentUserId());
        if (customer == null) {
            throw new NotFoundException("No customer profile is linked to your login");
        }
        return customer.getCustomerId();
    }
}