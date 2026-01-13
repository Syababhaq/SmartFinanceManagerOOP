package com.mycompany.oopfinal;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class View_Dashboard extends BorderPane {

    // --- STATE VARIABLES ---
    private LocalDate currentWeekStart;
    private BarChart<String, Number> barChart;
    private Label lblWeekRange;

    public View_Dashboard() {
        setPadding(new Insets(20));
        setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // === TITLE ===
        Label title = new Label("Financial Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + MainLayout.COL_DARK + ";");
        setTop(title);

        // === CENTER AREA ===
        VBox centerArea = new VBox(20);
        centerArea.setPadding(new Insets(20));
        
        // --- ROW 1: CHARTS (Pie + Bar) ---
        HBox chartsRow = new HBox(20);
        chartsRow.setAlignment(Pos.CENTER);
        
        VBox pieSection = createPieChartSection();
        VBox barSection = createBarChartSection(); 
        
        // Layout: 50% / 50%
        HBox.setHgrow(pieSection, Priority.ALWAYS);
        HBox.setHgrow(barSection, Priority.ALWAYS);
        pieSection.setMaxWidth(Double.MAX_VALUE);
        barSection.setMaxWidth(Double.MAX_VALUE);
        pieSection.setMaxHeight(Double.MAX_VALUE);
        barSection.setMaxHeight(Double.MAX_VALUE);
        
        chartsRow.getChildren().addAll(pieSection, barSection);

        // --- ROW 2: TRANSACTIONS + SUMMARY (NEW LAYOUT) ---
        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(Pos.CENTER);

        // A. Transactions (Left)
        VBox recentSection = createRecentTransactionsSection();
        HBox.setHgrow(recentSection, Priority.ALWAYS); // Give table more priority space
        recentSection.setMaxWidth(Double.MAX_VALUE);
        recentSection.setMaxHeight(Double.MAX_VALUE);

        // B. Financial Summary (Right) - NEW
        VBox summarySection = createFinancialSummarySection();
        summarySection.setPrefWidth(300); // Fixed width for summary card
        summarySection.setMinWidth(280);
        summarySection.setMaxHeight(Double.MAX_VALUE);

        bottomRow.getChildren().addAll(recentSection, summarySection);

        // Grow settings for Rows
        VBox.setVgrow(chartsRow, Priority.ALWAYS);
        VBox.setVgrow(bottomRow, Priority.ALWAYS);

        centerArea.getChildren().addAll(chartsRow, bottomRow);

        // === RIGHT SIDEBAR ===
        VBox rightPane = createRightSummarySection();
        rightPane.setPrefWidth(320);
        rightPane.setMinWidth(300);

        setCenter(centerArea);
        setRight(rightPane);
    }

    // --------------------------------------------------------------------------------
    // SECTION 1: PIE CHART
    // --------------------------------------------------------------------------------
    private VBox createPieChartSection() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        container.setPadding(new Insets(15));

        Map<String, Double> categoryTotals = new HashMap<>();
        double grandTotal = 0.0;

        for (View_Transactions.Transaction t : DataStore.getInstance().transactions) {
            categoryTotals.put(t.category, categoryTotals.getOrDefault(t.category, 0.0) + t.amount);
            grandTotal += t.amount;
        }

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            double amount = entry.getValue();
            double percentage = (grandTotal > 0) ? (amount / grandTotal) * 100 : 0;
            String label = String.format("%s: %.0f%%", entry.getKey(), percentage);
            pieData.add(new PieChart.Data(label, amount));
        }

        PieChart pieChart = new PieChart(pieData);
        pieChart.setTitle("Spending Composition");
        pieChart.setLabelsVisible(false);
        pieChart.setLegendVisible(true);
        pieChart.setLegendSide(Side.BOTTOM);
        pieChart.setMinHeight(200);
        VBox.setVgrow(pieChart, Priority.ALWAYS);

        Label lblTotal = new Label(String.format("Total: RM %.2f", grandTotal));
        lblTotal.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        container.getChildren().addAll(pieChart, lblTotal);
        return container;
    }

    // --------------------------------------------------------------------------------
    // SECTION 2: BAR CHART
    // --------------------------------------------------------------------------------
    private VBox createBarChartSection() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        container.setPadding(new Insets(15));

        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER);
        Button btnPrev = new Button("<");
        Button btnNext = new Button(">");
        btnPrev.setStyle("-fx-background-radius: 20; -fx-min-width: 30px;");
        btnNext.setStyle("-fx-background-radius: 20; -fx-min-width: 30px;");
        lblWeekRange = new Label();
        lblWeekRange.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        header.getChildren().addAll(btnPrev, lblWeekRange, btnNext);

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount (RM)");
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Weekly Trend");
        barChart.setLegendVisible(false);
        barChart.setAnimated(false);
        barChart.setMinHeight(200);
        VBox.setVgrow(barChart, Priority.ALWAYS);

        btnPrev.setOnAction(e -> { currentWeekStart = currentWeekStart.minusWeeks(1); updateBarChart(); });
        btnNext.setOnAction(e -> { currentWeekStart = currentWeekStart.plusWeeks(1); updateBarChart(); });
        updateBarChart();

        container.getChildren().addAll(header, barChart);
        return container;
    }

    private void updateBarChart() {
        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        LocalDate weekEnd = currentWeekStart.plusDays(6);
        DateTimeFormatter rangeFmt = DateTimeFormatter.ofPattern("dd MMM");
        lblWeekRange.setText(currentWeekStart.format(rangeFmt) + " - " + weekEnd.format(rangeFmt));

        Map<LocalDate, Double> weekData = new TreeMap<>();
        for (int i = 0; i < 7; i++) weekData.put(currentWeekStart.plusDays(i), 0.0);
        for (View_Transactions.Transaction t : DataStore.getInstance().transactions) {
            try {
                LocalDate tDate = LocalDate.parse(t.date);
                if (!tDate.isBefore(currentWeekStart) && !tDate.isAfter(weekEnd)) {
                    weekData.put(tDate, weekData.get(tDate) + t.amount);
                }
            } catch (Exception e) {}
        }
        DateTimeFormatter tickFmt = DateTimeFormatter.ofPattern("dd\nEEE");
        for (Map.Entry<LocalDate, Double> entry : weekData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().format(tickFmt), entry.getValue()));
        }
        barChart.getData().add(series);
    }

    // --------------------------------------------------------------------------------
    // SECTION 3: RECENT TRANSACTIONS
    // --------------------------------------------------------------------------------
    private VBox createRecentTransactionsSection() {
        VBox container = new VBox(10);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        container.setPadding(new Insets(15));

        Label title = new Label("Recent Transactions");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TableView<View_Transactions.Transaction> table = new TableView<>();
        ObservableList<View_Transactions.Transaction> allData = DataStore.getInstance().transactions;
        ObservableList<View_Transactions.Transaction> recentData = FXCollections.observableArrayList();
        int start = Math.max(0, allData.size() - 5);
        for(int i = allData.size() - 1; i >= start; i--) recentData.add(allData.get(i));

        table.setItems(recentData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<View_Transactions.Transaction, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<View_Transactions.Transaction, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        TableColumn<View_Transactions.Transaction, Double> colAmt = new TableColumn<>("Amount");
        colAmt.setCellValueFactory(new PropertyValueFactory<>("amount"));
        table.getColumns().addAll(colDate, colDesc, colAmt);

        container.getChildren().addAll(title, table);
        return container;
    }

    // --------------------------------------------------------------------------------
    // SECTION 4: FINANCIAL SUMMARY (NEW)
    // --------------------------------------------------------------------------------
    private VBox createFinancialSummarySection() {
        VBox container = new VBox(15);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Total Summary");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        DataStore ds = DataStore.getInstance();
        
        // 1. Calculate Totals
        double income = ds.fixedIncome;
        double fixedExpenses = ds.fixedBillings + ds.fixedInsurance + ds.fixedOther;
        double variableExpenses = 0;
        for(View_Transactions.Transaction t : ds.transactions) {
            variableExpenses += t.amount;
        }
        
        double totalExpenses = fixedExpenses + variableExpenses;
        double netBalance = income - totalExpenses;

        // 2. Create UI Rows
        VBox rows = new VBox(10);
        rows.getChildren().add(createSummaryRow("Monthly Income", income, "#27ae60")); // Green
        rows.getChildren().add(new Separator());
        rows.getChildren().add(createSummaryRow("Fixed Budget", fixedExpenses, "#7f8c8d"));
        rows.getChildren().add(createSummaryRow("Transactions", variableExpenses, "#7f8c8d"));
        rows.getChildren().add(createSummaryRow("Total Spending", totalExpenses, "#c0392b")); // Red
        rows.getChildren().add(new Separator());
        
        // Net Balance Row (Large)
        Label lblNetTitle = new Label("Net Savings");
        lblNetTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        Label lblNetVal = new Label(String.format("RM %.2f", netBalance));
        if (netBalance >= 0) {
            lblNetVal.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #27ae60;"); // Green
        } else {
            lblNetVal.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #e74c3c;"); // Red
        }
        
        VBox netBox = new VBox(5, lblNetTitle, lblNetVal);
        netBox.setAlignment(Pos.CENTER);
        netBox.setPadding(new Insets(10, 0, 0, 0));

        container.getChildren().addAll(title, rows, netBox);
        return container;
    }

    private HBox createSummaryRow(String label, double value, String colorHex) {
        Label lbl = new Label(label);
        lbl.setStyle("-fx-text-fill: #34495e;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label val = new Label(String.format("RM %.2f", value));
        val.setStyle("-fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        
        HBox row = new HBox(lbl, spacer, val);
        return row;
    }

    // --------------------------------------------------------------------------------
    // SECTION 5: RIGHT SIDEBAR
    // --------------------------------------------------------------------------------
    private VBox createRightSummarySection() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20, 0, 0, 10));

        VBox budgetBox = createScrollableBox("Budget Limits", createBudgetList());
        VBox.setVgrow(budgetBox, Priority.ALWAYS);
        VBox goalBox = createScrollableBox("Active Goals", createGoalList());
        VBox.setVgrow(goalBox, Priority.ALWAYS);
        VBox debtBox = createScrollableBox("Outstanding Debts", createDebtList());
        VBox.setVgrow(debtBox, Priority.ALWAYS);

        container.getChildren().addAll(budgetBox, goalBox, debtBox);
        return container;
    }

    private VBox createScrollableBox(String titleStr, VBox contentList) {
        VBox box = new VBox(10);
        box.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        box.setMaxHeight(Double.MAX_VALUE); 

        Label title = new Label(titleStr);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");

        ScrollPane scroll = new ScrollPane(contentList);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        box.getChildren().addAll(title, new Separator(), scroll);
        return box;
    }

    private VBox createBudgetList() {
        VBox list = new VBox(10);
        if (DataStore.getInstance().budgetCategories.isEmpty()) {
            Label placeholder = new Label("No budgets set.");
            placeholder.setStyle("-fx-text-fill: #7f8c8d;");
            list.getChildren().add(placeholder); return list;
        }
        for (View_Budget.BudgetCategory b : DataStore.getInstance().budgetCategories) {
            double spent = DataStore.getInstance().getSpentByCategory(b.name);
            double progress = (b.limit > 0) ? spent / b.limit : 0;
            Label name = new Label(b.name);
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50;");
            ProgressBar pb = new ProgressBar(progress);
            pb.setMaxWidth(Double.MAX_VALUE);
            if (spent > b.limit) pb.setStyle("-fx-accent: #e74c3c;"); else pb.setStyle("-fx-accent: #27ae60;");
            Label stats = new Label(String.format("RM %.2f / RM %.2f", spent, b.limit));
            stats.setStyle("-fx-font-size: 10px; -fx-text-fill: grey;");
            VBox item = new VBox(2, name, pb, stats);
            list.getChildren().add(item);
        }
        return list;
    }

    private VBox createGoalList() {
        VBox list = new VBox(10);
        if (DataStore.getInstance().goals.isEmpty()) {
            Label placeholder = new Label("No active goals.");
            placeholder.setStyle("-fx-text-fill: #7f8c8d;");
            list.getChildren().add(placeholder); return list;
        }
        for (View_Goals.GoalItem g : DataStore.getInstance().goals) {
            double progress = (g.target > 0) ? g.current / g.target : 0;
            Label name = new Label(g.name);
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50;");
            ProgressBar pb = new ProgressBar(progress);
            pb.setMaxWidth(Double.MAX_VALUE);
            pb.setStyle("-fx-accent: #3498db;");
            Label stats = new Label(String.format("RM %.2f / RM %.2f", g.current, g.target));
            stats.setStyle("-fx-font-size: 10px; -fx-text-fill: grey;");
            VBox item = new VBox(2, name, pb, stats);
            list.getChildren().add(item);
        }
        return list;
    }

    private VBox createDebtList() {
        VBox list = new VBox(10);
        if (DataStore.getInstance().debts.isEmpty()) {
            Label placeholder = new Label("Debt free!");
            placeholder.setStyle("-fx-text-fill: #7f8c8d;");
            list.getChildren().add(placeholder); return list;
        }
        for (View_Debt.DebtItem d : DataStore.getInstance().debts) {
            HBox item = new HBox();
            Label name = new Label(d.name);
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50;");
            Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
            Label amount = new Label(String.format("RM %.2f", d.amount));
            amount.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
            item.getChildren().addAll(name, spacer, amount);
            list.getChildren().add(item);
        }
        return list;
    }
}
