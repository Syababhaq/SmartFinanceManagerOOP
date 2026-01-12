package com.mycompany.oopfinal;

import java.io.Serializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class View_Budget extends VBox {

    // --- MODEL ---
    public static class BudgetCategory implements Serializable {
        public String name;
        public double limit;

        public BudgetCategory(String name, double limit) {
            this.name = name;
            this.limit = limit;
        }
        @Override
        public String toString() { return name; } 
    }

    private ListView<BudgetCategory> listView;

    public View_Budget() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        Label title = new Label("Budget Limits & Categories");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // --- INPUTS ---
        TextField tfName = new TextField(); 
        tfName.setPromptText("Category Name (e.g., Food)");
        TextField tfLimit = new TextField(); 
        tfLimit.setPromptText("Monthly Limit (RM)");
        Button btnAdd = new Button("Set Budget");
        btnAdd.setStyle("-fx-background-color: " + MainLayout.COL_ACCENT + "; -fx-text-fill: white;");

        // --- LIST VIEW ---
        listView = new ListView<>();
        listView.setItems(DataStore.getInstance().budgetCategories);
        
        // === FIX 1: Make List Fill the Screen Height ===
        VBox.setVgrow(listView, Priority.ALWAYS);

        // Custom Cell Factory
        listView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(BudgetCategory item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null); setGraphic(null);
                } else {
                    double spent = DataStore.getInstance().getSpentByCategory(item.name);
                    double progress = (item.limit > 0) ? spent / item.limit : 0;

                    VBox card = new VBox(5);
                    Label nameLbl = new Label(item.name);
                    nameLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                    
                    ProgressBar pb = new ProgressBar(progress);
                    // === FIX 2: Make Progress Bar Dynamic Width ===
                    pb.setMaxWidth(Double.MAX_VALUE); 
                    
                    if(spent > item.limit) pb.setStyle("-fx-accent: #e74c3c;"); 
                    else pb.setStyle("-fx-accent: #27ae60;");

                    Label statLbl = new Label(String.format("Spent: RM %.2f / RM %.2f (%.0f%%)", spent, item.limit, progress * 100));
                    
                    card.getChildren().addAll(nameLbl, pb, statLbl);
                    setGraphic(card);
                }
            }
        });
        
        // Context Menu
        ContextMenu cm = new ContextMenu();
        MenuItem miDelete = new MenuItem("Delete Category");
        miDelete.setStyle("-fx-text-fill: red;");
        miDelete.setOnAction(e -> {
            BudgetCategory selected = listView.getSelectionModel().getSelectedItem();
            if(selected != null) DataStore.getInstance().budgetCategories.remove(selected);
        });
        cm.getItems().add(miDelete);
        listView.setContextMenu(cm);

        btnAdd.setOnAction(e -> {
            try {
                String name = tfName.getText();
                if(name.isEmpty()) return;
                double limit = Double.parseDouble(tfLimit.getText());
                DataStore.getInstance().budgetCategories.add(new BudgetCategory(name, limit));
                tfName.clear(); tfLimit.clear();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Limit Amount").show();
            }
        });

        HBox inputRow = new HBox(10, tfName, tfLimit, btnAdd);
        getChildren().addAll(title, inputRow, listView);
    }
}
