
package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

import model.User;
import model.Product;
import model.Order;

import service.OrderManager;
import utils.UITheme;

/**
 * ShopEase Admin Dashboard.
 *
 * Features:
 * 1. Dashboard statistics.
 * 2. Recent orders.
 * 3. Order management.
 * 4. Order status updates.
 * 5. Product management.
 * 6. Stock management.
 * 7. Product activation/deactivation.
 *
 * Concepts:
 * 1. Inheritance
 * 2. Interface implementation
 * 3. Event handling
 * 4. Arrays
 * 5. Exception handling
 */
public class AdminWindow extends JFrame implements ActionListener {

    private static final long serialVersionUID = 1L;

    // Application data
    private final User currentUser;
    private Product[] catalogue;
    private final OrderManager orderManager;
    private final LoginWindow loginWindow;

    // Navigation
    private String activePage = "Dashboard";

    private JButton dashBtn;
    private JButton ordersBtn;
    private JButton productsBtn;
    private JButton logoutBtn;

    // Main content
    private JPanel contentArea;

    // Dashboard
    private JLabel totalOrdersVal;
    private JLabel ongoingVal;
    private JLabel completedVal;
    private JLabel revenueVal;

    // Orders
    private DefaultTableModel orderModel;
    private JTable orderTable;
    private String orderFilter = "ALL";

    // Products
    private DefaultTableModel productModel;
    private JTable productTable;

    // =====================================================
    // CONSTRUCTOR
    // =====================================================

    public AdminWindow(
            User currentUser,
            Product[] catalogue,
            OrderManager orderManager,
            LoginWindow loginWindow) {

        if (currentUser == null
                || catalogue == null
                || orderManager == null
                || loginWindow == null) {

            throw new IllegalArgumentException(
                "Admin dashboard data cannot be null."
            );
        }

        this.currentUser = currentUser;
        this.catalogue = catalogue;
        this.orderManager = orderManager;
        this.loginWindow = loginWindow;

        setTitle("ShopEase - Admin Dashboard");
        setSize(1200, 740);
        setMinimumSize(new Dimension(1000, 650));
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(UITheme.BG);
        setLayout(new BorderLayout());

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildNavRail(), BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG);

        add(contentArea, BorderLayout.CENTER);

        showDashboard();
    }

    // =====================================================
    // TOP BAR
    // =====================================================

    private JPanel buildTopBar() {

        JPanel bar = new JPanel(new BorderLayout());

        bar.setBackground(UITheme.NAV_BG);

        bar.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                    0, 0, 1, 0, UITheme.BORDER
                ),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
            )
        );

        // Left section.
        JPanel left = new JPanel(
            new FlowLayout(FlowLayout.LEFT, 8, 0)
        );

        left.setBackground(UITheme.NAV_BG);

        JLabel logo = new JLabel("\uD83D\uDED2");

        logo.setFont(
            new Font("Segoe UI Emoji", Font.PLAIN, 24)
        );

        JLabel title = UITheme.makeHeadingLabel(
            "ShopEase | Admin"
        );

        title.setForeground(UITheme.PRIMARY);

        left.add(logo);
        left.add(title);

        // Right section.
        JPanel right = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );

        right.setBackground(UITheme.NAV_BG);

        JLabel adminLabel = UITheme.makeBodyLabel(
            "\uD83D\uDC64  " + currentUser.getUsername()
        );

        logoutBtn = UITheme.makeSecondaryBtn("Logout");

        logoutBtn.setPreferredSize(
            new Dimension(80, 30)
        );

        logoutBtn.addActionListener(this);

        right.add(adminLabel);
        right.add(logoutBtn);

        bar.add(left, BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);

        return bar;
    }

    // =====================================================
    // NAVIGATION
    // =====================================================

    private JPanel buildNavRail() {

        JPanel nav = new JPanel();

        nav.setLayout(
            new BoxLayout(nav, BoxLayout.Y_AXIS)
        );

        nav.setBackground(UITheme.SIDEBAR_BG);

        nav.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                    0, 0, 0, 1, UITheme.BORDER
                ),
                BorderFactory.createEmptyBorder(16, 0, 16, 0)
            )
        );

        nav.setPreferredSize(
            new Dimension(190, 0)
        );

        dashBtn = UITheme.makeNavBtn(
            "   \uD83D\uDCCA  Dashboard"
        );

        ordersBtn = UITheme.makeNavBtn(
            "   \uD83D\uDCCB  Orders"
        );

        productsBtn = UITheme.makeNavBtn(
            "   \uD83D\uDCE6  Products"
        );

        JButton[] buttons = {
            dashBtn,
            ordersBtn,
            productsBtn
        };

        for (JButton btn : buttons) {

            btn.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
            );

            btn.setAlignmentX(Component.LEFT_ALIGNMENT);

            btn.addActionListener(this);

            nav.add(btn);
            nav.add(Box.createVerticalStrut(2));
        }

        updateNavHighlight();

        return nav;
    }

    private void updateNavHighlight() {

        styleNavBtn(dashBtn, "Dashboard");
        styleNavBtn(ordersBtn, "Orders");
        styleNavBtn(productsBtn, "Products");
    }

    private void styleNavBtn(JButton btn, String page) {

        if (btn == null) {
            return;
        }

        if (page.equals(activePage)) {

            btn.setBackground(new Color(0xE3F2FD));
            btn.setForeground(UITheme.PRIMARY);
            btn.setFont(UITheme.FONT_SUBHEAD);

        } else {

            btn.setBackground(UITheme.SIDEBAR_BG);
            btn.setForeground(UITheme.TEXT_DARK);
            btn.setFont(UITheme.FONT_BODY);
        }
    }

    // =====================================================
    // ACTION LISTENER
    // =====================================================

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == dashBtn) {

            activePage = "Dashboard";
            updateNavHighlight();
            showDashboard();

        } else if (source == ordersBtn) {

            activePage = "Orders";
            updateNavHighlight();
            showOrders();

        } else if (source == productsBtn) {

            activePage = "Products";
            updateNavHighlight();
            showProducts();

        } else if (source == logoutBtn) {

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
    }

    // =====================================================
    // DASHBOARD
    // =====================================================

    private void showDashboard() {

        contentArea.removeAll();

        JPanel page = new JPanel(new BorderLayout());

        page.setBackground(UITheme.BG);

        page.setBorder(
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        );

        JLabel heading = UITheme.makeTitleLabel(
            "Dashboard"
        );

        heading.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 18, 0)
        );

        page.add(heading, BorderLayout.NORTH);

        // KPI cards.
        JPanel kpiRow = new JPanel(
            new GridLayout(1, 4, 16, 0)
        );

        kpiRow.setBackground(UITheme.BG);

        JPanel totalCard = kpiCard(
            "\uD83D\uDCCB",
            "Total Orders",
            "0",
            UITheme.PRIMARY
        );

        JPanel ongoingCard = kpiCard(
            "\u23F3",
            "Ongoing",
            "0",
            UITheme.WARN
        );

        JPanel completedCard = kpiCard(
            "\u2714",
            "Completed",
            "0",
            UITheme.SUCCESS
        );

        JPanel revenueCard = kpiCard(
            "\uD83D\uDCB0",
            "Revenue",
            "\u20B9 0",
            new Color(0x6A1B9A)
        );

        totalOrdersVal = extractValueLabel(totalCard);
        ongoingVal = extractValueLabel(ongoingCard);
        completedVal = extractValueLabel(completedCard);
        revenueVal = extractValueLabel(revenueCard);

        kpiRow.add(totalCard);
        kpiRow.add(ongoingCard);
        kpiRow.add(completedCard);
        kpiRow.add(revenueCard);

        // Recent orders.
        JPanel bottomPanel = new JPanel(new BorderLayout());

        bottomPanel.setBackground(UITheme.BG);

        bottomPanel.setBorder(
            BorderFactory.createEmptyBorder(20, 0, 0, 0)
        );

        JLabel recentTitle = UITheme.makeHeadingLabel(
            "Recent Orders"
        );

        recentTitle.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 10, 0)
        );

        bottomPanel.add(recentTitle, BorderLayout.NORTH);

        DefaultTableModel recentModel = new DefaultTableModel(
            new String[]{
                "Order ID",
                "Customer",
                "Items",
                "Total",
                "Status"
            },
            0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable recentTable = new JTable(recentModel);

        UITheme.styleTable(recentTable);

        JScrollPane scrollPane = UITheme.makeScroll(recentTable);

        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        // Display the latest five orders.
        Order[] orders = orderManager.getOrders();

        int count = orderManager.getOrderCount();

        int start = Math.max(0, count - 5);

        for (int i = start; i < count; i++) {

            Order order = orders[i];

            recentModel.addRow(new Object[]{
                order.getOrderId(),
                order.getCustomerName(),
                order.getItemsSummary(),
                formatCurrency(order.getGrandTotal()),
                order.getStatus()
            });
        }

        page.add(kpiRow, BorderLayout.CENTER);
        page.add(bottomPanel, BorderLayout.SOUTH);

        contentArea.add(page, BorderLayout.CENTER);

        refreshContent();

        updateKpis();
    }

    /**
     * Creates a dashboard KPI card.
     */
    private JPanel kpiCard(
            String icon,
            String label,
            String initialValue,
            Color accent) {

        JPanel card = UITheme.makeCard();

        card.setLayout(
            new BoxLayout(card, BoxLayout.Y_AXIS)
        );

        card.setBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.BORDER),
                BorderFactory.createEmptyBorder(16, 20, 16, 20)
            )
        );

        JLabel iconLabel = new JLabel(icon);

        iconLabel.setFont(
            new Font("Segoe UI Emoji", Font.PLAIN, 28)
        );

        JLabel nameLabel = UITheme.makeLabel(
            label,
            UITheme.FONT_SMALL,
            UITheme.TEXT_MUTED
        );

        JLabel valueLabel = UITheme.makeLabel(
            initialValue,
            UITheme.FONT_TITLE,
            accent
        );

        valueLabel.putClientProperty("type", "value");

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(nameLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(valueLabel);

        return card;
    }

    private JLabel extractValueLabel(JPanel card) {

        for (Component component : card.getComponents()) {

            if (component instanceof JLabel) {

                JLabel label = (JLabel) component;

                if ("value".equals(
                    label.getClientProperty("type")
                )) {
                    return label;
                }
            }
        }

        return new JLabel();
    }

    /**
     * Refreshes dashboard statistics.
     */
    private void updateKpis() {

        if (totalOrdersVal == null) {
            return;
        }

        int total = orderManager.getOrderCount();

        int ongoing = orderManager.countByStatus("Ongoing");

        int completed = orderManager.countByStatus("Completed");

        double revenue = orderManager.getTotalRevenue();

        totalOrdersVal.setText(String.valueOf(total));

        ongoingVal.setText(String.valueOf(ongoing));

        completedVal.setText(String.valueOf(completed));

        revenueVal.setText(formatCurrency(revenue));
    }

    // =====================================================
    // ORDERS PAGE
    // =====================================================

    private void showOrders() {

        contentArea.removeAll();

        JPanel page = new JPanel(
            new BorderLayout(0, 12)
        );

        page.setBackground(UITheme.BG);

        page.setBorder(
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        );

        // Title and toolbar.
        JPanel topRow = new JPanel(new BorderLayout());

        topRow.setBackground(UITheme.BG);

        JLabel heading = UITheme.makeTitleLabel("Orders");

        topRow.add(heading, BorderLayout.WEST);

        JPanel toolbar = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        toolbar.setBackground(UITheme.BG);

        JButton allBtn = filterBtn("All", "ALL");

        JButton ongoingBtn = filterBtn(
            "Ongoing",
            "Ongoing"
        );

        JButton completedBtn = filterBtn(
            "Completed",
            "Completed"
        );

        JButton markBtn = UITheme.makePrimaryBtn(
            "Mark Completed \u2714"
        );

        JButton refreshBtn = UITheme.makeSecondaryBtn(
            "\u21BB Refresh"
        );

        markBtn.addActionListener(
            e -> doMarkCompleted()
        );

        refreshBtn.addActionListener(e -> {

            fillOrderTable();
            updateKpis();
        });

        toolbar.add(allBtn);
        toolbar.add(ongoingBtn);
        toolbar.add(completedBtn);
        toolbar.add(Box.createHorizontalStrut(8));
        toolbar.add(markBtn);
        toolbar.add(refreshBtn);

        topRow.add(toolbar, BorderLayout.EAST);

        page.add(topRow, BorderLayout.NORTH);

        // Orders table.
        orderModel = new DefaultTableModel(
            new String[]{
                "Order ID",
                "Customer",
                "Items",
                "Total (Rs.)",
                "Status"
            },
            0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderModel);

        UITheme.styleTable(orderTable);

        orderTable.getColumnModel()
            .getColumn(4)
            .setCellRenderer(new StatusRenderer());

        page.add(
            UITheme.makeScroll(orderTable),
            BorderLayout.CENTER
        );

        contentArea.add(page, BorderLayout.CENTER);

        refreshContent();

        fillOrderTable();
    }

    /**
     * Creates order filter buttons.
     */
    private JButton filterBtn(String label, String filter) {

        JButton btn = orderFilter.equals(filter)
            ? UITheme.makePrimaryBtn(label)
            : UITheme.makeSecondaryBtn(label);

        btn.setPreferredSize(
            new Dimension(90, 30)
        );

        btn.addActionListener(e -> {

            orderFilter = filter;

            showOrders();
        });

        return btn;
    }

    /**
     * Loads orders into the table.
     */
    private void fillOrderTable() {

        if (orderModel == null) {
            return;
        }

        orderModel.setRowCount(0);

        Order[] orders = orderManager.getOrders();

        for (int i = 0; i < orderManager.getOrderCount(); i++) {

            Order order = orders[i];

            boolean show = orderFilter.equals("ALL")
                || order.getStatus().equalsIgnoreCase(orderFilter);

            if (show) {

                orderModel.addRow(new Object[]{
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getItemsSummary(),
                    formatCurrency(order.getGrandTotal()),
                    order.getStatus()
                });
            }
        }
    }

    /**
     * Marks the selected order as completed.
     */
    private void doMarkCompleted() {

        try {

            int selectedRow = orderTable.getSelectedRow();

            if (selectedRow < 0) {

                throw new Exception(
                    "Please select an order first."
                );
            }

            int modelRow = orderTable.convertRowIndexToModel(
                selectedRow
            );

            int orderId = Integer.parseInt(
                orderModel.getValueAt(modelRow, 0).toString()
            );

            orderManager.markCompleted(orderId);

            fillOrderTable();

            updateKpis();

            showInfo("Order marked as completed.");

        } catch (Exception ex) {

            showError(ex.getMessage());
        }
    }

    /**
     * Renderer for order status.
     */
    private static class StatusRenderer
            extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {

            JLabel label = (JLabel)
                super.getTableCellRendererComponent(
                    table,
                    value,
                    selected,
                    focused,
                    row,
                    column
                );

            String status = value == null
                ? ""
                : value.toString();

            if (status.equalsIgnoreCase("Completed")
                    || status.equalsIgnoreCase("Delivered")) {

                label.setForeground(UITheme.SUCCESS);

            } else if (status.equalsIgnoreCase("Cancelled")
                    || status.equalsIgnoreCase("Returned")) {

                label.setForeground(new Color(0xC62828));

            } else {

                label.setForeground(UITheme.WARN);
            }

            label.setFont(UITheme.FONT_SUBHEAD);

            label.setOpaque(true);

            label.setBackground(
                selected
                    ? new Color(0xE3F2FD)
                    : UITheme.CARD
            );

            return label;
        }
    }

    // =====================================================
    // PRODUCTS PAGE
    // =====================================================

    private void showProducts() {

        contentArea.removeAll();

        JPanel page = new JPanel(
            new BorderLayout(0, 12)
        );

        page.setBackground(UITheme.BG);

        page.setBorder(
            BorderFactory.createEmptyBorder(20, 24, 20, 24)
        );

        // Title and toolbar.
        JPanel topRow = new JPanel(new BorderLayout());

        topRow.setBackground(UITheme.BG);

        JLabel heading = UITheme.makeTitleLabel("Products");

        topRow.add(heading, BorderLayout.WEST);

        JPanel toolbar = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 8, 0)
        );

        toolbar.setBackground(UITheme.BG);

        JButton addBtn = UITheme.makePrimaryBtn(
            "+ Add Product"
        );

        JButton editStockBtn = UITheme.makeSecondaryBtn(
            "Edit Stock"
        );

        JButton refreshBtn = UITheme.makeSecondaryBtn(
            "\u21BB Refresh"
        );

        addBtn.addActionListener(
            e -> showAddProductDialog()
        );

        editStockBtn.addActionListener(
            e -> showEditStockDialog()
        );

        refreshBtn.addActionListener(
            e -> fillProductTable()
        );

        toolbar.add(addBtn);
        toolbar.add(editStockBtn);
        toolbar.add(refreshBtn);

        topRow.add(toolbar, BorderLayout.EAST);

        page.add(topRow, BorderLayout.NORTH);

        // Product table.
        productModel = new DefaultTableModel(
            new String[]{
                "ID",
                "Name",
                "Category",
                "Price (Rs.)",
                "Stock",
                "Status"
            },
            0
        ) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        productTable = new JTable(productModel);

        UITheme.styleTable(productTable);

        productTable.getColumnModel()
            .getColumn(5)
            .setCellRenderer(new StockRenderer());

        page.add(
            UITheme.makeScroll(productTable),
            BorderLayout.CENTER
        );

        contentArea.add(page, BorderLayout.CENTER);

        refreshContent();

        fillProductTable();
    }

    /**
     * Loads catalogue products into the table.
     */
    private void fillProductTable() {

        if (productModel == null) {
            return;
        }

        productModel.setRowCount(0);

        for (Product product : catalogue) {

            if (product == null) {
                continue;
            }

            String status;

            if (!product.isActive()) {

                status = "Inactive";

            } else if (product.getStock() == 0) {

                status = "Out of Stock";

            } else if (product.getStock() < 4) {

                status = "Low Stock";

            } else {

                status = "In Stock";
            }

            productModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                product.getCategory(),
                formatCurrency(product.getPrice()),
                product.getStock(),
                status
            });
        }
    }

    // =====================================================
    // ADD PRODUCT
    // =====================================================

    private void showAddProductDialog() {

        JDialog dialog = new JDialog(
            this,
            "Add New Product",
            true
        );

        dialog.setSize(420, 390);

        dialog.setLocationRelativeTo(this);

        dialog.getContentPane().setBackground(UITheme.CARD);

        JPanel form = new JPanel(
            new GridLayout(6, 2, 8, 10)
        );

        form.setBackground(UITheme.CARD);

        form.setBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        );

        JTextField nameField = UITheme.makeTextField(14);

        JTextField priceField = UITheme.makeTextField(14);

        JTextField stockField = UITheme.makeTextField(14);

        JTextField emojiField = UITheme.makeTextField(14);

        String[] categories = {
            "Electronics",
            "Fashion",
            "Kitchen",
            "Books",
            "Sports"
        };

        JComboBox<String> categoryBox =
            new JComboBox<>(categories);

        categoryBox.setFont(UITheme.FONT_BODY);

        form.add(UITheme.makeBodyLabel("Product Name:"));
        form.add(nameField);

        form.add(UITheme.makeBodyLabel("Category:"));
        form.add(categoryBox);

        form.add(UITheme.makeBodyLabel("Price (Rs.):"));
        form.add(priceField);

        form.add(UITheme.makeBodyLabel("Stock:"));
        form.add(stockField);

        form.add(UITheme.makeBodyLabel("Emoji Icon:"));
        form.add(emojiField);

        JButton saveBtn = UITheme.makePrimaryBtn(
            "Add Product"
        );

        JButton cancelBtn = UITheme.makeSecondaryBtn(
            "Cancel"
        );

        saveBtn.addActionListener(e -> {

            try {

                String name = nameField.getText().trim();

                String category = categoryBox
                    .getSelectedItem()
                    .toString();

                double price = Double.parseDouble(
                    priceField.getText().trim()
                );

                int stock = Integer.parseInt(
                    stockField.getText().trim()
                );

                String emoji = emojiField.getText().trim();

                if (name.isEmpty()) {

                    throw new IllegalArgumentException(
                        "Product name cannot be empty."
                    );
                }

                if (!Double.isFinite(price) || price <= 0) {

                    throw new IllegalArgumentException(
                        "Price must be a valid positive amount."
                    );
                }

                if (stock < 0) {

                    throw new IllegalArgumentException(
                        "Stock cannot be negative."
                    );
                }

                if (emoji.isEmpty()) {
                    emoji = "\uD83D\uDCE6";
                }

                // Find the next unique product ID.
                int newId = getNextProductId();

                // Expand the catalogue.
                Product[] newCatalogue =
                    new Product[catalogue.length + 1];

                System.arraycopy(
                    catalogue,
                    0,
                    newCatalogue,
                    0,
                    catalogue.length
                );

                newCatalogue[catalogue.length] = new Product(
                    newId,
                    name,
                    price,
                    stock,
                    category,
                    emoji
                );

                // Update admin and login catalogue references.
                catalogue = newCatalogue;

                loginWindow.updateCatalogue(newCatalogue);

                fillProductTable();

                dialog.dispose();

                showInfo(
                    "Product \"" + name
                    + "\" added successfully."
                );

            } catch (NumberFormatException ex) {

                showError(
                    "Enter a valid number for price and stock."
                );

            } catch (Exception ex) {

                showError(ex.getMessage());
            }
        });

        cancelBtn.addActionListener(
            e -> dialog.dispose()
        );

        form.add(saveBtn);
        form.add(cancelBtn);

        dialog.add(form);

        dialog.setVisible(true);
    }

    /**
     * Generates a unique product ID.
     */
    private int getNextProductId() {

        int maxId = 0;

        for (Product product : catalogue) {

            if (product != null) {

                maxId = Math.max(
                    maxId,
                    product.getId()
                );
            }
        }

        return maxId + 1;
    }

    // =====================================================
    // EDIT STOCK
    // =====================================================

    private void showEditStockDialog() {

        try {

            int selectedRow = productTable.getSelectedRow();

            if (selectedRow < 0) {

                throw new Exception(
                    "Please select a product first."
                );
            }

            int modelRow = productTable.convertRowIndexToModel(
                selectedRow
            );

            int productId = Integer.parseInt(
                productModel.getValueAt(modelRow, 0).toString()
            );

            Product target = findProductById(productId);

            if (target == null) {

                throw new Exception(
                    "Product not found."
                );
            }

            String input = JOptionPane.showInputDialog(
                this,
                "Enter new stock for " + target.getName(),
                String.valueOf(target.getStock())
            );

            if (input == null) {
                return;
            }

            int newStock = Integer.parseInt(input.trim());

            if (newStock < 0) {

                throw new IllegalArgumentException(
                    "Stock cannot be negative."
                );
            }

            target.setStock(newStock);

            fillProductTable();

            showInfo("Product stock updated successfully.");

        } catch (NumberFormatException ex) {

            showError(
                "Please enter a valid whole number for stock."
            );

        } catch (Exception ex) {

            showError(ex.getMessage());
        }
    }

    /**
     * Finds a product using its ID.
     */
    private Product findProductById(int productId) {

        for (Product product : catalogue) {

            if (product != null
                    && product.getId() == productId) {

                return product;
            }
        }

        return null;
    }

    // =====================================================
    // STOCK STATUS RENDERER
    // =====================================================

    private static class StockRenderer
            extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean selected,
                boolean focused,
                int row,
                int column) {

            JLabel label = (JLabel)
                super.getTableCellRendererComponent(
                    table,
                    value,
                    selected,
                    focused,
                    row,
                    column
                );

            String status = value == null
                ? ""
                : value.toString();

            if (status.equals("Out of Stock")
                    || status.equals("Inactive")) {

                label.setForeground(new Color(0xC62828));

            } else if (status.equals("Low Stock")) {

                label.setForeground(UITheme.WARN);

            } else {

                label.setForeground(UITheme.SUCCESS);
            }

            label.setFont(UITheme.FONT_SMALL);

            label.setOpaque(true);

            label.setBackground(
                selected
                    ? new Color(0xE3F2FD)
                    : UITheme.CARD
            );

            return label;
        }
    }

    // =====================================================
    // COMMON HELPERS
    // =====================================================

    private String formatCurrency(double amount) {

        return String.format(
            java.util.Locale.US,
            "\u20B9 %,.2f",
            amount
        );
    }

    private void refreshContent() {

        contentArea.revalidate();
        contentArea.repaint();
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
}
