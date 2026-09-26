# Loan Management System

A console-based loan management application built with Java and MySQL. It handles
users, loan products, customers, loan applications and loans, with role-based
access for an administrator, a loan officer and a customer.

Built as a training project (Revature Project Zero).

## Tech stack

- Java 21
- Maven
- MySQL 8
- Plain JDBC (no ORM)
- SLF4J + Logback for logging
- Console interface (no web frontend)

Dependencies are limited to `mysql-connector-j`, `slf4j-api` and `logback-classic`.

## Project structure

```
src/main/java/com/loanmanagement/
├── controller/   console menus and user input
├── service/      business rules and role checks
├── service/impl/
├── dao/          database access interfaces
├── dao/impl/     SQL statements and JDBC code
├── model/        data classes and enums
├── exception/    ValidationException, BusinessException,
│                 NotFoundException, DataAccessException
└── util/         DBConnection, Session, ConsoleUtil

src/main/resources/
├── schema.sql    table definitions
├── seed.sql      demo data
├── db.properties database credentials (not in version control)
└── logback.xml   logging configuration
```

The layers run in one direction: a controller calls a service, a service calls a
DAO, and only a DAO writes SQL. A service never opens a connection and a DAO
never enforces a business rule.

## Setup

### 1. Create the database

In MySQL Workbench or the `mysql` client:

```sql
CREATE DATABASE lms_db;
```

### 2. Run the scripts

Run `src/main/resources/schema.sql` first, then `src/main/resources/seed.sql`.
Run each file in full — `seed.sql` clears the tables before inserting, so it can
be re-run at any time to reset the demo data.

`schema.sql` is only for a database that has no tables yet.

### 3. Create `db.properties`

This file is deliberately not in version control because it holds a password.
Create `src/main/resources/db.properties` with:

```properties
db.url=jdbc:mysql://localhost:3306/lms_db
db.user=root
db.password=your_password_here
```

### 4. Build and run

```bash
mvn clean install
```

Then run `com.loanmanagement.controller.AppController` from your IDE, or:

```bash
mvn exec:java -Dexec.mainClass=com.loanmanagement.controller.AppController
```

## Logins

| Username | Password | Role | Note |
|---|---|---|---|
| admin | Admin@123 | ADMIN | |
| officer1 | Officer@123 | LOAN_OFFICER | |
| customer1 | Cust@1234 | CUSTOMER | Priya Sharma — has an application and a loan |
| customer2 | Cust@1234 | CUSTOMER | Arjun Reddy |
| customer3 | Cust@1234 | CUSTOMER | Inactive — login is refused |
| customer4 | Cust@1234 | CUSTOMER | Meera Iyer |

## What each role can do

**Admin** — manage users, loan types, customers, applications and loans.

**Loan officer** — manage customers, applications and loans. Reviews applications
and converts approved ones into loans.

**Customer** — view loan types, apply for a loan, and view their own
applications, loans and profile. A customer can only see their own records; the
customer id comes from the session, never from console input.

## Creating a customer

A customer needs two records, created in this order:

1. **Admin → Manage users → Add user** with role CUSTOMER. This creates the login.
2. **Manage customers → Add customer**, using the user id from step 1. This
   creates the profile.

They are separate tables joined by `customers.user_id`, so the login must exist
first.
