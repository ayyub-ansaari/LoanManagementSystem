package com.loanmanagement.dao;

import com.loanmanagement.model.LoanApplication;

import java.util.List;

public interface LoanApplicationDao {
    void addApplication(LoanApplication application);
    LoanApplication getApplicationById(int applicationId);
    List<LoanApplication> getAllApplications();
    List<LoanApplication> getApplicationsByCustomerId(int customerId);
    void updateApplication(LoanApplication application);
    void deleteApplication(int applicationId);
}
