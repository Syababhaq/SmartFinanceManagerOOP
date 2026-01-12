package com.mycompany.oopfinal;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class View_Login {
    private Stage stage;

    public View_Login(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + MainLayout.COL_BG + ";");

        // Title
        Label title = new Label("Smart Finance Manager");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: " + MainLayout.COL_DARK + ";");

        Label subtitle = new Label("Welcome Back");
        subtitle.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 16px;");

        // Inputs
        TextField userField = new TextField();
        userField.setPromptText("Username");
        userField.setMaxWidth(300);
        userField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");

        PasswordField passField = new PasswordField();
        passField.setPromptText("Password");
        passField.setMaxWidth(300);
        passField.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-border-color: #bdc3c7; -fx-border-radius: 5;");

        // Login Button
        Button btnLogin = new Button("Login");
        btnLogin.setStyle("-fx-background-color: " + MainLayout.COL_ACCENT + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        btnLogin.setPrefWidth(150);
        
        // Login Logic
        btnLogin.setOnAction(e -> {
    String user = userField.getText();
    String pass = passField.getText();

    if(user.isEmpty() || pass.isEmpty()) {
        new Alert(Alert.AlertType.ERROR, "Please enter username and password").show();
        return;
    }

    // --- REAL LOGIN LOGIC ---
    boolean isValid = DataStore.getInstance().validateLogin(user, pass);

    if(isValid) {
        System.out.println("Login Successful");
        new MainLayout(stage); // Go to Dashboard
    } else {
        new Alert(Alert.AlertType.ERROR, "Invalid Username or Password").show();
    }
});

        // === REGISTER LINK (NEW) ===
        Hyperlink linkRegister = new Hyperlink("Don't have an account? Register here");
        linkRegister.setStyle("-fx-text-fill: " + MainLayout.COL_ACCENT + "; -fx-font-size: 12px;");
        
        // Switch to Register Screen
        linkRegister.setOnAction(e -> {
            new View_Register(stage).show();
        });

        root.getChildren().addAll(title, subtitle, userField, passField, btnLogin, linkRegister);
        stage.setScene(new Scene(root, 600, 450));
        stage.show();
    }
}
