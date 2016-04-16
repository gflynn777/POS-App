package edu.gatech.seclass.tccart;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import edu.gatech.seclass.services.CreditCardService;
import edu.gatech.seclass.services.EmailService;
import edu.gatech.seclass.services.PaymentService;
import edu.gatech.seclass.services.PrintingService;
import edu.gatech.seclass.services.QRCodeService;
import tccart.seclass.gatech.edu.tccart.R;

public class TCCart {
    private static TCCart tccart;
    private static ArrayList<Customer> customers;
    private Customer currentCustomer;
    private Transaction currentTransaction;
    public static final String CUSTOMER_FILE = "CUSTOMERS.DAT";
    private static Context context;
    private static Date previousYear;

    private TCCart(){
    }

    public static TCCart getInstance(){
        if (tccart == null){
            tccart = new TCCart();
            context = MainActivity.getContextInstance();
            customers = new ArrayList<Customer>();
            readFromDisk();
            hardcodeSampleCustomers();
            checkToSetNewVips();//Check if the year has changed since the last run!
        }
        return tccart;
    }

    public Customer getCurrentCustomer(){
        return currentCustomer;
    }

    public void setCurrentCustomer(Customer c){
        currentCustomer = c;
    }

    public Transaction getCurrentTransaction(){
        return currentTransaction;
    }

    public void setCurrentTransaction(){
        currentTransaction = null;
    }

    public static void hardcodeSampleCustomers(){
        //Check if these are already in the list
        for (Customer c : customers){
            if (c.getId().contentEquals("7c86ffee"))
                return;
        }
        Customer c = new Customer("Ralph", "Hapschatt", "7c86ffee", "rhapschatt@gatech.edu");
        customers.add(c);
        Customer c2 = new Customer("Betty", "Monroe", "b59441af", "bmonroe@gatech.edu");
        customers.add(c2);
        Customer c3 = new Customer("Everett", "Scott", "cd0f0e05", "escott@gatech.edu");
        customers.add(c3);
    }

    public ArrayList<Customer> getCustomerList(){
        return customers;
    }

    public Customer createCustomer(TCCart tccart, String fName, String lName, String email){
        Customer c = new Customer(fName, lName, email);
        customers.add((c));
        currentCustomer = c;

        //Print customer card
        printCustomerCard();
        return c;
    }

    public void editCustomerInfo(String newFirstName, String newLastName, String newEmail){
        Customer c = getCurrentCustomer();//TODO: Change UML
        if (newFirstName != null)
            c.setfName(newFirstName);
        if (newLastName != null)
            c.setlName(newLastName);
        if (newEmail != null)
            c.setEmail(newEmail);
    }

    public Customer readCustomerQrCard(){
        String id = QRCodeService.scanQRCode();
        if (id.contentEquals("ERR")) {
            currentCustomer = null;
            return null;
        }
        Log.d("GFLYNN", "QRCodeService ID: " + id);
        for (Customer c : customers){
            Log.d("GFLYNN", "Matching: "+c.getId() + " Against: "+id);
            if (c.getId().contentEquals(id)) {
                currentCustomer = c;
                Log.d("GFLYNN", "Matched Name: "+c.getfName() +" "+ c.getlName() +" ID: "+c.getId());
                return c;
            }
        }
        Log.d("GFLYNN", "Customer not found!");
        return null;//Should never get here
    }

    public Transaction processTransaction(double subtotal){
        Customer c = getCurrentCustomer();//TODO: Change UML
        Transaction t = new Transaction(c, subtotal);
        currentTransaction = t;
        return t;
    }

    public void eraseExpiredCredit(){
        ArrayList<Customer> customers = getCustomerList();
        for (Customer c : customers) {
            Date today = new Date();
            if (c.getCreditExpiration().after(today))
                c.nullifyCredit();
        }
    }

    public static void setAnnualVips(){
        Log.d("VIPCHECK", "Inside setAnnualVips() method");
        for (Customer c : customers) {
            if (c.isEarnedVipNextYear()) {
                Log.d("VIPCHECK", "Setting "+c.getfName()+" "+ c.getlName()+ " as VIP.");
                c.setVipStatus(true);
            }
            c.setEarnedVipNextYear(false);
            c.nullifyTransactions();
        }
    }

    //Write customer list object to disk
    public void writeToDisk(){
        try{
            FileOutputStream fos = MainActivity.getContextInstance().openFileOutput(CUSTOMER_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(customers);
            os.close();
            fos.close();
            Log.d("GFLYNN", "Writing customer list to file.");

        }catch(Exception ex) {
            ex.printStackTrace();
            Log.e("GFLYNN", "ERROR reading file: " + ex.toString());
        }
    }

    //Read an ArrayList from disk
    public static void readFromDisk(){

        try{
            FileInputStream fis = MainActivity.getContextInstance().openFileInput(CUSTOMER_FILE);
            ObjectInputStream is = new ObjectInputStream(fis);
            customers = (ArrayList<Customer>) is.readObject();
            is.close();
            fis.close();

        }catch(ClassNotFoundException | IOException ex){
            Log.e("GFLYNN", "Read ERROR!!!!! Erasing CUSTOMER.DAT file... "+ex.toString());
            ex.printStackTrace();
            try {
                //Cannot read file: erase customer file
                new FileOutputStream(CUSTOMER_FILE).close();
            } catch (IOException e) {}
        }
    }

    public static void checkToSetNewVips(){
        Log.d("VIPCHECK", "In checkToSetNewVips() method.");
        Context ctx = MainActivity.getContextInstance();
        SharedPreferences prefs = ctx.getSharedPreferences(ctx.getString(R.string.tccart_pref_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        //Get previous year and compare to see if current year is different
        long longDate = prefs.getLong(ctx.getString(R.string.previous_year), -1);
        if (longDate == -1) {//Exit if default value found
            Log.d("VIPCHECK", "SharedPrefs could not find the previous year value!");
        }
        else {
            Date previousDate = new Date(longDate);
            Log.d("VIPCHECK", "PreviousDate from SharedPref: " + previousDate.toString());
            Calendar cal = Calendar.getInstance();
            cal.setTime(previousDate);
            int prevYear = cal.get(Calendar.YEAR);

            //Save current year to variable
            cal.setTime(new Date());
            int currYear = cal.get(Calendar.YEAR);
            Log.d("VIPCHECK", "Current Year: " + currYear);

            //Compare
            if (currYear > prevYear) {
                setAnnualVips();
            }
        }

        //Save current year to file as previous year
        editor.putLong(ctx.getString(R.string.previous_year), new Date().getTime());
        editor.commit();
        Log.d("VIPCHECK", "Previous date saved.");
    }

    public CreditCard readCreditCard(){
        String cardRead = CreditCardService.readCreditCard();
        if (cardRead.contentEquals("ERR"))
            return null;
        String card[] = cardRead.split("#");
        Log.d("GFLYNN", "CC: " + cardRead);
        String fName = card[0];
        String lName = card[1];
        String accntNum = card[2];
        String expDate = card[3];
        String secCode = card[4];
        CreditCard cc = new CreditCard(fName, lName, accntNum, secCode, expDate);
        return cc;
    }

    public void processCreditTransaction(CreditCard cc, Transaction t){
        Date exp = new Date(Long.parseLong(cc.getExpDate()));
        while(PaymentService.processPayment(cc.getfName(), cc.getlName(), cc.getAccntNumber(), exp, cc.getSecurityCode(), t.getSubtotal()) == false);
        Log.d("STUBMETHOD", "Proccessed credit transaction successfully.");
        sendEmail("TCCart Purchase Receipt", t);
        t = null;
    }

    public void printCustomerCard(){
        if (currentCustomer == null)
            return;
        while(PrintingService.printCard(currentCustomer.getfName(), currentCustomer.getlName(), currentCustomer.getId()) == false);
        Log.d("STUBMETHOD", "Printed customer card successfully");
    }

    public void sendEmail(String subject, Transaction t){
        String body = "";
        if (subject.contentEquals("Earned New Credit"))
            body = "You earned a new credit!";
        else if (subject.contentEquals("TCCart Purchase Receipt")){
            StringBuilder sb = new StringBuilder();
            sb.append("TCCart Receipt\n\n");
            sb.append("$" + t.getSubtotal() + "\tSubtotal\n");
            if (t.getCreditApplied() > 0)
                sb.append("($"+ t.getCreditApplied() +")\tCredit Applied\n");
            if (t.getDiscountApplied() > 0)
                sb.append("($"+ t.getDiscountApplied() +")\tDiscount Applied\n");

            sb.append("\n-----------------------------------\n");
            sb.append("$" + t.getTotal() + "\tTOTAL");
            body = sb.toString();
            System.out.println("Receipt Body:" + body);
        }
        else if (subject.contentEquals("Earned VIP Status For Next Year"))
            body = "Congratulations! Your recent purchase has earned you VIP Status for next year!\n"+
                    "This will save you 10% on every purchase throughout the year.";

        //Loop until email is sent
        while (EmailService.sendEMail(getCurrentCustomer().getEmail(), subject, body) == false);
        Log.d("STUBMETHOD", "sendEmail method has sent email regarding: "+subject);
    }
}