# Smart Finance Manager 💰

A comprehensive, desktop-based personal finance application built with **JavaFX** and **Maven**. This application helps users track their income, expenses, debts, and financial goals locally with a modern, responsive user interface.

## 📋 Table of Contents
- [Features](#-features)
- [Project Structure](#-project-structure)
- [Prerequisites](#-prerequisites)
- [Installation & Running](#-installation--running)
- [Data Storage](#-data-storage)
- [Screenshots](#-screenshots)

---

## ✨ Features

### 🔐 Authentication
* **User Login & Registration:** Secure access to personal financial data.
* **Multi-User Support:** Data is stored specifically for registered users.

### 📊 Dashboard
* **Visual Analytics:** Interactive **Pie Chart** for spending composition and **Bar Chart** for weekly spending trends.
* **Weekly Navigation:** Toggle between weeks to view past spending history.
* **At-a-Glance Summaries:** Scrollable widgets for Active Goals, Budget Limits, and Outstanding Debts.
* **Recent Activity:** Quick view of the last 5 transactions.

### 💸 Transaction Management
* **Log Expenses:** Add transactions with Date, Description, Amount, and Category.
* **History:** View a complete table of all historical transactions.
* **Edit/Delete:** Right-click any transaction row to modify or remove it.

### 🎯 Goal Tracker
* **Progress Tracking:** Visual progress bars showing how close you are to your targets.
* **Fund Management:** Right-click to **Deposit** (Cash In) or **Withdraw** (Cash Out) from specific goals.
* **CRUD Operations:** Create, Read, Update, and Delete goals easily.

### 📉 Debt Manager
* **Loan Tracking:** Keep track of money owed to others (e.g., PTPTN, Credit Cards).
* **Payment System:** Right-click to **Pay Debt** (reduces amount) or **Borrow More** (increases amount).
* **Visual Indicators:** Red highlighted amounts for outstanding balances.

### 🛑 Budget Planning
* **Fixed Income:** Define monthly income and bills for auto allocation (e.g., Income, Insurance, Bills).
* **Set Limits:** Define monthly spending limits for specific categories (e.g., Food, Transport).
* **Dynamic Monitoring:** Progress bars change color from **Green** (Safe) to **Red** (Over Budget) automatically based on transaction data.

---

## 📂 Project Structure

The project follows a modular Object-Oriented design using the **Single-File Component** pattern for JavaFX.

```text
src/main/java/com/mycompany/oopfinal/
├── App.java                 # Application Entry Point (Launch)
├── DataStore.java           # Singleton Database (Handles Save/Load & Logic)
├── MainLayout.java          # Main UI Shell (Sidebar, Navigation, Layout)
├── View_Login.java          # Login Screen
├── View_Register.java       # Registration Screen
├── View_Dashboard.java      # Analytics & Overview Screen
├── View_Transactions.java   # Transaction History & Entry
├── View_Goals.java          # Financial Goal Tracker
├── View_Debt.java           # Debt Reduction Manager
├── View_FixedBudget.java    # Monthly Budget Settings
└── View_Budget.java         # Budget Limit Settings
```
## 🛠 Prerequisites
Before running this project, ensure you have the following installed:

* **Java Development Kit (JDK):** Version 17 or higher.
* **Apache Maven:** For dependency management.
* **IDE:** Apache NetBeans (Recommended), IntelliJ IDEA, or Eclipse.

## 🚀 Installation & Running
1.  **Clone or Download** this repository.
2.  **Open the project** in your IDE (e.g., NetBeans).
    * *File > Open Project > Select Folder*.
3.  **Clean and Build** the project to download Maven dependencies.
    * *Right-click Project > Clean and Build*.
4.  **Run the Application**:
    * Navigate to `src/main/java/com/mycompany/oopfinal/App.java`.
    * Right-click `App.java` > **Run File**.

## 💾 Data Storage
This application uses a lightweight, text-based persistence system.

* **File Name:** `finance_data.txt`
* **Location:** Project Root Directory.
* **Format:**
    * The file uses a semi-colon (`;`) separated format.
    * It is human-readable and editable.

**Example Data:**
```text
USER;admin;123;admin@gmail.com
CAT;Food;500.00
GOAL;New Laptop;4000.00;1200.00
DEBT;PTPTN;15000.00;2026-12-31
TRANS;2024-01-15;Nasi Lemak;12.50;Food
```

## 🖼️ Screenshots
1. Dashboard
(Shows charts, summaries, and weekly trends)

2. Transaction Log
(Table view with add/edit functionality)

3. Goal Tracker
(Progress bars for financial targets)
