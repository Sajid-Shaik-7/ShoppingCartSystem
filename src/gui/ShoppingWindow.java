
package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

import javax.swing.*;

import model.User;
import model.Product;
import model.CartItem;
import model.ShoppingCart;
import model.Order;
import model.BookRental;

import service.OrderManager;
import service.BillGenerator;
import service.RentalManager;

import utils.UITheme;
import utils.WrapLayout;

import exception.EmptyCartException;
import exception.InvalidQuantityException;
import exception.OutOfStockException;

/**
 * Main customer shopping window for ShopEase.
 *
 * Features:
 * 1. Product search.
 * 2. Category filtering.
 * 3. Product cards.
 * 4. Product discounts.
 * 5. Shopping cart management.
 * 6. Quantity controls.
 * 7. Coupon application.
 * 8. GST and bill calculation.
 * 9. Checkout and stock management.
 * 10. Customer order history.
 * 11. Book rentals and rental history.
 *
 * Concepts:
 * 1. Inheritance
 * 2. Interface Implementation
 * 3. Event Handling
 * 4. Arrays
 * 5. Exception Handling
 * 6. Encapsulation
 */
public class ShoppingWindow extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    // ================= APPLICATION DATA =================

    private final User currentUser;
    private final Product[] catalogue;
    private final OrderManager orderManager;
    private final LoginWindow loginWindow;
    private final RentalManager rentalManager;

    private final ShoppingCart cart;

    // ================= SHOPPING STATE =================

    private String activeCategory = "All";
    private String appliedCoupon = "";

    // ================= NAVBAR COMPONENTS =================

    private JTextField searchField;
    private JLabel cartBadge;

    // ================= CATEGORY COMPONENTS =================

    private JButton[] catBtns;

    private static final String[] CATEGORIES = {
        "All",
        "Electronics",
        "Fashion",
        "Kitchen",
        "Books",
        "Sports"
    };

    // ================= PRODUCT GRID =================

    private JPanel productGrid;

    // ================= CART COMPONENTS =================

    private JPanel cartListPanel;

    private JLabel subtotalValLabel;
    private JLabel discountValLabel;
    private JLabel gstValLabel;
    private JLabel totalValLabel;

    private JTextField couponField;

    private JButton applyBtn;
    private JButton checkoutBtn;

    // ================= CONSTRUCTOR =================

    public ShoppingWindow(
            User currentUser,
            Product[] catalogue,
            OrderManager orderManager,
            LoginWindow loginWindow) {

        if (currentUser == null
                || catalogue == null
                || orderManager == null
                || loginWindow == null) {

            throw new IllegalArgumentException(
                "Shopping window data cannot be null."
            );
        }

        this.currentUser = currentUser;
        this.catalogue = catalogue;
        this.orderManager = orderManager;
        this.loginWindow = loginWindow;
        this.rentalManager = loginWindow.getRentalManager();

        if (this.rentalManager == null) {
            throw new IllegalArgumentException(
                "Rental manager cannot be null."
            );
        }

        this.cart = new ShoppingCart();

        setTitle("ShopEase - Smart Shopping");
        setSize(1250, 760);
        setMinimumSize(new Dimension(1000, 650));

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(UITheme.BG);

        setLayout(new BorderLayout());

        add(buildNavbar(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        refreshCart();
    }

    // =====================================================
    // NAVBAR
    // =====================================================

    private JPanel buildNavbar() {

        JPanel nav = new JPanel(new BorderLayout());

        nav.setBackground(UITheme.NAV_BG);

        nav.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                    0, 0, 1, 0, UITheme.BORDER
                ),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            )
        );

        // Left: ShopEase logo.
        JPanel left = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 0)
        );

        left.setBackground(UITheme.NAV_BG);

        JLabel logo = new JLabel("\uD83D\uDED2");

        logo.setFont(
            new Font("Segoe UI Emoji", Font.PLAIN, 26)
        );

        JLabel storeName = UITheme.makeHeadingLabel("ShopEase");

        storeName.setForeground(UITheme.PRIMARY);

        left.add(logo);
        left.add(storeName);

        // Center: Search field.
        JPanel center = new JPanel(
            new FlowLayout(FlowLayout.CENTER, 6, 0)
        );

        center.setBackground(UITheme.NAV_BG);

        searchField = UITheme.makeTextField(28);

        searchField.setPreferredSize(
            new Dimension(340, 36)
        );

        searchField.setToolTipText(
            "Search products by name or category"
        );

        JButton searchBtn = UITheme.makePrimaryBtn(
            "\uD83D\uDD0D"
        );

        searchBtn.setPreferredSize(
            new Dimension(44, 36)
        );

        searchBtn.setToolTipText("Search products");

        searchBtn.addActionListener(e -> refreshProducts());

        // Live search.
        searchField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                refreshProducts();
            }
        });

        center.add(searchField);
        center.add(searchBtn);

        // Right: Cart, user and logout.
        JPanel right = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );

        right.setBackground(UITheme.NAV_BG);

        JPanel cartIconPanel = new JPanel(null);

        cartIconPanel.setBackground(UITheme.NAV_BG);

        cartIconPanel.setPreferredSize(
            new Dimension(40, 36)
        );

        JLabel cartIcon = new JLabel("\uD83D\uDED2");

        cartIcon.setFont(
            new Font("Segoe UI Emoji", Font.PLAIN, 22)
        );

        cartIcon.setBounds(0, 4, 30, 28);

        cartBadge = new JLabel("0");

        cartBadge.setFont(UITheme.FONT_SMALL);
        cartBadge.setForeground(Color.WHITE);
        cartBadge.setBackground(UITheme.DANGER);
        cartBadge.setOpaque(true);

        cartBadge.setHorizontalAlignment(
            SwingConstants.CENTER
        );

        cartBadge.setBounds(16, 0, 18, 18);

        cartIconPanel.add(cartIcon);
        cartIconPanel.add(cartBadge);

        JLabel userLabel = UITheme.makeBodyLabel(
            "Hi, " + currentUser.getUsername()
        );

        JButton logoutBtn = UITheme.makeSecondaryBtn("Logout");

        logoutBtn.setPreferredSize(
            new Dimension(80, 30)
        );

        logoutBtn.addActionListener(e -> doLogout());

        right.add(cartIconPanel);
        right.add(userLabel);
        right.add(logoutBtn);

        nav.add(left, BorderLayout.WEST);
        nav.add(center, BorderLayout.CENTER);
        nav.add(right, BorderLayout.EAST);

        return nav;
    }

    // =====================================================
    // MAIN BODY
    // =====================================================

    private JPanel buildBody() {

        JPanel body = new JPanel(new BorderLayout());

        body.setBackground(UITheme.BG);

        body.add(buildSidebar(), BorderLayout.WEST);
        body.add(buildProductArea(), BorderLayout.CENTER);
        body.add(buildCartPanel(), BorderLayout.EAST);

        return body;
    }

    // =====================================================
    // CATEGORY SIDEBAR
    // =====================================================

    private JPanel buildSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setLayout(
            new BoxLayout(sidebar, BoxLayout.Y_AXIS)
        );

        sidebar.setBackground(UITheme.SIDEBAR_BG);

        sidebar.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                    0, 0, 0, 1, UITheme.BORDER
                ),
                BorderFactory.createEmptyBorder(16, 0, 16, 0)
            )
        );

        sidebar.setPreferredSize(
            new Dimension(170, 0)
        );

        JLabel categoryTitle = UITheme.makeLabel(
            "  Categories",
            UITheme.FONT_SUBHEAD,
            UITheme.TEXT_MUTED
        );

        categoryTitle.setAlignmentX(
            Component.LEFT_ALIGNMENT
        );

        sidebar.add(categoryTitle);

        sidebar.add(Box.createVerticalStrut(8));

        catBtns = new JButton[CATEGORIES.length];

        for (int i = 0; i < CATEGORIES.length; i++) {

            String category = CATEGORIES[i];

            JButton btn = UITheme.makeNavBtn(
                "  " + getCatIcon(category) + "  " + category
            );

            btn.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 38)
            );

            btn.setAlignmentX(Component.LEFT_ALIGNMENT);

            btn.addActionListener(e -> {

                activeCategory = category;

                refreshProducts();
                updateCatBtns();
            });

            catBtns[i] = btn;

            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(2));
        }

        // My Orders.
        sidebar.add(Box.createVerticalGlue());

        JSeparator separator = new JSeparator();

        separator.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 1)
        );

        sidebar.add(separator);

        sidebar.add(Box.createVerticalStrut(8));

        JButton ordersBtn = UITheme.makeNavBtn(
            "  \uD83D\uDCCB  My Orders"
        );

        ordersBtn.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 38)
        );

        ordersBtn.setAlignmentX(Component.LEFT_ALIGNMENT);

        ordersBtn.addActionListener(e -> showMyOrders());

        sidebar.add(ordersBtn);

        sidebar.add(Box.createVerticalStrut(4));

        // My Rentals.
        JButton rentalsBtn = UITheme.makeNavBtn(
            "  📚  My Rentals"
        );

        rentalsBtn.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 38)
        );

        rentalsBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        rentalsBtn.addActionListener(e -> showMyRentals());

        sidebar.add(rentalsBtn);

        updateCatBtns();

        return sidebar;
    }

    /**
     * Returns an emoji for each product category.
     */
    private String getCatIcon(String category) {

        switch (category) {

            case "Electronics":
                return "\uD83D\uDCBB";

            case "Fashion":
                return "\uD83D\uDC55";

            case "Kitchen":
                return "\uD83C\uDF73";

            case "Books":
                return "\uD83D\uDCDA";

            case "Sports":
                return "\uD83C\uDFCB";

            default:
                return "\uD83C\uDFEA";
        }
    }

    /**
     * Updates the active category button.
     */
    private void updateCatBtns() {

        if (catBtns == null) {
            return;
        }

        for (int i = 0; i < catBtns.length; i++) {

            boolean active =
                CATEGORIES[i].equals(activeCategory);

            if (active) {

                catBtns[i].setBackground(UITheme.SELECT_BG);
                catBtns[i].setForeground(UITheme.PRIMARY);
                catBtns[i].setFont(UITheme.FONT_SUBHEAD);

            } else {

                catBtns[i].setBackground(UITheme.SIDEBAR_BG);
                catBtns[i].setForeground(UITheme.TEXT_DARK);
                catBtns[i].setFont(UITheme.FONT_BODY);
            }
        }
    }

    // =====================================================
    // PRODUCT AREA
    // =====================================================

    private JScrollPane buildProductArea() {

        productGrid = new JPanel(
            new WrapLayout(FlowLayout.LEFT, 12, 12)
        );

        productGrid.setBackground(UITheme.BG);

        productGrid.setBorder(
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );

        JScrollPane scrollPane = new JScrollPane(productGrid);

        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        scrollPane.getViewport().setBackground(UITheme.BG);

        refreshProducts();

        return scrollPane;
    }

    /**
     * Refreshes products based on category and search query.
     */
    private void refreshProducts() {

        if (productGrid == null) {
            return;
        }

        productGrid.removeAll();

        String query = searchField == null
            ? ""
            : searchField.getText().trim().toLowerCase(Locale.ROOT);

        int visibleCount = 0;

        for (Product product : catalogue) {

            if (product == null || !product.isActive()) {
                continue;
            }

            boolean categoryMatches =
                activeCategory.equals("All")
                || product.getCategory().equalsIgnoreCase(activeCategory);

            boolean nameMatches =
                query.isEmpty()
                || product.getName().toLowerCase(Locale.ROOT).contains(query)
                || product.getCategory().toLowerCase(Locale.ROOT).contains(query);

            if (categoryMatches && nameMatches) {

                productGrid.add(buildProductCard(product));

                visibleCount++;
            }
        }

        if (visibleCount == 0) {

            JLabel emptyLabel = UITheme.makeBodyLabel(
                "No products found. Try another search."
            );

            emptyLabel.setBorder(
                BorderFactory.createEmptyBorder(40, 40, 40, 40)
            );

            productGrid.add(emptyLabel);
        }

        productGrid.revalidate();
        productGrid.repaint();
    }

    /**
     * Creates a product card.
     */
    private JPanel buildProductCard(Product product) {

        JPanel card = new JPanel();

        card.setLayout(
            new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBackground(UITheme.CARD);

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER, 1),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            )
        );

        card.setPreferredSize(
            new Dimension(210, 315)
        );

        // Product emoji.
        JLabel emojiLabel = new JLabel(
            product.getEmoji(),
            SwingConstants.CENTER
        );

        emojiLabel.setFont(UITheme.FONT_EMOJI);

        emojiLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        emojiLabel.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 56)
        );

        card.add(emojiLabel);

        card.add(Box.createVerticalStrut(8));

        // Product name.
        JLabel nameLabel = UITheme.makeLabel(
            wrapText(product.getName(), 22),
            UITheme.FONT_SUBHEAD,
            UITheme.TEXT_DARK
        );

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(nameLabel);

        card.add(Box.createVerticalStrut(4));

        // Category.
        JLabel categoryLabel = UITheme.makeSmallLabel(
            product.getCategory()
        );

        categoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(categoryLabel);

        card.add(Box.createVerticalStrut(6));

        // Product rating.
        if (product.getReviewCount() > 0) {

            JLabel ratingLabel = UITheme.makeSmallLabel(
                String.format(
                    Locale.US,
                    "\u2605 %.1f (%d reviews)",
                    product.getRating(),
                    product.getReviewCount()
                )
            );

            ratingLabel.setForeground(UITheme.WARN);

            ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            card.add(ratingLabel);

            card.add(Box.createVerticalStrut(4));
        }

        // Price.
        JLabel priceLabel = UITheme.makeLabel(
            String.format(
                Locale.US,
                "\u20B9 %,.2f",
                product.getDiscountedPrice()
            ),
            UITheme.FONT_HEADING,
            UITheme.PRIMARY
        );

        priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(priceLabel);

        // Original price and discount.
        if (product.hasDiscount()) {

            JLabel originalPriceLabel = UITheme.makeSmallLabel(
                "<html><strike>\u20B9 "
                + String.format(Locale.US, "%,.2f", product.getPrice())
                + "</strike>  <font color='#2E7D32'>"
                + String.format(
                    Locale.US,
                    "%.0f%% OFF",
                    product.getDiscountPercentage()
                )
                + "</font></html>"
            );

            originalPriceLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
            );

            card.add(originalPriceLabel);
        }

        card.add(Box.createVerticalStrut(5));

        // Stock indicator.
        String stockText;

        Color stockColor;

        if (product.getStock() == 0) {

            stockText = "Out of Stock";
            stockColor = UITheme.DANGER;

        } else if (product.getStock() < 4) {

            stockText = "Only " + product.getStock() + " left";
            stockColor = UITheme.WARN;

        } else {

            stockText = "In Stock";
            stockColor = UITheme.SUCCESS;
        }

        JLabel stockLabel = UITheme.makeLabel(
            stockText,
            UITheme.FONT_SMALL,
            stockColor
        );

        stockLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(stockLabel);

        card.add(Box.createVerticalStrut(8));

        // Purchase option.
        if (product.getStock() > 0) {

            JButton addBtn = UITheme.makePrimaryBtn(
                "Add to Cart"
            );

            addBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

            addBtn.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 32)
            );

            addBtn.addActionListener(e -> doAdd(product));

            card.add(addBtn);

        } else {

            JLabel unavailableLabel = UITheme.makeSmallLabel(
                "Purchase Out of Stock"
            );

            unavailableLabel.setAlignmentX(
                Component.CENTER_ALIGNMENT
            );

            card.add(unavailableLabel);
        }

        // Rental option is shown only for books configured as rentable.
        if (product.isRentable()) {

            card.add(Box.createVerticalStrut(5));

            JLabel rentalInfo = UITheme.makeSmallLabel(
                String.format(
                    Locale.US,
                    "Rent: ₹%.2f/day | %d available",
                    product.getRentalPricePerDay(),
                    product.getRentalStock()
                )
            );

            rentalInfo.setForeground(UITheme.SUCCESS);
            rentalInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(rentalInfo);

            JButton rentBtn = UITheme.makeSecondaryBtn(
                "Rent Book"
            );

            rentBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            rentBtn.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 32)
            );

            rentBtn.setEnabled(product.getRentalStock() > 0);
            rentBtn.addActionListener(e -> doRent(product));

            card.add(Box.createVerticalStrut(4));
            card.add(rentBtn);
        }
        return card;
    }

    // =====================================================
    // CART PANEL
    // =====================================================

    private JPanel buildCartPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(UITheme.CARD);

        panel.setBorder(
            BorderFactory.createMatteBorder(
                0, 1, 0, 0, UITheme.BORDER
            )
        );

        panel.setPreferredSize(
            new Dimension(300, 0)
        );

        // Cart header.
        JLabel header = UITheme.makeHeadingLabel(
            " \uD83D\uDED2  Shopping Cart"
        );

        header.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                    0, 0, 1, 0, UITheme.BORDER
                ),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
            )
        );

        panel.add(header, BorderLayout.NORTH);

        // Cart item list.
        cartListPanel = new JPanel();

        cartListPanel.setLayout(
            new BoxLayout(cartListPanel, BoxLayout.Y_AXIS)
        );

        cartListPanel.setBackground(UITheme.CARD);

        JScrollPane cartScroll = new JScrollPane(cartListPanel);

        cartScroll.setBorder(BorderFactory.createEmptyBorder());

        cartScroll.getVerticalScrollBar().setUnitIncrement(12);

        panel.add(cartScroll, BorderLayout.CENTER);

        // Cart footer.
        panel.add(buildCartFooter(), BorderLayout.SOUTH);

        return panel;
    }

    // =====================================================
    // CART FOOTER
    // =====================================================

    private JPanel buildCartFooter() {

        JPanel footer = new JPanel();

        footer.setLayout(
            new BoxLayout(footer, BoxLayout.Y_AXIS)
        );

        footer.setBackground(UITheme.CARD);

        footer.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                    1, 0, 0, 0, UITheme.BORDER
                ),
                BorderFactory.createEmptyBorder(10, 12, 12, 12)
            )
        );

        // Coupon input.
        JPanel couponRow = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 6, 0)
        );

        couponRow.setBackground(UITheme.CARD);

        couponField = UITheme.makeTextField(10);

        couponField.setPreferredSize(
            new Dimension(130, 30)
        );

        couponField.setToolTipText("Enter coupon code");

        applyBtn = UITheme.makeAccentBtn("Apply");

        applyBtn.setPreferredSize(
            new Dimension(60, 30)
        );

        applyBtn.addActionListener(e -> applyCoupon());

        couponRow.add(UITheme.makeSmallLabel("Coupon:"));
        couponRow.add(couponField);
        couponRow.add(applyBtn);

        footer.add(couponRow);

        footer.add(Box.createVerticalStrut(8));

        // Subtotal.
        footer.add(totalsRow("Subtotal:", "subtotal"));

        footer.add(Box.createVerticalStrut(3));

        // Discount.
        footer.add(totalsRow("Discount:", "discount"));

        footer.add(Box.createVerticalStrut(3));

        // GST.
        footer.add(totalsRow("GST (18%):", "gst"));

        footer.add(Box.createVerticalStrut(5));

        JSeparator separator = new JSeparator();

        separator.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 1)
        );

        footer.add(separator);

        footer.add(Box.createVerticalStrut(5));

        // Final total.
        footer.add(totalsRow("Total:", "total"));

        footer.add(Box.createVerticalStrut(10));

        // Checkout.
        checkoutBtn = UITheme.makeSuccessBtn(
            "Checkout \u2714"
        );

        checkoutBtn.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 40)
        );

        checkoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        checkoutBtn.addActionListener(e -> doCheckout());

        footer.add(checkoutBtn);

        return footer;
    }

    /**
     * Creates a row for subtotal, discount, GST or total.
     */
    private JPanel totalsRow(String labelText, String type) {

        JPanel row = new JPanel(new BorderLayout());

        row.setBackground(UITheme.CARD);

        JLabel label = UITheme.makeBodyLabel(labelText);

        JLabel value = UITheme.makeBodyLabel("\u20B9 0.00");

        value.setHorizontalAlignment(SwingConstants.RIGHT);

        if (type.equals("total")) {

            label.setFont(UITheme.FONT_SUBHEAD);
            value.setFont(UITheme.FONT_SUBHEAD);

            value.setForeground(UITheme.PRIMARY);
        }

        if (type.equals("discount")) {
            value.setForeground(UITheme.SUCCESS);
        }

        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);

        switch (type) {

            case "subtotal":
                subtotalValLabel = value;
                break;

            case "discount":
                discountValLabel = value;
                break;

            case "gst":
                gstValLabel = value;
                break;

            case "total":
                totalValLabel = value;
                break;

            default:
                break;
        }

        return row;
    }

    // =====================================================
    // CART OPERATIONS
    // =====================================================

    /**
     * Adds one product to the cart.
     */
    private void doAdd(Product product) {

        try {

            cart.addToCart(product);

            refreshCart();

        } catch (InvalidQuantityException
                | OutOfStockException ex) {

            showError(ex.getMessage());

        } catch (Exception ex) {

            showError(ex.getMessage());
        }
    }

    /**
     * Removes a product from the cart using its product ID.
     */
    private void doRemove(int productId) {

        try {
            cart.removeProduct(productId);
            refreshCart();

        } catch (InvalidQuantityException ex) {
            showError(ex.getMessage());
        }
    }

    /**
     * Increases the quantity of a cart product.
     */
    private void increaseQuantity(int productId) {

        try {

            cart.increaseQty(productId);

            refreshCart();

        } catch (InvalidQuantityException
                | OutOfStockException ex) {

            showError(ex.getMessage());

        } catch (Exception ex) {

            showError(ex.getMessage());
        }
    }

    /**
     * Decreases the quantity of a cart product.
     */
    private void decreaseQuantity(int productId) {

        try {

            cart.decreaseQty(productId);

            refreshCart();

        } catch (Exception ex) {

            showError(ex.getMessage());
        }
    }

    // =====================================================
    // CART REFRESH
    // =====================================================

    /**
     * Refreshes cart rows, totals, badge and product stock.
     */
    private void refreshCart() {

        if (cartListPanel == null) {
            return;
        }

        cartListPanel.removeAll();

        CartItem[] items = cart.getItems();

        if (cart.isEmpty()) {

            JLabel emptyLabel = UITheme.makeBodyLabel(
                "Your cart is empty."
            );

            emptyLabel.setBorder(
                BorderFactory.createEmptyBorder(20, 15, 20, 15)
            );

            cartListPanel.add(emptyLabel);
        }

        for (int i = 0; i < items.length; i++) {

            cartListPanel.add(
                buildCartRow(items[i])
            );
        }

        // Subtotal already includes product-level discounts.
        double subtotal = cart.getSubtotal();

        // Additional coupon/order discount.
        double discount = appliedCoupon.isEmpty()
            ? 0
            : BillGenerator.getDiscountAmount(
                subtotal,
                appliedCoupon
            );

        // GST after discounts.
        double gst = (subtotal - discount) * 0.18;

        // Final amount.
        double total = subtotal - discount + gst;

        subtotalValLabel.setText(
            formatCurrency(subtotal)
        );

        discountValLabel.setText(
            discount > 0
                ? "- " + formatCurrency(discount)
                : formatCurrency(0)
        );

        gstValLabel.setText(
            formatCurrency(gst)
        );

        totalValLabel.setText(
            formatCurrency(total)
        );

        // Badge displays total product quantity, not distinct products.
        cartBadge.setText(
            String.valueOf(cart.getTotalQuantity())
        );

        checkoutBtn.setEnabled(!cart.isEmpty());

        cartListPanel.revalidate();
        cartListPanel.repaint();

        // Refresh product stock information.
        refreshProducts();
    }

    /**
     * Creates a cart row with quantity controls.
     */
    private JPanel buildCartRow(CartItem item) {

        int productId = item.getProduct().getId();

        JPanel row = new JPanel(new BorderLayout(6, 2));

        row.setBackground(UITheme.CARD);

        row.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                    0, 0, 1, 0, UITheme.BORDER
                ),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            )
        );

        row.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 100)
        );

        // Product information.
        JPanel info = new JPanel(
            new GridLayout(2, 1, 0, 2)
        );

        info.setBackground(UITheme.CARD);

        JLabel nameLabel = UITheme.makeLabel(
            item.getProduct().getName(),
            UITheme.FONT_SUBHEAD,
            UITheme.TEXT_DARK
        );

        JLabel priceLabel = UITheme.makeLabel(
            formatCurrency(item.getLineTotal()),
            UITheme.FONT_SMALL,
            UITheme.PRIMARY
        );

        info.add(nameLabel);
        info.add(priceLabel);

        // Quantity controls.
        JPanel quantityPanel = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 4, 0)
        );

        quantityPanel.setBackground(UITheme.CARD);

        JButton minusBtn = UITheme.makeSecondaryBtn("-");

        minusBtn.setPreferredSize(
            new Dimension(28, 25)
        );

        JLabel quantityLabel = UITheme.makeSmallLabel(
            String.valueOf(item.getQuantity())
        );

        JButton plusBtn = UITheme.makeSecondaryBtn("+");

        plusBtn.setPreferredSize(
            new Dimension(28, 25)
        );

        minusBtn.addActionListener(
            e -> decreaseQuantity(productId)
        );

        plusBtn.addActionListener(
            e -> increaseQuantity(productId)
        );

        quantityPanel.add(minusBtn);
        quantityPanel.add(quantityLabel);
        quantityPanel.add(plusBtn);

        // Remove button.
        JButton removeBtn = new JButton("\u2715");

        removeBtn.setFont(UITheme.FONT_SMALL);
        removeBtn.setForeground(UITheme.DANGER);
        removeBtn.setBackground(UITheme.CARD);
        removeBtn.setBorderPainted(false);
        removeBtn.setFocusPainted(false);

        removeBtn.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        removeBtn.addActionListener(
            e -> doRemove(productId)
        );

        JPanel right = new JPanel(
            new BorderLayout(0, 4)
        );

        right.setBackground(UITheme.CARD);

        right.add(removeBtn, BorderLayout.NORTH);
        right.add(quantityPanel, BorderLayout.SOUTH);

        row.add(info, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    // =====================================================
    // BOOK RENTAL
    // =====================================================

    /**
     * Opens the rental form and creates a rental after confirmation.
     */
    private void doRent(Product product) {

        if (product == null || !product.isRentable()) {
            showError("This book is not available for rental.");
            return;
        }

        if (product.getRentalStock() <= 0) {
            showError("No rental copies are currently available.");
            return;
        }

        JSpinner quantitySpinner = new JSpinner(
            new SpinnerNumberModel(
                1,
                1,
                product.getRentalStock(),
                1
            )
        );

        JSpinner daysSpinner = new JSpinner(
            new SpinnerNumberModel(
                7,
                1,
                product.getMaxRentalDays(),
                1
            )
        );

        JLabel costLabel = new JLabel();

        Runnable updateCost = () -> {
            int quantity = (Integer) quantitySpinner.getValue();
            int days = (Integer) daysSpinner.getValue();

            double rentalCost =
                    product.calculateRentalCost(days, quantity);

            double deposit =
                    product.getSecurityDeposit() * quantity;

            double payable = rentalCost + deposit;

            costLabel.setText(
                "<html>Rental amount: " + formatCurrency(rentalCost)
                + "<br>Refundable deposit: " + formatCurrency(deposit)
                + "<br><b>Total payable: " + formatCurrency(payable)
                + "</b></html>"
            );
        };

        quantitySpinner.addChangeListener(e -> updateCost.run());
        daysSpinner.addChangeListener(e -> updateCost.run());

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));

        form.add(new JLabel("Book:"));
        form.add(new JLabel(product.getName()));

        form.add(new JLabel("Rental price per day:"));
        form.add(new JLabel(formatCurrency(product.getRentalPricePerDay())));

        form.add(new JLabel("Available rental copies:"));
        form.add(new JLabel(String.valueOf(product.getRentalStock())));

        form.add(new JLabel("Quantity:"));
        form.add(quantitySpinner);

        form.add(new JLabel("Rental days (max "
                + product.getMaxRentalDays() + "):"));
        form.add(daysSpinner);

        form.add(new JLabel("Payment breakdown:"));
        form.add(costLabel);

        updateCost.run();

        int choice = JOptionPane.showConfirmDialog(
            this,
            form,
            "Rent Book - " + product.getName(),
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        int quantity = (Integer) quantitySpinner.getValue();
        int days = (Integer) daysSpinner.getValue();

        try {
            BookRental rental = rentalManager.createRental(
                currentUser.getUsername(),
                product,
                quantity,
                days
            );

            refreshProducts();

            JOptionPane.showMessageDialog(
                this,
                rental.getRentalSummary()
                    + "\n\nPlease return the book by "
                    + rental.getDueDate() + ".",
                "Book Rental Confirmed",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalArgumentException
                | IllegalStateException ex) {

            showError(ex.getMessage());
            refreshProducts();

        } catch (Exception ex) {

            showError("Unable to create rental: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Displays the current customer's rental history and allows returns.
     */
    private void showMyRentals() {

        BookRental[] myRentals =
                rentalManager.getRentalsByCustomer(
                    currentUser.getUsername()
                );

        if (myRentals.length == 0) {
            showInfo("You have no book rentals yet.");
            return;
        }

        String[] rentalOptions = new String[myRentals.length];

        for (int i = 0; i < myRentals.length; i++) {

            BookRental rental = myRentals[i];
            rental.refreshStatus();

            rentalOptions[i] =
                rental.getRentalId()
                + " | " + rental.getProduct().getName()
                + " | Due: " + rental.getDueDate()
                + " | " + rental.getStatus();
        }

        JComboBox<String> rentalSelector =
                new JComboBox<>(rentalOptions);

        JPanel panel = new JPanel(new BorderLayout(8, 8));

        panel.add(
            new JLabel("Your book rentals:"),
            BorderLayout.NORTH
        );

        panel.add(rentalSelector, BorderLayout.CENTER);

        panel.add(
            new JLabel(
                "<html>Select an active rental and choose Return "
                + "to register the book's return.</html>"
            ),
            BorderLayout.SOUTH
        );

        Object[] buttons = {"Return Selected", "Close"};

        int choice = JOptionPane.showOptionDialog(
            this,
            panel,
            "My Book Rentals",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            buttons,
            buttons[1]
        );

        if (choice != 0) {
            return;
        }

        int selectedIndex = rentalSelector.getSelectedIndex();

        if (selectedIndex < 0) {
            return;
        }

        BookRental selectedRental = myRentals[selectedIndex];
        selectedRental.refreshStatus();

        if (selectedRental.isReturned()) {
            showInfo("This book has already been returned.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Confirm that you are returning:\n"
                + selectedRental.getProduct().getName()
                + "\nRental ID: " + selectedRental.getRentalId(),
            "Confirm Book Return",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            rentalManager.returnRental(selectedRental.getRentalId());

            refreshProducts();

            showInfo(
                "Book return recorded successfully.\n"
                + "The rental stock has been restored.\n"
                + "Refundable deposit: "
                + formatCurrency(selectedRental.getSecurityDeposit())
            );

        } catch (Exception ex) {

            showError("Unable to process return: " + ex.getMessage());
        }
    }

    // =====================================================
    // COUPON MANAGEMENT
    // =====================================================

    /**
     * Checks whether a coupon code is supported.
     */
    private boolean isValidCoupon(String code) {

        if (code == null) {
            return false;
        }

        String normalized = code.trim().toUpperCase(Locale.ROOT);

        return normalized.equals("SAVE10")
            || normalized.equals("FLAT200")
            || normalized.equals("SAVE200");
    }

    /**
     * Applies a coupon using the shared BillGenerator logic.
     */
    private void applyCoupon() {

        String code = couponField.getText().trim();

        if (code.isEmpty()) {

            appliedCoupon = "";

            refreshCart();

            showError("Please enter a coupon code.");

            return;
        }

        if (cart.isEmpty()) {

            showError(
                "Add products to your cart before applying a coupon."
            );

            return;
        }

        if (!isValidCoupon(code)) {

            appliedCoupon = "";

            refreshCart();

            showError(
                "Invalid coupon code.\n"
                + "Available coupons: SAVE10, FLAT200"
            );

            return;
        }

        appliedCoupon = code.toUpperCase(Locale.ROOT);

        refreshCart();

        showInfo(
            "Coupon " + appliedCoupon + " applied successfully!"
        );
    }

    // =====================================================
    // CHECKOUT
    // =====================================================

    /**
     * Handles checkout, bill generation, stock reduction and order creation.
     */
    private void doCheckout() {

        boolean stockReduced = false;

        CartItem[] items = cart.getItems();

        try {

            // Step 1: Validate cart.
            if (cart.isEmpty()) {

                throw new EmptyCartException(
                    "Your cart is empty. Add products before checkout."
                );
            }

            // Step 2: Validate stock for every product before changing anything.
            for (CartItem item : items) {

                Product product = item.getProduct();

                if (!product.isActive()) {

                    throw new OutOfStockException(
                        product.getName() + " is no longer available."
                    );
                }

                if (product.getStock() < item.getQuantity()) {

                    throw new OutOfStockException(
                        "Insufficient stock for "
                        + product.getName()
                        + ". Available: "
                        + product.getStock()
                    );
                }
            }

            // Step 3: Calculate final amount using shared bill logic.
            double grandTotal = BillGenerator.getGrandTotal(
                cart,
                appliedCoupon
            );

            // Step 4: Generate the bill before clearing the cart.
            String bill = BillGenerator.buildBill(
                cart,
                appliedCoupon,
                currentUser.getUsername()
            );

            // Step 5: Reduce stock after all products are validated.
            for (CartItem item : items) {

                item.getProduct().reduceStock(
                    item.getQuantity()
                );
            }

            stockReduced = true;

            // Step 6: Create the order.
            Order order = orderManager.addOrder(
                currentUser.getUsername(),
                cart.getItemsSummary(),
                grandTotal
            );

            // Step 7: Clear cart after successful order creation.
            cart.clear();

            appliedCoupon = "";

            couponField.setText("");

            refreshCart();

            // Step 8: Show bill.
            showBill(bill, order);

            JOptionPane.showMessageDialog(
                this,
                "Thank you for shopping at ShopEase! \uD83D\uDE0A",
                "Order Placed",
                JOptionPane.INFORMATION_MESSAGE
            );

        } catch (EmptyCartException
                | OutOfStockException ex) {

            showError(ex.getMessage());

        } catch (Exception ex) {

            // Restore stock if it was reduced but order creation failed.
            if (stockReduced) {

                for (CartItem item : items) {

                    item.getProduct().increaseStock(
                        item.getQuantity()
                    );
                }

                refreshProducts();
            }

            showError(
                "Checkout failed: " + ex.getMessage()
            );
        }
    }

    // =====================================================
    // BILL DISPLAY
    // =====================================================

    /**
     * Displays the generated bill and order information.
     */
    private void showBill(String bill, Order order) {

        JTextArea area = new JTextArea(bill, 20, 38);

        area.setFont(UITheme.FONT_MONO);
        area.setEditable(false);
        area.setBackground(UITheme.CARD);
        area.setCaretPosition(0);

        JPanel content = new JPanel(
            new BorderLayout(0, 8)
        );

        content.add(
            UITheme.makeHeadingLabel(
                "Order #" + order.getOrderId()
            ),
            BorderLayout.NORTH
        );

        content.add(
            new JScrollPane(area),
            BorderLayout.CENTER
        );

        content.setPreferredSize(
            new Dimension(450, 420)
        );

        JOptionPane.showMessageDialog(
            this,
            content,
            "Your ShopEase Order Summary",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // CUSTOMER ORDER HISTORY
    // =====================================================

    /**
     * Displays orders belonging to the current customer.
     */
    private void showMyOrders() {

        Order[] myOrders = orderManager.getOrdersByCustomer(
            currentUser.getUsername()
        );

        if (myOrders.length == 0) {

            showInfo("You have not placed any orders yet.");

            return;
        }

        String[] columns = {
            "Order ID",
            "Amount",
            "Status"
        };

        Object[][] data = new Object[myOrders.length][3];

        for (int i = 0; i < myOrders.length; i++) {

            Order order = myOrders[i];

            data[i][0] = order.getOrderId();

            data[i][1] = formatCurrency(
                order.getGrandTotal()
            );

            data[i][2] = order.getStatus();
        }

        JTable table = new JTable(data, columns);

        UITheme.styleTable(table);

        JScrollPane scrollPane = UITheme.makeScroll(table);

        scrollPane.setPreferredSize(
            new Dimension(480, 250)
        );

        JOptionPane.showMessageDialog(
            this,
            scrollPane,
            "My Orders",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // LOGOUT
    // =====================================================

    private void doLogout() {

        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            dispose();

            loginWindow.setVisible(true);
        }
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Formats an amount as Indian rupees.
     */
    private String formatCurrency(double amount) {

        return String.format(
            Locale.US,
            "\u20B9 %,.2f",
            amount
        );
    }

    /**
     * Shortens long product names for cards.
     */
    private String wrapText(String text, int maxLength) {

        if (text == null || text.length() <= maxLength) {
            return text;
        }

        return text.substring(0, maxLength - 1) + "\u2026";
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "ShopEase - Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    private void showInfo(String message) {

        JOptionPane.showMessageDialog(
            this,
            message,
            "ShopEase",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // =====================================================
    // ACTION LISTENER
    // =====================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        // Button actions are handled by their individual listeners.
    }
}
