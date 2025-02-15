package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import databasePart1.DatabaseHelper;

/**
 * This page displays a simple welcome message for the user.
 */

public class UserHomePage {

	private Stage primaryStage;
    private DatabaseHelper databaseHelper = new DatabaseHelper();

	
    public void show(Stage primaryStage) {
    	this.primaryStage = primaryStage; // Store the primary stage for later use
        
        // Create a VBox layout with 20px spacing between elements
        VBox layout = new VBox(20);
        layout.setStyle("-fx-background-color: #a6b4be; -fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2f3131;");
        layout.setAlignment(Pos.CENTER); // Center everything in the VBox

        // Label to display "Hello, User!"
        Label userLabel = new Label("Hello, User!");
        userLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2f3131;");

        // Button to log out
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #ddd; -fx-text-fill: black; -fx-font-size: 14px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        layout.setStyle("-fx-background-color: #a6b4be;"); 
        logoutButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });

        // Add the label and button to the layout
        layout.getChildren().addAll(userLabel, logoutButton);

        // Create the scene and set it to the primary stage
        Scene userScene = new Scene(layout, 800, 400);
        primaryStage.setScene(userScene);
        primaryStage.setTitle("User Page");
        primaryStage.show();
        }
}
