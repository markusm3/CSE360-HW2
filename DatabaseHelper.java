package databasePart1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import application.User;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");
			

			createTables();  // Create the necessary tables if they don't exist
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}
	
	public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT userName, password, role FROM cse360users"; // Select all users

        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                User user = new User(rs.getString("userName"), rs.getString("password"));
                user.setMultipleRoles(rs.getString("role")); // Set roles properly
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }
// Deletes a user from the database
 public boolean deleteUser(String userName) {
     String query = "DELETE FROM cse360users WHERE userName = ?";
     try (PreparedStatement pstmt = connection.prepareStatement(query)) {
         pstmt.setString(1, userName);
         int affectedRows = pstmt.executeUpdate();
         return affectedRows > 0; //Returns true if a user was deleted
     } catch (SQLException e) {
         e.printStackTrace();
         return false;
     }
 }

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(255))";
		statement.execute(userTable);
		
		String rolesTable = "CREATE TABLE IF NOT EXISTS Roles ("
				+ "rolename VARCHAR(255) PRIMARY KEY)";
		statement.execute(rolesTable);
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	}


	
	// This is to store a new role inside of the database
	public void addRole(String roleName) throws SQLException {
		
		roleName = roleName.toLowerCase();
		
		if (duplicateCheck(roleName)) {return;}
		
		String query = "INSERT INTO Roles (roleName) VALUES (?)";
		
		try (PreparedStatement pstmt = connection.prepareStatement(query)) 
		{
			pstmt.setString(1, roleName);
			pstmt.executeUpdate();
		}
		
	}
	
	public boolean duplicateCheck(String roleName) throws SQLException
	{
		roleName = roleName.toLowerCase();
		
		String query = "SELECT COUNT(*) FROM Roles WHERE roleName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) 
		{
			pstmt.setString(1, roleName);
			ResultSet rs = pstmt.executeQuery();
			// If the count is bigger than 0, then this indicates that there is a duplicate which returns true
			if (rs.next()) {return rs.getInt(1) > 0;}
		}
		return false;
	}
	
	public void deleteRole(String roleName) throws SQLException
	{
		roleName = roleName.toLowerCase();
		
		String query = "DELETE FROM Roles WHERE roleName = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, roleName);
			pstmt.executeUpdate();
		}
		
		String query2 = "UPDATE cse360users SET role = REPLACE(role, ?, '') WHERE role LIKE ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query2)) {
			pstmt.setString(1, roleName);
			pstmt.setString(2, "%" + roleName + "%");
			pstmt.executeUpdate();
		}
		
	}
	
	
	public List<String> getAllRoles() throws SQLException {
		if (connection == null) { connectToDatabase(); }
		List<String> roles = new ArrayList<>();
		String query = "SELECT roleName FROM Roles";
		try (Statement stmt = connection.createStatement(); 
				ResultSet rs = stmt.executeQuery(query))
		{
			while (rs.next()) 
			{
				roles.add(rs.getString("roleName"));
			}
		}
		return roles;
	}
	
	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRolesAsString());
			pstmt.executeUpdate();
			System.out.println("New user has registerd! " + "Username: " + "[" + user.getUserName() + "]" + " Roles" +": " + user.getRolesAsString());
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT role FROM cse360users WHERE userName = ? AND password = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                // User exists, retrieve roles
	                user.setMultipleRoles(rs.getString("role")); // Convert "admin,user" → Set<String>
	                return true;
	            }
			}
		}
		return false;
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRoles(String userName) {
		try {
		    String query = "SELECT role FROM cse360users WHERE userName = ?";
		    PreparedStatement pstmt = connection.prepareStatement(query);
		    pstmt.setString(1, userName);
		    ResultSet rs = pstmt.executeQuery();
		        
		    if (rs.next()) {
		    	return rs.getString("role"); // Return the role if user exists
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
	    String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}

}

