package parafarm;

import java.util.ArrayList;
import java.util.List;

public abstract class Sale {
    private double totalCost;     // Συνολικό_κόστος
    private String saleDate;      // SaveSaleDate
    private List<Product> products;
    private List<Integer> quantities;

    public Sale() {
        this.products   = new ArrayList<>();
        this.quantities = new ArrayList<>();
        this.totalCost  = 0.0;
    }

    // ── Getters & Setters ───────────────────────────────────
    public double getTotalCost()             { return totalCost; }
    public void   setTotalCost(double total) { this.totalCost = total; }

    public String getSaleDate()                 { return saleDate; }
    /** SaveSaleDate : Sale, void — αποθηκεύει την ημερομηνία της πώλησης */
    public void   saveSaleDate(String date)     { this.saleDate = date; }

    public List<Product> getProducts()       { return products; }
    public List<Integer> getQuantities()     { return quantities; }

    /** Βοηθητική μέθοδος (δεν υπάρχει ρητά στο διάγραμμα, αλλά απαιτείται
     *  ώστε ο Cashier.addProduct() να μπορεί να ενημερώνει την Πώληση). */
    public void addProduct(Product product, int quantity) {
        products.add(product);
        quantities.add(quantity);
        totalCost += product.getPrice() * quantity;
    }

    /** CalculateChange(Cash) : float — επιστρέφει τα ρέστα, ή -1 αν
     *  το ποσό είναι ανεπαρκές (εναλλακτική ροή 4α). */
    public float calculateChange(double cash) {
        if (cash < totalCost) {
            return -1f;
        }
        return (float) (cash - totalCost);
    }

    /** ShowSuccessAndChange(Change) : void */
    public void showSuccessAndChange(double change) {
        if (change < 0) {
            System.out.println("Error: amount is less than the total cost.");
        } else {
            System.out.println("Transaction completed successfully. Change: " + change + " €");
        }
    }

    public void printData() {
        System.out.println("Total Cost : " + totalCost + " EUR");
        System.out.println("Sale Date  : " + saleDate);
        System.out.println("Products:");
        for (int i = 0; i < products.size(); i++) {
            System.out.println("  - " + products.get(i).getName()
                    + " x" + quantities.get(i));
        }
    }
}


// ============================================================
// Retail.java - Πώληση Λιανικής
// ============================================================
class Retail extends Sale {
    public Retail() {
        super();
    }

    @Override
    public void printData() {
        System.out.println("=== Retail Sale ===");
        super.printData();
    }
}


// ============================================================
// WholeSale.java - Πώληση Χονδρικής
// ============================================================
class WholeSale extends Sale {
    private String status;           // Κατάσταση
    private String deliveryDetails;  // Στοιχεία_Παράδοσης

    public WholeSale(String status, String deliveryDetails) {
        super();
        this.status          = status;
        this.deliveryDetails = deliveryDetails;
    }

    public String getStatus()                  { return status; }
    public void   setStatus(String status)     { this.status = status; }

    public String getDeliveryDetails()                       { return deliveryDetails; }
    public void   setDeliveryDetails(String deliveryDetails) { this.deliveryDetails = deliveryDetails; }

    @Override
    public void printData() {
        System.out.println("=== Wholesale Sale ===");
        super.printData();
        System.out.println("Status           : " + status);
        System.out.println("Delivery Details : " + deliveryDetails);
    }
}
