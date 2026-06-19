package parafarm;

//============================================================
//SupplierOrder.java - Παραγγελία Προμηθευτή
//Δημιουργείται αυτόματα όταν ένα προϊόν πέσει κάτω
//από το κατώτατο όριο ασφαλείας αποθέματος.
//============================================================
public class SupplierOrder {
 private Product product;        // Το προϊόν που παραγγέλθηκε
 private int     quantity;       // Ποσότητα προς παραγγελία
 private String  reason;         // Λόγος δημιουργίας
 private String  date;           // Ημερομηνία δημιουργίας

 public SupplierOrder(Product product, int quantity, String date) {
     this.product  = product;
     this.quantity = quantity;
     this.date     = date;
     // Ο λόγος είναι πάντα ο ίδιος: απόθεμα κάτω από όριο ασφαλείας
     this.reason   = "Stock fell below safety limit for product: " + product.getName();
 }

 // Getters & Setters
 public Product getProduct()              { return product; }
 public void    setProduct(Product prod)  { this.product = prod; }

 public int  getQuantity()          { return quantity; }
 public void setQuantity(int qty)   { this.quantity = qty; }

 public String getReason()              { return reason; }
 public void   setReason(String reason) { this.reason = reason; }

 public String getDate()            { return date; }
 public void   setDate(String date) { this.date = date; }

 public void printData() {
     System.out.println("=== Supplier Order ===");
     System.out.println("Product  : " + product.getName());
     System.out.println("Quantity : " + quantity);
     System.out.println("Reason   : " + reason);
     System.out.println("Date     : " + date);
 }
}
