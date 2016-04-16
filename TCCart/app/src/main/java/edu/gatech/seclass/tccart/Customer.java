package edu.gatech.seclass.tccart;

import android.util.Log;
import android.widget.Toast;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

public class Customer implements Serializable {
    private static final long serialVersionUID = 2L;
    private String fName;
    private String lName;
    private String email;
    private String id;
    private double currentCredit;
    private Date creditExpiration;
    private boolean vipStatus;
    private ArrayList<Transaction> transactions;
    private boolean earnedVipNextYear;
    private double annualTotal;

    public Customer(String fName, String lName, String email) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        id = generateId();
        currentCredit = 0;
        annualTotal = 0;
        transactions = new ArrayList<Transaction>();
    }

    public Customer(String fName, String lName, String id, String email) {
        this.fName = fName;
        this.lName = lName;
        this.email = email;
        this.id = id;
        currentCredit = 0;
        annualTotal = 0;
        transactions = new ArrayList<Transaction>();
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String generateId() {
        ArrayList<String> ids = new ArrayList<String>();
        String newId = getNewId();
        for (Customer c : TCCart.getInstance().getCustomerList())
            ids.add(c.getId());
        String id = "";
        while (ids.contains(newId))
            newId = getNewId();

        return newId;
    }

    public String getNewId() {
        StringBuilder sb = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 8; i++)
            sb.append(Integer.toHexString(rand.nextInt(15) + 1));
        Log.d("CUSTCLASS", "ID Generated: " + sb.toString());
        return sb.toString();
    }

    public double getCurrentCredit() {
        return currentCredit;
    }

    public void setCurrentCredit(double creditApplied) {
        this.currentCredit = Math.max(currentCredit-creditApplied, 0);
        setCreditExpiration();
    }

    public Date getCreditExpiration() {
        return creditExpiration;
    }

    public void setCreditExpiration() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, 30);
        Log.d("CUSTCLASS", "Setting credit expiration: " + cal.getTime());
        this.creditExpiration = cal.getTime();
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        annualTotal += transaction.getTotal();
        //Check if customer just earned VIP Status
        checkJustEarnedVipNextYear(transaction);
    }

    public boolean isVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(boolean vipStatus) {
        this.vipStatus = vipStatus;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    public boolean checkJustEarnedVipNextYear(Transaction transaction) {
        double total = 0;
        if (earnedVipNextYear == true)
            return false;
        for (Transaction t : transactions) {
            total += t.getTotal();
            if (total >= 300) {
                earnedVipNextYear = true;
                Toast toast = Toast.makeText(MainActivity.getContextInstance(), "Customer has just earned VIP status for" +
                        " next year!!", Toast.LENGTH_LONG);
                toast.show();
                TCCart.getInstance().sendEmail("Earned VIP Status For Next Year",transaction);
                setEarnedVipNextYear(true);
            }
        }
        return earnedVipNextYear;
    }

    public void setEarnedVipNextYear(boolean earnedVipNextYear) {
        this.earnedVipNextYear = earnedVipNextYear;
    }

    public void nullifyCredit() {
        currentCredit = 0;
        creditExpiration = null;
    }

    public void nullifyTransactions() {
        transactions.clear();
    }

    public boolean isEarnedVipNextYear() {
        return earnedVipNextYear;
    }

    public String toString(){
        return fName +" "+ lName +"\n"+email;
    }

    static Comparator<Customer> nameCompare(){
        return new Comparator<Customer>(){

            @Override
            public int compare(Customer cust1, Customer cust2) {
                Log.d("COMPARES", "Comparing "+cust1.getlName() +" to "+ cust2.getlName() +" Result: "+cust1.getlName().compareTo(cust2.getlName()));
                return cust1.getlName().compareToIgnoreCase(cust2.getlName());
            }

        };
    }

    public double getAmountUntilVip(){
        if (annualTotal > 300)
            return 0;
        return 300 - annualTotal;
    }
}