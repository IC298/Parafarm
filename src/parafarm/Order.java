package parafarm;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private String        state;       // State (αντί του γενικού "status")
    private String        date;
    private Customer      customer;
    private User          processedBy;
    private List<Product> products;
    private List<Integer> quantities;

    public Order(String date, Customer customer, User processedBy) {
        this.date        = date;
        this.customer    = customer;
        this.processedBy = processedBy;
        this.state       = "Pending";
        this.products    = new ArrayList<>();
        this.quantities  = new ArrayList<>();
    }

    // ── Getters & Setters (διαγράμματος) ────────────────────
    public String getState()              { return state; }
    public void   setState(String state)  { this.state = state; }

    // ── Πρόσθετα getters/setters ────────────────────────────
    public String getDate()            { return date; }
    public void   setDate(String date) { this.date = date; }

    public Customer getCustomer()                { return customer; }
    public void     setCustomer(Customer cust)   { this.customer = cust; }

    public User getProcessedBy()              { return processedBy; }
    public void setProcessedBy(User user)     { this.processedBy = user; }

    public List<Product> getProducts()    { return products; }
    public List<Integer> getQuantities()  { return quantities; }

    public void addProduct(Product product, int quantity) {
        products.add(product);
        quantities.add(quantity);
    }

    /** PlaceOrder() : Order — οριστικοποιεί την παραγγελία */
    public Order placeOrder() {
        this.state = "Placed";
        System.out.println("Order for " + customer.getName() + " has been placed.");
        return this;
    }

    /** GetSupplierData() — στο διάγραμμα ανήκει εννοιολογικά εδώ, καθώς
     *  η Order χρειάζεται στοιχεία προμηθευτή όταν προκύψει ανάγκη
     *  αναπλήρωσης. Η πραγματική λειτουργικότητα (έλεγχος αποθέματος +
     *  δημιουργία SupplierOrder) υλοποιείται κεντρικά στη
     *  Store.checkStockAndReorder(), καθώς αφορά ΟΛΑ τα προϊόντα του
     *  καταλόγου και όχι μόνο όσα περιέχει μία μεμονωμένη Order. */
    public String getSupplierData() {
        return "Supplier data is resolved centrally via Store.checkStockAndReorder().";
    }

    // ── Απαίτηση εκφώνησης: printData() με συγκεκριμένα πεδία ──
    public void printData() {
        System.out.println("=== Order ===");
        System.out.println("Date         : " + date);
        System.out.println("State        : " + state);
        System.out.println("Customer     : " + customer.getName()
                + " (AFM: " + customer.getAfm() + ")");
        System.out.println("Processed By : " + processedBy.getFullName());
        System.out.println("Products:");
        for (int i = 0; i < products.size(); i++) {
            System.out.println("  - " + products.get(i).getName()
                    + " x" + quantities.get(i)
                    + " @ " + products.get(i).getPrice() + " EUR");
        }
    }
}
