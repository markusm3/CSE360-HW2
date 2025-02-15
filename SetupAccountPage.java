package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);
        userNameField.setStyle("-fx-padding: 10px; -fx-background-radius: 5px; -fx-border-radius: 5px;");


        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        passwordField.setStyle("-fx-padding: 10px; -fx-background-radius: 5px; -fx-border-radius: 5px;");

        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(250);
        confirmPasswordField.setStyle("-fx-padding: 10px; -fx-background-radius: 5px; -fx-border-radius: 5px;");


        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        inviteCodeField.setStyle("-fx-padding: 10px; -fx-background-radius: 5px; -fx-border-radius: 5px;");

        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px; -fx-font-weight: bold;");
        

        Button setupButton = new Button("Setup");
        setupButton.setStyle("-fx-background-color: #2F3131; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        setupButton.setMaxWidth(250);
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            String code = inviteCodeField.getText();
            
            String userNameError = UserNameRecognizer.checkForValidUserName(userName);
            if (!userNameError.isEmpty()) {
                errorLabel.setText(userNameError); // Display username validation error
                return;
            }

            String passwordError = PasswordEvaluator.evaluatePassword(password);
            if (!passwordError.isEmpty()) {
                errorLabel.setText(passwordError); // Display password validation error
                return;
            }
            
            try {
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		
            		if (password.equals(confirmPassword))
            		{
            		
	            		// Validate the invitation code
	            		if(databaseHelper.validateInvitationCode(code)) {
	            			
	            			// Create a new user and register them in the database
			            	User user=new User(userName, password);
			            	user.addRole("user");
			                databaseHelper.register(user);
			                
			             // Navigate to the Welcome Login Page
			                new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
	            		}
	            		else {
	            			errorLabel.setText("Please enter a valid invitation code");
	            		}
            		}
            		else
            		{
            			errorLabel.setText("Passwords do not match!");
            		}
            	}
            	else {
            		errorLabel.setText("This useruserName is taken!!.. Please use another to setup an account");
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


        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center; -fx-background-color: #A6B4BE;");  
        layout.getChildren().addAll(userNameField, passwordField,confirmPasswordField,inviteCodeField, setupButton,backButton, errorLabel);

        Scene scene = new Scene(layout, 800, 500);
        scene.setFill(Color.GREY);  // You can change this to any color you want
        primaryStage.setScene(scene);
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
}
