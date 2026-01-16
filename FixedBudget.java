package com.mycompany.oopfinal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class FixedBudget extends VBox implements DataStore.Refreshable {

    private VBox summaryBubble;
    private TextField incomeField, billingsField, insuranceField, otherField;
    private DataStore dataStore;

    public FixedBudget(DataStore dataStore) {
        this.dataStore = dataStore;
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        Label title = new Label("Fixed Monthly Budget");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + MainLayout.COL_DARK + ";");

        DataStore ds = dataStore;
        incomeField = createAmountField(ds.getFixedIncome());
        billingsField = createAmountField(ds.getFixedBillings());
        insuranceField = createAmountField(ds.getFixedInsurance());
        otherField = createAmountField(ds.getFixedOther());

        VBox inputBubbles = new VBox(10,
                createInputBubble("Total Monthly Income", incomeField),
                createInputBubble("Billings", billingsField),
                createInputBubble("Insurance", insuranceField),
                createInputBubble("Other Fixed Costs", otherField));

        inputBubbles.setMaxWidth(Double.MAX_VALUE);

        Button saveBtn = new Button("Save & Calculate");
        saveBtn.setStyle("-fx-background-color: " + MainLayout.COL_ACCENT
                + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        saveBtn.setPrefWidth(200);

        saveBtn.setOnAction(e -> {
            DataStore store = dataStore;
            store.setFixedIncome(parse(incomeField.getText()));
            store.setFixedBillings(parse(billingsField.getText()));
            store.setFixedInsurance(parse(insuranceField.getText()));
            store.setFixedOther(parse(otherField.getText()));

            dataStore.saveData();
            updateSummary();
        });

        Label summaryTitle = new Label("Budget Summary");
        summaryTitle
                .setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + MainLayout.COL_DARK + ";");

        summaryBubble = new VBox(8);
        summaryBubble.setPadding(new Insets(15));
        summaryBubble.setMaxWidth(Double.MAX_VALUE);
        summaryBubble.setStyle(
                "-fx-background-color: #e3f2fd;" +
                        "-fx-background-radius: 20;" +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        updateSummary();

        getChildren().addAll(title, inputBubbles, saveBtn, new Separator(), summaryTitle, summaryBubble);
    }

    public VBox getSummaryBubble() {
        return summaryBubble;
    }

    public void setSummaryBubble(VBox summaryBubble) {
        this.summaryBubble = summaryBubble;
    }

    public TextField getIncomeField() {
        return incomeField;
    }

    public void setIncomeField(TextField incomeField) {
        this.incomeField = incomeField;
    }

    public TextField getBillingsField() {
        return billingsField;
    }

    public void setBillingsField(TextField billingsField) {
        this.billingsField = billingsField;
    }

    public TextField getInsuranceField() {
        return insuranceField;
    }

    public void setInsuranceField(TextField insuranceField) {
        this.insuranceField = insuranceField;
    }

    public TextField getOtherField() {
        return otherField;
    }

    public void setOtherField(TextField otherField) {
        this.otherField = otherField;
    }

    private void updateSummary() {
        DataStore ds = dataStore;
        summaryBubble.getChildren().clear();

        double totalFixed = ds.getFixedBillings() + ds.getFixedInsurance() + ds.getFixedOther();
        double remainder = ds.getFixedIncome() - totalFixed;

        summaryBubble.getChildren().addAll(
                createSummaryRow("Total Income", format(ds.getFixedIncome()), false),
                createSummaryRow("Total Fixed Expenses", "- " + format(totalFixed), false),
                new Separator(),
                createSummaryRow("Disposable Income (Remainder)", format(remainder), true));

        Label valLbl = (Label) ((HBox) summaryBubble.getChildren().get(3)).getChildren().get(2);
        if (remainder < 0)
            valLbl.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 16px;");
        else
            valLbl.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 16px;");
    }

    private HBox createInputBubble(String name, TextField field) {
        Label label = new Label(name);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        field.setPrefWidth(150);
        field.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bubble = new HBox(10, label, spacer, field);
        bubble.setAlignment(Pos.CENTER_LEFT);
        bubble.setPadding(new Insets(12));
        bubble.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #d1d5db; -fx-border-radius: 15;");

        return bubble;
    }

    private HBox createSummaryRow(String name, String value, boolean bold) {
        Label nameLabel = new Label(name);
        Label valueLabel = new Label(value);

        if (bold) {
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            valueLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        }

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(10, nameLabel, spacer, valueLabel);
        return row;
    }

    private TextField createAmountField(double val) {
        return new TextField(String.valueOf(val));
    }

    private String format(double value) {
        return "RM " + String.format("%.2f", value);
    }

    private double parse(String text) {
        try {
            return Double.parseDouble(text);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void refresh() {
        updateSummary();
    }
}
