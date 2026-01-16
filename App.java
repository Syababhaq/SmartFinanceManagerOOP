package com.mycompany.oopfinal;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {

    private DataStore dataStore;

    @Override
    public void start(Stage stage) {
        dataStore = new DataStore();

        dataStore.loadUserRegistry();

        Login loginScreen = new Login(stage, dataStore);
        loginScreen.show();
    }

    @Override
    public void stop() {
        System.out.println("Application closing... Saving data.");
        if (dataStore != null) {
            dataStore.saveData();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
