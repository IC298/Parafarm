package parafarm;

public class Stock {
    private int     limit;     // Κατώτατο Όριο Ασφαλείας Αποθέματος
    private Product product;   // Πρόσθετο πεδίο (βλ. σημείωση πάνω)

    public Stock(Product product, int limit) {
        this.product = product;
        this.limit   = limit;
    }

    // ── Getters & Setters (διαγράμματος) ────────────────────
    public int  getLimit()             { return limit; }
    public void setLimit(int limit)    { this.limit = limit; }

    public Product getProduct()              { return product; }
    public void    setProduct(Product prod)  { this.product = prod; }

    /** Πρόσθετη βοηθητική μέθοδος (δεν υπάρχει στο διάγραμμα, αλλά είναι
     *  απαραίτητη για τον αυτόματο έλεγχο αναπλήρωσης αποθέματος). */
    public boolean isBelowLimit() {
        return product.getQuantity() < limit;
    }

    public void printData() {
        System.out.println("=== Stock ===");
        System.out.println("Product : " + product.getName());
        System.out.println("Limit   : " + limit);
        System.out.println("Current : " + product.getQuantity());
    }
}
