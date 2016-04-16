package edu.gatech.seclass.tccart;

import android.util.Log;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    private double subtotal;
    private Customer customer;
    private Date orderDate;
    private double discountApplied;
    private double creditApplied;
    private boolean qualifiesForCredit;
    private double total;
    private boolean paid = false;

    public Transaction(Customer customer, double subtotal) {
        this.customer = customer;
        this.subtotal = subtotal;
        setOrderDate(new Date());
        calculateAndSetTotal();
    }

    public void calculateAndSetTotal(){
        total = subtotal;
        Log.d("TRANSACTION", "Total = subtotal: "+total);
        Log.d("TRANSACTION", "Exp Date: " + customer.getCreditExpiration() + " Current: " + new Date());

        //Check for any customer credits
        if (customer.getCurrentCredit() > 0 && new Date().before(customer.getCreditExpiration())){
            //If purchase is less than amount of credits
            if (customer.getCurrentCredit() > total) {
                creditApplied = customer.getCurrentCredit() - total;
                //customer.setCurrentCredit(creditApplied);
                total = 0;
            }
            else {//Purchase is more than credits, erase credits
                creditApplied = customer.getCurrentCredit();
                total = total - creditApplied;
                //customer.setCurrentCredit(0);
            }
            Log.d("TRANSACTION", "Total after credits: "+total);
        }

        //Check for VIP Discount
        if (customer.isVipStatus()) {
            discountApplied = total * 0.10;
            total = total * 0.90;
        }
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(double discountApplied) {
        this.discountApplied = discountApplied;
    }

    public double getCreditApplied() {
        return creditApplied;
    }

    public void setCreditApplied(double creditApplied) {
        this.creditApplied = creditApplied;
    }

    public boolean isQualifiesForCredit() {
        return qualifiesForCredit;
    }

    public double getTotal(){
        return total;
    }

    public void setAsPaid(){
        paid = true;
        customer.setCurrentCredit(creditApplied);
        //If qualifying purchase, give the customer credit
        if (total >= 30) {
            qualifiesForCredit = true;
            customer.setCurrentCredit(3);
            customer.setCreditExpiration();
            Log.d("TRANSACTION", "Giving customer $3 credit.");
        }
        customer.addTransaction(this);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        SimpleDateFormat df = new SimpleDateFormat("E dd/MM/yyyy 'at' hh:mm a zzz");
        String formatString = "%-20s %s";
        int justify = 0;

        sb.append(df.format(orderDate) +"\n");
        formatString = justify(formatter.format((subtotal)));
        sb.append(String.format(formatString, "$" + formatter.format(subtotal), "Subtotal") + "\n");
        if (creditApplied > 0) {
            formatString = justify(formatter.format(creditApplied));
            sb.append(String.format(formatString, "($" + formatter.format(creditApplied) + ")", "Coupon Credit Applied") + "\n");
        }
        if (discountApplied > 0) {
            formatString = justify(formatter.format(discountApplied));
            sb.append(String.format(formatString, "($" + formatter.format(discountApplied) + ")", "VIP Discount Applied") + "\n");
        }
        sb.append(String.format(formatString, "$" + formatter.format(total), "Total"));

        return sb.toString();
    }

    public String justify(String s){
        int incr = s.length();
        int left = -25 + incr;

        return "%"+left+"s %"+left+"s";
    }

    static Comparator<Transaction> dateCompare(){
        return new Comparator<Transaction>(){

            @Override
            public int compare(Transaction x1, Transaction x2) {
                //Log.d("COMPARES", "Comparing " + x1.getlName() + " to " + x2.getlName() + " Result: " + x1.getlName().compareTo(x2.getlName()));
                return x2.getOrderDate().compareTo(x1.getOrderDate());
            }

        };
    }

}
