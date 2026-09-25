package com.loanmanagement.model;

import java.time.LocalDate;

public class Customer {
    private int customerId;
    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate dob;
    private String address;
    private double monthlyIncome;
    private String panNumber;
    private String aadhaarLast4;
    private EmploymentType employmentType;
    private String accountNumber;
    private String ifscCode;
    private RecordStatus status;

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public void setMonthlyIncome(double monthlyIncome) {
        this.monthlyIncome = monthlyIncome;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public String getAadhaarLast4() {
        return aadhaarLast4;
    }

    public void setAadhaarLast4(String aadhaarLast4) {
        this.aadhaarLast4 = aadhaarLast4;
    }

    public EmploymentType getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = (employmentType == null) ? null : EmploymentType.valueOf(employmentType);
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public RecordStatus getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = RecordStatus.valueOf(status);
    }
    @Override
    public String toString() {
        return customerId
                + " | userId " + userId
                + " | " + fullName
                + " | " + email
                + " | " + phone
                + " | dob " + dob
                + " | income " + monthlyIncome
                + " | pan " + panNumber
                + " | aadhaar " + aadhaarLast4
                + " | employment " + employmentType
                + " | account " + accountNumber
                + " | ifsc " + ifscCode
                + " | address " + address
                + " | " + status;
    }
}