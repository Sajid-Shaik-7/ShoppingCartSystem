import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

// CONCEPT: Interface - LoginWindow implements ActionListener
public class LoginWindow extends JFrame implements ActionListener {

    private Product[]    catalogue;
    private User[]       users;
    private OrderManager orderManager;

    // Form fields
    private JTextField    userField;
    private JPasswordField passField;
    private JButton       customerLoginBtn;
    private JButton       adminLoginBtn;
    private JLabel        statusLabel;
    private String        selectedRole = "User";  // tracks which tab is active

    // ── Constructor ─────────────────────────────────────────────────────────────
    public LoginWindow(Product[] catalogue, User[] users, OrderManager orderManager) {
        this.catalogue    = catalogue;
        this.users        = users;
        this.orderManager = orderManager;

        setTitle("ShopEase — Login");
        setSize(440, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Light grey outer background
        getContentPane().setBackground(UITheme.BG);
        getContentPane().setLayout(new GridBagLayout());

        getContentPane().add(buildCard());
    }

    // ── Build the centred white login card ──────────────────────────────────────
    private JPanel buildCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            BorderFactory.createEmptyBorder(36, 40, 36, 40)));
        card.setPreferredSize(new Dimension(360, 500));

        // ── Logo & store name ────────────────────────────────────────────────
        JLabel logoLbl = new JLabel("\uD83D\uDED2");
        logoLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 44));
        logoLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLbl = UITheme.makeTitleLabel("ShopEase");
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel tagLbl = UITheme.makeSmallLabel("Your everyday online store");
        tagLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(logoLbl);
        card.add(Box.createVerticalStrut(6));
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(2));
        card.add(tagLbl);
        card.add(Box.createVerticalStrut(24));

        // ── Role tabs (Customer / Admin) ─────────────────────────────────────
        card.add(buildRoleTabs());
        card.add(Box.createVerticalStrut(20));

        // ── Username ─────────────────────────────────────────────────────────
        card.add(fieldLabel("Username"));
        card.add(Box.createVerticalStrut(4));
        userField = UITheme.makeTextField(20);
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(userField);
        card.add(Box.createVerticalStrut(14));

        // ── Password ─────────────────────────────────────────────────────────
        card.add(fieldLabel("Password"));
        card.add(Box.createVerticalStrut(4));
        passField = UITheme.makePasswordField(20);
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        // Allow Enter key to trigger login
        passField.addActionListener(this);
        card.add(passField);
        card.add(Box.createVerticalStrut(24));

        // ── Sign-in button ───────────────────────────────────────────────────
        JButton signInBtn = UITheme.makePrimaryBtn("Sign In  →");
        signInBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        signInBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInBtn.addActionListener(this);
        card.add(signInBtn);
        card.add(Box.createVerticalStrut(12));

        // ── Status / error label ─────────────────────────────────────────────
        statusLabel = UITheme.makeSmallLabel("");
        statusLabel.setForeground(new Color(0xC62828));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(statusLabel);

        card.add(Box.createVerticalGlue());

        // ── Hint credentials ─────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setForeground(UITheme.BORDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(12));

        JLabel hint = UITheme.makeSmallLabel(
            "<html><center>Demo — Customer: <b>user1 / user123</b><br>" +
            "Admin: <b>admin / admin123</b></center></html>");
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(hint);

        return card;
    }

    // ── Tab row: Customer | Admin ────────────────────────────────────────────────
    private JPanel buildRoleTabs() {
        JPanel tabs = new JPanel(new GridLayout(1, 2, 0, 0));
        tabs.setBackground(UITheme.BG);
        tabs.setBorder(BorderFactory.createLineBorder(UITheme.BORDER, 1));
        tabs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        customerLoginBtn = new JButton("\uD83D\uDED2  Customer");
        adminLoginBtn    = new JButton("\uD83D\uDEE0  Admin");

        styleTab(customerLoginBtn, true);
        styleTab(adminLoginBtn, false);

        customerLoginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedRole = "User";
                styleTab(customerLoginBtn, true);
                styleTab(adminLoginBtn, false);
            }
        });
        adminLoginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectedRole = "Admin";
                styleTab(adminLoginBtn, true);
                styleTab(customerLoginBtn, false);
            }
        });

        tabs.add(customerLoginBtn);
        tabs.add(adminLoginBtn);
        return tabs;
    }

    private void styleTab(JButton btn, boolean active) {
        btn.setFont(UITheme.FONT_BODY);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        if (active) {
            btn.setBackground(UITheme.PRIMARY);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(UITheme.TEXT_MED);
        }
    }

    // Small bold field label
    private JLabel fieldLabel(String text) {
        JLabel l = UITheme.makeLabel(text, UITheme.FONT_SUBHEAD, UITheme.TEXT_DARK);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    // ── Login Logic ──────────────────────────────────────────────────────────────

    // CONCEPT: Strings - login uses equals() and equalsIgnoreCase()
    public User login(String username, String password, String role)
            throws InvalidLoginException {
        int i = 0;
        while (i < users.length) {
            User u = users[i];
            boolean userOk = u.getUsername().equalsIgnoreCase(username);
            boolean passOk = u.getPassword().equals(password);
            boolean roleOk = u.getRole().equalsIgnoreCase(role);
            if (userOk && passOk && roleOk) return u;
            i = i + 1;
        }
        throw new InvalidLoginException("Invalid username or password.");
    }

    // CONCEPT: Interface - actionPerformed from ActionListener
    public void actionPerformed(ActionEvent e) {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword());
        statusLabel.setText("");
        try {
            User current = login(username, password, selectedRole);
            openNextWindow(current);
        } catch (InvalidLoginException ex) {
            statusLabel.setText(ex.getMessage());
        } finally {
            passField.setText("");
        }
    }

    // Hides login and routes to customer store or admin dashboard.
    public void openNextWindow(User current) {
        setVisible(false);
        if (current.getRole().equalsIgnoreCase("Admin")) {
            AdminWindow admin = new AdminWindow(current, catalogue, orderManager, this);
            admin.setVisible(true);
        } else {
            ShoppingWindow shop = new ShoppingWindow(current, catalogue, orderManager, this);
            shop.setVisible(true);
        }
    }
}
