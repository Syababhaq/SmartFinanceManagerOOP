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

public class Dashboard extends BorderPane implements DataStore.Refreshable {

    private LocalDate currentWeekStart;
    private BarChart<String, Number> barChart;
    private Label lblWeekRange;
    private DataStore dataStore;

    public Dashboard(DataStore dataStore) {
        this.dataStore = dataStore;
        setPadding(new Insets(20));
        setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        currentWeekStart = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        Label title = new Label("Financial Dashboard");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + MainLayout.COL_DARK + ";");
        setTop(title);

        VBox centerArea = new VBox(20);
        centerArea.setPadding(new Insets(20));

        HBox chartsRow = new HBox(20);
        chartsRow.setAlignment(Pos.CENTER);

        VBox pieSection = createPieChartSection();
        VBox barSection = createBarChartSection();

        HBox.setHgrow(pieSection, Priority.ALWAYS);
        HBox.setHgrow(barSection, Priority.ALWAYS);
        pieSection.setMaxWidth(Double.MAX_VALUE);
        barSection.setMaxWidth(Double.MAX_VALUE);
        pieSection.setMaxHeight(Double.MAX_VALUE);
        barSection.setMaxHeight(Double.MAX_VALUE);

        chartsRow.getChildren().addAll(pieSection, barSection);

        HBox bottomRow = new HBox(20);
        bottomRow.setAlignment(Pos.CENTER);

        VBox recentSection = createRecentTransactionsSection();
        HBox.setHgrow(recentSection, Priority.ALWAYS);
        recentSection.setMaxWidth(Double.MAX_VALUE);
        recentSection.setMaxHeight(Double.MAX_VALUE);

        VBox summarySection = createFinancialSummarySection();
        summarySection.setPrefWidth(300);
        summarySection.setMinWidth(280);
        summarySection.setMaxHeight(Double.MAX_VALUE);

        bottomRow.getChildren().addAll(recentSection, summarySection);

        VBox.setVgrow(chartsRow, Priority.ALWAYS);
        VBox.setVgrow(bottomRow, Priority.ALWAYS);

        centerArea.getChildren().addAll(chartsRow, bottomRow);

        VBox rightPane = createRightSummarySection();
        rightPane.setPrefWidth(320);
        rightPane.setMinWidth(300);

        setCenter(centerArea);
        setRight(rightPane);
    }

    public LocalDate getCurrentWeekStart() {
        return currentWeekStart;
    }

    public void setCurrentWeekStart(LocalDate currentWeekStart) {
        this.currentWeekStart = currentWeekStart;
    }

    public BarChart<String, Number> getBarChart() {
        return barChart;
    }

    public void setBarChart(BarChart<String, Number> barChart) {
        this.barChart = barChart;
    }

    public Label getLblWeekRange() {
        return lblWeekRange;
    }

    public void setLblWeekRange(Label lblWeekRange) {
        this.lblWeekRange = lblWeekRange;
    }

    private VBox createPieChartSection() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        container.setPadding(new Insets(15));

        Map<String, Double> categoryTotals = new HashMap<>();
        double grandTotal = 0.0;

        for (Transaction.TransactionItem t : dataStore.transactions) {
            categoryTotals.put(t.getCategory(), categoryTotals.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            grandTotal += t.getAmount();
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

    private VBox createBarChartSection() {
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
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

        btnPrev.setOnAction(e -> {
            currentWeekStart = currentWeekStart.minusWeeks(1);
            updateBarChart();
        });
        btnNext.setOnAction(e -> {
            currentWeekStart = currentWeekStart.plusWeeks(1);
            updateBarChart();
        });
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
        for (int i = 0; i < 7; i++)
            weekData.put(currentWeekStart.plusDays(i), 0.0);
        for (Transaction.TransactionItem t : dataStore.transactions) {
            try {
                LocalDate tDate = LocalDate.parse(t.getDate());
                if (!tDate.isBefore(currentWeekStart) && !tDate.isAfter(weekEnd)) {
                    weekData.put(tDate, weekData.get(tDate) + t.getAmount());
                }
            } catch (Exception e) {
            }
        }
        DateTimeFormatter tickFmt = DateTimeFormatter.ofPattern("dd\nEEE");
        for (Map.Entry<LocalDate, Double> entry : weekData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey().format(tickFmt), entry.getValue()));
        }
        barChart.getData().add(series);
    }

    private VBox createRecentTransactionsSection() {
        VBox container = new VBox(10);
        container.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        container.setPadding(new Insets(15));

        Label title = new Label("Recent Transactions");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        TableView<Transaction.TransactionItem> table = new TableView<>();
        ObservableList<Transaction.TransactionItem> allData = dataStore.transactions;
        ObservableList<Transaction.TransactionItem> recentData = FXCollections.observableArrayList();
        int start = Math.max(0, allData.size() - 5);
        for (int i = allData.size() - 1; i >= start; i--)
            recentData.add(allData.get(i));

        table.setItems(recentData);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<Transaction.TransactionItem, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Transaction.TransactionItem, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        TableColumn<Transaction.TransactionItem, Double> colAmt = new TableColumn<>("Amount");
        colAmt.setCellValueFactory(new PropertyValueFactory<>("amount"));
        table.getColumns().addAll(colDate, colDesc, colAmt);

        container.getChildren().addAll(title, table);
        return container;
    }

    private VBox createFinancialSummarySection() {
        VBox container = new VBox(15);
        container.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Total Summary");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        DataStore ds = dataStore;

        double income = ds.getFixedIncome();
        double fixedExpenses = ds.getFixedBillings() + ds.getFixedInsurance() + ds.getFixedOther();
        double variableExpenses = 0;
        for (Transaction.TransactionItem t : ds.transactions) {
            variableExpenses += t.getAmount();
        }

        double totalExpenses = fixedExpenses + variableExpenses;
        double netBalance = income - totalExpenses;

        VBox rows = new VBox(10);
        rows.getChildren().add(createSummaryRow("Monthly Income", income, "#27ae60"));
        rows.getChildren().add(new Separator());
        rows.getChildren().add(createSummaryRow("Fixed Budget", fixedExpenses, "#7f8c8d"));
        rows.getChildren().add(createSummaryRow("Transactions", variableExpenses, "#7f8c8d"));
        rows.getChildren().add(createSummaryRow("Total Spending", totalExpenses, "#c0392b"));
        rows.getChildren().add(new Separator());

        Label lblNetTitle = new Label("Net Savings");
        lblNetTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblNetVal = new Label(String.format("RM %.2f", netBalance));
        if (netBalance >= 0) {
            lblNetVal.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #27ae60;");
        } else {
            lblNetVal.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #e74c3c;");
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
        box.setStyle(
                "-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
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
        if (dataStore.budgetCategories.isEmpty()) {
            Label placeholder = new Label("No budgets set.");
            placeholder.setStyle("-fx-text-fill: #7f8c8d;");
            list.getChildren().add(placeholder);
            return list;
        }
        for (Budget.BudgetCategory b : dataStore.budgetCategories) {
            double spent = dataStore.getSpentByCategory(b.getName());
            double progress = (b.getLimit() > 0) ? spent / b.getLimit() : 0;
            Label name = new Label(b.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50;");
            ProgressBar pb = new ProgressBar(progress);
            pb.setMaxWidth(Double.MAX_VALUE);
            if (spent > b.getLimit())
                pb.setStyle("-fx-accent: #e74c3c;");
            else
                pb.setStyle("-fx-accent: #27ae60;");
            Label stats = new Label(String.format("RM %.2f / RM %.2f", spent, b.getLimit()));
            stats.setStyle("-fx-font-size: 10px; -fx-text-fill: grey;");
            VBox item = new VBox(2, name, pb, stats);
            list.getChildren().add(item);
        }
        return list;
    }

    private VBox createGoalList() {
        VBox list = new VBox(10);
        if (dataStore.goals.isEmpty()) {
            Label placeholder = new Label("No active goals.");
            placeholder.setStyle("-fx-text-fill: #7f8c8d;");
            list.getChildren().add(placeholder);
            return list;
        }
        for (Goal.GoalItem g : dataStore.goals) {
            double progress = (g.getTarget() > 0) ? g.getCurrent() / g.getTarget() : 0;
            Label name = new Label(g.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50;");
            ProgressBar pb = new ProgressBar(progress);
            pb.setMaxWidth(Double.MAX_VALUE);
            pb.setStyle("-fx-accent: #3498db;");
            Label stats = new Label(String.format("RM %.2f / RM %.2f", g.getCurrent(), g.getTarget()));
            stats.setStyle("-fx-font-size: 10px; -fx-text-fill: grey;");
            VBox item = new VBox(2, name, pb, stats);
            list.getChildren().add(item);
        }
        return list;
    }

    private VBox createDebtList() {
        VBox list = new VBox(10);
        if (dataStore.debts.isEmpty()) {
            Label placeholder = new Label("Debt free!");
            placeholder.setStyle("-fx-text-fill: #7f8c8d;");
            list.getChildren().add(placeholder);
            return list;
        }
        for (Debt.DebtItem d : dataStore.debts) {
            HBox item = new HBox();
            Label name = new Label(d.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50;");
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            Label amount = new Label(String.format("RM %.2f", d.getAmount()));
            amount.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
            item.getChildren().addAll(name, spacer, amount);
            list.getChildren().add(item);
        }
        return list;
    }

    
    @Override
    public void refresh() {
        updateBarChart();
    }
}
