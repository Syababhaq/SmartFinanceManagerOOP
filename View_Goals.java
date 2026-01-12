package com.mycompany.oopfinal;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.Serializable;

public class View_Goals extends VBox {

    // --- MODEL ---
    public static class GoalItem implements Serializable {
        public String name;
        public double target;
        public double current;
        public GoalItem(String n, double t, double c) { name = n; target = t; current = c; }
        @Override
        public String toString() { return name; }
    }

    private ListView<GoalItem> listView;

    public View_Goals() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        Label title = new Label("Financial Goals");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // --- INPUTS ---
        TextField tfName = new TextField(); tfName.setPromptText("Goal Name");
        TextField tfTarget = new TextField(); tfTarget.setPromptText("Target Amount");
        TextField tfCurrent = new TextField(); tfCurrent.setPromptText("Current Amount");
        
        Button btnAdd = new Button("Add Goal");
        btnAdd.setStyle("-fx-background-color: " + MainLayout.COL_ACCENT + "; -fx-text-fill: white;");

        // --- LIST VIEW ---
        listView = new ListView<>();
        listView.setItems(DataStore.getInstance().goals);
        
        // FIX 1: Make List Fill Vertical Space
        VBox.setVgrow(listView, Priority.ALWAYS);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(GoalItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null);
                } else {
                    // Custom Card UI
                    VBox card = new VBox(5);
                    Label nameLbl = new Label(item.name);
                    nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    
                    double progress = (item.target > 0) ? item.current / item.target : 0;
                    ProgressBar pb = new ProgressBar(progress);
                    
                    // FIX 2: Make Progress Bar Dynamic Width
                    pb.setMaxWidth(Double.MAX_VALUE);
                    
                    Label progressLbl = new Label(String.format("RM %.2f / RM %.2f (%.0f%%)", item.current, item.target, progress * 100));
                    
                    card.getChildren().addAll(nameLbl, pb, progressLbl);
                    setGraphic(card);
                }
            }
        });

        // --- CONTEXT MENU (Right Click) ---
        ContextMenu cm = new ContextMenu();
        
        MenuItem miDeposit = new MenuItem("Deposit (Cash In)");
        miDeposit.setOnAction(e -> adjustFunds(listView.getSelectionModel().getSelectedItem(), true));

        MenuItem miWithdraw = new MenuItem("Withdraw (Cash Out)");
        miWithdraw.setOnAction(e -> adjustFunds(listView.getSelectionModel().getSelectedItem(), false));
        
        MenuItem miEdit = new MenuItem("Edit Details");
        miEdit.setOnAction(e -> editGoal(listView.getSelectionModel().getSelectedItem()));

        MenuItem miDelete = new MenuItem("Delete Goal");
        miDelete.setStyle("-fx-text-fill: red;");
        miDelete.setOnAction(e -> {
            GoalItem selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) DataStore.getInstance().goals.remove(selected);
        });

        cm.getItems().addAll(miDeposit, miWithdraw, new SeparatorMenuItem(), miEdit, miDelete);
        listView.setContextMenu(cm);

        // --- ADD BUTTON ACTION ---
        btnAdd.setOnAction(e -> {
            try {
                String name = tfName.getText();
                double target = Double.parseDouble(tfTarget.getText());
                double current = Double.parseDouble(tfCurrent.getText());
                DataStore.getInstance().goals.add(new GoalItem(name, target, current));
                tfName.clear(); tfTarget.clear(); tfCurrent.clear();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Number").show();
            }
        });

        HBox inputRow = new HBox(10, tfName, tfTarget, tfCurrent, btnAdd);
        getChildren().addAll(title, inputRow, listView);
    }

    // --- HELPER: Cash In / Cash Out ---
    private void adjustFunds(GoalItem item, boolean isDeposit) {
        if (item == null) return;
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle(isDeposit ? "Deposit Funds" : "Withdraw Funds");
        dialog.setHeaderText((isDeposit ? "Add to " : "Take from ") + item.name);
        dialog.setContentText("Amount (RM):");

        dialog.showAndWait().ifPresent(val -> {
            try {
                double amount = Double.parseDouble(val);
                if (isDeposit) item.current += amount;
                else item.current -= amount;
                listView.refresh();
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid Amount").show();
            }
        });
    }

    // --- HELPER: Edit Dialog ---
    private void editGoal(GoalItem item) {
        if (item == null) return;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Goal");
        ButtonType saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        TextField tfName = new TextField(item.name);
        TextField tfTarget = new TextField(String.valueOf(item.target));
        grid.add(new Label("Goal Name:"), 0, 0); grid.add(tfName, 1, 0);
        grid.add(new Label("Target Amount:"), 0, 1); grid.add(tfTarget, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                item.name = tfName.getText();
                try { item.target = Double.parseDouble(tfTarget.getText()); } catch(Exception e){}
                return saveBtnType;
            }
            return null;
        });
        dialog.showAndWait();
        listView.refresh();
    }
}