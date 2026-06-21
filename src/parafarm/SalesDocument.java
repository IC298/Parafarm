package parafarm;

public abstract class SalesDocument {
    private double paymentAmount; // Ποσό Πληρωμής
    private String type;          // Τύπος ("Receipt" / "Invoice")

    public SalesDocument(double paymentAmount, String type) {
        this.paymentAmount = paymentAmount;
        this.type          = type;
    }

    // Getters & Setters
    public double getPaymentAmount()               { return paymentAmount; }
    public void   setPaymentAmount(double amount)  { this.paymentAmount = amount; }

    public String getType()           { return type; }
    public void   setType(String t)   { this.type = t; }

    public void printData() {
        System.out.println("Payment Amount : " + paymentAmount + " €");
        System.out.println("Type           : " + type);
    }
}


// ============================================================
// Receipt.java - Απόδειξη
// ============================================================
class Receipt extends SalesDocument {
    public Receipt(double paymentAmount) {
        super(paymentAmount, "Receipt");
    }

    @Override
    public void printData() {
        System.out.println("=== Receipt ===");
        super.printData();
    }
}


// ============================================================
// Invoice.java - Τιμολόγιο
// ============================================================
class Invoice extends SalesDocument {
    private String afm; // ΑΦΜ πελάτη

    public Invoice(double paymentAmount, String afm) {
        super(paymentAmount, "Invoice");
        this.afm = afm;
    }

    // Getters & Setters
    public String getAfm()           { return afm; }
    public void   setAfm(String afm) { this.afm = afm; }

    @Override
    public void printData() {
        System.out.println("=== Invoice ===");
        super.printData();
        System.out.println("AFM : " + afm);
    }
}