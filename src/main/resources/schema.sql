

-- 10.0 Users table ----------------------------------
create table users (
user_id int auto_increment PRIMARY KEY,
username varchar(20) not null unique,
password_hash varchar(128) not null,
role enum('ADMIN','LOAN_OFFICER','CUSTOMER') not null,
status enum('ACTIVE' , 'INACTIVE') not null default 'ACTIVE',
created_at datetime not null default current_timestamp
);

-- 10.1 Customer table ----------------------------------
 -- Parents: users (own login, and the officer who verified KYC)

create table customers (
customer_id int auto_increment primary key,
user_id int not null unique,
full_name varchar(100) not null,
email varchar(120) not null unique,
phone VARCHAR(10) not null unique,
dob date,
address varchar(255),
monthly_income decimal(12,2) not null,
pan_number varchar(10) unique,
aadhaar_last4   CHAR(4),
employment_type ENUM('SALARIED','SELF_EMPLOYED'),
account_number varchar(18),
ifsc_code char(11),
kyc_status  ENUM('PENDING','VERIFIED','REJECTED') NOT NULL DEFAULT 'PENDING',
kyc_remarks varchar(300),
kyc_verified_by int null,
kyc_verified_at datetime null,
credit_score int null,
existing_emi decimal(12,2) not null default 0,
status enum('ACTIVE','INACTIVE') not null default 'ACTIVE',
constraint fk_cust_user foreign key (user_id) references users(user_id),
constraint fk_cust_kyc_user foreign key (kyc_verified_by) references users(user_id)

);

-- 10.3 loan type ---------------------------------------------
--  No parents.

create table loan_types (
loan_type_id int auto_increment primary key,
name varchar(60) not null unique,
description varchar(255),
interest_rate decimal(5,2) not null,
min_amount decimal(12,2) not null,
max_amount decimal(12,2) not null,
max_tenure_months int not null,
status enum('ACTIVE','INACTIVE') NOT NULL default 'ACTIVE',
constraint chk_amount check (min_amount <= max_amount)

);

-- 10.4 loan application --------------------------------------
--  Parents: customers, loan_types, users

create table loan_applications(
application_id int auto_increment primary key,
customer_id int not null,
loan_type_id int not null,
requested_amount decimal(12,2) not null,
tenure_months int not null,
purpose varchar(250),
status enum('PENDING','APPROVED','REJECTED') not null default 'PENDING',
remarks varchar(300),
reviewed_by int null,
applied_at datetime not null default current_timestamp,
reviewed_at datetime null,

constraint fk_app_cust foreign key(customer_id) references customers(customer_id),
constraint fk_app_type foreign key (loan_type_id) references loan_types(loan_type_id),
constraint fk_app_user foreign key(reviewed_by) references users(user_id)
);

-- 10.5 loans -------------------------------------------

create table loans (
loan_id int auto_increment primary key,
application_id int not null unique,
customer_id int not null,
loan_type_id int not null,
principal_amount decimal(12,2) not null,
interest_rate decimal(5,2) not null,
tenure_months int not null,
total_payable decimal(12,2) not null,
outstanding_amount decimal(12,2) not null,
start_date date not null,
status enum('ACTIVE','CLOSED') not null default 'ACTIVE',
created_by int not null,

constraint fk_loan_app foreign key(application_id) references loan_applications(application_id),
constraint fk_loan_cust foreign key(customer_id) references customers(customer_id),
constraint fk_loan_type foreign key(loan_type_id) references loan_types(loan_type_id),
constraint fk_loan_user foreign key (created_by) references users(user_id)
);

-- 10.6 Repayments -----------------------------------------

create table repayments (
repayment_id int auto_increment primary key,
loan_id int not null,
amount decimal(12,2) not null,
payment_date date not null,
payment_mode enum('CASH','UPI','CARD','BANK_TRANSFER','CHEQUE') not null,
reference_no varchar(50),
remarks varchar(255),
recorded_by int not null,
created_at datetime not null default current_timestamp,
constraint fk_rep_loan foreign key (loan_id) references loans(loan_id),
constraint fk_rep_user foreign key(recorded_by) references users(user_id),
constraint chk_rep_amount check (amount > 0)
);

-- Indexes (BRD section 17: Performance) --------------------------------------
CREATE INDEX idx_app_status  ON loan_applications(status);
CREATE INDEX idx_loan_status ON loans(status);
CREATE INDEX idx_cust_kyc    ON customers(kyc_status);
CREATE INDEX idx_rep_loan    ON repayments(loan_id);
