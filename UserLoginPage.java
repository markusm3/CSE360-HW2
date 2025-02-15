package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

public class UserLoginPage {
	
    private final DatabaseHelper databaseHelper;

    public UserLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	// Input field for the user's userName, password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        // Label to display error messages
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");


        Button loginButton = new Button("Login");
        loginButton.setStyle("-fx-background-color: #2F3131; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        loginButton.setMaxWidth(250);

        loginButton.setOnAction(a -> {
        	// Retrieve user inputs
            String userName = userNameField.getText();
            String password = passwordField.getText();
            try {
            	User user = new User(userName, password);
            	WelcomeLoginPage welcomeLoginPage = new WelcomeLoginPage(databaseHelper);
            	
            	// Retrieve the user's role from the database using userName
            	String rolesString = databaseHelper.getUserRoles(userName);
            	
            	if (rolesString == null || rolesString.trim().isEmpty())
            	{
            		errorLabel.setText("User account doesn't exist or doesn't have any assigned roles");
            		return;
            	}
            	
            	if(!rolesString.isEmpty()) {
            		user.setMultipleRoles(rolesString);
            		if(databaseHelper.login(user)) {
            			welcomeLoginPage.show(primaryStage,user);
            		}
            		else {
            			// Display an error if the login fails
                        errorLabel.setText("Error logging in");
            		}
            	}
            	else {
            		// Display an error if the account does not exist
                    errorLabel.setText("User account doesn't exist");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            } 
        });
        Button backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #ddd; -fx-text-fill: black; -fx-font-size: 14px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        backButton.setMaxWidth(250);
        backButton.setOnAction(e -> {
        	new SetupLoginSelectionPage(databaseHelper).show(primaryStage); // goes back
        });
        
        Button forgotPasswordButton = new Button("Forgot Password?");
        forgotPasswordButton.setStyle("-fx-background-color: #ddd; -fx-text-fill: black; -fx-font-size: 14px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        forgotPasswordButton.setMaxWidth(250);
        forgotPasswordButton.setOnAction(a -> {
            showForgotPasswordDialog(primaryStage);
        });
    

        VBox layout = new VBox(20);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center; -fx-background-color: #A6B4BE;");
        layout.getChildren().addAll(userNameField, passwordField, loginButton, backButton, forgotPasswordButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("User Login");
        primaryStage.show();
    }

private void showForgotPasswordDialog(Stage primaryStage) {
    Stage resetStage = new Stage();
    resetStage.setTitle("Forgot Password");

    VBox layout = new VBox(10);
    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
    
    Label instructionLabel = new Label("Enter your username to reset your password.");
    TextField usernameField = new TextField();
    usernameField.setPromptText("Username");

    Button resetButton = new Button("Reset Password");
    resetButton.setOnAction(a -> {
        String username = usernameField.getText();
        if (username != null && !username.isEmpty()) {
            resetPassword(username);
            resetStage.close();  
        } else {
            showErrorMessage("Please enter a valid username.");
        }
    });

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(a -> resetStage.close());  

    layout.getChildren().addAll(instructionLabel, usernameField, resetButton, cancelButton);

    Scene resetScene = new Scene(layout, 300, 200);
    resetStage.setScene(resetScene);
    resetStage.show();
}

private void resetPassword(String username) {
    // Check if the username exists 
    boolean userExists = databaseHelper.doesUserExist(username);
    
    if (userExists) {
        
        showInfoMessage("A password reset has been sent to an admin.");
    } else {
        showErrorMessage("Username not found.");
    }
}

private void showErrorMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}

private void showInfoMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Information");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
}