-- =====================================
-- FINANCIAL MANAGEMENT SUBSYSTEM DATABASE
-- ERP Mini Project
-- =====================================

CREATE DATABASE IF NOT EXISTS financial_management;

USE financial_management;


-- =====================================
-- USERS (Login + Role Based Access)
-- =====================================

CREATE TABLE IF NOT EXISTS users(

    user_id VARCHAR(30) PRIMARY KEY,

    username VARCHAR(50) UNIQUE NOT NULL,

    password VARCHAR(100) NOT NULL,

    role VARCHAR(20) NOT NULL

);


-- =====================================
-- ACCOUNTS RECEIVABLE (Revenue)
-- =====================================

CREATE TABLE IF NOT EXISTS accounts_receivable(

    invoice_no VARCHAR(30) PRIMARY KEY,

    customer_name VARCHAR(100) NOT NULL,

    invoice_number VARCHAR(50) UNIQUE NOT NULL,

    issue_date DATE NOT NULL,

    amount DECIMAL(15,2) NOT NULL,

    payment_status VARCHAR(20) DEFAULT 'Pending',

    revenue_status VARCHAR(20) DEFAULT 'Pending'

);


-- =====================================
-- ACCOUNTS PAYABLE (Expenses)
-- =====================================

CREATE TABLE IF NOT EXISTS accounts_payable(

    invoice_id VARCHAR(30) PRIMARY KEY,

    vendor_name VARCHAR(100) NOT NULL,

    invoice_number VARCHAR(50) UNIQUE NOT NULL,

    invoice_date DATE NOT NULL,

    due_date DATE NOT NULL,

    amount DECIMAL(15,2) NOT NULL,

    tax_percent DECIMAL(5,2) DEFAULT 0,

    net_amount DECIMAL(15,2) NOT NULL,

    status VARCHAR(20) DEFAULT 'Pending'

);


-- =====================================
-- GENERAL LEDGER
-- =====================================

CREATE TABLE IF NOT EXISTS ledger_entries(

    entry_id VARCHAR(40) PRIMARY KEY,

    entry_date DATE NOT NULL,

    account_name VARCHAR(100) NOT NULL,

    debit DECIMAL(15,2) DEFAULT 0,

    credit DECIMAL(15,2) DEFAULT 0,

    balance DECIMAL(15,2) DEFAULT 0,

    description VARCHAR(255)

);


-- =====================================
-- ASSET MANAGEMENT
-- =====================================

CREATE TABLE IF NOT EXISTS assets(

    asset_id VARCHAR(30) PRIMARY KEY,

    asset_name VARCHAR(100) NOT NULL,

    category VARCHAR(50) NOT NULL,

    purchase_date DATE NOT NULL,

    purchase_value DECIMAL(15,2) NOT NULL,

    annual_depreciation DECIMAL(15,2) DEFAULT 0,

    current_value DECIMAL(15,2) NOT NULL,

    depreciation_method VARCHAR(30) DEFAULT 'Straight Line'

);


-- =====================================
-- CASH MANAGEMENT
-- =====================================

CREATE TABLE IF NOT EXISTS cash_entries(

    entry_id VARCHAR(50) PRIMARY KEY,

    type VARCHAR(20) CHECK(type IN ('Inflow','Outflow')),

    amount DECIMAL(15,2) NOT NULL,

    balance DECIMAL(15,2) NOT NULL

);


-- =====================================
-- BUDGETING
-- =====================================

CREATE TABLE IF NOT EXISTS budgets(

    budget_id VARCHAR(30) PRIMARY KEY,

    department VARCHAR(60) NOT NULL,

    fiscal_year VARCHAR(10) NOT NULL,

    budget_amount DECIMAL(15,2) NOT NULL,

    actual_amount DECIMAL(15,2) DEFAULT 0,

    notes VARCHAR(255)

);


-- =====================================
-- FORECASTING
-- =====================================

CREATE TABLE IF NOT EXISTS forecasts(

    forecast_id VARCHAR(30) PRIMARY KEY,

    period VARCHAR(30) NOT NULL,

    revenue_growth DOUBLE DEFAULT 0,

    expense_growth DOUBLE DEFAULT 0,

    proj_revenue DECIMAL(15,2) DEFAULT 0,

    proj_expenses DECIMAL(15,2) DEFAULT 0,

    proj_profit DECIMAL(15,2) DEFAULT 0

);


-- =====================================
-- TAX MANAGEMENT
-- =====================================

CREATE TABLE IF NOT EXISTS tax_records(

    tax_id VARCHAR(30) PRIMARY KEY,

    tax_type VARCHAR(30) NOT NULL,

    applicable_period VARCHAR(20) NOT NULL,

    base_amount DECIMAL(15,2) NOT NULL,

    tax_rate DECIMAL(5,2) NOT NULL,

    tax_amount DECIMAL(15,2) NOT NULL,

    description VARCHAR(255),

    filing_status VARCHAR(20) DEFAULT 'Pending'

);


-- =====================================
-- AUDIT LOGS
-- =====================================

CREATE TABLE IF NOT EXISTS audit_logs(

    log_id INT AUTO_INCREMENT PRIMARY KEY,

    user_id VARCHAR(30),

    action VARCHAR(100),

    module VARCHAR(100),

    details VARCHAR(255),

    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP

);


-- =====================================
-- SAMPLE USERS
-- =====================================

INSERT INTO users VALUES
('U1','admin','admin123','ADMIN'),
('U2','accountant','acc123','ACCOUNTANT'),
('U3','auditor','audit123','AUDITOR');

