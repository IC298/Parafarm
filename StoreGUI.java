package parafarm;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

// ============================================================
// StoreGUI.java - Γραφική Διεπαφή Χρήστη (Java Swing)
// Εμφανίζει τους καταλόγους σε JTable και παρέχει
// φόρμα δημιουργίας νέας παραγγελίας.
// ============================================================
public class StoreGUI extends JFrame {

    private Store store;

    // ── Χρώματα / Styling ───────────────────────────────────
    private static final Color COLOR_BG        = new Color(245, 247, 250);
    private static final Color COLOR_PRIMARY   = new Color(33,  90,  168);
    private static final Color COLOR_ACCENT    = new Color(52, 152, 219);
    private static final Color COLOR_SUCCESS   = new Color(39, 174,  96);
    private static final Color COLOR_WARNING   = new Color(230,126,  34);
    private static final Color COLOR_WHITE     = Color.WHITE;
    private static final Color COLOR_ROW_ALT   = new Color(235, 242, 252);
    private static final Font  FONT_TITLE      = new Font("SansSerif", Font.BOLD, 20);
    private static final Font  FONT_TAB        = new Font("SansSerif", Font.BOLD, 13);
    private static final Font  FONT_LABEL      = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font  FONT_BOLD       = new Font("SansSerif", Font.BOLD,  13);

    // ── Tabs ────────────────────────────────────────────────
    private JTabbedPane tabbedPane;

    // ── Tables ──────────────────────────────────────────────
    private JTable productTable, customerTable, employeeTable,
                   orderTable, supplierOrderTable;

    // ── New-Order form fields ────────────────────────────────
    private JComboBox<String> cbCustomer;
    private JComboBox<String> cbEmployee;
    private JComboBox<String> cbProduct;
    private JSpinner           spQuantity;
    private DefaultTableModel  orderItemsModel;   // temp list in form
    private JLabel             lblOrderTotal;

    // Running total for the order being built
    private double             orderTotal = 0.0;

    // ============================================================
    // Constructor
    // ============================================================
    public StoreGUI(Store store) {
        this.store = store;

        setTitle("Pharmacy Store — Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setBackground(COLOR_BG);

        buildMenuBar();
        buildMainPanel();

        setVisible(true);
    }

    // ============================================================
    // MENU BAR
    // ============================================================
    private void buildMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(COLOR_PRIMARY);
        menuBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        String[] menuNames = {"Products", "Customers", "Employees",
                              "Orders", "Supplier Orders", "New Order", "Stock Check"};
        int[]    tabIndex  = {0, 1, 2, 3, 4, 5, -1};

        for (int i = 0; i < menuNames.length; i++) {
            final int idx = tabIndex[i];
            final String name = menuNames[i];
            JMenu menu = new JMenu(name);
            menu.setForeground(COLOR_WHITE);
            menu.setFont(FONT_TAB);
            menu.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

            if (idx >= 0) {
                JMenuItem goTo = new JMenuItem("Go to " + name);
                goTo.addActionListener(e -> tabbedPane.setSelectedIndex(idx));
                menu.add(goTo);
            } else {
                // Stock Check — triggers directly
                JMenuItem check = new JMenuItem("Run Stock Check Now");
                check.addActionListener(e -> runStockCheck());
                menu.add(check);
            }
            menuBar.add(menu);
        }

        setJMenuBar(menuBar);
    }

    // ============================================================
    // MAIN PANEL — tabbed pane
    // ============================================================
    private void buildMainPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG);

        // Header
        JLabel header = new JLabel("  Pharmacy Store — Management System", JLabel.LEFT);
        header.setFont(FONT_TITLE);
        header.setForeground(COLOR_WHITE);
        header.setOpaque(true);
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 0));
        root.add(header, BorderLayout.NORTH);

        // Tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_TAB);
        tabbedPane.setBackground(COLOR_BG);

        tabbedPane.addTab("📦 Products",        buildProductTab());
        tabbedPane.addTab("👤 Customers",       buildCustomerTab());
        tabbedPane.addTab("👷 Employees",       buildEmployeeTab());
        tabbedPane.addTab("📋 Orders",          buildOrderTab());
        tabbedPane.addTab("🚚 Supplier Orders", buildSupplierOrderTab());
        tabbedPane.addTab("➕ New Order",       buildNewOrderTab());

        root.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(root);
    }

    // ============================================================
    // TAB HELPERS
    // ============================================================

    /** Wraps a JTable in a styled scroll pane */
    private JScrollPane styledScroll(JTable table) {
        table.setFont(FONT_LABEL);
        table.setRowHeight(28);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(COLOR_WHITE);
        table.setGridColor(new Color(210, 220, 235));
        table.setSelectionBackground(COLOR_ACCENT);
        table.setSelectionForeground(COLOR_WHITE);
        table.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        table.setEnabled(false); // read-only

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_ACCENT, 1));
        return scroll;
    }

    /** Creates a titled panel */
    private JPanel titledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 8));
        p.setBackground(COLOR_BG);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT),
                "  " + title + "  ",
                TitledBorder.LEFT, TitledBorder.TOP,
                FONT_BOLD, COLOR_PRIMARY));
        return p;
    }

    /** Refresh button */
    private JButton refreshBtn(Runnable action) {
        JButton btn = new JButton("🔄 Refresh");
        styleBtn(btn, COLOR_ACCENT);
        btn.addActionListener(e -> action.run());
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        p.setBackground(COLOR_BG);
        p.add(btn);
        return btn;
    }

    private void styleBtn(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(COLOR_WHITE);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }

    // ============================================================
    // 1 — PRODUCTS TAB
    // ============================================================
    private JPanel buildProductTab() {
        JPanel panel = titledPanel("Product Catalogue");

        String[] cols = {"Name", "Category", "Code", "Price (€)", "Quantity"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        populateProducts(model);

        productTable = new JTable(model);
        panel.add(styledScroll(productTable), BorderLayout.CENTER);

        JButton btnRefresh = refreshBtn(() -> {
            model.setRowCount(0);
            populateProducts(model);
        });
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(COLOR_BG);
        south.add(btnRefresh);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private void populateProducts(DefaultTableModel model) {
        for (Product p : store.getProductCatalogue()) {
            model.addRow(new Object[]{
                p.getName(), p.getCategory(), p.getCode(),
                String.format("%.2f", p.getPrice()), p.getQuantity()
            });
        }
    }

    // ============================================================
    // 2 — CUSTOMERS TAB
    // ============================================================
    private JPanel buildCustomerTab() {
        JPanel panel = titledPanel("Customer Catalogue");

        String[] cols = {"Name", "Type", "AFM", "DOY", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Customer c : store.getCustomerCatalogue()) {
            model.addRow(new Object[]{
                c.getName(), c.getType(), c.getAfm(), c.getDoy(), c.getStatus()
            });
        }
        customerTable = new JTable(model);
        panel.add(styledScroll(customerTable), BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    // 3 — EMPLOYEES TAB
    // ============================================================
    private JPanel buildEmployeeTab() {
        JPanel panel = titledPanel("Employee Catalogue");

        String[] cols = {"Name", "Role", "Account Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (User u : store.getEmployeeCatalogue()) {
            model.addRow(new Object[]{
                u.getFullName(), u.getRole(), u.getAccountType()
            });
        }
        employeeTable = new JTable(model);
        panel.add(styledScroll(employeeTable), BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    // 4 — ORDERS TAB
    // ============================================================
    private JPanel buildOrderTab() {
        JPanel panel = titledPanel("Order Catalogue");

        String[] cols = {"Customer", "AFM", "Processed By", "Date", "State", "Document", "Products"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        populateOrders(model);

        orderTable = new JTable(model);
        panel.add(styledScroll(orderTable), BorderLayout.CENTER);

        JButton btnRefresh = refreshBtn(() -> {
            model.setRowCount(0);
            populateOrders(model);
        });
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(COLOR_BG);
        south.add(btnRefresh);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private void populateOrders(DefaultTableModel model) {
        for (Order o : store.getOrderCatalogue()) {
            // Build product summary string
            StringBuilder sb = new StringBuilder();
            List<Product> prods = o.getProducts();
            List<Integer> qtys  = o.getQuantities();
            for (int i = 0; i < prods.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(prods.get(i).getName()).append(" x").append(qtys.get(i));
            }
            // Document type
            String docType = o.getCustomer().isPharmacy() ? "Invoice" : "Receipt";

            model.addRow(new Object[]{
                o.getCustomer().getName(),
                o.getCustomer().getAfm(),
                o.getProcessedBy().getFullName(),
                o.getDate(),
                o.getState(),
                docType,
                sb.toString()
            });
        }
    }

    // ============================================================
    // 5 — SUPPLIER ORDERS TAB
    // ============================================================
    private JPanel buildSupplierOrderTab() {
        JPanel panel = titledPanel("Supplier Order Catalogue");

        String[] cols = {"Product", "Quantity", "Date", "Reason"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        populateSupplierOrders(model);

        supplierOrderTable = new JTable(model);
        panel.add(styledScroll(supplierOrderTable), BorderLayout.CENTER);

        JButton btnRefresh = refreshBtn(() -> {
            model.setRowCount(0);
            populateSupplierOrders(model);
        });
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.setBackground(COLOR_BG);
        south.add(btnRefresh);
        panel.add(south, BorderLayout.SOUTH);

        return panel;
    }

    private void populateSupplierOrders(DefaultTableModel model) {
        for (SupplierOrder so : store.getSupplierOrderCatalogue()) {
            model.addRow(new Object[]{
                so.getProduct().getName(),
                so.getQuantity(),
                so.getDate(),
                so.getReason()
            });
        }
    }

    // ============================================================
    // 6 — NEW ORDER TAB
    // ============================================================
    private JPanel buildNewOrderTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // ── Title ───────────────────────────────────────────
        JLabel title = new JLabel("Create New Order");
        title.setFont(FONT_TITLE);
        title.setForeground(COLOR_PRIMARY);
        panel.add(title, BorderLayout.NORTH);

        // ── Centre: form + items table ───────────────────────
        JPanel centre = new JPanel(new GridLayout(1, 2, 16, 0));
        centre.setBackground(COLOR_BG);

        // LEFT — form fields
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_WHITE);
        form.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT),
                "  Order Details  ", TitledBorder.LEFT, TitledBorder.TOP,
                FONT_BOLD, COLOR_PRIMARY));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Customer
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        form.add(makeLabel("Customer:"), gbc);
        cbCustomer = new JComboBox<>();
        for (Customer c : store.getCustomerCatalogue())
            cbCustomer.addItem(c.getName() + " [" + c.getType() + "]");
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cbCustomer, gbc);

        // Employee
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        form.add(makeLabel("Processed By:"), gbc);
        cbEmployee = new JComboBox<>();
        for (User u : store.getEmployeeCatalogue())
            cbEmployee.addItem(u.getFullName() + " [" + u.getRole() + "]");
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cbEmployee, gbc);

        // Divider label
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel lblAdd = new JLabel("── Add Product to Order ──");
        lblAdd.setFont(FONT_BOLD);
        lblAdd.setForeground(COLOR_ACCENT);
        form.add(lblAdd, gbc);
        gbc.gridwidth = 1;

        // Product
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        form.add(makeLabel("Product:"), gbc);
        cbProduct = new JComboBox<>();
        for (Product p : store.getProductCatalogue())
            cbProduct.addItem(p.getName() + " (stock: " + p.getQuantity() + ")");
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(cbProduct, gbc);

        // Quantity
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        form.add(makeLabel("Quantity:"), gbc);
        spQuantity = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        gbc.gridx = 1; gbc.weightx = 1;
        form.add(spQuantity, gbc);

        // Add item button
        JButton btnAddItem = new JButton("➕ Add Product to Order");
        styleBtn(btnAddItem, COLOR_ACCENT);
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        form.add(btnAddItem, gbc);

        // Total
        lblOrderTotal = new JLabel("Total: 0.00 €");
        lblOrderTotal.setFont(FONT_BOLD);
        lblOrderTotal.setForeground(COLOR_PRIMARY);
        gbc.gridy = 6;
        form.add(lblOrderTotal, gbc);

        centre.add(form);

        // RIGHT — order items preview table
        JPanel itemsPanel = new JPanel(new BorderLayout(0, 6));
        itemsPanel.setBackground(COLOR_BG);
        itemsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_ACCENT),
                "  Items in this Order  ", TitledBorder.LEFT, TitledBorder.TOP,
                FONT_BOLD, COLOR_PRIMARY));

        String[] itemCols = {"Product", "Qty", "Unit Price (€)", "Subtotal (€)"};
        orderItemsModel = new DefaultTableModel(itemCols, 0);
        JTable tblItems = new JTable(orderItemsModel);
        tblItems.setFont(FONT_LABEL);
        tblItems.setRowHeight(26);
        tblItems.getTableHeader().setFont(FONT_BOLD);
        tblItems.getTableHeader().setBackground(COLOR_PRIMARY);
        tblItems.getTableHeader().setForeground(COLOR_WHITE);
        tblItems.setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        itemsPanel.add(new JScrollPane(tblItems), BorderLayout.CENTER);

        // Remove last item button
        JButton btnRemove = new JButton("🗑 Remove Last Item");
        styleBtn(btnRemove, COLOR_WARNING);
        JPanel removePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        removePanel.setBackground(COLOR_BG);
        removePanel.add(btnRemove);
        itemsPanel.add(removePanel, BorderLayout.SOUTH);

        centre.add(itemsPanel);
        panel.add(centre, BorderLayout.CENTER);

        // ── SOUTH — submit + clear ───────────────────────────
        JButton btnSubmit = new JButton("✅ Submit Order");
        styleBtn(btnSubmit, COLOR_SUCCESS);
        JButton btnClear  = new JButton("🔄 Clear");
        styleBtn(btnClear,  new Color(120, 120, 120));

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        south.setBackground(COLOR_BG);
        south.add(btnClear);
        south.add(btnSubmit);
        panel.add(south, BorderLayout.SOUTH);

        // ── ACTIONS ──────────────────────────────────────────

        // Add item to order preview
        btnAddItem.addActionListener(e -> {
            int pIdx = cbProduct.getSelectedIndex();
            if (pIdx < 0) return;
            Product p   = store.getProductCatalogue().get(pIdx);
            int     qty = (Integer) spQuantity.getValue();
            if (qty > p.getQuantity()) {
                JOptionPane.showMessageDialog(this,
                    "⚠️ Insufficient stock! Available: " + p.getQuantity(),
                    "Stock Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            double sub = p.getPrice() * qty;
            orderTotal += sub;
            orderItemsModel.addRow(new Object[]{
                p.getName(), qty,
                String.format("%.2f", p.getPrice()),
                String.format("%.2f", sub)
            });
            lblOrderTotal.setText(String.format("Total: %.2f €", orderTotal));
        });

        // Remove last item
        btnRemove.addActionListener(e -> {
            int row = orderItemsModel.getRowCount() - 1;
            if (row < 0) return;
            double sub = Double.parseDouble(
                    orderItemsModel.getValueAt(row, 3).toString());
            orderTotal -= sub;
            orderItemsModel.removeRow(row);
            lblOrderTotal.setText(String.format("Total: %.2f €", orderTotal));
        });

        // Submit order
        btnSubmit.addActionListener(e -> submitOrder());

        // Clear form
        btnClear.addActionListener(e -> clearOrderForm());

        return panel;
    }

    // ── Submit order logic ───────────────────────────────────
    private void submitOrder() {
        if (orderItemsModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "Please add at least one product to the order.",
                "Empty Order", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int custIdx = cbCustomer.getSelectedIndex();
        int empIdx  = cbEmployee.getSelectedIndex();
        if (custIdx < 0 || empIdx < 0) return;

        Customer customer = store.getCustomerCatalogue().get(custIdx);
        User     employee = store.getEmployeeCatalogue().get(empIdx);
        Order    order    = new Order("2026-06-10", customer, employee);

        // Add products from the preview table
        for (int i = 0; i < orderItemsModel.getRowCount(); i++) {
            String prodName = orderItemsModel.getValueAt(i, 0).toString();
            int    qty      = Integer.parseInt(orderItemsModel.getValueAt(i, 1).toString());
            for (Product p : store.getProductCatalogue()) {
                if (p.getName().equals(prodName)) {
                    order.addProduct(p, qty);
                    break;
                }
            }
        }

        store.addOrder(order);
        SalesDocument doc = store.processOrder(order);

        // Determine document type for message
        String docMsg = (doc instanceof Invoice)
            ? "Invoice issued (AFM: " + customer.getAfm() + ")"
            : "Receipt issued";

        JOptionPane.showMessageDialog(this,
            "✅ Order created successfully!\n" +
            "Customer : " + customer.getName() + "\n" +
            "Document : " + docMsg + "\n" +
            String.format("Total    : %.2f €", orderTotal),
            "Order Confirmed", JOptionPane.INFORMATION_MESSAGE);

        // Check if any stock fell below safety limit
        List<SupplierOrder> before = store.getSupplierOrderCatalogue();
        int countBefore = before.size();
        store.checkStockAndReorder("2026-06-10");
        int countAfter = store.getSupplierOrderCatalogue().size();

        if (countAfter > countBefore) {
            StringBuilder sb = new StringBuilder();
            sb.append("⚠️  Stock Alert!\n\n");
            sb.append("The following supplier orders were created automatically:\n\n");
            List<SupplierOrder> soList = store.getSupplierOrderCatalogue();
            for (int i = countBefore; i < countAfter; i++) {
                SupplierOrder so = soList.get(i);
                sb.append("• ").append(so.getProduct().getName())
                  .append(" — qty: ").append(so.getQuantity()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(),
                "Supplier Order Created", JOptionPane.WARNING_MESSAGE);
        }

        clearOrderForm();
        // Refresh product combo stock numbers
        cbProduct.removeAllItems();
        for (Product p : store.getProductCatalogue())
            cbProduct.addItem(p.getName() + " (stock: " + p.getQuantity() + ")");
    }

    private void clearOrderForm() {
        orderItemsModel.setRowCount(0);
        orderTotal = 0.0;
        lblOrderTotal.setText("Total: 0.00 €");
        spQuantity.setValue(1);
    }

    // ── Stock check (from menu) ──────────────────────────────
    private void runStockCheck() {
        int before = store.getSupplierOrderCatalogue().size();
        store.checkStockAndReorder("2026-06-10");
        int after = store.getSupplierOrderCatalogue().size();

        if (after > before) {
            StringBuilder sb = new StringBuilder("⚠️  New supplier orders created:\n\n");
            List<SupplierOrder> soList = store.getSupplierOrderCatalogue();
            for (int i = before; i < after; i++) {
                SupplierOrder so = soList.get(i);
                sb.append("• ").append(so.getProduct().getName())
                  .append(" — qty: ").append(so.getQuantity()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(),
                "Stock Check — Alert", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "✅ All products are above their safety limits.",
                "Stock Check — OK", JOptionPane.INFORMATION_MESSAGE);
        }
        // Switch to supplier orders tab to see result
        tabbedPane.setSelectedIndex(4);
    }

    // ── Helper ──────────────────────────────────────────────
    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(COLOR_PRIMARY);
        return lbl;
    }

    // ============================================================
    // ALTERNATING ROW RENDERER
    // ============================================================
    private class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                setBackground(row % 2 == 0 ? COLOR_WHITE : COLOR_ROW_ALT);
                setForeground(Color.DARK_GRAY);
            }
            setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            return this;
        }
    }

    // ============================================================
    // MAIN — φορτώνει τα δεδομένα σεναρίου και ανοίγει το GUI
    // ============================================================
    public static void main(String[] args) {

        // ── Φόρτωση δεδομένων σεναρίου (ίδια λογική με Main.java) ──
        Store  store = new Store();
        String today = "2026-06-10";

        // Products
        Product product1 = new Product("Thermometer", "paramedical", "P001", 12.50, 80);
        Product product2 = new Product("Shoes",       "paramedical", "P002", 35.00, 45);
        Product product3 = new Product("Cream",       "cosmetic",    "P003", 18.00, 25);
        Product product4 = new Product("Makeup",      "cosmetic",    "P004", 22.00, 60);
        store.addProduct(product1); store.addProduct(product2);
        store.addProduct(product3); store.addProduct(product4);
        store.setSafetyLimit(product1, 30);
        store.setSafetyLimit(product2, 20);
        store.setSafetyLimit(product3, 40);
        store.setSafetyLimit(product4, 25);

        // Customers
        store.addCustomer(new Customer("Farmakeio Papadopoulou","Pharmacy","099845210","DOY Kalamarias","OK"));
        store.addCustomer(new Customer("Farmakeio Nikolaidis",  "Pharmacy","078632145","DOY Evosmou","OK"));
        store.addCustomer(new Customer("Georgiou Maria",        "Retail",  "112233445","DOY Thessalonikis","OK"));

        // Employees
        WarehouseWorker       emp1 = new WarehouseWorker      ("Antoniou",    "warehouse_user", "antoniou",    "pass123");
        CustomerServiceWorker emp2 = new CustomerServiceWorker("Eleftheriou", "service_user",   "eleftheriou", "pass123");
        Cashier               emp3 = new Cashier              ("Sotiriou",    "cashier_user",   "sotiriou",    "pass123");
        Seller                emp4 = new Seller               ("Seller1",     "sales_user",     "seller1",     "pass123");
        store.addEmployee(emp1); store.addEmployee(emp2);
        store.addEmployee(emp3); store.addEmployee(emp4);

        // Orders (scenario)
        Customer c1 = store.getCustomerCatalogue().get(0);
        Customer c2 = store.getCustomerCatalogue().get(1);
        Customer c3 = store.getCustomerCatalogue().get(2);

        Order order1 = new Order(today, c1, emp4);
        order1.addProduct(product1, 10); order1.addProduct(product3, 5);
        store.addOrder(order1); store.processOrder(order1);

        Order order2 = new Order(today, c1, emp4);
        order2.addProduct(product2, 8); order2.addProduct(product4, 12);
        store.addOrder(order2);
        store.cancelOrder(order2); // ακύρωση — δεν εκτελείται

        Order order3 = new Order(today, c2, emp4);
        order3.addProduct(product3, 20); order3.addProduct(product4, 10);
        order3.addProduct(product1, 6);
        store.addOrder(order3); store.processOrder(order3);

        Order order4 = new Order(today, c3, emp3);
        order4.addProduct(product1, 1); order4.addProduct(product4, 2);
        store.addOrder(order4); store.processOrder(order4);

        // Stock check → auto supplier order για Cream
        store.checkStockAndReorder(today);

        // ── Launch GUI ──────────────────────────────────────
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new StoreGUI(store);
        });
    }
}

