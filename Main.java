package parafarm;

public class Main {

    public static void main(String[] args) {

        Store  store = new Store();
        String today = "2026-06-10";

        // ════════════════════════════════════════════════════
        // 1. ΔΗΜΙΟΥΡΓΙΑ ΠΡΟΪΟΝΤΩΝ
        // ════════════════════════════════════════════════════
        Product product1 = new Product("Thermometer", "paramedical", "P001", 12.50, 80);
        Product product2 = new Product("Shoes",       "paramedical", "P002", 35.00, 45);
        Product product3 = new Product("Cream",       "cosmetic",    "P003", 18.00, 25);
        Product product4 = new Product("Makeup",      "cosmetic",    "P004", 22.00, 60);

        store.addProduct(product1);
        store.addProduct(product2);
        store.addProduct(product3);
        store.addProduct(product4);

        store.setSafetyLimit(product1, 30);
        store.setSafetyLimit(product2, 20);
        store.setSafetyLimit(product3, 40);
        store.setSafetyLimit(product4, 25);

        System.out.println();

        // ════════════════════════════════════════════════════
        // 2. ΔΗΜΙΟΥΡΓΙΑ ΠΕΛΑΤΩΝ
        // ════════════════════════════════════════════════════
        Customer customer1 = new Customer("Farmakeio Papadopoulou", "Pharmacy",
                                          "099845210", "DOY Kalamarias",   "OK");
        Customer customer2 = new Customer("Farmakeio Nikolaidis",   "Pharmacy",
                                          "078632145", "DOY Evosmou",      "OK");
        Customer customer3 = new Customer("Georgiou Maria",         "Retail",
                                          "112233445", "DOY Thessalonikis","OK");

        store.addCustomer(customer1);
        store.addCustomer(customer2);
        store.addCustomer(customer3);

        System.out.println();

        // ════════════════════════════════════════════════════
        // 3. ΔΗΜΙΟΥΡΓΙΑ ΥΠΑΛΛΗΛΩΝ
        //    (name, accountType, username, password)
        //    Username/Password προστέθηκαν για να ευθυγραμμιστεί
        //    η κλάση User με το επίσημο διάγραμμα (Login()).
        // ════════════════════════════════════════════════════
        WarehouseWorker       employee1 = new WarehouseWorker      ("Antoniou",    "warehouse_user", "antoniou",    "pass123");
        CustomerServiceWorker employee2 = new CustomerServiceWorker("Eleftheriou", "service_user",   "eleftheriou", "pass123");
        Cashier               employee3 = new Cashier              ("Sotiriou",    "cashier_user",   "sotiriou",    "pass123");
        Seller                employee4 = new Seller               ("Seller1",     "sales_user",     "seller1",     "pass123");

        store.addEmployee(employee1);
        store.addEmployee(employee2);
        store.addEmployee(employee3);
        store.addEmployee(employee4);

        System.out.println();

        // ════════════════════════════════════════════════════
        // 4. ΔΗΜΙΟΥΡΓΙΑ ΠΑΡΑΓΓΕΛΙΩΝ
        //    Κάθε αφήγηση ξεκινά με login() — βήμα 1 κάθε ροής
        //    ("Ο Χ συνδέεται στο σύστημα με τους κωδικούς του").
        // ════════════════════════════════════════════════════

        // --- order1 ---
        System.out.println("--- Creating order1 ---");
        employee4.login("seller1", "pass123");
        Order order1 = employee4.selectNewOrder(customer1, today);
        order1.addProduct(product1, 10);
        order1.addProduct(product3,  5);
        store.addOrder(order1);
        store.processOrder(order1);
        System.out.println("Object order1 has been created.");

        System.out.println();

        // --- order2 (θα ακυρωθεί) ---
        System.out.println("--- Creating order2 ---");
        employee4.login("seller1", "pass123");
        Order order2 = employee4.selectNewOrder(customer1, today);
        order2.addProduct(product2,  8);
        order2.addProduct(product4, 12);
        store.addOrder(order2);
        System.out.println("Object order2 has been created.");

        System.out.println();

        // --- order3 ---
        System.out.println("--- Creating order3 ---");
        employee4.login("seller1", "pass123");
        Order order3 = employee4.selectNewOrder(customer2, today);
        order3.addProduct(product3, 20);
        order3.addProduct(product4, 10);
        order3.addProduct(product1,  6);
        store.addOrder(order3);
        store.processOrder(order3);
        System.out.println("Object order3 has been created.");

        System.out.println();

        // --- order4 (Ταμίας, Retail, Απόδειξη) ---
        System.out.println("--- Creating order4 ---");
        employee3.login("sotiriou", "pass123");
        Order order4 = new Order(today, customer3, employee3);
        order4.addProduct(product1, 1);
        order4.addProduct(product4, 2);
        store.addOrder(order4);
        store.processOrder(order4);
        System.out.println("Object order4 has been created.");

        System.out.println();

        // ════════════════════════════════════════════════════
        // 5. ΑΚΥΡΩΣΗ ORDER2
        // ════════════════════════════════════════════════════
        System.out.println("--- Cancelling order2 ---");
        store.cancelOrder(order2);

        System.out.println();

        // ════════════════════════════════════════════════════
        // 6. ΕΛΕΓΧΟΣ ΑΠΟΘΕΜΑΤΩΝ & ΑΥΤΟΜΑΤΗ ΑΝΑΠΛΗΡΩΣΗ
        //
        //    product1 (Thermometer): 80-10-6-1 = 63  (limit 30) -> OK
        //    product2 (Shoes)      : 45 (ακυρώθηκε) -> OK
        //    product3 (Cream)      : 25-5-20   =  0  (limit 40) -> ΚΑΤΩ ΑΠΟ ΟΡΙΟ
        //    product4 (Makeup)     : 60-10-2   = 48  (limit 25) -> OK
        // ════════════════════════════════════════════════════
        store.checkStockAndReorder(today);

        // ════════════════════════════════════════════════════
        // 7. ΤΕΛΙΚΗ ΕΚΤΥΠΩΣΗ ΟΛΩΝ ΤΩΝ ΚΑΤΑΛΟΓΩΝ
        // ════════════════════════════════════════════════════
        store.printAllCatalogues();
    }
}
