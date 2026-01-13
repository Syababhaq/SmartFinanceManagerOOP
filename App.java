package com.mycompany.oopfinal;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        // Load the list of users so Login works
        DataStore.loadUserRegistry(); 
        
        View_Login loginScreen = new View_Login(stage);
        loginScreen.show();
    }

    @Override
    public void stop() {
        // Save everything when the app closes (Safety check)
        System.out.println("Application closing... Saving data.");
        DataStore.saveData();
    }

    public static void main(String[] args) {
        launch();
    }
}
