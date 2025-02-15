package application; 

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import databasePart1.*; //Importing database classes
import java.sql.SQLException; //Added missing import

/**
 * The SetupLoginSelectionPage class allows users to choose between setting up a new account
 * or logging into an existing account. It provides two buttons for navigation to the respective pages.
 */
public class SetupLoginSelectionPage {
    
    private final DatabaseHelper databaseHelper;

    public SetupLoginSelectionPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        try {
            databaseHelper.connectToDatabase(); //Ensures database is connected
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Buttons to select Login / Setup options that redirect to respective pages
        Button setupButton = new Button("SetUp");
        Button loginButton = new Button("Login");
        
        setupButton.setStyle("-fx-background-color: #2F3131; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        setupButton.setMaxWidth(250);
        loginButton.setStyle("-fx-background-color: #2F3131; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        loginButton.setMaxWidth(250);

        setupButton.setOnAction(a -> {
            new SetupAccountPage(databaseHelper).show(primaryStage);
        });
        loginButton.setOnAction(a -> {
            new UserLoginPage(databaseHelper).show(primaryStage);
        });

        VBox layout = new VBox(20);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #A6B4BE;");
        layout.getChildren().addAll(setupButton, loginButton);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}