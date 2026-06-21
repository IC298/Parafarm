package parafarm;

import java.util.List;

public class Product {
    private String name;
    private double price;
    private String category;
    private String code;
    private int    quantity;

    public Product(String name, String category, String code, double price, int quantity) {
        this.name     = name;
        this.category = category;
        this.code     = code;
        this.price    = price;
        this.quantity = quantity;
    }

    // ── Getters & Setters (διαγράμματος) ────────────────────
    public String getName()           { return name; }
    public void   setName(String n)   { this.name = n; }

    public double getPrice()          { return price; }
    public void   setPrice(double p)  { this.price = p; }

    public String getCategory()           { return category; }
    public void   setCategory(String c)   { this.category = c; }

    public String getCode()           { return code; }
    public void   setCode(String c)   { this.code = c; }

    public int  getQuantity()         { return quantity; }
    public void setQuantity(int q)    { this.quantity = q; }

    /** GetProductData(Price, Category, Code, Quantity) : void
     *  Υλοποιείται ως μέθοδος που επιστρέφει τα στοιχεία ως String
     *  (ώστε να μπορεί να χρησιμοποιηθεί τόσο από κονσόλα όσο και GUI). */
    public String getProductData() {
        return name + " | " + category + " | " + code + " | "
                + price + " € | qty: " + quantity;
    }

    /** DisplayAllProducts(Product) : void — στατική μέθοδος που εμφανίζει
     *  όλα τα προϊόντα ενός καταλόγου (αντιστοιχεί στο "Τελική εκτύπωση
     *  καταλόγων" που απαιτεί η εκφώνηση μέσω for loop). */
    public static void displayAllProducts(List<Product> catalogue) {
        for (Product p : catalogue) {
            p.printData();
        }
    }

    // ── Απαίτηση εκφώνησης: printData() με συγκεκριμένα πεδία ──
    public void printData() {
        System.out.println("=== Product ===");
        System.out.println("Name     : " + name);
        System.out.println("Category : " + category);
        System.out.println("Code     : " + code);
        System.out.println("Price    : " + price + " EUR");
        System.out.println("Quantity : " + quantity);
    }
}
