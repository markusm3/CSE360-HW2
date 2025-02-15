package application;
import java.util.HashSet;
import java.util.Set;
/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    private String userName;
    private String password;
    private Set<String> roles; // Use a set of strings instead of a string so the user can have more than one role
    // The reason for using sets here is that it is faster when looking up the roles and doesn't accept duplicates

    // Constructor to initialize a new User object with userName, password, and role.
    public User(String userName, String password) {
        this.userName = userName;
        this.password = password;
        this.roles = new HashSet<>(); 
    }
    
    // Look for role inside of set
    public boolean hasRole(String role)
    {
    	if (roles.contains(role))
    	{
    		return true;
    	}
    	else
    	{
    		return false;
    	}
    }
    
    public Set<String> getRolesAsSet()
    {
    	return roles;
    }
    
    
    // Returns all roles separated by a comma, so if user has admin & user roles, it will return [admin,user]
    public String getRolesAsString()
    {
    	return String.join(",", roles);
    }
    
    // This method is used if we want to assign multiple roles with one command instead of calling addRole() multiple times
    // It takes a string of roles that have to be separated by commas like [admin,user,VIP] and then we add each role in a new string array, after that we add every role individual in the roles set
    public void setMultipleRoles(String newRoles)
    {
    	if (newRoles == null || newRoles.trim().isEmpty()) { return; }
    	String[] rolesArray = newRoles.split(","); // Set each slot in the array with a roll
    	for (String role : rolesArray)
    	{
    		roles.add(role.trim()); // Using the .trim() to avoid any extra spaces 
    	}
    }
    
    
    public void addRole(String newRole) { roles.add(newRole); }
    public void removeRole(String role) { roles.remove(role); }
    public String getUserName() { return userName; }
    public String getPassword() { return password; }
}
