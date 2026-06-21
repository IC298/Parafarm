package parafarm;

public class Inventory {

	private int safetyLimit;   // κατώτατο όριο ασφαλείας
    private Product product;   // το προϊόν στο οποίο αναφέρεται το απόθεμα

    public Inventory(Product product, int safetyLimit) {
        this.product     = product;
        this.safetyLimit = safetyLimit;
    }

    // Getters & Setters
    public int  getSafetyLimit()          { return safetyLimit; }
    public void setSafetyLimit(int limit) { this.safetyLimit = limit; }

    public Product getProduct()              { return product; }
    public void    setProduct(Product prod)  { this.product = prod; }

    /** Returns true if current stock is below the safety limit */
    public boolean isBelowSafetyLimit() {
        return product.getQuantity() < safetyLimit;
    }

    public void printData() {
        System.out.println("=== Inventory ===");
        System.out.println("Product      : " + product.getName());
        System.out.println("Quantity     : " + product.getQuantity());
        System.out.println("Safety Limit : " + safetyLimit);
        System.out.println("Below limit  : " + isBelowSafetyLimit());
    }
}
