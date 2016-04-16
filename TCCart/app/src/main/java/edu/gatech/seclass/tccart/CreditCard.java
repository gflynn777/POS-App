package edu.gatech.seclass.tccart;

import java.util.Date;

public class CreditCard {
    private String fName;
    private String lName;
    private String accntNumber;
    private String expDate;
    private String securityCode;

    public CreditCard(String fName, String lName, String accntNumber, String securityCode, String expDate) {
        this.securityCode = securityCode;
        this.expDate = expDate;
        this.accntNumber = accntNumber;
        this.fName = fName;
        this.lName = lName;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public String getAccntNumber() {
        return accntNumber;
    }

    public String getExpDate() {
        return expDate;
    }

    public String getSecurityCode() {
        return securityCode;
    }
}
