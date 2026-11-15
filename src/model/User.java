
package model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User represents a registered ShopEase account.
 *
 * Concepts:
 * 1. Encapsulation
 * 2. Constructors and Constructor Overloading
 * 3. Data Validation
 * 4. Object Initialization
 * 5. Account Management
 */
public class User {

    // Unique account identification
    private final String userId;

    // Login information
    private String username;
    private String password;
    private String role;

    // Personal information
    private String fullName;
    private String email;
    private String phone;
    private String address;

    // Account information
    private boolean active;
    private final LocalDateTime createdAt;

    /**
     * Original constructor.
     * Maintains compatibility with existing ShoppingApp.java.
     */
    public User(String username, String password, String role) {
        this(username, password, role, "", "", "", "");
    }

    /**
     * Complete constructor for creating customer accounts.
     */
    public User(String username, String password, String role,
                String fullName, String email,
                String phone, String address) {

        validateRequired(username, "Username");
        validateRequired(password, "Password");
        validateRequired(role, "Role");

        this.userId = UUID.randomUUID().toString();

        this.username = username.trim();
        this.password = password;
        this.role = validateRole(role);

        this.fullName = normalize(fullName);
        this.email = validateEmail(email);
        this.phone = normalize(phone);
        this.address = normalize(address);

        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    // ---------------- GETTERS ----------------

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    /**
     * Temporary legacy method.
     * Will be replaced when LoginWindow is upgraded
     * to use secure password hashing.
     */
    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // ---------------- PROFILE UPDATES ----------------

    public void setFullName(String fullName) {
        this.fullName = normalize(fullName);
    }

    public void setEmail(String email) {
        this.email = validateEmail(email);
    }

    public void setPhone(String phone) {
        this.phone = normalize(phone);
    }

    public void setAddress(String address) {
        this.address = normalize(address);
    }

    // ---------------- ACCOUNT MANAGEMENT ----------------

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAdmin() {
        return role.equalsIgnoreCase("Admin");
    }

    public boolean isCustomer() {
        return role.equalsIgnoreCase("User")
                || role.equalsIgnoreCase("Customer");
    }

    // ---------------- VALIDATION ----------------

    private static void validateRequired(String value, String field) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                field + " cannot be empty."
            );
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static String validateRole(String role) {
        String normalizedRole = role.trim();

        if (!normalizedRole.equalsIgnoreCase("User")
                && !normalizedRole.equalsIgnoreCase("Customer")
                && !normalizedRole.equalsIgnoreCase("Admin")) {

            throw new IllegalArgumentException(
                "Role must be User, Customer or Admin."
            );
        }

        return normalizedRole;
    }

    private static String validateEmail(String email) {
        String value = normalize(email);

        if (!value.isEmpty()
                && !value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {

            throw new IllegalArgumentException(
                "Please enter a valid email address."
            );
        }

        return value;
    }

    // ---------------- UTILITY ----------------

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                ", active=" + active +
                '}';
    }
}
