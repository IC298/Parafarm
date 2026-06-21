package parafarm;

import java.util.List;

public abstract class User {

    // ── Πεδία από το επίσημο διάγραμμα ──────────────────────
    private String username;
    private String password;

    // ── Πρόσθετα πεδία που απαιτεί ρητά η εκφώνηση υλοποίησης ─
    private String fullName;
    private String role;
    private String accountType;

    public User(String fullName, String role, String accountType,
                String username, String password) {
        this.fullName    = fullName;
        this.role        = role;
        this.accountType = accountType;
        this.username    = username;
        this.password    = password;
    }

    // ── Getters & Setters (διαγράμματος) ────────────────────
    public String getUsername()                 { return username; }
    public void   setUsername(String username)  { this.username = username; }

    public String getPassword()                 { return password; }
    public void   setPassword(String password)  { this.password = password; }

    // ── Getters & Setters (πρόσθετα πεδία εκφώνησης) ────────
    public String getFullName()                    { return fullName; }
    public void   setFullName(String fullName)     { this.fullName = fullName; }

    public String getRole()                { return role; }
    public void   setRole(String role)     { this.role = role; }

    public String getAccountType()                     { return accountType; }
    public void   setAccountType(String accountType)   { this.accountType = accountType; }

    // ════════════════════════════════════════════════════════
    // ΜΕΘΟΔΟΙ ΤΟΥ ΔΙΑΓΡΑΜΜΑΤΟΣ
    // ════════════════════════════════════════════════════════

    /** Login(Username, Password) : boolean — αντιστοιχεί στο βήμα 1
     *  κάθε αφήγησης: "Ο Χ συνδέεται στο σύστημα με τους μοναδικούς κωδικούς του." */
    public boolean login(String username, String password) {
        boolean success = this.username.equals(username) && this.password.equals(password);
        if (success) {
            System.out.println(fullName + " (" + role + ") logged in successfully.");
        } else {
            System.out.println("Login failed for username: " + username);
        }
        return success;
    }

    /** SearchForProductName(ProductName) : string
     *  Σημείωση: το διάγραμμα δεν δείχνει αναφορά του User στον Κατάλογο
     *  Προϊόντων, οπότε ο κατάλογος περνιέται ως παράμετρος (αντιστοιχεί
     *  στο ρόλο της Οθόνης στο διάγραμμα ακολουθίας, που μεσολαβεί). */
    public String searchForProductName(List<Product> catalogue, String productName) {
        Product p = selectWantedProduct(catalogue, productName);
        if (p == null) return "Product not found: " + productName;
        return "Found: " + p.getName() + " (Code: " + p.getCode() + ")";
    }

    /** SelectWantedProduct(ProductName) : Product */
    public Product selectWantedProduct(List<Product> catalogue, String productName) {
        for (Product p : catalogue) {
            if (p.getName().equalsIgnoreCase(productName)) return p;
        }
        return null;
    }

    /** ProductDetails(ProductName) : Product */
    public Product productDetails(List<Product> catalogue, String productName) {
        return selectWantedProduct(catalogue, productName);
    }

    // ── Απαίτηση εκφώνησης: printData() ─────────────────────
    public void printData() {
        System.out.println("Name         : " + fullName);
        System.out.println("Role         : " + role);
        System.out.println("Account Type : " + accountType);
    }
}


// ============================================================
// WarehouseWorker.java - Υπάλληλος Αποθήκης
// Διαγράμματος: GetSafetyLimit(Limit):int, UpdateProductList(Product):void,
//               OrderConfirmation(Order):Order
// ============================================================
class WarehouseWorker extends User {

    public WarehouseWorker(String fullName, String accountType,
                            String username, String password) {
        super(fullName, "Warehouse", accountType, username, password);
    }

    /** GetSafetyLimit(Limit) : int — επιστρέφει το τρέχον όριο ασφαλείας */
    public int getSafetyLimit(Stock stock) {
        return stock.getLimit();
    }

    /** UpdateProductList(Product) : void — αντιστοιχεί στο βήμα
     *  "Το σύστημα δίνει εντολή στο Προϊόν να ενημερώσει..." */
    public void updateProductList(Product product) {
        System.out.println("Product list updated for: " + product.getName());
    }

    /** OrderConfirmation(Order) : Order — επιβεβαιώνει/οριστικοποιεί παραγγελία */
    public Order orderConfirmation(Order order) {
        order.setState("Confirmed");
        System.out.println("Order confirmed by Warehouse Worker " + getFullName() + ".");
        return order;
    }

    @Override
    public void printData() {
        System.out.println("=== Warehouse Worker ===");
        super.printData();
    }
}


// ============================================================
// CustomerServiceWorker.java - Υπάλληλος Εξυπηρέτησης Πελατών
// ============================================================
class CustomerServiceWorker extends User {
    public CustomerServiceWorker(String fullName, String accountType,
                                  String username, String password) {
        super(fullName, "Customer Service", accountType, username, password);
    }

    @Override
    public void printData() {
        System.out.println("=== Customer Service Worker ===");
        super.printData();
    }
}


// ============================================================
// Cashier.java - Ταμίας
// Διαγράμματος: SelectNewSale(NewSale):Sale, getProduct(ProductCode):string,
//   AddProduct(Product):void, UpdateListAndTotal(Product):void,
//   EnterCustomerData(Afm,Name):void, EnterCashAmount(Cash):float
// ============================================================
class Cashier extends User {

    // Προσωρινά στοιχεία πελάτη (για περίπτωση Τιμολογίου) - βήμα
    // "Ο Ταμίας επιλέγει Τιμολόγιο... συμπληρώνει ΑΦΜ/Επωνυμία"
    private String tempCustomerAfm;
    private String tempCustomerName;

    public Cashier(String fullName, String accountType,
                   String username, String password) {
        super(fullName, "Cashier", accountType, username, password);
    }

    /** SelectNewSale(NewSale) : Sale — δημιουργεί νέα Πώληση Λιανικής */
    public Sale selectNewSale() {
        System.out.println(getFullName() + " started a new sale.");
        return new Retail();
    }

    /** getProduct(ProductCode) : string */
    public String getProduct(String productCode, List<Product> catalogue) {
        for (Product p : catalogue) {
            if (p.getCode().equals(productCode)) {
                return p.getName() + " - " + p.getPrice() + " €";
            }
        }
        return null; // αντιστοιχεί στην εναλλακτική ροή 7α: προϊόν δεν βρέθηκε
    }

    /** AddProduct(Product) : void — προσθήκη προϊόντος σε ενεργή πώληση */
    public void addProduct(Sale sale, Product product, int quantity) {
        sale.addProduct(product, quantity);
        System.out.println("Added " + quantity + "x " + product.getName() + " to sale.");
    }

    /** UpdateListAndTotal(Product) : void — ενημέρωση υποσυνόλου στην οθόνη */
    public void updateListAndTotal(Sale sale) {
        System.out.println("Current subtotal: " + sale.getTotalCost() + " €");
    }

    /** EnterCustomerData(Afm, Name) : void — μόνο για έκδοση Τιμολογίου */
    public void enterCustomerData(String afm, String name) {
        this.tempCustomerAfm  = afm;
        this.tempCustomerName = name;
        System.out.println("Customer data entered for invoice: " + name + " (AFM: " + afm + ")");
    }

    /** EnterCashAmount(Cash) : float — εισαγωγή μετρητών, υπολογισμός ρέστων */
    public float enterCashAmount(double cash, Sale sale) {
        float change = sale.calculateChange(cash);
        sale.showSuccessAndChange(change);
        return change;
    }

    public String getTempCustomerAfm()  { return tempCustomerAfm; }
    public String getTempCustomerName() { return tempCustomerName; }

    @Override
    public void printData() {
        System.out.println("=== Cashier ===");
        super.printData();
    }
}


// ============================================================
// Seller.java - Πωλητής
// Διαγράμματος: SelectNewOrder(NewOrder) : Order
// ============================================================
class Seller extends User {
    public Seller(String fullName, String accountType,
                  String username, String password) {
        super(fullName, "Sales", accountType, username, password);
    }

    /** SelectNewOrder(NewOrder) : Order — δημιουργεί νέα Παραγγελία
     *  (χονδρικής, για Πελάτη-Φαρμακείο, μέσω Πωλητή) */
    public Order selectNewOrder(Customer customer, String date) {
        System.out.println(getFullName() + " started a new order for " + customer.getName() + ".");
        return new Order(date, customer, this);
    }

    @Override
    public void printData() {
        System.out.println("=== Seller ===");
        super.printData();
    }
}


// ============================================================
// Pharmacist.java - Φαρμακοποιός
//
// ΣΗΜΕΙΩΣΗ (διάκριση Pharmacist vs Customer):
// Ο Pharmacist είναι ΧΡΗΣΤΗΣ (User) — το πρόσωπο που συνδέεται στο
// Web Portal και υποβάλλει την παραγγελία (βλ. αφηγήσεις 9 & 10).
// Ο Customer (τύπου "Pharmacy") είναι η ΕΠΙΧΕΙΡΗΣΗ-ΠΕΛΑΤΗΣ για λογαριασμό
// της οποίας εκδίδεται το τιμολόγιο (έχει ΑΦΜ/ΔΟΥ). Π.χ. ο Φαρμακοποιός
// του φαρμακείου "Papadopoulou" κάνει login ως Pharmacist, αλλά η
// παραγγελία/τιμολόγιο εκδίδεται στο όνομα του Customer "Farmakeio
// Papadopoulou". Οι δύο κλάσεις παραμένουν διακριτές για αυτόν τον λόγο.
// ============================================================
class Pharmacist extends User {
    public Pharmacist(String fullName, String accountType,
                       String username, String password) {
        super(fullName, "Pharmacist", accountType, username, password);
    }

    @Override
    public void printData() {
        System.out.println("=== Pharmacist ===");
        super.printData();
    }
}
