-- =============================================================
-- Loan Management System � seed data (BRD section 18)
-- Run AFTER schema.sql.
-- All seed users have password: Password@123
-- The password_hash below is a placeholder BCrypt string; regenerate
-- with PasswordUtil once its hashing scheme is implemented.
-- =============================================================
USE lms_db;

-- 0. Clear existing rows so this script is idempotent ---------
-- Delete children first to respect foreign keys, then reset
-- AUTO_INCREMENT so the inserts below land on their expected IDs.
DELETE FROM repayments;
DELETE FROM loans;
DELETE FROM loan_applications;
DELETE FROM loan_types;
DELETE FROM customers;
DELETE FROM users;

ALTER TABLE repayments        AUTO_INCREMENT = 1;
ALTER TABLE loans             AUTO_INCREMENT = 1;
ALTER TABLE loan_applications AUTO_INCREMENT = 1;
ALTER TABLE loan_types        AUTO_INCREMENT = 1;
ALTER TABLE customers         AUTO_INCREMENT = 1;
ALTER TABLE users             AUTO_INCREMENT = 1;

-- 1. Users -----------------------------------------------------
INSERT INTO users (user_id, username, password_hash, role, status) VALUES
  (1, 'admin',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN',        'ACTIVE'),
  (2, 'officer1',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'LOAN_OFFICER', 'ACTIVE'),
  (3, 'priya',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER',     'ACTIVE'),
  (4, 'arjun',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER',     'ACTIVE'),
  (5, 'customer3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CUSTOMER',     'INACTIVE');

-- 2. Customers -------------------------------------------------
-- Priya Sharma: VERIFIED, credit 762 (Excellent), existing EMI 0
-- Arjun Reddy : VERIFIED, credit 690 (Fair),      existing EMI 12,000
-- customer3   : INACTIVE login used to demonstrate BR-14 (blocked sign-in)
INSERT INTO customers
  (customer_id, user_id, full_name, email, phone, dob, address, monthly_income,
   pan_number, aadhaar_last4, employment_type, account_number, ifsc_code,
   kyc_status, kyc_remarks, kyc_verified_by, kyc_verified_at,
   credit_score, existing_emi, status)
VALUES
  (1, 3, 'Priya Sharma', 'priya.sharma@example.com', '9876543210',
   '1992-05-14', '221B Baker Street, Mumbai', 90000.00,
   'ABCDE1234F', '1234', 'SALARIED',      '123456789012', 'HDFC0000123',
   'VERIFIED', NULL, 2, '2026-08-01 10:00:00',
   762, 0.00, 'ACTIVE'),

  (2, 4, 'Arjun Reddy', 'arjun.reddy@example.com', '9123456780',
   '1988-11-22', '17 MG Road, Hyderabad', 120000.00,
   'PQRSX5678Y', '5678', 'SELF_EMPLOYED', '987654321098', 'ICIC0000987',
   'VERIFIED', NULL, 2, '2026-08-10 15:30:00',
   690, 12000.00, 'ACTIVE'),

  (3, 5, 'Ravi Kumar', 'ravi.kumar@example.com', '9988776655',
   '1995-03-30', '9 Anna Salai, Chennai', 55000.00,
   NULL, NULL, NULL, NULL, NULL,
   'PENDING', NULL, NULL, NULL,
   NULL, 0.00, 'INACTIVE');

-- 3. Loan types ------------------------------------------------
-- Four active products + one INACTIVE product for BR-1 demo
INSERT INTO loan_types
  (loan_type_id, name, description, interest_rate, min_amount, max_amount, max_tenure_months, status)
VALUES
  (1, 'Personal Loan',   'Unsecured multi-purpose loan',            12.50,   50000.00,  1000000.00,  60,  'ACTIVE'),
  (2, 'Home Loan',       'Secured home purchase or construction',    8.50,  500000.00, 10000000.00,  360, 'ACTIVE'),
  (3, 'Car Loan',        'Secured vehicle loan',                     9.50,  100000.00,  2500000.00,  84,  'ACTIVE'),
  (4, 'Education Loan',  'Higher education funding',                10.00,   50000.00,  2000000.00,  120, 'ACTIVE'),
  (5, 'Legacy Personal', 'Discontinued product (BR-1 demo)',        15.00,   10000.00,   500000.00,  36,  'INACTIVE');

-- 4. Loan applications ----------------------------------------
-- Application 1: APPROVED  -> becomes Priya's ACTIVE loan
-- Application 2: APPROVED  -> becomes Arjun's CLOSED loan
-- Application 3: REJECTED  -> Arjun's second attempt (affordability)
-- Application 4: PENDING   -> Priya's new education-loan request
INSERT INTO loan_applications
  (application_id, customer_id, loan_type_id, requested_amount, tenure_months,
   purpose, status, remarks, reviewed_by, applied_at, reviewed_at)
VALUES
  (1, 1, 1, 240000.00, 24, 'Wedding expenses',
   'APPROVED', 'Good credit; income sufficient.',
   2, '2026-08-05 09:00:00', '2026-08-06 11:00:00'),

  (2, 2, 1, 100000.00, 12, 'Home renovation',
   'APPROVED', 'Approved with standard terms.',
   2, '2026-08-12 09:00:00', '2026-08-13 11:00:00'),

  (3, 2, 3, 500000.00, 60, 'New car purchase',
   'REJECTED', 'Affordability ratio too high with existing EMI.',
   2, '2026-09-01 14:00:00', '2026-09-02 10:00:00'),

  (4, 1, 4, 300000.00, 48, 'Postgraduate education',
   'PENDING', NULL,
   NULL, '2026-09-18 10:00:00', NULL);

-- 5. Loans -----------------------------------------------------
-- Interest = principal * rate * (tenure_months / 12) / 100  (flat simple interest)
--
-- Loan 1 (Priya, ACTIVE):
--   240,000 * 12.5 * (24/12) / 100 = 60,000 interest
--   total_payable = 300,000; EMI ~ 12,500
--   Two EMIs paid so far => outstanding = 275,000
--
-- Loan 2 (Arjun, CLOSED):
--   100,000 * 12.5 * (12/12) / 100 = 12,500 interest
--   total_payable = 112,500; two lump sums of 56,250 cleared it
INSERT INTO loans
  (loan_id, application_id, customer_id, loan_type_id,
   principal_amount, interest_rate, tenure_months,
   total_payable, outstanding_amount, start_date, status, created_by)
VALUES
  (1, 1, 1, 1, 240000.00, 12.50, 24, 300000.00, 275000.00, '2026-08-07', 'ACTIVE', 2),
  (2, 2, 2, 1, 100000.00, 12.50, 12, 112500.00,      0.00, '2026-08-14', 'CLOSED', 2);

-- 6. Repayments ------------------------------------------------
INSERT INTO repayments
  (loan_id, amount, payment_date, payment_mode, reference_no, remarks, recorded_by)
VALUES
  -- Priya's active loan: two EMIs recorded
  (1, 12500.00, '2026-09-07', 'UPI',           'UPI2609071',   'EMI 1', 2),
  (1, 12500.00, '2026-09-15', 'BANK_TRANSFER', 'NEFT09150001', 'EMI 2', 2),
  -- Arjun's closed loan: two payments cleared the outstanding balance
  (2, 56250.00, '2026-08-25', 'UPI',           'UPI2508251',   'Half payment',  2),
  (2, 56250.00, '2026-09-10', 'BANK_TRANSFER', 'NEFT09100003', 'Final payment', 2);
