package com.mycompany.oopfinal;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.io.Serializable;
import java.time.LocalDate;

public class View_Transactions extends VBox {

    // --- MODEL ---
    public static class Transaction implements Serializable {
        public String date;
        public String desc;
        public double amount;
        public String category;

        public Transaction(String d, String de, double a, String c) {
            date = d; desc = de; amount = a; category = c;
        }
        public String getDate() { return date; }
        public void setDate(String d) { date = d; }
        public String getDesc() { return desc; }
        public void setDesc(String d) { desc = d; }
        public double getAmount() { return amount; }
        public void setAmount(double a) { amount = a; }
        public String getCategory() { return category; }
        public void setCategory(String c) { category = c; }
    }

    private TableView<Transaction> table;

    public View_Transactions() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        Label title = new Label("Transaction History");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // --- TABLE SETUP ---
        table = new TableView<>();
        table.setItems(DataStore.getInstance().transactions);
        
        // FIX 1: Make Table Fill Vertical Space
        VBox.setVgrow(table, Priority.ALWAYS);
        
        TableColumn<Transaction, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        TableColumn<Transaction, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));
        
        TableColumn<Transaction, Double> colAmt = new TableColumn<>("Amount");
        colAmt.setCellValueFactory(new PropertyValueFactory<>("amount"));
        
        TableColumn<Transaction, String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(new PropertyValueFactory<>("category"));
        
        table.getColumns().addAll(colDate, colDesc, colAmt, colCat);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // --- ROW CONTEXT MENU (EDIT / DELETE) ---
        table.setRowFactory(tv -> {
            TableRow<Transaction> row = new TableRow<>();
            ContextMenu cm = new ContextMenu();
            
            MenuItem miEdit = new MenuItem("Edit Transaction");
            miEdit.setOnAction(e -> editTransaction(row.getItem()));
            
            MenuItem miDelete = new MenuItem("Delete");
            miDelete.setStyle("-fx-text-fill: red;");
            miDelete.setOnAction(e -> DataStore.getInstance().transactions.remove(row.getItem()));
            
            cm.getItems().addAll(miEdit, miDelete);
            
            row.contextMenuProperty().bind(
                javafx.beans.binding.Bindings.when(row.emptyProperty())
                .then((ContextMenu)null)
                .otherwise(cm)
            );
            return row;
        });

        // --- INPUTS ---
        DatePicker dp = new DatePicker();
        TextField tfDesc = new TextField(); tfDesc.setPromptText("Description");
        TextField tfAmt = new TextField(); tfAmt.setPromptText("Amount");
        ComboBox<View_Budget.BudgetCategory> cbCat = new ComboBox<>();
        cbCat.setItems(DataStore.getInstance().budgetCategories);
        cbCat.setPromptText("Category");
        cbCat.setPrefWidth(150);

        Button btnAdd = new Button("Add");
        btnAdd.setStyle("-fx-background-color: " + MainLayout.COL_ACCENT + "; -fx-text-fill: white;");

        btnAdd.setOnAction(e -> {
            try {
                if (dp.getValue() == null || cbCat.getValue() == null) return;
                DataStore.getInstance().transactions.add(new Transaction(
                    dp.getValue().toString(), tfDesc.getText(), 
                    Double.parseDouble(tfAmt.getText()), cbCat.getValue().name
                ));
                tfDesc.clear(); tfAmt.clear();
            } catch(Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Input").show();
            }
        });

        HBox inputRow = new HBox(10, dp, tfDesc, tfAmt, cbCat, btnAdd);
        getChildren().addAll(title, inputRow, table);
    }

    private void editTransaction(Transaction t) {
        if(t == null) return;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Transaction");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        DatePicker dp = new DatePicker(LocalDate.parse(t.date));
        TextField tfDesc = new TextField(t.desc);
        TextField tfAmt = new TextField(String.valueOf(t.amount));
        ComboBox<View_Budget.BudgetCategory> cbCat = new ComboBox<>();
        cbCat.setItems(DataStore.getInstance().budgetCategories);
        for(View_Budget.BudgetCategory bc : cbCat.getItems()) {
            if(bc.name.equals(t.category)) { cbCat.getSelectionModel().select(bc); break; }
        }
        grid.add(new Label("Date:"), 0, 0); grid.add(dp, 1, 0);
        grid.add(new Label("Desc:"), 0, 1); grid.add(tfDesc, 1, 1);
        grid.add(new Label("Amount:"), 0, 2); grid.add(tfAmt, 1, 2);
        grid.add(new Label("Category:"), 0, 3); grid.add(cbCat, 1, 3);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if(btn == saveBtn) {
                t.setDate(dp.getValue().toString());
                t.setDesc(tfDesc.getText());
                try { t.setAmount(Double.parseDouble(tfAmt.getText())); } catch(Exception e){}
                if(cbCat.getValue() != null) t.setCategory(cbCat.getValue().name);
                return saveBtn;
            }
            return null;
        });
        dialog.showAndWait();
        table.refresh();
    }
}