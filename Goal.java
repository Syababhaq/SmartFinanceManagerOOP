package com.mycompany.oopfinal;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.Serializable;

public class Goal extends VBox implements DataStore.Refreshable {

    
    public static class GoalItem extends DataStore.BaseModel {
        private String name;
        private double target;
        private double current;

        public GoalItem(String n, double t, double c) {
            super();
            name = n;
            target = t;
            current = c;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getTarget() {
            return target;
        }

        public void setTarget(double target) {
            this.target = target;
        }

        public double getCurrent() {
            return current;
        }

        public void setCurrent(double current) {
            this.current = current;
        }

        @Override
        public boolean validate() {
            return name != null && !name.isEmpty() && target > 0 && current >= 0;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private ListView<GoalItem> listView;
    private DataStore dataStore;

    public Goal(DataStore dataStore) {
        this.dataStore = dataStore;
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        Label title = new Label("Financial Goals");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        TextField tfName = new TextField();
        tfName.setPromptText("Goal Name");
        TextField tfTarget = new TextField();
        tfTarget.setPromptText("Target Amount");
        TextField tfCurrent = new TextField();
        tfCurrent.setPromptText("Current Amount");

        Button btnAdd = new Button("Add Goal");
        btnAdd.setStyle("-fx-background-color: " + MainLayout.COL_ACCENT + "; -fx-text-fill: white;");

        listView = new ListView<>();
        listView.setItems(dataStore.goals);

        VBox.setVgrow(listView, Priority.ALWAYS);

        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(GoalItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox card = new VBox(5);
                    Label nameLbl = new Label(item.getName());
                    nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    double progress = (item.getTarget() > 0) ? item.getCurrent() / item.getTarget() : 0;
                    ProgressBar pb = new ProgressBar(progress);
                    pb.setMaxWidth(Double.MAX_VALUE);

                    Label progressLbl = new Label(String.format("RM %.2f / RM %.2f (%.0f%%)", item.getCurrent(),
                            item.getTarget(), progress * 100));

                    card.getChildren().addAll(nameLbl, pb, progressLbl);
                    setGraphic(card);
                }
            }
        });

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
            if (selected != null)
                dataStore.goals.remove(selected);
        });

        cm.getItems().addAll(miDeposit, miWithdraw, new SeparatorMenuItem(), miEdit, miDelete);
        listView.setContextMenu(cm);

        btnAdd.setOnAction(e -> {
            try {
                String name = tfName.getText();
                double target = Double.parseDouble(tfTarget.getText());
                double current = Double.parseDouble(tfCurrent.getText());
                dataStore.goals.add(new GoalItem(name, target, current));
                tfName.clear();
                tfTarget.clear();
                tfCurrent.clear();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Number").show();
            }
        });

        HBox inputRow = new HBox(10, tfName, tfTarget, tfCurrent, btnAdd);
        getChildren().addAll(title, inputRow, listView);
    }

    public ListView<GoalItem> getListView() {
        return listView;
    }

    public void setListView(ListView<GoalItem> listView) {
        this.listView = listView;
    }

    private void adjustFunds(GoalItem item, boolean isDeposit) {
        if (item == null)
            return;
        TextInputDialog dialog = new TextInputDialog("0");
        dialog.setTitle(isDeposit ? "Deposit Funds" : "Withdraw Funds");
        dialog.setHeaderText((isDeposit ? "Add to " : "Take from ") + item.getName());
        dialog.setContentText("Amount (RM):");

        dialog.showAndWait().ifPresent(val -> {
            try {
                double amount = Double.parseDouble(val);
                if (isDeposit)
                    item.setCurrent(item.getCurrent() + amount);
                else
                    item.setCurrent(item.getCurrent() - amount);
                listView.refresh();
            } catch (NumberFormatException e) {
                new Alert(Alert.AlertType.ERROR, "Invalid Amount").show();
            }
        });
    }

    private void editGoal(GoalItem item) {
        if (item == null)
            return;
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Goal");
        ButtonType saveBtnType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtnType, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        TextField tfName = new TextField(item.getName());
        TextField tfTarget = new TextField(String.valueOf(item.getTarget()));
        grid.add(new Label("Goal Name:"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Target Amount:"), 0, 1);
        grid.add(tfTarget, 1, 1);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtnType) {
                item.setName(tfName.getText());
                try {
                    item.setTarget(Double.parseDouble(tfTarget.getText()));
                } catch (Exception e) {
                }
                return saveBtnType;
            }
            return null;
        });
        dialog.showAndWait();
        listView.refresh();
    }

    
    @Override
    public void refresh() {
        listView.refresh();
    }
}
