package com.mycompany.oopfinal;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.io.Serializable;
import java.time.LocalDate;

public class Transaction extends VBox implements DataStore.Refreshable {

    
    public static class TransactionItem extends DataStore.BaseModel {
        private String date;
        private String desc;
        private double amount;
        private String category;

        public TransactionItem(String d, String de, double a, String c) {
            super();
            date = d;
            desc = de;
            amount = a;
            category = c;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String d) {
            date = d;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String d) {
            desc = d;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double a) {
            amount = a;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String c) {
            category = c;
        }

        @Override
        public boolean validate() {
            return date != null && !date.isEmpty()
                    && amount > 0
                    && category != null && !category.isEmpty();
        }
    }

    private TableView<TransactionItem> table;
    private DataStore dataStore;

    public Transaction(DataStore dataStore) {
        this.dataStore = dataStore;
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        Label title = new Label("Transaction History");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        table = new TableView<>();
        table.setItems(dataStore.transactions);

        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<TransactionItem, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<TransactionItem, String> colDesc = new TableColumn<>("Description");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("desc"));

        TableColumn<TransactionItem, Double> colAmt = new TableColumn<>("Amount");
        colAmt.setCellValueFactory(new PropertyValueFactory<>("amount"));

        TableColumn<TransactionItem, String> colCat = new TableColumn<>("Category");
        colCat.setCellValueFactory(new PropertyValueFactory<>("category"));

        table.getColumns().addAll(colDate, colDesc, colAmt, colCat);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        table.setRowFactory(tv -> {
            TableRow<TransactionItem> row = new TableRow<>();
            ContextMenu cm = new ContextMenu();

            MenuItem miEdit = new MenuItem("Edit Transaction");
            miEdit.setOnAction(e -> editTransaction(row.getItem()));

            MenuItem miDelete = new MenuItem("Delete");
            miDelete.setStyle("-fx-text-fill: red;");
            miDelete.setOnAction(e -> dataStore.transactions.remove(row.getItem()));

            cm.getItems().addAll(miEdit, miDelete);

            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(cm));
            return row;
        });

        DatePicker dp = new DatePicker();
        TextField tfDesc = new TextField();
        tfDesc.setPromptText("Description");
        TextField tfAmt = new TextField();
        tfAmt.setPromptText("Amount");
        ComboBox<Budget.BudgetCategory> cbCat = new ComboBox<>();
        cbCat.setItems(dataStore.budgetCategories);
        cbCat.setPromptText("Category");
        cbCat.setPrefWidth(150);

        Button btnAdd = new Button("Add");
        btnAdd.setStyle("-fx-background-color: " + MainLayout.COL_ACCENT + "; -fx-text-fill: white;");

        btnAdd.setOnAction(e -> {
            try {
                if (dp.getValue() == null || cbCat.getValue() == null)
                    return;
                dataStore.transactions.add(new TransactionItem(
                        dp.getValue().toString(), tfDesc.getText(),
                        Double.parseDouble(tfAmt.getText()), cbCat.getValue().getName()));
                tfDesc.clear();
                tfAmt.clear();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Input").show();
            }
        });

        HBox inputRow = new HBox(10, dp, tfDesc, tfAmt, cbCat, btnAdd);
        getChildren().addAll(title, inputRow, table);
    }

    public TableView<TransactionItem> getTable() {
        return table;
    }

    public void setTable(TableView<TransactionItem> table) {
        this.table = table;
    }

    private void editTransaction(TransactionItem t) {
        if (t == null)
            return;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Transaction");
        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        DatePicker dp = new DatePicker(LocalDate.parse(t.getDate()));
        TextField tfDesc = new TextField(t.getDesc());
        TextField tfAmt = new TextField(String.valueOf(t.getAmount()));
        ComboBox<Budget.BudgetCategory> cbCat = new ComboBox<>();
        cbCat.setItems(dataStore.budgetCategories);
        for (Budget.BudgetCategory bc : cbCat.getItems()) {
            if (bc.getName().equals(t.getCategory())) {
                cbCat.getSelectionModel().select(bc);
                break;
            }
        }
        grid.add(new Label("Date:"), 0, 0);
        grid.add(dp, 1, 0);
        grid.add(new Label("Desc:"), 0, 1);
        grid.add(tfDesc, 1, 1);
        grid.add(new Label("Amount:"), 0, 2);
        grid.add(tfAmt, 1, 2);
        grid.add(new Label("Category:"), 0, 3);
        grid.add(cbCat, 1, 3);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                t.setDate(dp.getValue().toString());
                t.setDesc(tfDesc.getText());
                try {
                    t.setAmount(Double.parseDouble(tfAmt.getText()));
                } catch (Exception e) {
                }
                if (cbCat.getValue() != null)
                    t.setCategory(cbCat.getValue().getName());
                return saveBtn;
            }
            return null;
        });
        dialog.showAndWait();
        table.refresh();
    }

    @Override
    public void refresh() {
        table.refresh();
    }
}
