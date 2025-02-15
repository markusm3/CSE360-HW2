package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {

    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
        // Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin Username");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        PasswordField confirmPasswordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");

        Button setupButton = new Button("Setup");
        setupButton.setStyle("-fx-background-color: #2f3131; -fx-text-fill: black; -fx-font-size: 16px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        setupButton.setMaxWidth(250);


        setupButton.setOnAction(a -> {
            // Retrieve user input
            String userName = userNameField.getText().trim();
            String password = passwordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText();

            // Validate username using UserNameRecognizer
            String userNameError = UserNameRecognizer.checkForValidUserName(userName);

            // Validate password using PasswordEvaluator
            String passwordError = PasswordEvaluator.evaluatePassword(password);

            if (!userNameError.isEmpty()) {
                showAlert("Invalid Username", userNameError);
                return;
            }

            if (!passwordError.isEmpty()) {
                showAlert("Invalid Password", passwordError);
                return;
            }
            
            if (password.equals(confirmPassword)) {
            	// If both validations pass, register the admin in the database
                try {
                    User user = new User(userName, password);
                    user.setMultipleRoles("admin,user");
                    databaseHelper.register(user);
                    System.out.println("Administrator setup completed.");

                    // Navigate to the Welcome Login Page
                    new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                } catch (SQLException e) {
                    System.err.println("Database error: " + e.getMessage());
                    e.printStackTrace();
                    showAlert("Database Error", "An error occurred while registering the admin.");
                }
            }
            else {
            	errorLabel.setText("Passwords do not match!");
    		}	
        });

        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 40; -fx-background-color: #a6b4be;");
        layout.getChildren().addAll(userNameField, passwordField,confirmPasswordField, setupButton, errorLabel);
        
        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }

    /**
     * Utility method to display error alerts.
     *
     * @param title   The title of the alert dialog.
     * @param message The error message to display.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}