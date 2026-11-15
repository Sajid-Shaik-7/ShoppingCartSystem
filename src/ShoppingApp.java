
import javax.swing.SwingUtilities;

import model.Product;
import model.User;
import service.OrderManager;
import gui.LoginWindow;
import service.RentalManager;

/**
 * ShopEase - Main Application Class.
 *
 * Responsibilities:
 * 1. Create the product catalogue.
 * 2. Create demo user accounts.
 * 3. Initialize the order manager.
 * 4. Launch the login window.
 *
 * Concepts:
 * 1. Classes and Objects
 * 2. Arrays
 * 3. Methods
 * 4. Swing Event Dispatch Thread
 */
public class ShoppingApp {

    /**
     * Application entry point.
     */
    
public static void main(String[] args) {

    // Create shared application data.
    Product[] catalogue = createCatalogue();

    User[] users = createUsers();

    OrderManager orderManager = new OrderManager();

    // Create one shared rental manager.
    RentalManager rentalManager = new RentalManager();

    // Start Swing UI on the Event Dispatch Thread.
    SwingUtilities.invokeLater(() -> {

        try {

            LoginWindow login = new LoginWindow(
                catalogue,
                users,
                orderManager,
                rentalManager
            );

            login.setVisible(true);

        } catch (Exception ex) {

            ex.printStackTrace();

            javax.swing.JOptionPane.showMessageDialog(
                null,
                "Unable to start ShopEase:\n" + ex.getMessage(),
                "Application Error",
                javax.swing.JOptionPane.ERROR_MESSAGE
            );
        }
    });
}


    // =====================================================
    // PRODUCT CATALOGUE
    // =====================================================

    /**
     * Creates 20 demo products across five categories.
     *
     * @return product catalogue
     */
    public static Product[] createCatalogue() {

        Product[] list = new Product[20];

        // ---------------- ELECTRONICS ----------------

        list[0] = new Product(
            1, "Laptop Pro", 65999, 5,
            "Electronics", "💻"
        );

        list[1] = new Product(
            2, "Smartphone X", 29999, 8,
            "Electronics", "📱"
        );

        list[2] = new Product(
            3, "Wireless Mouse", 799, 20,
            "Electronics", "🖱"
        );

        list[3] = new Product(
            4, "Mechanical Keyboard", 2499, 15,
            "Electronics", "⌨"
        );

        list[4] = new Product(
            5, "Noise-Cancel Earbuds", 3499, 10,
            "Electronics", "🎧"
        );

        // ---------------- FASHION ----------------

        list[5] = new Product(
            6, "Running Shoes", 3299, 12,
            "Fashion", "👟"
        );

        list[6] = new Product(
            7, "Casual T-Shirt", 699, 30,
            "Fashion", "👕"
        );

        list[7] = new Product(
            8, "Denim Jeans", 1799, 18,
            "Fashion", "👖"
        );

        list[8] = new Product(
            9, "Leather Wallet", 1299, 25,
            "Fashion", "👛"
        );

        list[9] = new Product(
            10, "Sports Backpack", 2199, 10,
            "Fashion", "🎒"
        );

        // ---------------- KITCHEN ----------------

        list[10] = new Product(
            11, "Air Fryer", 5499, 7,
            "Kitchen", "🍳"
        );

        list[11] = new Product(
            12, "Coffee Maker", 3299, 9,
            "Kitchen", "☕"
        );

        list[12] = new Product(
            13, "Non-stick Pan Set", 1899, 14,
            "Kitchen", "🥘"
        );

        list[13] = new Product(
            14, "Smart Water Bottle", 699, 40,
            "Kitchen", "🚰"
        );

        list[14] = new Product(
            15, "Blender Pro", 2799, 6,
            "Kitchen", "🥤"
        );

        // ---------------- BOOKS ----------------

        list[15] = new Product(
            16, "Clean Code", 699, 5,
            "Books", "📚"
        );

        list[16] = new Product(
            17, "Java Programming", 549, 20,
            "Books", "☕"
        );

        list[17] = new Product(
            18, "Design Patterns", 799, 8,
            "Books", "📚"
        );

        // ---------------- SPORTS ----------------

        list[18] = new Product(
            19, "Yoga Mat", 1199, 15,
            "Sports", "🧘"
        );

        list[19] = new Product(
            20, "Dumbbell Set 10kg", 2999, 6,
            "Sports", "🏋"
        );

        // =================================================
        // BOOK RENTAL CONFIGURATION
        // =================================================

        // Clean Code
        list[15].setRentable(true);
        list[15].setRentalPricePerDay(10);
        list[15].setRentalStock(3);
        list[15].setSecurityDeposit(200);
        list[15].setMaxRentalDays(60);

        // Java Programming
        list[16].setRentable(true);
        list[16].setRentalPricePerDay(8);
        list[16].setRentalStock(5);
        list[16].setSecurityDeposit(150);
        list[16].setMaxRentalDays(60);

        // Design Patterns
        list[17].setRentable(true);
        list[17].setRentalPricePerDay(12);
        list[17].setRentalStock(2);
        list[17].setSecurityDeposit(250);
        list[17].setMaxRentalDays(45);

        return list;
    }

    // =====================================================
    // DEMO USER ACCOUNTS
    // =====================================================

    /**
     * Creates two demo customer accounts and one admin account.
     *
     * @return user accounts
     */
    public static User[] createUsers() {

        User[] users = new User[3];

        users[0] = new User(
            "user1",
            "user123",
            "User"
        );

        users[1] = new User(
            "user2",
            "user456",
            "User"
        );

        users[2] = new User(
            "admin",
            "admin123",
            "Admin"
        );

        return users;
    }
}
