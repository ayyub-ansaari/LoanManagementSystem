package com.loanmanagement.model;

import java.time.LocalDateTime;

public class LoanApplication {
    private int applicationId;
    private int customerId;
    private int loanTypeId;
    private double requestedAmount;
    private int tenureMonths;
    private String purpose;
    private LoanApplicationStatus status;
    private String remarks;
    private int reviewedBy;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getLoanTypeId() {
        return loanTypeId;
    }

    public void setLoanTypeId(int loanTypeId) {
        this.loanTypeId = loanTypeId;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public void setRequestedAmount(double requestedAmount) {
        this.requestedAmount = requestedAmount;
    }

    public int getTenureMonths() {
        return tenureMonths;
    }

    public void setTenureMonths(int tenureMonths) {
        this.tenureMonths = tenureMonths;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LoanApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = LoanApplicationStatus.valueOf(status);
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(int reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    @Override
    public String toString() {
        return applicationId
                + " | customerId " + customerId
                + " | loanTypeId " + loanTypeId
                + " | amount " + requestedAmount
                + " | tenure " + tenureMonths + " months"
                + " | purpose " + purpose
                + " | " + status
                + " | remarks " + remarks
                + " | reviewedBy " + reviewedBy
                + " | appliedAt " + appliedAt
                + " | reviewedAt " + reviewedAt;
    }
}
