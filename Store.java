package parafarm;

import java.util.ArrayList;
import java.util.List;

public class Store {

    private List<Product>       productCatalogue;
    private List<Customer>      customerCatalogue;
    private List<User>          employeeCatalogue;
    private List<Order>         orderCatalogue;
    private List<SupplierOrder> supplierOrderCatalogue;
    private List<Stock>         stockCatalogue;

    private static final int DEFAULT_REORDER_QTY = 50;

    public Store() {
        productCatalogue       = new ArrayList<>();
        customerCatalogue      = new ArrayList<>();
        employeeCatalogue      = new ArrayList<>();
        orderCatalogue         = new ArrayList<>();
        supplierOrderCatalogue = new ArrayList<>();
        stockCatalogue          = new ArrayList<>();
    }

    // ════════════════════════════════════════════════════════
    // ADD methods
    // ════════════════════════════════════════════════════════

    public void addProduct(Product p) {
        productCatalogue.add(p);
        stockCatalogue.add(new Stock(p, 0)); // αρχικό όριο 0, ορίζεται μετά
        System.out.println("Object " + p.getName() + " has been created and added to Product Catalogue.");
    }

    public void addCustomer(Customer c) {
        customerCatalogue.add(c);
        System.out.println("Object " + c.getName() + " has been created and added to Customer Catalogue.");
    }

    public void addEmployee(User u) {
        employeeCatalogue.add(u);
        System.out.println("Object " + u.getFullName() + " has been created and added to Employee Catalogue.");
    }

    public void addOrder(Order o) {
        orderCatalogue.add(o);
        System.out.println("Order for customer " + o.getCustomer().getName() + " has been added.");
    }

    // ════════════════════════════════════════════════════════
    // CANCEL order
    // ════════════════════════════════════════════════════════

    public void cancelOrder(Order o) {
        if (orderCatalogue.remove(o)) {
            o.setState("Cancelled");
            System.out.println("Order for " + o.getCustomer().getName() + " has been cancelled and removed.");
        } else {
            System.out.println("Order not found in catalogue.");
        }
    }

    // ════════════════════════════════════════════════════════
    // SetLimit — ορισμός ορίου ασφαλείας (Stock.SetLimit στο διάγραμμα)
    // ════════════════════════════════════════════════════════

    public void setSafetyLimit(Product product, int limit) {
        for (Stock s : stockCatalogue) {
            if (s.getProduct().equals(product)) {
                s.setLimit(limit);
                System.out.println("Safety limit for " + product.getName() + " set to " + limit + ".");
                return;
            }
        }
        System.out.println("Product not found in stock catalogue.");
    }

    // ════════════════════════════════════════════════════════
    // STOCK CHECK & αυτόματη αναπλήρωση
    // ════════════════════════════════════════════════════════

    public void checkStockAndReorder(String date) {
        System.out.println("\n--- Running Stock Check ---");
        for (Stock s : stockCatalogue) {
            if (s.isBelowLimit()) {
                Product p = s.getProduct();
                System.out.println("WARNING: " + p.getName()
                        + " is below safety limit (qty=" + p.getQuantity()
                        + ", limit=" + s.getLimit() + ").");

                if (hasPendingSupplierOrder(p)) {
                    System.out.println("Skipped: a supplier order for " + p.getName()
                            + " is already pending.");
                    continue;
                }

                SupplierOrder so = new SupplierOrder(p, DEFAULT_REORDER_QTY, date);
                supplierOrderCatalogue.add(so);
                System.out.println("Supplier order created automatically for: " + p.getName()
                        + " (qty=" + DEFAULT_REORDER_QTY + ").");
            }
        }
        System.out.println("--- Stock Check Complete ---\n");
    }

    private boolean hasPendingSupplierOrder(Product product) {
        for (SupplierOrder so : supplierOrderCatalogue) {
            if (so.getProduct().equals(product)) return true;
        }
        return false;
    }

    // ════════════════════════════════════════════════════════
    // PROCESS ORDER — μείωση αποθέματος + έκδοση παραστατικού
    //   Pharmacy → Invoice | Retail → Receipt
    // ════════════════════════════════════════════════════════

    public SalesDocument processOrder(Order order) {
        Customer customer = order.getCustomer();
        double   total    = 0.0;

        List<Product> products   = order.getProducts();
        List<Integer> quantities = order.getQuantities();

        for (int i = 0; i < products.size(); i++) {
            Product prod = products.get(i);
            int     qty  = quantities.get(i);
            prod.setQuantity(prod.getQuantity() - qty);
            total += prod.getPrice() * qty;
            System.out.println("Stock updated: " + prod.getName()
                    + " -> new quantity = " + prod.getQuantity());
        }

        order.placeOrder();

        SalesDocument doc;
        if (customer.isPharmacy()) {
            doc = new Invoice(total, customer.getAfm());
            System.out.println("Invoice issued for " + customer.getName()
                    + " (AFM: " + customer.getAfm() + "), amount: " + total + " EUR");
        } else {
            doc = new Receipt(total);
            System.out.println("Receipt issued for " + customer.getName()
                    + ", amount: " + total + " EUR");
        }

        return doc;
    }

    // ════════════════════════════════════════════════════════
    // FIND helpers
    // ════════════════════════════════════════════════════════

    public Product findProductByCode(String code) {
        for (Product p : productCatalogue) {
            if (p.getCode().equals(code)) return p;
        }
        return null;
    }

    public Customer findCustomerByAfm(String afm) {
        for (Customer c : customerCatalogue) {
            if (c.getAfm().equals(afm)) return c;
        }
        return null;
    }

    // ════════════════════════════════════════════════════════
    // PRINT ALL CATALOGUES
    // ════════════════════════════════════════════════════════

    public void printAllCatalogues() {
        System.out.println("\n========== PRODUCT CATALOGUE ==========");
        for (Product p : productCatalogue)              { p.printData(); System.out.println(); }

        System.out.println("========== CUSTOMER CATALOGUE ==========");
        for (Customer c : customerCatalogue)            { c.printData(); System.out.println(); }

        System.out.println("========== EMPLOYEE CATALOGUE ==========");
        for (User u : employeeCatalogue)                { u.printData(); System.out.println(); }

        System.out.println("========== ORDER CATALOGUE ==========");
        for (Order o : orderCatalogue)                  { o.printData(); System.out.println(); }

        System.out.println("========== SUPPLIER ORDER CATALOGUE ==========");
        for (SupplierOrder so : supplierOrderCatalogue) { so.printData(); System.out.println(); }
    }

    // ── Getters ──────────────────────────────────────────────
    public List<Product>       getProductCatalogue()       { return productCatalogue; }
    public List<Customer>      getCustomerCatalogue()      { return customerCatalogue; }
    public List<User>          getEmployeeCatalogue()      { return employeeCatalogue; }
    public List<Order>         getOrderCatalogue()         { return orderCatalogue; }
    public List<SupplierOrder> getSupplierOrderCatalogue() { return supplierOrderCatalogue; }
    public List<Stock>         getStockCatalogue()         { return stockCatalogue; }
}
