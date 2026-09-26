-- =============================================================
-- Loan Management System - seed data
-- Run AFTER schema.sql. Safe to re-run any number of times.
--
-- Passwords are stored in plain text. Production-grade security
-- is outside the project scope (requirements section 13).
--   admin      Admin@123
--   officer1   Officer@123
--   customer1  Cust@1234   (and customer2, customer3, customer4)
-- =============================================================
USE lms_db;

-- 0. Clear existing rows so this script is idempotent ----------
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE repayments;
TRUNCATE TABLE loans;
TRUNCATE TABLE loan_applications;
TRUNCATE TABLE loan_types;
TRUNCATE TABLE customers;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;
-- 1. Users -----------------------------------------------------
-- customer3 is INACTIVE on purpose: proves an inactive login is refused.
INSERT INTO users (user_id, username, password_hash, role, status) VALUES
  (1, 'admin',     'Admin@123',   'ADMIN',        'ACTIVE'),
  (2, 'officer1',  'Officer@123', 'LOAN_OFFICER', 'ACTIVE'),
  (3, 'customer1', 'Cust@1234',   'CUSTOMER',     'ACTIVE'),
  (4, 'customer2', 'Cust@1234',   'CUSTOMER',     'ACTIVE'),
  (5, 'customer3', 'Cust@1234',   'CUSTOMER',     'INACTIVE'),
  (6, 'customer4', 'Cust@1234',   'CUSTOMER',     'ACTIVE');

-- 2. Customers -------------------------------------------------
-- Customer 4 has every optional field NULL: proves the nullable
-- columns and the blank-to-NULL handling in the console form.
INSERT INTO customers
  (customer_id, user_id, full_name, email, phone, dob, address, monthly_income,
   pan_number, aadhaar_last4, employment_type, account_number, ifsc_code,
   kyc_status, kyc_remarks, kyc_verified_by, kyc_verified_at,
   credit_score, existing_emi, status)
VALUES
  (1, 3, 'Priya Sharma', 'priya.sharma@example.com', '9876543210',
   '1996-04-12', '14 MG Road, Bengaluru 560001', 85000.00,
   'ABCDE1234F', '4821', 'SALARIED', '112233445566', 'HDFC0001234',
   'VERIFIED', NULL, 2, '2026-09-01 10:15:00',
   762, 0.00, 'ACTIVE'),

  (2, 4, 'Arjun Reddy', 'arjun.reddy@example.com', '9812345678',
   '1992-11-02', '7 Banjara Hills, Hyderabad 500034', 64000.00,
   'PQRST5678K', '7310', 'SELF_EMPLOYED', '998877665544', 'ICIC0005678',
   'VERIFIED', NULL, 2, '2026-09-02 11:40:00',
   690, 12000.00, 'ACTIVE'),

  (3, 5, 'Karthik Nair', 'karthik.nair@example.com', '9745612380',
   '1994-01-25', '22 Marine Drive, Kochi 682031', 52000.00,
   'LMNOP9012C', '5566', 'SALARIED', '445566778899', 'SBIN0009012',
   'VERIFIED', NULL, 2, '2026-09-03 09:05:00',
   705, 0.00, 'INACTIVE'),

  (4, 6, 'Meera Iyer', 'meera.iyer@example.com', '9632587410',
   '1998-07-19', '5 Anna Salai, Chennai 600002', 47000.00,
   NULL, NULL, NULL, NULL, NULL,
   'PENDING', NULL, NULL, NULL,
   NULL, 0.00, 'ACTIVE');

-- 3. Loan types ------------------------------------------------
-- Gold Loan is INACTIVE on purpose: a withdrawn product.
INSERT INTO loan_types
  (loan_type_id, name, description, interest_rate, min_amount, max_amount, max_tenure_months, status)
VALUES
  (1, 'Personal Loan',  'Unsecured loan for personal needs',           12.50,  50000.00,  1000000.00,  60, 'ACTIVE'),
  (2, 'Home Loan',      'Loan for purchase or construction of a house', 8.50, 500000.00, 10000000.00, 360, 'ACTIVE'),
  (3, 'Car Loan',       'Loan for a new or used car',                   9.50, 100000.00,  2500000.00,  84, 'ACTIVE'),
  (4, 'Education Loan', 'Loan for higher education',                   10.00,  50000.00,  2000000.00, 120, 'ACTIVE'),
  (5, 'Gold Loan',      'Withdrawn product - kept inactive for demo',  11.00,  25000.00,   500000.00,  36, 'INACTIVE');

-- 4. Loan applications ----------------------------------------
-- All three statuses present, so every branch is demonstrable.
-- Application 3 is PENDING: review it, then convert it to a loan.
INSERT INTO loan_applications
  (application_id, customer_id, loan_type_id, requested_amount, tenure_months,
   purpose, status, remarks, reviewed_by, applied_at, reviewed_at)
VALUES
  (1, 1, 1, 300000.00, 24, 'Home renovation',
   'APPROVED', 'Good credit profile, income comfortably covers the instalment.',
   2, '2026-09-04 09:30:00', '2026-09-04 15:10:00'),

  (2, 2, 3, 200000.00, 12, 'Purchase of a used car',
   'APPROVED', 'Approved with standard terms.',
   2, '2026-06-01 10:00:00', '2026-06-01 16:20:00'),

  (3, 2, 4, 500000.00, 60, 'Post graduate course fees',
   'PENDING', NULL,
   NULL, '2026-09-15 12:45:00', NULL),

  (4, 1, 2, 2500000.00, 240, 'Purchase of an apartment',
   'REJECTED', 'Requested amount and tenure are beyond the affordability limit.',
   2, '2026-09-10 14:00:00', '2026-09-11 10:05:00');

-- 5. Loans -----------------------------------------------------
-- total_payable equals principal_amount: interest schedules are
-- outside the scope of this phase, so a loan records what was
-- borrowed and what is still owed.
INSERT INTO loans
  (loan_id, application_id, customer_id, loan_type_id,
   principal_amount, interest_rate, tenure_months,
   total_payable, outstanding_amount, start_date, status, created_by)
VALUES
  (1, 1, 1, 1, 300000.00, 12.50, 24, 300000.00, 300000.00, '2026-09-05', 'ACTIVE', 2),
  (2, 2, 2, 3, 200000.00,  9.50, 12, 200000.00,      0.00, '2026-06-02', 'CLOSED', 2);

-- 6. Repayments ------------------------------------------------
-- Recorded against loan 2 only. Repayment recording is outside the
-- scope of the Java application; these rows keep the data model
-- complete and demonstrate the foreign key on loan deletion.
INSERT INTO repayments
  (repayment_id, loan_id, amount, payment_date, payment_mode, reference_no, remarks, recorded_by)
VALUES
  (1, 2, 100000.00, '2026-08-02', 'BANK_TRANSFER', 'NEFT-51001', 'First payment', 2),
  (2, 2, 100000.00, '2026-09-02', 'UPI',           'UPI-770021', 'Final settlement', 2);

-- verify ------------------------------------------------------
-- Expected: 6, 5, 4, 4, 2, 2
SELECT
  (SELECT COUNT(*) FROM users)             AS users,
  (SELECT COUNT(*) FROM loan_types)        AS loan_types,
  (SELECT COUNT(*) FROM customers)         AS customers,
  (SELECT COUNT(*) FROM loan_applications) AS applications,
  (SELECT COUNT(*) FROM loans)             AS loans,
  (SELECT COUNT(*) FROM repayments)        AS repayments;