package com.mycompany.oopfinal;

import java.io.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataStore {

    private String currentUser = null;

    public ObservableList<Goal.GoalItem> goals = FXCollections.observableArrayList();
    public ObservableList<Debt.DebtItem> debts = FXCollections.observableArrayList();
    public ObservableList<Transaction.TransactionItem> transactions = FXCollections.observableArrayList();
    public ObservableList<Budget.BudgetCategory> budgetCategories = FXCollections.observableArrayList();

    public ObservableList<User> users = FXCollections.observableArrayList();

    private double fixedIncome = 0;
    private double fixedBillings = 0;
    private double fixedInsurance = 0;
    private double fixedOther = 0;

    private static final String USER_FILE = "users.txt";

    public DataStore() {
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    public ObservableList<Goal.GoalItem> getGoals() {
        return goals;
    }

    public void setGoals(ObservableList<Goal.GoalItem> goals) {
        this.goals = goals;
    }

    public ObservableList<Debt.DebtItem> getDebts() {
        return debts;
    }

    public void setDebts(ObservableList<Debt.DebtItem> debts) {
        this.debts = debts;
    }

    public ObservableList<Transaction.TransactionItem> getTransactions() {
        return transactions;
    }

    public void setTransactions(ObservableList<Transaction.TransactionItem> transactions) {
        this.transactions = transactions;
    }

    public ObservableList<Budget.BudgetCategory> getBudgetCategories() {
        return budgetCategories;
    }

    public void setBudgetCategories(ObservableList<Budget.BudgetCategory> budgetCategories) {
        this.budgetCategories = budgetCategories;
    }

    public ObservableList<User> getUsers() {
        return users;
    }

    public void setUsers(ObservableList<User> users) {
        this.users = users;
    }

    public double getFixedIncome() {
        return fixedIncome;
    }

    public void setFixedIncome(double fixedIncome) {
        this.fixedIncome = fixedIncome;
    }

    public double getFixedBillings() {
        return fixedBillings;
    }

    public void setFixedBillings(double fixedBillings) {
        this.fixedBillings = fixedBillings;
    }

    public double getFixedInsurance() {
        return fixedInsurance;
    }

    public void setFixedInsurance(double fixedInsurance) {
        this.fixedInsurance = fixedInsurance;
    }

    public double getFixedOther() {
        return fixedOther;
    }

    public void setFixedOther(double fixedOther) {
        this.fixedOther = fixedOther;
    }

    public boolean registerUser(String user, String pass, String email) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(user))
                return false;
        }

        users.add(new User(user, pass, email));
        saveUserRegistry();

        goals.clear();
        debts.clear();
        transactions.clear();
        budgetCategories.clear();
        fixedIncome = 0;
        fixedBillings = 0;
        fixedInsurance = 0;
        fixedOther = 0;

        saveUserDataFor(user);

        loadUserData(user);

        return true;
    }

    public boolean validateLogin(String user, String pass) {
        loadUserRegistry();
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(user) && u.getPassword().equals(pass)) {
                loadUserData(user);
                return true;
            }
        }
        return false;
    }

    public void loadUserRegistry() {
        this.users.clear();
        File file = new File(USER_FILE);
        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length >= 4 && parts[0].equals("USER")) {
                    this.users.add(new User(parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUserRegistry() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE))) {
            for (User u : users) {
                writer.write(String.format("USER;%s;%s;%s", u.getUsername(), u.getPassword(), u.getEmail()));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadUserData(String username) {
        this.currentUser = username;
        String filename = "data_" + username + ".txt";
        File file = new File(filename);

        goals.clear();
        debts.clear();
        transactions.clear();
        budgetCategories.clear();
        fixedIncome = 0;
        fixedBillings = 0;
        fixedInsurance = 0;
        fixedOther = 0;

        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length < 2)
                    continue;

                switch (parts[0]) {
                    case "FIXED":
                        if (parts.length >= 5) {
                            fixedIncome = Double.parseDouble(parts[1]);
                            fixedBillings = Double.parseDouble(parts[2]);
                            fixedInsurance = Double.parseDouble(parts[3]);
                            fixedOther = Double.parseDouble(parts[4]);
                        }
                        break;
                    case "GOAL":
                        if (parts.length >= 4)
                            goals.add(new Goal.GoalItem(parts[1], Double.parseDouble(parts[2]),
                                    Double.parseDouble(parts[3])));
                        break;
                    case "DEBT":
                        if (parts.length >= 4)
                            debts.add(new Debt.DebtItem(parts[1], Double.parseDouble(parts[2]), parts[3]));
                        break;
                    case "TRANS":
                        if (parts.length >= 5)
                            transactions.add(new Transaction.TransactionItem(parts[1], parts[2],
                                    Double.parseDouble(parts[3]), parts[4]));
                        break;
                    case "CAT":
                        if (parts.length >= 3)
                            budgetCategories.add(new Budget.BudgetCategory(parts[1], Double.parseDouble(parts[2])));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        if (this.currentUser != null) {
            this.saveUserDataFor(this.currentUser);
        }
    }

    private void saveUserDataFor(String username) {
        String filename = "data_" + username + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(
                    String.format("FIXED;%.2f;%.2f;%.2f;%.2f", fixedIncome, fixedBillings, fixedInsurance, fixedOther));
            writer.newLine();

            for (Goal.GoalItem g : goals) {
                writer.write(String.format("GOAL;%s;%.2f;%.2f", g.getName(), g.getTarget(), g.getCurrent()));
                writer.newLine();
            }
            for (Debt.DebtItem d : debts) {
                writer.write(String.format("DEBT;%s;%.2f;%s", d.getName(), d.getAmount(), d.getDue()));
                writer.newLine();
            }
            for (Transaction.TransactionItem t : transactions) {
                writer.write(
                        String.format("TRANS;%s;%s;%.2f;%s", t.getDate(), t.getDesc(), t.getAmount(), t.getCategory()));
                writer.newLine();
            }
            for (Budget.BudgetCategory b : budgetCategories) {
                writer.write(String.format("CAT;%s;%.2f", b.getName(), b.getLimit()));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getSpentByCategory(String categoryName) {
        double total = 0;
        for (Transaction.TransactionItem t : transactions) {
            if (t.getCategory().equalsIgnoreCase(categoryName))
                total += t.getAmount();
        }
        return total;
    }

    public abstract static class BaseModel implements Serializable {
        private static final long serialVersionUID = 1L;
        private long id;
        private java.util.Date createdDate;

        public BaseModel() {
            this.id = System.currentTimeMillis();
            this.createdDate = new java.util.Date();
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public java.util.Date getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(java.util.Date createdDate) {
            this.createdDate = createdDate;
        }

        public abstract boolean validate();
    }

    public static class User extends BaseModel {
        private String username, password, email;

        public User(String u, String p, String e) {
            super();
            username = u;
            password = p;
            email = e;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public boolean validate() {
            return username != null && !username.isEmpty()
                    && password != null && !password.isEmpty()
                    && email != null && !email.isEmpty();
        }

        @Override
        public String toString() {
            return username + " (" + email + ")";
        }
    }

    public interface Refreshable {
        void refresh();
    }
}
