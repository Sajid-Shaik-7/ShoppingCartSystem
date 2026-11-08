import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

// CONCEPT: Interface - AdminWindow implements ActionListener
public class AdminWindow extends JFrame implements ActionListener {

    private User         currentUser;
    private Product[]    catalogue;
    private OrderManager orderManager;
    private LoginWindow  loginWindow;

    // Nav rail state
    private String activePage = "Dashboard";

    // Nav rail buttons
    private JButton dashBtn;
    private JButton ordersBtn;
    private JButton productsBtn;
    private JButton logoutBtn;

    // Content area (swapped per page)
    private JPanel contentArea;

    // ── Dashboard stats labels ───────────────────────────────────────────────────
    private JLabel totalOrdersVal;
    private JLabel ongoingVal;
    private JLabel completedVal;
    private JLabel revenueVal;

    // ── Orders page ──────────────────────────────────────────────────────────────
    private DefaultTableModel orderModel;
    private JTable            orderTable;
    private String            orderFilter = "ALL";

    // ── Products page ─────────────────────────────────────────────────────────────
    private DefaultTableModel productModel;
    private JTable            productTable;

    // ── Constructor ─────────────────────────────────────────────────────────────
    public AdminWindow(User currentUser, Product[] catalogue,
                       OrderManager orderManager, LoginWindow loginWindow) {
        this.currentUser  = currentUser;
        this.catalogue    = catalogue;
        this.orderManager = orderManager;
        this.loginWindow  = loginWindow;

        setTitle("ShopEase — Admin Dashboard");
        setSize(1150, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG);

        setLayout(new BorderLayout());
        add(buildTopBar(),   BorderLayout.NORTH);
        add(buildNavRail(),  BorderLayout.WEST);

        contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(UITheme.BG);
        add(contentArea, BorderLayout.CENTER);

        showDashboard();
    }

    // ── Top bar ──────────────────────────────────────────────────────────────────
    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(UITheme.NAV_BG);
        bar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setBackground(UITheme.NAV_BG);
        JLabel logo = new JLabel("\uD83D\uDED2");
        logo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        JLabel title = UITheme.makeHeadingLabel("ShopEase  |  Admin");
        title.setForeground(UITheme.PRIMARY);
        left.add(logo);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(UITheme.NAV_BG);
        JLabel adminLbl = UITheme.makeBodyLabel("\uD83D\uDC64  " + currentUser.getUsername());
        logoutBtn = UITheme.makeSecondaryBtn("Logout");
        logoutBtn.setPreferredSize(new Dimension(80, 30));
        logoutBtn.addActionListener(this);
        right.add(adminLbl);
        right.add(logoutBtn);

        bar.add(left,  BorderLayout.WEST);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    // ── Left navigation rail ─────────────────────────────────────────────────────
    private JPanel buildNavRail() {
        JPanel nav = new JPanel();
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBackground(UITheme.SIDEBAR_BG);
        nav.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 1, UITheme.BORDER),
            BorderFactory.createEmptyBorder(16, 0, 16, 0)));
        nav.setPreferredSize(new Dimension(190, 0));

        dashBtn     = UITheme.makeNavBtn("   \uD83D\uDCCA  Dashboard");
        ordersBtn   = UITheme.makeNavBtn("   \uD83D\uDCCB  Orders");
        productsBtn = UITheme.makeNavBtn("   \uD83D\uDCE6  Products");

        for (JButton btn : new JButton[]{ dashBtn, ordersBtn, productsBtn }) {
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.addActionListener(this);
            nav.add(btn);
            nav.add(Box.createVerticalStrut(2));
        }

        updateNavHighlight();
        return nav;
    }

    private void updateNavHighlight() {
        styleNavBtn(dashBtn,     "Dashboard");
        styleNavBtn(ordersBtn,   "Orders");
        styleNavBtn(productsBtn, "Products");
    }

    private void styleNavBtn(JButton btn, String page) {
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

    // ── CONCEPT: Interface - actionPerformed routes button clicks ─────────────────
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == dashBtn) {
            activePage = "Dashboard"; updateNavHighlight(); showDashboard();
        } else if (src == ordersBtn) {
            activePage = "Orders";    updateNavHighlight(); showOrders();
        } else if (src == productsBtn) {
            activePage = "Products";  updateNavHighlight(); showProducts();
        } else if (src == logoutBtn) {
            dispose();
            loginWindow.setVisible(true);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // PAGE: Dashboard
    // ══════════════════════════════════════════════════════════════════════════════
    private void showDashboard() {
        contentArea.removeAll();

        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(UITheme.BG);
        page.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Page title
        JLabel heading = UITheme.makeTitleLabel("Dashboard");
        heading.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        page.add(heading, BorderLayout.NORTH);

        // KPI cards row
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiRow.setBackground(UITheme.BG);

        JPanel totalCard    = kpiCard("\uD83D\uDCCB", "Total Orders",  "0", UITheme.PRIMARY);
        JPanel ongoingCard  = kpiCard("\u23F3",        "Ongoing",        "0", UITheme.WARN);
        JPanel completedCard= kpiCard("\u2714",         "Completed",      "0", UITheme.SUCCESS);
        JPanel revenueCard  = kpiCard("\uD83D\uDCB0",  "Revenue",    "\u20B9 0", new Color(0x6A1B9A));

        // Extract the value labels from each card (the second label added)
        totalOrdersVal = extractValueLabel(totalCard);
        ongoingVal     = extractValueLabel(ongoingCard);
        completedVal   = extractValueLabel(completedCard);
        revenueVal     = extractValueLabel(revenueCard);

        kpiRow.add(totalCard);
        kpiRow.add(ongoingCard);
        kpiRow.add(completedCard);
        kpiRow.add(revenueCard);

        // Recent orders mini-table
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(UITheme.BG);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel recentTitle = UITheme.makeHeadingLabel("Recent Orders");
        recentTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        bottomPanel.add(recentTitle, BorderLayout.NORTH);

        DefaultTableModel recentModel = new DefaultTableModel(
            new String[]{ "Order ID", "Customer", "Items", "Total", "Status" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable recentTable = new JTable(recentModel);
        UITheme.styleTable(recentTable);
        JScrollPane sp = UITheme.makeScroll(recentTable);
        bottomPanel.add(sp, BorderLayout.CENTER);

        // Fill recent table (last 5 orders)
        Order[] orders = orderManager.getOrders();
        int count = orderManager.getOrderCount();
        int start = Math.max(0, count - 5);
        int i = start;
        while (i < count) {
            Order o = orders[i];
            recentModel.addRow(new Object[]{
                o.getOrderId(), o.getCustomerName(),
                o.getItemsSummary(),
                String.format("\u20B9 %,.0f", o.getGrandTotal()),
                o.getStatus() });
            i = i + 1;
        }

        page.add(kpiRow,       BorderLayout.CENTER);
        page.add(bottomPanel,  BorderLayout.SOUTH);

        contentArea.add(page, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
        updateKpis();
    }

    // Builds a KPI card with emoji icon, label and value.
    private JPanel kpiCard(String icon, String label, String initVal, Color accent) {
        JPanel card = UITheme.makeCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(UITheme.BORDER),
            BorderFactory.createEmptyBorder(16, 20, 16, 20)));

        JLabel iconLbl  = new JLabel(icon);
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLbl  = UITheme.makeLabel(label, UITheme.FONT_SMALL, UITheme.TEXT_MUTED);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLbl.putClientProperty("type", "name");

        JLabel valLbl   = UITheme.makeLabel(initVal, UITheme.FONT_TITLE, accent);
        valLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        valLbl.putClientProperty("type", "value");

        card.add(iconLbl);
        card.add(Box.createVerticalStrut(8));
        card.add(nameLbl);
        card.add(Box.createVerticalStrut(4));
        card.add(valLbl);
        return card;
    }

    // Extracts the value JLabel (the one with type="value") from a KPI card.
    private JLabel extractValueLabel(JPanel card) {
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel) {
                JLabel l = (JLabel) c;
                if ("value".equals(l.getClientProperty("type"))) return l;
            }
        }
        return new JLabel();
    }

    // Recalculates and updates all KPI card values.
    private void updateKpis() {
        if (totalOrdersVal == null) return;
        int total     = orderManager.getOrderCount();
        int ongoing   = orderManager.countByStatus("Ongoing");
        int completed = orderManager.countByStatus("Completed");
        double rev    = orderManager.getTotalRevenue();
        totalOrdersVal.setText(String.valueOf(total));
        ongoingVal.setText(String.valueOf(ongoing));
        completedVal.setText(String.valueOf(completed));
        revenueVal.setText(String.format("\u20B9 %,.0f", rev));
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // PAGE: Orders
    // ══════════════════════════════════════════════════════════════════════════════
    private void showOrders() {
        contentArea.removeAll();

        JPanel page = new JPanel(new BorderLayout(0, 12));
        page.setBackground(UITheme.BG);
        page.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Title row + filter toolbar
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(UITheme.BG);

        JLabel heading = UITheme.makeTitleLabel("Orders");
        topRow.add(heading, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setBackground(UITheme.BG);
        JButton allBtn       = filterBtn("All",       "ALL");
        JButton ongoingBtn   = filterBtn("Ongoing",   "Ongoing");
        JButton completedBtn = filterBtn("Completed", "Completed");
        JButton markBtn      = UITheme.makePrimaryBtn("Mark Completed \u2714");
        JButton refreshBtn   = UITheme.makeSecondaryBtn("\u21BB Refresh");

        markBtn.addActionListener(e -> doMarkCompleted());
        refreshBtn.addActionListener(e -> fillOrderTable());

        toolbar.add(allBtn);
        toolbar.add(ongoingBtn);
        toolbar.add(completedBtn);
        toolbar.add(Box.createHorizontalStrut(12));
        toolbar.add(markBtn);
        toolbar.add(refreshBtn);
        topRow.add(toolbar, BorderLayout.EAST);

        page.add(topRow, BorderLayout.NORTH);

        // Orders table
        orderModel = new DefaultTableModel(
            new String[]{ "Order ID", "Customer", "Items", "Total (Rs.)", "Status" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        orderTable = new JTable(orderModel);
        UITheme.styleTable(orderTable);
        orderTable.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());

        JScrollPane sp = UITheme.makeScroll(orderTable);
        page.add(sp, BorderLayout.CENTER);

        contentArea.add(page, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
        fillOrderTable();
    }

    private JButton filterBtn(String label, String filter) {
        JButton btn = orderFilter.equals(filter) ?
            UITheme.makePrimaryBtn(label) : UITheme.makeSecondaryBtn(label);
        btn.setPreferredSize(new Dimension(90, 30));
        btn.addActionListener(e -> {
            orderFilter = filter;
            showOrders();
        });
        return btn;
    }

    private void fillOrderTable() {
        if (orderModel == null) return;
        orderModel.setRowCount(0);
        Order[] orders = orderManager.getOrders();
        int i = 0;
        // CONCEPT: Arrays - iterating Order[] with while loop
        while (i < orderManager.getOrderCount()) {
            Order o = orders[i];
            boolean show = orderFilter.equals("ALL") ||
                           o.getStatus().equalsIgnoreCase(orderFilter);
            if (show) {
                orderModel.addRow(new Object[]{
                    o.getOrderId(), o.getCustomerName(),
                    o.getItemsSummary(),
                    String.format("%,.0f", o.getGrandTotal()),
                    o.getStatus() });
            }
            i = i + 1;
        }
    }

    private void doMarkCompleted() {
        try {
            int row = orderTable.getSelectedRow();
            if (row < 0) throw new Exception("Please select an order row first.");
            int orderId = Integer.parseInt(orderModel.getValueAt(row, 0).toString());
            orderManager.markCompleted(orderId);
            fillOrderTable();
            updateKpis();
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Custom renderer for the Status column.
    private static class StatusRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                t, val, sel, foc, row, col);
            String status = val == null ? "" : val.toString();
            if (status.equalsIgnoreCase("Completed")) {
                lbl.setForeground(UITheme.SUCCESS);
                lbl.setFont(UITheme.FONT_SUBHEAD);
            } else {
                lbl.setForeground(UITheme.WARN);
                lbl.setFont(UITheme.FONT_SUBHEAD);
            }
            if (sel) lbl.setBackground(new Color(0xE3F2FD));
            else     lbl.setBackground(UITheme.CARD);
            lbl.setOpaque(true);
            return lbl;
        }
    }

    // ══════════════════════════════════════════════════════════════════════════════
    // PAGE: Products
    // ══════════════════════════════════════════════════════════════════════════════
    private void showProducts() {
        contentArea.removeAll();

        JPanel page = new JPanel(new BorderLayout(0, 12));
        page.setBackground(UITheme.BG);
        page.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // Title + toolbar
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(UITheme.BG);
        JLabel heading = UITheme.makeTitleLabel("Products");
        topRow.add(heading, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        toolbar.setBackground(UITheme.BG);
        JButton addProdBtn    = UITheme.makePrimaryBtn("+ Add Product");
        JButton editStockBtn  = UITheme.makeSecondaryBtn("Edit Stock");
        addProdBtn.addActionListener(e -> showAddProductDialog());
        editStockBtn.addActionListener(e -> showEditStockDialog());
        toolbar.add(addProdBtn);
        toolbar.add(editStockBtn);
        topRow.add(toolbar, BorderLayout.EAST);
        page.add(topRow, BorderLayout.NORTH);

        // Product table
        productModel = new DefaultTableModel(
            new String[]{ "ID", "Name", "Category", "Price (Rs.)", "Stock", "Status" }, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        productTable = new JTable(productModel);
        UITheme.styleTable(productTable);
        productTable.getColumnModel().getColumn(5).setCellRenderer(new StockRenderer());

        JScrollPane sp = UITheme.makeScroll(productTable);
        page.add(sp, BorderLayout.CENTER);

        contentArea.add(page, BorderLayout.CENTER);
        contentArea.revalidate();
        contentArea.repaint();
        fillProductTable();
    }

    // CONCEPT: Arrays - iterate Product[] and populate table
    private void fillProductTable() {
        if (productModel == null) return;
        productModel.setRowCount(0);
        int i = 0;
        while (i < catalogue.length) {
            Product p = catalogue[i];
            String status = p.getStock() == 0 ? "Out of Stock" :
                            p.getStock() < 4  ? "Low Stock"   : "In Stock";
            productModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getCategory(),
                String.format("%,.0f", p.getPrice()),
                p.getStock(), status });
            i = i + 1;
        }
    }

    // Dialog to add a new product to the catalogue.
    private void showAddProductDialog() {
        JDialog dialog = new JDialog(this, "Add New Product", true);
        dialog.setSize(380, 360);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(UITheme.CARD);

        JPanel form = new JPanel(new GridLayout(6, 2, 8, 10));
        form.setBackground(UITheme.CARD);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField nameF  = UITheme.makeTextField(14);
        JTextField priceF = UITheme.makeTextField(14);
        JTextField stockF = UITheme.makeTextField(14);
        JTextField emojiF = UITheme.makeTextField(14);

        String[] cats = { "Electronics", "Fashion", "Kitchen", "Books", "Sports" };
        JComboBox<String> catBox = new JComboBox<>(cats);
        catBox.setFont(UITheme.FONT_BODY);

        form.add(UITheme.makeBodyLabel("Name:"));      form.add(nameF);
        form.add(UITheme.makeBodyLabel("Category:"));  form.add(catBox);
        form.add(UITheme.makeBodyLabel("Price (Rs.):"));form.add(priceF);
        form.add(UITheme.makeBodyLabel("Stock:"));     form.add(stockF);
        form.add(UITheme.makeBodyLabel("Emoji Icon:")); form.add(emojiF);

        JButton saveBtn   = UITheme.makePrimaryBtn("Add Product");
        JButton cancelBtn = UITheme.makeSecondaryBtn("Cancel");

        // CONCEPT: Exception Handling - parse price and stock
        saveBtn.addActionListener(e -> {
            try {
                String name     = nameF.getText().trim();
                String category = catBox.getSelectedItem().toString();
                double price    = Double.parseDouble(priceF.getText().trim());
                int    stock    = Integer.parseInt(stockF.getText().trim());
                String emoji    = emojiF.getText().trim().isEmpty() ? "\uD83D\uDCE6" : emojiF.getText().trim();

                if (name.isEmpty()) throw new Exception("Name cannot be empty.");
                if (price <= 0)     throw new Exception("Price must be positive.");
                if (stock < 0)      throw new Exception("Stock cannot be negative.");

                // Expand catalogue by creating a new larger array
                // CONCEPT: Arrays - manual array expansion (no ArrayList)
                Product[] newCat = new Product[catalogue.length + 1];
                int j = 0;
                while (j < catalogue.length) {
                    newCat[j] = catalogue[j];
                    j = j + 1;
                }
                int newId = catalogue.length + 1;
                newCat[catalogue.length] = new Product(newId, name, price, stock, category, emoji);
                catalogue = newCat;
                // Update the ShoppingWindow's reference via loginWindow
                fillProductTable();
                dialog.dispose();
                JOptionPane.showMessageDialog(this,
                    "Product \"" + name + "\" added successfully!",
                    "Product Added", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());

        form.add(saveBtn);
        form.add(cancelBtn);
        dialog.add(form);
        dialog.setVisible(true);
    }

    // Dialog to update stock of a selected product.
    private void showEditStockDialog() {
        try {
            int row = productTable.getSelectedRow();
            if (row < 0) throw new Exception("Please select a product row first.");
            int productId = Integer.parseInt(productModel.getValueAt(row, 0).toString());

            Product target = null;
            int i = 0;
            while (i < catalogue.length) {
                if (catalogue[i].getId() == productId) { target = catalogue[i]; break; }
                i = i + 1;
            }
            if (target == null) throw new Exception("Product not found.");

            // CONCEPT: Strings - JOptionPane input dialog
            String input = JOptionPane.showInputDialog(this,
                "New stock for \"" + target.getName() + "\":",
                String.valueOf(target.getStock()));
            if (input == null) return;

            int newStock = Integer.parseInt(input.trim());
            if (newStock < 0) throw new Exception("Stock cannot be negative.");
            target.setStock(newStock);
            fillProductTable();

        } catch (NumberFormatException ex) {
            showError("Please enter a valid integer for stock.");
        } catch (Exception ex) {
            showError(ex.getMessage());
        }
    }

    // Custom renderer for Stock Status column.
    private static class StockRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean foc, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                t, val, sel, foc, row, col);
            String status = val == null ? "" : val.toString();
            if (status.equals("Out of Stock")) lbl.setForeground(new Color(0xC62828));
            else if (status.equals("Low Stock")) lbl.setForeground(UITheme.WARN);
            else lbl.setForeground(UITheme.SUCCESS);
            lbl.setFont(UITheme.FONT_SMALL);
            lbl.setOpaque(true);
            lbl.setBackground(sel ? new Color(0xE3F2FD) : UITheme.CARD);
            return lbl;
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
