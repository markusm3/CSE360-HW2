package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;
import java.util.Set;

/**
 * The WelcomeLoginPage class displays a welcome screen for authenticated users.
 * It allows users to navigate to their respective pages based on their role or quit the application.
 */
public class WelcomeLoginPage {
	
	private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    public void show( Stage primaryStage, User user) {
    	
    	VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 40; -fx-background-color: #a6b4be;");
	    
	    Label welcomeLabel = new Label("Welcome!!");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2f3131;");
        
	    
	    // Button to navigate to the user's respective page based on their role
	    Button continueButton = new Button("Continue to your Page");
        continueButton.setStyle("-fx-background-color: #2f3131; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        continueButton.setMaxWidth(250);

	    continueButton.setOnAction(a -> {
	    	Set<String> roles = user.getRolesAsSet();
	    	System.out.println("[" + user.getUserName() + "]" + " Roles: " + user.getRolesAsString());

	    	if (roles.contains("admin"))
	    	{
	    		new AdminHomePage(databaseHelper).show(primaryStage);	    			
	    	}
	    	
	    	else if (roles.contains("user")) {
		    	new UserHomePage().show(primaryStage);
	    	}

	    });
	    
	    // Button to quit the application
	    Button quitButton = new Button("Quit");
        quitButton.setStyle("-fx-background-color: #ddd; -fx-text-fill: black; -fx-font-size: 16px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        quitButton.setMaxWidth(250);

	    quitButton.setOnAction(a -> {
	    	databaseHelper.closeConnection();
	    	Platform.exit(); // Exit the JavaFX application
	    });
	    
	    // "Invite" button for admin to generate invitation codes
	    if (user.hasRole("admin")) {
            Button inviteButton = new Button("Invite");
            inviteButton.setStyle("-fx-background-color: #ddd; -fx-text-fill: black; -fx-font-size: 16px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
            inviteButton.setMaxWidth(250);

            inviteButton.setOnAction(a -> {
                new InvitationPage().show(databaseHelper, primaryStage);
            });
            layout.getChildren().add(inviteButton);
        }
	    
	    //Button to logout of the page
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-background-color: #2f3131; -fx-text-fill: white; -fx-font-size: 16px; -fx-border-radius: 25px; -fx-background-radius: 25px;");
        logoutButton.setMaxWidth(250);

        logoutButton.setOnAction(a -> {
            databaseHelper.closeConnection();
            new SetupLoginSelectionPage(databaseHelper).show(primaryStage);
        });
        
	    layout.getChildren().addAll(welcomeLabel,continueButton,quitButton,logoutButton);
	    Scene welcomeScene = new Scene(layout, 800, 400);

	    // Set the scene to primary stage
	    primaryStage.setScene(welcomeScene);
	    primaryStage.setTitle("Welcome Page");
    }
}