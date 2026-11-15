
package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.*;

import model.Product;
import model.User;
import service.OrderManager;
import service.RentalManager;
import utils.UITheme;
import exception.InvalidLoginException;

/**
 * ShopEase Login Window.
 *
 * Provides separate login access for:
 * 1. Customers
 * 2. Administrators
 *
 * Concepts:
 * 1. Inheritance
 * 2. Interface Implementation
 * 3. Event Handling
 * 4. Exception Handling
 * 5. Encapsulation
 */
public class LoginWindow extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    // Shared application data.
    // Catalogue is not final because AdminWindow can update it.
    private Product[] catalogue;
    private final User[] users;
    private final OrderManager orderManager;
    private final RentalManager rentalManager;

    // Login form components.
    private JTextField userField;
    private JPasswordField passField;

    private JButton customerLoginBtn;
    private JButton adminLoginBtn;
    private JButton signInBtn;

    private JLabel statusLabel;

    // Currently selected login role.
    private String selectedRole = "User";

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    /**
     * Backward-compatible constructor.
     * Creates a rental manager when one is not supplied by the application.
     */
    public LoginWindow(
            Product[] catalogue,
            User[] users,
            OrderManager orderManager) {

        this(catalogue, users, orderManager, new RentalManager());
    }

    /**
     * Creates the login window with shared application managers.
     */
    public LoginWindow(
            Product[] catalogue,
            User[] users,
            OrderManager orderManager,
            RentalManager rentalManager) {

        if (catalogue == null || users == null
                || orderManager == null || rentalManager == null) {
            throw new IllegalArgumentException(
                "Application data cannot be null."
            );
        }

        this.catalogue = catalogue;
        this.users = users;
        this.orderManager = orderManager;
        this.rentalManager = rentalManager;

        setTitle("ShopEase - Login");
        setSize(440, 600);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        getContentPane().setBackground(UITheme.BG);
        getContentPane().setLayout(new GridBagLayout());

        getContentPane().add(buildCard());

        // Pressing Enter submits the login form.
        getRootPane().setDefaultButton(signInBtn);
    }

    // =====================================================
    // CATALOGUE UPDATE
    // =====================================================

    /**
     * Updates the shared product catalogue reference.
     * Called when the administrator adds a new product.
     */
    public void updateCatalogue(Product[] newCatalogue) {

        if (newCatalogue == null) {
            throw new IllegalArgumentException(
                "Product catalogue cannot be null."
            );
        }

        this.catalogue = newCatalogue;
    }

    /**
     * Returns the shared rental manager.
     * Customer and admin windows will use this same manager.
     */
    public RentalManager getRentalManager() {
        return rentalManager;
    }

    // =====================================================
    // LOGIN CARD
    // =====================================================

    /**
     * Builds the centered login card.
     */
    private JPanel buildCard() {

        JPanel card = new JPanel();

        card.setLayout(
            new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBackground(UITheme.CARD);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(30, 36, 30, 36)
            )
        );

        card.setPreferredSize(new Dimension(360, 520));

        // Logo.
        JLabel logoLabel = new JLabel("\uD83D\uDED2");

        logoLabel.setFont(
            new Font("Segoe UI Emoji", Font.PLAIN, 44)
        );

        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Application name.
        JLabel nameLabel = UITheme.makeTitleLabel("ShopEase");

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tagline.
        JLabel tagLabel = UITheme.makeSmallLabel(
            "Smart Shopping. Better Savings."
        );

        tagLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logoLabel);
        card.add(Box.createVerticalStrut(5));

        card.add(nameLabel);
        card.add(Box.createVerticalStrut(3));

        card.add(tagLabel);
        card.add(Box.createVerticalStrut(22));

        // Customer/Admin tabs.
        card.add(buildRoleTabs());

        card.add(Box.createVerticalStrut(20));

        // Username.
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(5));

        userField = UITheme.makeTextField(20);

        userField.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 38)
        );

        userField.addActionListener(this);

        card.add(userField);
        card.add(Box.createVerticalStrut(14));

        // Password.
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(5));

        passField = UITheme.makePasswordField(20);

        passField.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 38)
        );

        passField.addActionListener(this);

        card.add(passField);
        card.add(Box.createVerticalStrut(22));

        // Sign-in button.
        signInBtn = UITheme.makePrimaryBtn("Sign In  \u2192");

        signInBtn.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 42)
        );

        signInBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        signInBtn.addActionListener(this);

        card.add(signInBtn);
        card.add(Box.createVerticalStrut(12));

        // Error/status message.
        statusLabel = UITheme.makeSmallLabel("");

        statusLabel.setForeground(UITheme.DANGER);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel.setPreferredSize(new Dimension(280, 25));
        statusLabel.setMaximumSize(new Dimension(280, 35));

        card.add(statusLabel);

        card.add(Box.createVerticalGlue());

        // Separator.
        JSeparator separator = new JSeparator();

        separator.setForeground(UITheme.BORDER);

        separator.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 1)
        );

        card.add(separator);
        card.add(Box.createVerticalStrut(12));

        // Demo credentials.
        JLabel hint = UITheme.makeSmallLabel(
            "<html><center>"
            + "Demo Customer: <b>user1 / user123</b><br>"
            + "Demo Admin: <b>admin / admin123</b>"
            + "</center></html>"
        );

        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(hint);

        return card;
    }

    // =====================================================
    // ROLE SELECTION
    // =====================================================

    /**
     * Creates Customer and Admin login tabs.
     */
    private JPanel buildRoleTabs() {

        JPanel tabs = new JPanel(
            new GridLayout(1, 2, 0, 0)
        );

        tabs.setBackground(UITheme.BG);

        tabs.setBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1)
        );

        tabs.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 40)
        );

        customerLoginBtn = new JButton(
            "\uD83D\uDED2  Customer"
        );

        adminLoginBtn = new JButton(
            "\uD83D\uDEE0  Admin"
        );

        styleTab(customerLoginBtn, true);
        styleTab(adminLoginBtn, false);

        // Customer selection.
        customerLoginBtn.addActionListener(e -> {

            selectedRole = "User";

            styleTab(customerLoginBtn, true);
            styleTab(adminLoginBtn, false);

            statusLabel.setText("");
        });

        // Admin selection.
        adminLoginBtn.addActionListener(e -> {

            selectedRole = "Admin";

            styleTab(adminLoginBtn, true);
            styleTab(customerLoginBtn, false);

            statusLabel.setText("");
        });

        tabs.add(customerLoginBtn);
        tabs.add(adminLoginBtn);

        return tabs;
    }

    /**
     * Styles selected and unselected role tabs.
     */
    private void styleTab(JButton button, boolean active) {

        button.setFont(UITheme.FONT_BODY);

        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);

        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        if (active) {

            button.setBackground(UITheme.PRIMARY);
            button.setForeground(Color.WHITE);

        } else {

            button.setBackground(Color.WHITE);
            button.setForeground(UITheme.TEXT_MED);
        }
    }

    // =====================================================
    // FORM HELPERS
    // =====================================================

    /**
     * Creates a form field label.
     */
    private JLabel fieldLabel(String text) {

        JLabel label = UITheme.makeLabel(
            text,
            UITheme.FONT_SUBHEAD,
            UITheme.TEXT_DARK
        );

        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        return label;
    }

    // =====================================================
    // LOGIN LOGIC
    // =====================================================

    /**
     * Validates username, password and selected role.
     *
     * @param username entered username
     * @param password entered password
     * @param role selected role
     * @return authenticated user
     * @throws InvalidLoginException if credentials are invalid
     */
    public User login(
            String username,
            String password,
            String role) throws InvalidLoginException {

        if (username == null || username.trim().isEmpty()
                || password == null || password.isEmpty()
                || role == null || role.trim().isEmpty()) {

            throw new InvalidLoginException(
                "Please enter your username and password."
            );
        }

        for (User user : users) {

            if (user == null) {
                continue;
            }

            boolean usernameMatches =
                user.getUsername().equalsIgnoreCase(username.trim());

            boolean passwordMatches =
                user.getPassword().equals(password);

            boolean roleMatches =
                user.getRole().equalsIgnoreCase(role.trim());

            if (usernameMatches && passwordMatches && roleMatches) {

                if (!user.isActive()) {

                    throw new InvalidLoginException(
                        "Your account is currently inactive."
                    );
                }

                return user;
            }
        }

        throw new InvalidLoginException(
            "Invalid username or password."
        );
    }

    // =====================================================
    // EVENT HANDLING
    // =====================================================

    /**
     * Handles login button and Enter key actions.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        String username = userField.getText().trim();

        char[] passwordChars = passField.getPassword();

        String password = new String(passwordChars);

        statusLabel.setText("");

        signInBtn.setEnabled(false);

        try {

            User currentUser = login(
                username,
                password,
                selectedRole
            );

            openNextWindow(currentUser);

        } catch (InvalidLoginException ex) {

            statusLabel.setText(ex.getMessage());

        } catch (RuntimeException ex) {

            statusLabel.setText(
                "Unable to open your account. Please try again."
            );

            ex.printStackTrace();

        } finally {

            Arrays.fill(passwordChars, '\0');

            passField.setText("");

            signInBtn.setEnabled(true);
        }
    }

    // =====================================================
    // WINDOW NAVIGATION
    // =====================================================

    /**
     * Opens the appropriate window based on user role.
     *
     * Admin -> AdminWindow
     * Customer -> ShoppingWindow
     */
    public void openNextWindow(User currentUser) {

        if (currentUser == null) {
            return;
        }

        if (currentUser.isAdmin()) {

            AdminWindow admin = new AdminWindow(
                currentUser,
                catalogue,
                orderManager,
                this
            );

            admin.setVisible(true);

        } else if (currentUser.isCustomer()) {

            ShoppingWindow shop = new ShoppingWindow(
                currentUser,
                catalogue,
                orderManager,
                this
            );

            shop.setVisible(true);

        } else {

            statusLabel.setText("Invalid account role.");
            return;
        }

        setVisible(false);
    }
}
