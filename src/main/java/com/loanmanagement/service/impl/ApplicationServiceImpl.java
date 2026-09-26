package com.loanmanagement.service.impl;

import com.loanmanagement.dao.CustomerDao;
import com.loanmanagement.dao.LoanApplicationDao;
import com.loanmanagement.exception.BusinessException;
import com.loanmanagement.exception.NotFoundException;
import com.loanmanagement.exception.ValidationException;
import com.loanmanagement.model.Customer;
import com.loanmanagement.model.LoanApplication;
import com.loanmanagement.model.LoanApplicationStatus;
import com.loanmanagement.model.Role;
import com.loanmanagement.service.ApplicationService;
import com.loanmanagement.util.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

public class ApplicationServiceImpl implements ApplicationService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceImpl.class);

    private final LoanApplicationDao applicationDao;
    private final CustomerDao customerDao;

    public ApplicationServiceImpl(LoanApplicationDao applicationDao, CustomerDao customerDao) {
        this.applicationDao = applicationDao;
        this.customerDao = customerDao;
    }

    @Override
    public void addApplication(LoanApplication application) {
        if (!Session.isLoggedIn()) {
            throw new BusinessException("You must be logged in to do this");
        }

        if (Session.getRole() == Role.CUSTOMER) {
            application.setCustomerId(currentCustomerId());
        }

        validate(application);
        applicationDao.addApplication(application);

        logger.info("Application filed: applicationId={}, customerId={}, loanTypeId={}, "
                        + "amount={}, tenure={} months, by userId={} ({})",
                application.getApplicationId(), application.getCustomerId(),
                application.getLoanTypeId(), application.getRequestedAmount(),
                application.getTenureMonths(), Session.getCurrentUserId(), Session.getRole());
    }

    @Override
    public LoanApplication getApplicationById(int applicationId) {
        requireStaff();
        return applicationDao.getApplicationById(applicationId);
    }

    @Override
    public List<LoanApplication> getAllApplications() {
        requireStaff();
        return applicationDao.getAllApplications();
    }

    @Override
    public void updateApplication(LoanApplication application) {
        requireStaff();

        LoanApplication existing = applicationDao.getApplicationById(application.getApplicationId());

        if (existing == null) {
            throw new NotFoundException("No application found with id " + application.getApplicationId());
        }

        if (existing.getStatus() != LoanApplicationStatus.PENDING) {
            logger.warn("Review refused: applicationId={} is already {}",
                    application.getApplicationId(), existing.getStatus());
            throw new BusinessException("Application " + application.getApplicationId()
                    + " is already " + existing.getStatus() + " and cannot be reviewed again");
        }

        if (application.getStatus() == null || application.getStatus() == LoanApplicationStatus.PENDING) {
            throw new ValidationException("A review must be either APPROVED or REJECTED");
        }

        if (application.getStatus() == LoanApplicationStatus.REJECTED
                && (application.getRemarks() == null || application.getRemarks().isBlank())) {
            throw new ValidationException("A rejection must include a reason");
        }

        application.setReviewedBy(Session.getCurrentUserId());
        application.setReviewedAt(LocalDateTime.now());

        applicationDao.updateApplication(application);

        logger.info("Application reviewed: applicationId={}, decision={}, by userId={}",
                application.getApplicationId(), application.getStatus(), Session.getCurrentUserId());
    }

    @Override
    public void deleteApplication(int applicationId) {
        requireStaff();

        if (applicationDao.getApplicationById(applicationId) == null) {
            throw new NotFoundException("No application found with id " + applicationId);
        }

        applicationDao.deleteApplication(applicationId);

        logger.info("Application deleted: applicationId={}, by userId={}",
                applicationId, Session.getCurrentUserId());
    }

    @Override
    public List<LoanApplication> getMyApplications() {
        requireCustomer();
        return applicationDao.getApplicationsByCustomerId(currentCustomerId());
    }

    private void requireStaff() {
        if (!Session.isLoggedIn()) {
            throw new BusinessException("You must be logged in to do this");
        }
        Role role = Session.getRole();
        if (role != Role.ADMIN && role != Role.LOAN_OFFICER) {
            throw new BusinessException("Only an admin or a loan officer can manage applications");
        }
    }

    private void requireCustomer() {
        if (!Session.isLoggedIn()) {
            throw new BusinessException("You must be logged in to do this");
        }
        if (Session.getRole() != Role.CUSTOMER) {
            throw new BusinessException("Only a customer can view their own applications");
        }
    }

    private int currentCustomerId() {
        Customer customer = customerDao.getCustomerByUserId(Session.getCurrentUserId());
        if (customer == null) {
            throw new NotFoundException("No customer profile is linked to your login");
        }
        return customer.getCustomerId();
    }

    private void validate(LoanApplication application) {
        if (application == null) {
            throw new ValidationException("Application details are missing");
        }
        if (application.getCustomerId() <= 0) {
            throw new ValidationException("A valid customer id is required");
        }
        if (application.getLoanTypeId() <= 0) {
            throw new ValidationException("A valid loan type id is required");
        }
        if (application.getRequestedAmount() <= 0) {
            throw new ValidationException("Requested amount must be greater than zero");
        }
        if (application.getTenureMonths() <= 0) {
            throw new ValidationException("Tenure must be greater than zero months");
        }
    }
}