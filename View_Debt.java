package com.mycompany.oopfinal;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.Serializable;

public class View_Debt extends VBox {

    // --- MODEL ---
    public static class DebtItem implements Serializable {
        public String name;
        public double amount;
        public String due;
        public DebtItem(String n, double a, String d) { name = n; amount = a; due = d; }
    }

    private ListView<DebtItem> listView;

    public View_Debt() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        Label title = new Label("Debt Manager");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // --- INPUTS ---
        TextField tfName = new TextField(); tfName.setPromptText("Debt Name");
        TextField tfAmount = new TextField(); tfAmount.setPromptText("Amount Owed");
        TextField tfDue = new TextField(); tfDue.setPromptText("Due Date (YYYY-MM-DD)");
        Button btnAdd = new Button("Add Debt");
        btnAdd.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

        // --- LIST VIEW ---
        listView = new ListView<>();
        listView.setItems(DataStore.getInstance().debts);
        
        // FIX 1: Make List Fill Vertical Space
        VBox.setVgrow(listView, Priority.ALWAYS);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(DebtItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null);
                } else {
                    VBox card = new VBox(5);
                    card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-background-color: white;");
                    Label nameLbl = new Label(item.name + " (Due: " + item.due + ")");
                    nameLbl.setStyle("-fx-font-weight: bold;");
                    Label amtLbl = new Label("Owed: RM " + String.format("%.2f", item.amount));
                    amtLbl.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    card.getChildren().addAll(nameLbl, amtLbl);
                    setGraphic(card);
                }
            }
        });
        
        // --- CONTEXT MENU ---
        ContextMenu cm = new ContextMenu();
        
        MenuItem miPay = new MenuItem("Pay Debt (Cash Out)");
        miPay.setOnAction(e -> adjustDebt(listView.getSelectionModel().getSelectedItem(), false)); 

        MenuItem miBorrow = new MenuItem("Borrow More (Cash In)");
        miBorrow.setOnAction(e -> adjustDebt(listView.getSelectionModel().getSelectedItem(), true)); 

        MenuItem miEdit = new MenuItem("Edit Details");
        miEdit.setOnAction(e -> editDebt(listView.getSelectionModel().getSelectedItem()));
        
        MenuItem miDelete = new MenuItem("Delete Debt");
        miDelete.setStyle("-fx-text-fill: red;");
        miDelete.setOnAction(e -> {
            DebtItem selected = listView.getSelectionModel().getSelectedItem();
            if(selected != null) DataStore.getInstance().debts.remove(selected);
        });
        
        cm.getItems().addAll(miPay, miBorrow, new SeparatorMenuItem(), miEdit, miDelete);
        listView.setContextMenu(cm);

        // --- ADD BUTTON ---
        btnAdd.setOnAction(e -> {
            try {
                DataStore.getInstance().debts.add(new DebtItem(
                    tfName.getText(), Double.parseDouble(tfAmount.getText()), tfDue.getText()
                ));
                tfName.clear(); tfAmount.clear(); tfDue.clear();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Number").show();
            }
        });

        HBox inputRow = new HBox(10, tfName, tfAmount, tfDue, btnAdd);
        getChildren().addAll(title, inputRow, listView);
    }
    
    private void adjustDebt(DebtItem item, boolean isBorrowing) {
        if(item == null) return;
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle(isBorrowing ? "Borrow More" : "Pay Debt");
        dialog.setHeaderText(isBorrowing ? "Increase debt for: " + item.name : "Pay off: " + item.name);
        dialog.showAndWait().ifPresent(val -> {
            try {
                double amt = Double.parseDouble(val);
                if(isBorrowing) item.amount += amt;
                else item.amount -= amt;
                if(item.amount < 0) item.amount = 0;
                listView.refresh();
            } catch(Exception e) {}
        });
    }
    
    private void editDebt(DebtItem item) {
        if(item == null) return;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Debt");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        TextField tfName = new TextField(item.name);
        TextField tfDue = new TextField(item.due);
        grid.add(new Label("Name:"), 0, 0); grid.add(tfName, 1, 0);
        grid.add(new Label("Due Date:"), 0, 1); grid.add(tfDue, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if(btn == saveBtn) {
                item.name = tfName.getText();
                item.due = tfDue.getText();
                return saveBtn;
            }
            return null;
        });
        dialog.showAndWait();
        listView.refresh();
    }
}