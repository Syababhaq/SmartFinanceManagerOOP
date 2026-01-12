package com.mycompany.oopfinal;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainLayout {

    private final Stage stage;
    private final BorderPane root;
    private final VBox sidebar;
    private boolean isSidebarOpen = true;

    // Theme Colors
    public static final String COL_DARK = "#2c3e50";
    public static final String COL_ACCENT = "#3498db";
    public static final String COL_BG = "#f5f7fb";

    public MainLayout(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        
        // Initialize Data
        DataStore.loadData();

        // --- SIDEBAR ---
        sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: " + COL_DARK + ";");
        sidebar.setPrefWidth(200);

        Label lblTitle = new Label("Smart Finance");
        lblTitle.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");

        // Navigation Buttons
        Button btnDash = createNavBtn("Dashboard", () -> setCenter(new View_Dashboard()));
        Button btnFixed = createNavBtn("Fixed Budget", () -> setCenter(new View_FixedBudget()));
        Button btnBudget = createNavBtn("Budget Limit", () -> setCenter(new View_Budget()));
        Button btnGoals = createNavBtn("Goals", () -> setCenter(new View_Goals()));
        Button btnDebts = createNavBtn("Debts", () -> setCenter(new View_Debt()));
        Button btnTrans = createNavBtn("Transactions", () -> setCenter(new View_Transactions()));
    
        // System Buttons
        Button btnSave = createNavBtn("Save Data", DataStore::saveData);
        btnSave.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
        
        Button btnLogout = createNavBtn("Logout", () -> new View_Login(stage).show());
        btnLogout.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");

        sidebar.getChildren().addAll(lblTitle, new Region(), btnDash, btnFixed, btnBudget, btnGoals, btnDebts, btnTrans, new Region(), btnSave, btnLogout);

        // --- TOP BAR (Hamburger) ---
        Button btnMenu = new Button("☰");
        btnMenu.setStyle("-fx-font-size: 16px; -fx-background-color: transparent;");
        btnMenu.setOnAction(e -> toggleSidebar());
        
        HBox topBar = new HBox(10, btnMenu);
        topBar.setPadding(new Insets(10));
        topBar.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        root.setLeft(sidebar);
        root.setTop(topBar);
        root.setCenter(new View_Dashboard()); // Default view

        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("Smart Finance Manager");
        stage.setScene(scene);
        stage.show();
    }

    private void setCenter(Node node) {
        root.setCenter(node);
    }

    private Button createNavBtn(String text, Runnable action) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPadding(new Insets(10));
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
        
        // Hover Animation
        btn.setOnMouseEntered(e -> {
            btn.setStyle("-fx-background-color: " + COL_ACCENT + "; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
            addHoverScale(btn, true);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER_LEFT;");
            addHoverScale(btn, false);
        });
        
        btn.setOnAction(e -> action.run());
        return btn;
    }

    private void addHoverScale(Node node, boolean enter) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setToX(enter ? 1.05 : 1.0);
        st.setToY(enter ? 1.05 : 1.0);
        st.play();
    }

    private void toggleSidebar() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(300), sidebar);
        if (isSidebarOpen) {
            tt.setToX(-200);
            tt.setOnFinished(e -> root.setLeft(null)); // Remove to allow content to expand
        } else {
            root.setLeft(sidebar);
            sidebar.setTranslateX(-200);
            tt.setToX(0);
        }
        tt.play();
        isSidebarOpen = !isSidebarOpen;
    }
    
}