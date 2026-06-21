package parafarm;

public class Customer {
 private String name;        // Επωνυμία / Ονοματεπώνυμο
 private String type;        // "Pharmacy" (χονδρική) ή "Retail" (λιανική)
 private String afm;         // ΑΦΜ
 private String doy;         // ΔΟΥ
 private String status;      // Κατάσταση (π.χ. "OK")

 public Customer(String name, String type, String afm, String doy, String status) {
     this.name   = name;
     this.type   = type;
     this.afm    = afm;
     this.doy    = doy;
     this.status = status;
 }

 // Getters & Setters
 public String getName()             { return name; }
 public void   setName(String name)  { this.name = name; }

 public String getType()             { return type; }
 public void   setType(String type)  { this.type = type; }

 public String getAfm()              { return afm; }
 public void   setAfm(String afm)    { this.afm = afm; }

 public String getDoy()              { return doy; }
 public void   setDoy(String doy)    { this.doy = doy; }

 public String getStatus()                  { return status; }
 public void   setStatus(String status)     { this.status = status; }

 /** Returns true if this customer is a wholesale pharmacy client */
 public boolean isPharmacy() {
     return "Pharmacy".equalsIgnoreCase(type);
 }

 public void printData() {
     System.out.println("=== Customer ===");
     System.out.println("Name   : " + name);
     System.out.println("Type   : " + type);
     System.out.println("AFM    : " + afm);
     System.out.println("DOY    : " + doy);
     System.out.println("Status : " + status);
 }
}
