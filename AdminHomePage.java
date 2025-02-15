package application;

import java.sql.SQLException;
import java.util.List;

import databasePart1.DatabaseHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.UUID;


public class AdminHomePage {
    /**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    private DatabaseHelper databaseHelper = new DatabaseHelper();

    public AdminHomePage() { }

    public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	VBox layout = new VBox(20);
        
        VBox textFields = new VBox();
        VBox roleSection = new VBox(10);
        VBox listSection = new VBox(10);
        HBox mainLayout = new HBox(20);

        Label roleLabel = new Label("Establish role");
        Label listLabel = new Label("Roles List");
        Label errorLabel = new Label();

        Button submitButton = new Button("Add Role");
        Button deleteButton = new Button("Delete Role");
        Button listUsersButton = new Button("List All Users");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #ddd; -fx-text-fill: black; -fx-font-size: 14px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        layout.setStyle("-fx-background-color: #a6b4be;"); 
        logoutButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
        submitButton.setMaxWidth(250);
        submitButton.setStyle("-fx-background-color: #2f3131; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 25px; -fx-background-radius: 25px;");

        deleteButton.setMaxWidth(250);
        deleteButton.setStyle("-fx-background-color: #2f3131; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 25px; -fx-background-radius: 25px;");

        listUsersButton.setMaxWidth(160);
        listUsersButton.setStyle("-fx-background-color: #2f3131; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 25px; -fx-background-radius: 25px;");

        listUsersButton.setOnAction(event -> {
            showUserList(primaryStage);
        });

        
        ListView<String> rolesListView = new ListView<>();

        rolesListView.setMaxHeight(150);
        mainLayout.setAlignment(Pos.CENTER);
        roleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        roleSection.setAlignment(Pos.TOP_CENTER);
        rolesListView.setPrefHeight(325);
        rolesListView.setMaxHeight(325);
        rolesListView.setMinHeight(325);
        listSection.setAlignment(Pos.TOP_CENTER);
        listLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        listSection.getChildren().addAll(listLabel, rolesListView, deleteButton);
        layout.setStyle("-fx-padding: 20; -fx-spacing: 10;");
        layout.setAlignment(Pos.BOTTOM_CENTER);
        textFields.setStyle("-fx-alignment: center; -fx-padding: 20; -fx-spacing: 10;");
        textFields.setAlignment(Pos.CENTER);
        textFields.setMinHeight(300);
        try {
            List<String> storedRoles = databaseHelper.getAllRoles();
            rolesListView.getItems().addAll(storedRoles);
        } catch (SQLException e) {
            System.out.println("Error loading roles: " + e.getMessage());
        }

        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // label to display the welcome message for the admin
        Label adminLabel = new Label("Admin Panel");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        layout.setStyle("-fx-background-color: #a6b4be;"); 
        layout.getChildren().add(adminLabel);
        Scene adminScene = new Scene(layout, 800, 400);
        primaryStage.setScene(adminScene);
        TextField estRole = new TextField();
        estRole.setPromptText("Enter new role");
        estRole.setMaxWidth(300);
        textFields.getChildren().addAll(roleLabel, estRole, errorLabel, submitButton);
        layout.getChildren().add(textFields);

        // DELETE BUTTON LOGIC
        deleteButton.setOnAction(e -> {
            String selectedRole = rolesListView.getSelectionModel().getSelectedItem();
            if (selectedRole != null) {
                try {
                    databaseHelper.deleteRole(selectedRole);
                    System.out.println("Role \"" + selectedRole + "\" Has Been Deleted");
                    rolesListView.getItems().remove(selectedRole);
                } catch (SQLException e1) {
                    System.out.println("Error: Cannot delete role");
                }
            }
        });

        // ADD ROLE BUTTON LOGIC
        submitButton.setOnAction(e -> {
            String role = estRole.getText().trim();
            if (!role.isEmpty()) {
                try {
                    if (databaseHelper.duplicateCheck(role)) {
                        errorLabel.setText("Role \"" + role + "\" already exists");
                        return;
                    }
                    databaseHelper.addRole(role);
                    rolesListView.getItems().add(role);
                    errorLabel.setText("");
                    System.out.println("New Role Established: " + role);
                    estRole.clear(); // Clears the text field after clicking
                } catch (SQLException e1) {
                    System.out.println("Error: Cannot store role in database");
                    errorLabel.setText("Error: Cannot store role in database");
                    e1.printStackTrace();
                }
            }
        });
        
        
        // random password generator
        Button generatePasswordButton = new Button("Generate One-Time Password");
        generatePasswordButton.setMaxWidth(250);
        generatePasswordButton.setStyle("-fx-background-color: #2f3131; -fx-text-fill: white; -fx-font-size: 14px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        generatePasswordButton.setOnAction(event -> {
            String oneTimePassword = generateRandomPassword(); 
            displayGeneratedPassword(oneTimePassword); 
        });
                
        roleSection.getChildren().addAll(adminLabel, textFields, listUsersButton, generatePasswordButton, logoutButton);
        mainLayout.getChildren().addAll(roleSection, listSection);
        Scene adminScene1 = new Scene(mainLayout, 800, 500);

        // Set the scene to primary stage
        primaryStage.setScene(adminScene1);
    }

    private void showUserList(Stage primaryStage) {
        List<User> users = databaseHelper.getAllUsers(); // Fetch users from database

        if (users == null || users.isEmpty()) { // If no users exist, show alert
            Alert noUsersAlert = new Alert(Alert.AlertType.INFORMATION);
            noUsersAlert.setTitle("No Users");
            noUsersAlert.setHeaderText(null);
            noUsersAlert.setContentText("There are no users to display.");
            noUsersAlert.showAndWait();
            return;
        }

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("User Management");
        dialog.setHeaderText("Select a user to delete");

        VBox vbox = new VBox(10);
        ToggleGroup toggleGroup = new ToggleGroup(); // Allows selecting one user at a time

        for (User user : users) {
            RadioButton radioButton = new RadioButton(user.getUserName()); // Display each user
            radioButton.setToggleGroup(toggleGroup); // Group radio buttons
            vbox.getChildren().add(radioButton);
        }

        // Add delete and cancel buttons
        ButtonType deleteButton = new ButtonType("Delete User", ButtonBar.ButtonData.OK_DONE);
        ButtonType backButton = new ButtonType("Back", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(deleteButton, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(vbox);

        // Handle user selection and deletion
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == deleteButton) {
                RadioButton selected = (RadioButton) toggleGroup.getSelectedToggle();
                if (selected != null) {
                    return selected.getText(); // Return the selected username
                }
            }
            return null;
        });

        // Perform deletion if a user is selected
        dialog.showAndWait().ifPresent(userName -> {
            if (userName != null) {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Deletion");
                confirmAlert.setHeaderText("Delete User");
                confirmAlert.setContentText("Are you sure you want to delete " + userName + "?");

                // Wait for user confirmation
                confirmAlert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) { // If "Yes" is clicked, delete user
                        if (databaseHelper.deleteUser(userName)) {
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText(null);
                            successAlert.setContentText("User " + userName + " has been deleted.");
                            successAlert.showAndWait();

                            showUserList(primaryStage); // Refresh the list after deletion
                        } else {
                            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                            errorAlert.setTitle("Error");
                            errorAlert.setHeaderText(null);
                            errorAlert.setContentText("Failed to delete user " + userName);
                            errorAlert.showAndWait();
                        }
                    }
                });
            }
        });
    }
    
    private void displayGeneratedPassword(String oneTimePassword) {
        Alert passwordAlert = new Alert(Alert.AlertType.INFORMATION);
        passwordAlert.setTitle("One-Time Password Generated");
        passwordAlert.setHeaderText("Here is the One-Time Password");
        passwordAlert.setContentText("Generated Password: " + oneTimePassword);
        passwordAlert.showAndWait();
        
    }
        
    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8); 
    }    
}
