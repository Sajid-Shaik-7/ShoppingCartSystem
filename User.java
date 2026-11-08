// CONCEPT: Class - a login account with username, password and role
public class User {
    private String username;
    private String password;
    private String role;

    // Creates one user account (User or Admin).
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Returns the username.
    public String getUsername() {
        return username;
    }

    // Returns the password.
    public String getPassword() {
        return password;
    }

    // Returns the role: "User" or "Admin".
    public String getRole() {
        return role;
    }
}
