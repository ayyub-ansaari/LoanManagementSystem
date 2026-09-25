package com.loanmanagement.service;

import com.loanmanagement.model.LoanApplication;

import java.util.List;

public interface ApplicationService {
    void addApplication(LoanApplication application);
    LoanApplication getApplicationById(int applicationId);
    List<LoanApplication> getAllApplications();
    void updateApplication(LoanApplication application);
    void deleteApplication(int applicationId);
}
