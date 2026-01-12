package com.mycompany.oopfinal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class View_FixedBudget extends VBox {

    private VBox summaryBubble;
    private TextField incomeField, billingsField, insuranceField, otherField;

    public View_FixedBudget() {
        setSpacing(20);
        setPadding(new Insets(30));
        setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        Label title = new Label("Fixed Monthly Budget");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + MainLayout.COL_DARK + ";");

        // --- 1. Load Data from Store ---
        DataStore ds = DataStore.getInstance();
        incomeField = createAmountField(ds.fixedIncome);
        billingsField = createAmountField(ds.fixedBillings);
        insuranceField = createAmountField(ds.fixedInsurance);
        otherField = createAmountField(ds.fixedOther);

        // --- 2. Input Section ---
        VBox inputBubbles = new VBox(10,
                createInputBubble("Total Monthly Income", incomeField),
                createInputBubble("Billings", billingsField),
                createInputBubble("Insurance", insuranceField),
                createInputBubble("Other Fixed Costs", otherField)
        );
        
        // FIX: Allow full width expansion
        inputBubbles.setMaxWidth(Double.MAX_VALUE);

        // --- 3. Save Button ---
        Button saveBtn = new Button("Save & Calculate");
        saveBtn.setStyle("-fx-background-color: " + MainLayout.COL_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        saveBtn.setPrefWidth(200);
        
        saveBtn.setOnAction(e -> {
            DataStore store = DataStore.getInstance();
            store.fixedIncome = parse(incomeField.getText());
            store.fixedBillings = parse(billingsField.getText());
            store.fixedInsurance = parse(insuranceField.getText());
            store.fixedOther = parse(otherField.getText());
            
            DataStore.saveData();
            updateSummary();
        });

        // --- 4. Summary Section ---
        Label summaryTitle = new Label("Budget Summary");
        summaryTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + MainLayout.COL_DARK + ";");

        summaryBubble = new VBox(8);
        summaryBubble.setPadding(new Insets(15));
        
        // FIX: Allow full width expansion
        summaryBubble.setMaxWidth(Double.MAX_VALUE);
        
        summaryBubble.setStyle(
                "-fx-background-color: #e3f2fd;" +
                "-fx-background-radius: 20;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );
        
        updateSummary();

        getChildren().addAll(title, inputBubbles, saveBtn, new Separator(), summaryTitle, summaryBubble);
    }

    // --- LOGIC ---
    private void updateSummary() {
        DataStore ds = DataStore.getInstance();
        summaryBubble.getChildren().clear();

        double totalFixed = ds.fixedBillings + ds.fixedInsurance + ds.fixedOther;
        double remainder = ds.fixedIncome - totalFixed;

        summaryBubble.getChildren().addAll(
                createSummaryRow("Total Income", format(ds.fixedIncome), false),
                createSummaryRow("Total Fixed Expenses", "- " + format(totalFixed), false),
                new Separator(),
                createSummaryRow("Disposable Income (Remainder)", format(remainder), true)
        );

        Label valLbl = (Label) ((HBox) summaryBubble.getChildren().get(3)).getChildren().get(2);
        if (remainder < 0) valLbl.setStyle("-fx-text-fill: red; -fx-font-weight: bold; -fx-font-size: 16px;");
        else valLbl.setStyle("-fx-text-fill: green; -fx-font-weight: bold; -fx-font-size: 16px;");
    }

    // --- UI HELPERS ---
    private HBox createInputBubble(String name, TextField field) {
        Label label = new Label(name);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #374151;");

        field.setPrefWidth(150);
        field.setAlignment(Pos.CENTER_RIGHT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS); // This pushes content to edges

        HBox bubble = new HBox(10, label, spacer, field);
        bubble.setAlignment(Pos.CENTER_LEFT);
        bubble.setPadding(new Insets(12));
        bubble.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-color: #d1d5db; -fx-border-radius: 15;");

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
        HBox.setHgrow(spacer, Priority.ALWAYS); // This pushes content to edges

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
        try { return Double.parseDouble(text); } catch (Exception e) { return 0; }
    }
}
