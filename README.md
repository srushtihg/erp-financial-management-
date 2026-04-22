# ERP Financial Management Subsystem

## 📌 Overview

This project implements a **Financial Management Subsystem** of an ERP system designed for managing departmental budgets, tracking expenses, handling approvals, and generating financial reports.

The system follows a **modular, layered architecture** and uses a **metadata-driven approach** to support scalable and flexible financial data management.

---

## 🚀 Features

* 🔐 User authentication and session management
* 💰 Department-wise budget planning and allocation
* 🧾 Expense entry with approval/rejection workflow
* 📊 Real-time budget vs expenditure tracking
* 📈 Financial reporting and dashboard visualization
* 🛡️ Validation-driven data entry
* 🧾 Audit logging for all critical operations
* ⚙️ Metadata-driven CRUD operations for scalability

---

## 🏗️ Architecture

The system follows a **Layered Architecture**:

* **UI Layer** → Handles user interaction (Swing-based UI)
* **Service Layer** → Business logic and workflows
* **Domain Layer** → Core models and metadata (TableRegistry, TableDefinition)
* **Database Layer** → Database connection and schema management

---

## 🧠 Design Principles

* **SOLID Principles** (SRP, OCP, LSP, ISP, DIP)
* **GRASP Principles** (Controller, Information Expert, Low Coupling, High Cohesion)

---

## 🧱 Design Patterns Used

* **Layered Architecture Pattern**
* **Facade Pattern (AppContext)**
* **Factory Pattern (ConnectionFactory)**
* **Registry Pattern (TableRegistry)**
* **Strategy Pattern (ValidationProfile)**
* **Observer Pattern (UI Event Handling)**

---

## 🛠️ Tech Stack

* **Language:** Java
* **UI:** Java Swing
* **Database:** MySQL
* **Build Tools:** PowerShell scripts
* **Libraries:** MySQL Connector, SLF4J

---

## ⚙️ How to Run

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/YOUR_REPO.git
cd YOUR_REPO
```

### 2. Configure Database

* Copy `database.properties.template` → `database.properties`
* Update credentials:

```
db.url=jdbc:mysql://localhost:3306/your_db
db.user=your_username
db.password=your_password
```

### 3. Initialize Database

* Run the SQL file:

```
resources/financial_management_schema.sql
```

### 4. Run the Application

```bash
./run.ps1
```

---

## 📸 Screenshots

(Add screenshots of Login, Dashboard, Management Panel, Reports)

---

## 👥 Team Members

* Srushti HG – Validation, UI Integration
* Rahul – Metadata-driven CRUD, TableRegistry
* Harshal – Reporting and business logic
* Aman – Database setup and integration

---

## 🔗 GitHub Repository

(Add your repository link here)

---

## 📌 Notes

* Compiled files (`bin/`, `.class`) are excluded from version control
* The system is designed for **extensibility and maintainability** using metadata-driven design

---

## 📈 Future Enhancements

* Role-based access control improvements
* Advanced analytics and forecasting
* Web-based UI integration
* API-based service layer

---

## 📄 License

This project is developed for academic purposes as part of the OOAD course.
