# User Manual

## 1.0 GENERAL INFORMATION
General Information section explains in general terms the system and purpose for which it is intended.

### 1.1 Application Overview
Point of Sale is an Android application, which allows users to process customer credit card payments and manage rewards information for select customers. The application operates on mobile and tablet devices that run on Android Operating System 4.0 and above.

## 2.0 SYSTEM SUMMARY

### 2.1 System Configuration
The application runs on Android API 15 and above. It does not required connection to the internet to function.

### 2.2 Data Persistence
The application achieves data persistence by serializing an ArrayList of customers into a file named CUSTOMERS.DAT which gets stored on the user device.   

The file is read in when the application starts and whenever the application goes into a stopped state, the file is updated with any new customer information.

## 3.0 SYSTEM INTERFACES

The following section overviews all of the functionalities of the application and the process flows that the user should follow.

### 3.1 Installing the Application
The application can be installed in the following methods:

- Download the APK file to their device, click on the file, and choose to install it.

- Download the application from the Google Play Store (if available) and follow on screen instructions.

### 3.2 Home Page

(description below image)   
![Imgur](http://i.imgur.com/nbkRRRc.png?1)


Upon launching the application, the user is displayed the Home Page which displays the following interfaces:
- Current customer   
- Add Customer        
- Scan Card   
- Edit Customer   
- View Customer List      
- Process Transaction   
- Process Credit Card

This launch page is the main interface for user interaction with the application.

### 3.3 Add Customer

(description below image)   
![Imgur](http://i.imgur.com/c7x7fQL.png?1)


To add a new customer, the User will select the 'Add Customer' icon and proceed to add the Customer First Name, Last Name, and Email. The email must be valid and will throw an error for an invalid email or invalid  syntax.

The customer is then stored on a java object file.  A card is printed when a new customer is succesfully added. 

### 3.4 Scan Card

The User can scan the customer card by selecting the 'Scan Card' button and proceeding to use the external scanning device to scan the card. This will  interact with the application via the internal customer list and will look up the customer information so a transaction can be processed for that customer. 

### 3.5 Edit Customer

(description below image)   
![Imgur](http://i.imgur.com/qJaMqad.png?1)



The User can edit customer information on request basis from the customer. This change will be reflected on their customer card and the Customer ID can never be edited. The user is able to edit First Name, Last Name, and Email. 

### 3.6 View Customer List

(description below image)   
![Imgur](http://i.imgur.com/QEmlHMi.png?2)


The user can view a list of all customers by selecting the 'View Customer List' button. Customers are listed in order of Last Name. Customers displayed in Gold denote that they have VIP Status for the current year. Customers in blue denotes that they have not reached VIP status for the current year yet.   

When the user holds a customer name for 2 seconds they will have the option to view additional customer information, view a list of the transactions specific to that customer, or delete that customer as seen below.

![Imgur](http://i.imgur.com/QtzvkKH.png?1)


*Show Customer Info* (below)
- This interface shows the user all of the base customer information such as Name, Email, and ID. It also shows Rewards information such as VIP Status, Credit Balance, and VIP Status for next year. All of this information is easily accessible in the event a customer requests to see it.   
![Imgur](http://i.imgur.com/BQpBjCC.png?1)


*Show Transactions* (below)
- This interface shows the user a list of all of the transactions (descending order) made by the customer. Any discounts applied are also visible on this interface along with a time stamp (DD/MM/YYYY).
![Imgur](http://i.imgur.com/bktHakY.png?2)
 

### 3.7 Process Transaction

(description below image)   
![Imgur](http://i.imgur.com/XaaNNrb.png?1)


The user can begin to process a transaction once a customer has been called from the application. The user manually enters the subtotal based on what the customer is purchasing and clicks 'Process'.  
 
This will take the User back to the launch page where the Subtotal and Customer Information are displayed. From this step, payment is ready to be processed.   
![Imgur](http://i.imgur.com/PwUlWJB.png)

### 3.8 Process Credit Card

(description below image)   
![Imgur](http://i.imgur.com/geUpCgQ.png?1)


Once the subtotal has been added to the 'cart', the User can process a credit card transaction by selecting on the associated icon. This will prompt the User to swipe the credit card and the external payment processor will tokenize and authorize the credit card transaction.

## 4.0 SYSTEM INTERACTIONS

The following section overviews all of the external communications that the application makes.

### 4.1 Customer Emails

The application sends an email to the customer with their receipt whenever they make a purchase, whenever they earn a credit, and when they earn VIP status for next year.   

In each instance, the external email method is called in a loop until it produces a non-error result.

### 4.2 Credit Card Processing

In order to simulate the reading of a customer credit card, the "Process Credit Card" button may be pressed (but only after a transaction has first been processed). If a card read error is found, a user-friendly message prints at the bottom of the screen telling the user to try again.   

Once a card is read successfully, the application enters a loop that exits only when that card is processed successfully without error. This prevents the user from having to reread the card due to a processing error.
