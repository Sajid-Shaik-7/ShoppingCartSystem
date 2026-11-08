import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

// CONCEPT: Interface - ShoppingWindow implements ActionListener
public class ShoppingWindow extends JFrame implements ActionListener {

    private User         currentUser;
    private Product[]    catalogue;
    private OrderManager orderManager;
    private LoginWindow  loginWindow;
    private ShoppingCart cart;

    // Category filter state
    private String activeCategory = "All";

    // Navbar components
    private JTextField searchField;
    private JLabel     cartBadge;
    private JLabel     cartCountLabel;

    // Category sidebar buttons
    private JButton[] catBtns;
    private static final String[] CATEGORIES =
        { "All", "Electronics", "Fashion", "Kitchen", "Books", "Sports" };

    // Product grid panel (cards)
    private JPanel productGrid;

    // Cart sidebar components
    private JPanel      cartListPanel;
    private JLabel      subtotalValLabel;
    private JLabel      discountValLabel;
    private JLabel      totalValLabel;
    private JTextField  couponField;
    private JButton     applyBtn;
    private JButton     checkoutBtn;
    private double      appliedDiscount = 0;

    // ── Constructor ─────────────────────────────────────────────────────────────
    public ShoppingWindow(User currentUser, Product[] catalogue,
                          OrderManager orderManager, LoginWindow loginWindow) {
        this.currentUser  = currentUser;
        this.catalogue    = catalogue;
        this.orderManager = orderManager;
        this.loginWindow  = loginWindow;
        this.cart         = new ShoppingCart();

        setTitle("ShopEase — Store");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG);

        setLayout(new BorderLayout());
        add(buildNavbar(),   BorderLayout.NORTH);
        add(buildBody(),     BorderLayout.CENTER);

        refreshCart();
    }

    // ── Navbar ───────────────────────────────────────────────────────────────────
    private JPanel buildNavbar() {
        JPanel nav = new JPanel(new BorderLayout());
        nav.setBackground(UITheme.NAV_BG);
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        // Left: logo
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(UITheme.NAV_BG);
        JLabel logo = new JLabel("\uD83D\uDED2");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));
        JLabel storeName = UITheme.makeHeadingLabel("ShopEase");
        storeName.setForeground(UITheme.PRIMARY);
        left.add(logo);
        left.add(storeName);

        // Centre: search bar
        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        center.setBackground(UITheme.NAV_BG);
        searchField = UITheme.makeTextField(28);
        searchField.setPreferredSize(new Dimension(340, 34));
        JButton searchBtn = UITheme.makePrimaryBtn("\uD83D\uDD0D");
        searchBtn.setPreferredSize(new Dimension(44, 34));
        searchBtn.setToolTipText("Search products");
        searchBtn.addActionListener(e -> refreshProducts());
        // Live search on type
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) { refreshProducts(); }
        });
        center.add(searchField);
        center.add(searchBtn);

        // Right: cart icon + badge, user, logout
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(UITheme.NAV_BG);

        JPanel cartIconPanel = new JPanel(null);
        cartIconPanel.setBackground(UITheme.NAV_BG);
        cartIconPanel.setPreferredSize(new Dimension(40, 36));
        JLabel cartIcon = new JLabel("\uD83D\uDED2");
        cartIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        cartIcon.setBounds(0, 4, 30, 28);
        cartBadge = new JLabel("0");
        cartBadge.setFont(UITheme.FONT_SMALL);
        cartBadge.setForeground(Color.WHITE);
        cartBadge.setBackground(new Color(0xE53935));
        cartBadge.setOpaque(true);
        cartBadge.setHorizontalAlignment(SwingConstants.CENTER);
        cartBadge.setBounds(16, 0, 18, 18);
        cartIconPanel.add(cartIcon);
        cartIconPanel.add(cartBadge);

        JLabel userLbl = UITheme.makeBodyLabel("Hi, " + currentUser.getUsername());
        JButton logoutBtn = UITheme.makeSecondaryBtn("Logout");
        logoutBtn.setPreferredSize(new Dimension(80, 30));
        logoutBtn.addActionListener(e -> doLogout());

        right.add(cartIconPanel);
        right.add(userLbl);
        right.add(logoutBtn);

        nav.add(left, BorderLayout.WEST);
        nav.add(center, BorderLayout.CENTER);
        nav.add(right, BorderLayout.EAST);
        return nav;
    }

    // ── Body = sidebar + product grid + cart panel ───────────────────────────────
    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 0));
        body.setBackground(UITheme.BG);
        body.add(buildSidebar(),     BorderLayout.WEST);
        body.add(buildProductArea(), BorderLayout.CENTER);
        body.add(buildCartPanel(),   BorderLayout.EAST);
        return body;
    }

    // ── Left sidebar: category filters ─────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER),
            BorderFactory.createEmptyBorder(16, 0, 16, 0)));
        sidebar.setPreferredSize(new Dimension(170, 0));

        JLabel catTitle = UITheme.makeLabel("  Categories", UITheme.FONT_SUBHEAD, UITheme.TEXT_MUTED);
        catTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(catTitle);
        sidebar.add(Box.createVerticalStrut(8));

        catBtns = new JButton[CATEGORIES.length];
        for (int i = 0; i < CATEGORIES.length; i++) {
            String cat = CATEGORIES[i];
            String icon = getCatIcon(cat);
            JButton btn = UITheme.makeNavBtn("  " + icon + "  " + cat);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            final String c = cat;
            btn.addActionListener(e -> {
                activeCategory = c;
                refreshProducts();
                updateCatBtns();
            });
            catBtns[i] = btn;
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        // "My Orders" button at bottom
        sidebar.add(Box.createVerticalGlue());
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(8));

        JButton ordersBtn = UITheme.makeNavBtn("  \uD83D\uDCCB  My Orders");
        ordersBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        ordersBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        ordersBtn.addActionListener(e -> showMyOrders());
        sidebar.add(ordersBtn);

        updateCatBtns();
        return sidebar;
    }

    private String getCatIcon(String cat) {
        if (cat.equals("Electronics")) return "\uD83D\uDCBB";
        if (cat.equals("Fashion"))     return "\uD83D\uDC55";
        if (cat.equals("Kitchen"))     return "\uD83C\uDF73";
        if (cat.equals("Books"))       return "\uD83D\uDCDA";
        if (cat.equals("Sports"))      return "\uD83C\uDFCB";
        return "\uD83C\uDFEA";
    }

    private void updateCatBtns() {
        for (int i = 0; i < catBtns.length; i++) {
            String cat = CATEGORIES[i];
            if (cat.equals(activeCategory)) {
                catBtns[i].setBackground(new Color(0xE3F2FD));
                catBtns[i].setForeground(UITheme.PRIMARY);
                catBtns[i].setFont(UITheme.FONT_SUBHEAD);
            } else {
                catBtns[i].setBackground(UITheme.SIDEBAR_BG);
                catBtns[i].setForeground(UITheme.TEXT_DARK);
                catBtns[i].setFont(UITheme.FONT_BODY);
            }
        }
    }

    // ── Scrollable product card grid ─────────────────────────────────────────────
    private JScrollPane buildProductArea() {
        productGrid = new JPanel(new WrapLayout(FlowLayout.LEFT, 12, 12));
        productGrid.setBackground(UITheme.BG);
        productGrid.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JScrollPane sp = new JScrollPane(productGrid);
        sp.setBorder(BorderFactory.createEmptyBorder());
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getViewport().setBackground(UITheme.BG);
        refreshProducts();
        return sp;
    }

    // Rebuilds the product card grid based on category and search query.
    private void refreshProducts() {
        productGrid.removeAll();
        String query = searchField != null ? searchField.getText().trim().toLowerCase() : "";

        int i = 0;
        while (i < catalogue.length) {
            Product p = catalogue[i];
            boolean catOk  = activeCategory.equals("All") ||
                             p.getCategory().equalsIgnoreCase(activeCategory);
            // CONCEPT: Strings - case-insensitive search with toLowerCase() + contains()
            boolean nameOk = query.isEmpty() || p.getName().toLowerCase().contains(query);
            if (catOk && nameOk) {
                productGrid.add(buildProductCard(p));
            }
            i = i + 1;
        }

        if (productGrid.getComponentCount() == 0) {
            JLabel empty = UITheme.makeBodyLabel("No products found.");
            empty.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
            productGrid.add(empty);
        }

        productGrid.revalidate();
        productGrid.repaint();
    }

    // Builds a single product card with emoji, name, price, Add button.
    private JPanel buildProductCard(Product p) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(UITheme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER, 1),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        card.setPreferredSize(new Dimension(190, 230));

        // Emoji image
        JLabel emojiLbl = new JLabel(p.getEmoji(), SwingConstants.CENTER);
        emojiLbl.setFont(UITheme.FONT_EMOJI);
        emojiLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        emojiLbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        card.add(emojiLbl);
        card.add(Box.createVerticalStrut(10));

        // Product name
        JLabel nameLbl = UITheme.makeLabel(wrapText(p.getName(), 20),
                                           UITheme.FONT_SUBHEAD, UITheme.TEXT_DARK);
        nameLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(4));

        // Category tag
        JLabel catTag = UITheme.makeSmallLabel(p.getCategory());
        catTag.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(catTag);
        card.add(Box.createVerticalStrut(6));

        // Price
        JLabel priceLbl = UITheme.makeLabel(
            String.format("\u20B9 %,.0f", p.getPrice()),
            UITheme.FONT_HEADING, UITheme.PRIMARY);
        priceLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(priceLbl);
        card.add(Box.createVerticalStrut(4));

        // Stock indicator
        String stockText = p.getStock() == 0 ? "Out of Stock" :
                           p.getStock() < 4  ? "Only " + p.getStock() + " left" :
                           "In Stock";
        Color stockColor = p.getStock() == 0 ? UITheme.WARN :
                           p.getStock() < 4  ? new Color(0xE65100) : UITheme.SUCCESS;
        JLabel stockLbl = UITheme.makeLabel(stockText, UITheme.FONT_SMALL, stockColor);
        stockLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(stockLbl);
        card.add(Box.createVerticalStrut(10));

        // Add to Cart button
        if (p.getStock() > 0) {
            JButton addBtn = UITheme.makePrimaryBtn("Add to Cart");
            addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            addBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
            addBtn.addActionListener(e -> doAdd(p));
            card.add(addBtn);
        } else {
            JLabel outLbl = UITheme.makeLabel("Unavailable",
                UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
            outLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(outLbl);
        }

        return card;
    }

    // ── Right Cart Panel ─────────────────────────────────────────────────────────
    private JPanel buildCartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UITheme.CARD);
        panel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, UITheme.BORDER));
        panel.setPreferredSize(new Dimension(280, 0));

        // Header
        JLabel header = UITheme.makeHeadingLabel(" \uD83D\uDED2  Cart");
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        panel.add(header, BorderLayout.NORTH);

        // Cart item list
        cartListPanel = new JPanel();
        cartListPanel.setLayout(new BoxLayout(cartListPanel, BoxLayout.Y_AXIS));
        cartListPanel.setBackground(UITheme.CARD);
        JScrollPane cartScroll = new JScrollPane(cartListPanel);
        cartScroll.setBorder(BorderFactory.createEmptyBorder());
        cartScroll.getVerticalScrollBar().setUnitIncrement(10);
        panel.add(cartScroll, BorderLayout.CENTER);

        // Footer: coupon + totals + checkout
        panel.add(buildCartFooter(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildCartFooter() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setBackground(UITheme.CARD);
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(10, 12, 12, 12)));

        // Coupon row
        JPanel couponRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        couponRow.setBackground(UITheme.CARD);
        couponField = UITheme.makeTextField(10);
        couponField.setPreferredSize(new Dimension(130, 30));
        couponField.setToolTipText("Enter coupon code");
        applyBtn = UITheme.makeAccentBtn("Apply");
        applyBtn.setPreferredSize(new Dimension(60, 30));
        applyBtn.addActionListener(e -> applyCoupon());
        couponRow.add(UITheme.makeSmallLabel("Coupon:"));
        couponRow.add(couponField);
        couponRow.add(applyBtn);
        footer.add(couponRow);
        footer.add(Box.createVerticalStrut(8));

        // Subtotal
        JPanel subRow = totalsRow("Subtotal:", "subtotal");
        footer.add(subRow);
        footer.add(Box.createVerticalStrut(2));

        // Discount
        JPanel discRow = totalsRow("Discount:", "discount");
        footer.add(discRow);
        footer.add(Box.createVerticalStrut(2));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        footer.add(sep);
        footer.add(Box.createVerticalStrut(4));

        // Total
        JPanel totalRow = totalsRow("Total:", "total");
        footer.add(totalRow);
        footer.add(Box.createVerticalStrut(10));

        // Checkout button
        checkoutBtn = UITheme.makeSuccessBtn("Checkout \u2714");
        checkoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        checkoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkoutBtn.addActionListener(e -> doCheckout());
        footer.add(checkoutBtn);

        return footer;
    }

    // Returns a subtotal / discount / total row panel and stores the value label.
    private JPanel totalsRow(String labelText, String type) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(UITheme.CARD);
        JLabel lbl = UITheme.makeBodyLabel(labelText);
        JLabel val = UITheme.makeBodyLabel("\u20B9 0");
        val.setHorizontalAlignment(SwingConstants.RIGHT);
        if (type.equals("total")) {
            lbl.setFont(UITheme.FONT_SUBHEAD);
            val.setFont(UITheme.FONT_SUBHEAD);
            val.setForeground(UITheme.PRIMARY);
        }
        if (type.equals("discount")) val.setForeground(UITheme.SUCCESS);
        row.add(lbl, BorderLayout.WEST);
        row.add(val, BorderLayout.EAST);

        if (type.equals("subtotal")) subtotalValLabel = val;
        else if (type.equals("discount")) discountValLabel = val;
        else totalValLabel = val;

        return row;
    }

    // ── Cart Operations ──────────────────────────────────────────────────────────

    // CONCEPT: Methods - doAdd calls overloaded addToCart (qty 1 by default)
    private void doAdd(Product p) {
        try {
            cart.addToCart(p);
            refreshCart();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Removes an item from the cart (called by cart-row delete button).
    private void doRemove(int index) {
        try {
            cart.removeItem(index);
            appliedDiscount = 0;
            couponField.setText("");
            refreshCart();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Updates totals panel after any cart change.
    private void refreshCart() {
        cartListPanel.removeAll();

        CartItem[] items = cart.getItems();
        int count = cart.getItemCount();

        if (count == 0) {
            JLabel empty = UITheme.makeBodyLabel("Your cart is empty");
            empty.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            cartListPanel.add(empty);
        }

        int i = 0;
        while (i < count) {
            cartListPanel.add(buildCartRow(items[i], i));
            i = i + 1;
        }

        double sub   = cart.getSubtotal();
        double disc  = appliedDiscount;
        double total = sub - disc;

        subtotalValLabel.setText(String.format("\u20B9 %,.0f", sub));
        discountValLabel.setText(disc > 0 ?
            String.format("- \u20B9 %,.0f", disc) : "\u20B9 0");
        totalValLabel.setText(String.format("\u20B9 %,.0f", total));

        // Update cart badge
        cartBadge.setText(String.valueOf(count));

        cartListPanel.revalidate();
        cartListPanel.repaint();
        refreshProducts();      // refresh stock badges
    }

    // Builds a single cart row with name, qty, total and remove button.
    private JPanel buildCartRow(CartItem item, int index) {
        JPanel row = new JPanel(new BorderLayout(6, 2));
        row.setBackground(UITheme.CARD);
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setBackground(UITheme.CARD);
        JLabel nameLbl = UITheme.makeLabel(item.getProduct().getName(),
                                           UITheme.FONT_SUBHEAD, UITheme.TEXT_DARK);
        JLabel detLbl  = UITheme.makeLabel(
            "Qty " + item.getQuantity() + "  ×  " +
            String.format("\u20B9 %,.0f", item.getProduct().getPrice()),
            UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        info.add(nameLbl);
        info.add(detLbl);

        JLabel totalLbl = UITheme.makeLabel(
            String.format("\u20B9 %,.0f", item.getLineTotal()),
            UITheme.FONT_SUBHEAD, UITheme.PRIMARY);

        JButton removeBtn = new JButton("\u2715");
        removeBtn.setFont(UITheme.FONT_SMALL);
        removeBtn.setForeground(UITheme.TEXT_MUTED);
        removeBtn.setBackground(UITheme.CARD);
        removeBtn.setBorderPainted(false);
        removeBtn.setFocusPainted(false);
        removeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        final int idx = index;
        removeBtn.addActionListener(e -> doRemove(idx));

        JPanel right = new JPanel(new BorderLayout(0, 4));
        right.setBackground(UITheme.CARD);
        right.add(removeBtn, BorderLayout.NORTH);
        right.add(totalLbl,  BorderLayout.SOUTH);

        row.add(info,  BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    // ── Coupon / Checkout ────────────────────────────────────────────────────────

    // CONCEPT: Strings - coupon code comparison with equalsIgnoreCase()
    private void applyCoupon() {
        String code = couponField.getText().trim();
        double sub  = cart.getSubtotal();
        if (code.equalsIgnoreCase("SAVE10")) {
            appliedDiscount = sub * 0.10;
            showInfo("Coupon applied! 10% off.");
        } else if (code.equalsIgnoreCase("FLAT200")) {
            appliedDiscount = Math.min(200, sub);
            showInfo("Coupon applied! \u20B9200 off.");
        } else if (code.isEmpty()) {
            showError("Please enter a coupon code.");
            return;
        } else {
            appliedDiscount = 0;
            showError("Invalid coupon code.\nTry SAVE10 or FLAT200.");
            return;
        }
        refreshCart();
    }

    // CONCEPT: Exception Handling - try/catch/finally in checkout
    private void doCheckout() {
        boolean done = false;
        try {
            // CONCEPT: Custom Exception - EmptyCartException
            if (cart.getItemCount() == 0) {
                throw new EmptyCartException("Cart is empty. Add items before checkout.");
            }
            String coupon    = couponField.getText().trim();
            double grandTotal = cart.getSubtotal() - appliedDiscount;
            String bill      = buildBill(grandTotal);

            reduceStock();
            orderManager.addOrder(currentUser.getUsername(),
                cart.getItemsSummary(), grandTotal);

            cart.clear();
            appliedDiscount = 0;
            couponField.setText("");
            refreshCart();
            showBill(bill);
            done = true;

        } catch (EmptyCartException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            showError(ex.getMessage());
        } finally {
            if (done) {
                JOptionPane.showMessageDialog(this,
                    "Thank you for shopping at ShopEase! \uD83D\uDE0A",
                    "Order Placed", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Builds a formatted bill string.
    private String buildBill(double grandTotal) {
        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("               SHOPEASE ORDER              \n");
        sb.append("============================================\n");
        sb.append(String.format("Customer : %s%n", currentUser.getUsername()));
        sb.append("--------------------------------------------\n");

        CartItem[] items = cart.getItems();
        int i = 0;
        while (i < cart.getItemCount()) {
            CartItem line = items[i];
            sb.append(String.format("%-20s x%-3d %8.0f%n",
                line.getProduct().getName(),
                line.getQuantity(),
                line.getLineTotal()));
            i = i + 1;
        }

        sb.append("--------------------------------------------\n");
        sb.append(String.format("%-24s %8.0f%n", "Subtotal:", cart.getSubtotal()));
        if (appliedDiscount > 0) {
            sb.append(String.format("%-24s %8.0f%n", "Discount:", -appliedDiscount));
        }
        sb.append("============================================\n");
        sb.append(String.format("%-24s %8.0f%n", "TOTAL (Rs.):", grandTotal));
        sb.append("============================================\n");
        sb.append("         Thank you, shop again!            \n");
        return sb.toString();
    }

    private void showBill(String bill) {
        JTextArea area = new JTextArea(bill, 16, 36);
        area.setFont(UITheme.FONT_MONO);
        area.setEditable(false);
        area.setBackground(UITheme.CARD);
        JOptionPane.showMessageDialog(this, new JScrollPane(area),
            "Your Order Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    // Reduces stock of each product in the cart after checkout.
    private void reduceStock() {
        CartItem[] items = cart.getItems();
        int i = 0;
        while (i < cart.getItemCount()) {
            Product p = items[i].getProduct();
            p.setStock(p.getStock() - items[i].getQuantity());
            i = i + 1;
        }
    }

    // Shows this user's past orders in a dialog.
    private void showMyOrders() {
        StringBuilder sb = new StringBuilder();
        int i = 0, found = 0;
        while (i < orderManager.getOrderCount()) {
            Order o = orderManager.getOrders()[i];
            if (o.getCustomerName().equalsIgnoreCase(currentUser.getUsername())) {
                sb.append(String.format("Order #%d   |   Rs. %,.0f   |   %s%n",
                    o.getOrderId(), o.getGrandTotal(), o.getStatus()));
                found = found + 1;
            }
            i = i + 1;
        }
        if (found == 0) sb.append("No orders placed yet.");
        JOptionPane.showMessageDialog(this, sb.toString(),
            "My Orders", JOptionPane.INFORMATION_MESSAGE);
    }

    // Logout and return to login screen.
    private void doLogout() {
        dispose();
        loginWindow.setVisible(true);
    }

    // Wraps long product names.
    private String wrapText(String text, int maxLen) {
        return text.length() > maxLen ? text.substring(0, maxLen - 1) + "…" : text;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    // CONCEPT: Interface - required by ActionListener (not directly used here)
    public void actionPerformed(ActionEvent e) { }
}
