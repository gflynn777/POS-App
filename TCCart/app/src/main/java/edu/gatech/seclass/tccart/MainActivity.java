package edu.gatech.seclass.tccart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tccart.seclass.gatech.edu.tccart.R;

public class MainActivity extends AppCompatActivity implements OnClickListener {
    TCCart tccart;
    private EditText txtCustomerfName;
    private EditText txtCustomerlName;
    private EditText txtCustomerEmail;
    private EditText txtSubtotal;
    public static  EditText txtCurrCustomer;
    public static EditText txtTotalDue;
    private static Context context;
    public static final int CUSTOMER_LIST_CODE = 1;
    public static final int TRANSACTION_LIST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageButton addCustomer;
        ImageButton lookupCustomer;
        ImageButton scanCard;
        ImageButton processTransaction;
        ImageButton editCustomer;
        ImageButton processCreditCard;
        setContentView(R.layout.activity_main);
        context = this;
        tccart = TCCart.getInstance();

        //Buttons
        addCustomer = (ImageButton)findViewById(R.id.btnAddCustomer);
        lookupCustomer = (ImageButton)findViewById(R.id.btnLookupCustomer);
        scanCard = (ImageButton)findViewById(R.id.btnScanCard);
        processTransaction = (ImageButton)findViewById(R.id.btnProcessTransaction);
        editCustomer = (ImageButton)findViewById(R.id.btnEditCustomer);
        processCreditCard = (ImageButton)findViewById(R.id.btnCreditCard);

        //Current Customer
        txtCurrCustomer = (EditText)findViewById(R.id.txtCurrCustomer);
        txtCurrCustomer.setText(getString(R.string.scanOrAdd));
        txtCurrCustomer.setEnabled(false);

        //Total Due
        txtTotalDue = (EditText)findViewById(R.id.txtTotalDue);
        txtTotalDue.setEnabled(false);
        txtTotalDue.setText(getString(R.string.zero_total));

        //Set Listeners
        addCustomer.setOnClickListener(this);
        lookupCustomer.setOnClickListener(this);
        scanCard.setOnClickListener(this);
        processTransaction.setOnClickListener(this);
        editCustomer.setOnClickListener(this);
        processCreditCard.setOnClickListener(this);

        Log.d("CustomerListSize: " + tccart.getCustomerList().size(), "GFLYNN");

    }

    @Override
    protected void onStop() {
        super.onStop();
        tccart.writeToDisk();
    }

    public static Context getContextInstance(){
        return context;
    }

    @Override
    public void onClick(View view){
        final Context ctx = getApplicationContext();
        Toast toast;
        switch (view.getId()){

            case R.id.btnAddCustomer:
                customerDialog(ctx, "add").show();
                break;

            case R.id.btnEditCustomer:
                if (tccart.getCurrentCustomer() == null){
                    toast = Toast.makeText(ctx, getString(R.string.no_customer_error), Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                    customerDialog(ctx, "edit").show();
                break;

            case R.id.btnLookupCustomer:
                showCustList();
                break;

            case R.id.btnProcessTransaction:
                if (tccart.getCurrentCustomer() == null){
                    toast = Toast.makeText(ctx, getString(R.string.no_customer_error), Toast.LENGTH_SHORT);
                    toast.show();
                }
                else
                    transactionDialog(ctx).show();
                break;

            case R.id.btnScanCard:
                Customer c = tccart.readCustomerQrCard();
                if (c != null){
                    txtCurrCustomer.setText(c.getfName() +" "+ c.getlName());
                    txtTotalDue.setText(getString(R.string.zero_total));

                }
                else {
                    toast = Toast.makeText(ctx, getString(R.string.card_error), Toast.LENGTH_SHORT);
                    txtCurrCustomer.setText(getString(R.string.scanOrAdd));
                    txtTotalDue.setText(getString(R.string.zero_total));
                    toast.show();
                }
                break;

            case R.id.btnCreditCard:
                CreditCard cc = tccart.readCreditCard();
                //Bad read
                if (cc == null) {
                    toast = Toast.makeText(ctx, getString(R.string.card_error), Toast.LENGTH_SHORT);
                    toast.show();
                }
                //No transaction yet
                else if (tccart.getCurrentTransaction() == null){
                    toast = Toast.makeText(ctx, getString(R.string.no_transaction_error), Toast.LENGTH_SHORT);
                    toast.show();
                }
                //Successful read
                else{
                    //Process Credit card
                    Transaction t = tccart.getCurrentTransaction();
                    tccart.processCreditTransaction(cc, t);
                    t.setAsPaid();
                    txtTotalDue.setText(getString(R.string.zero_total));

                    //Send email about credit earned
                    if (t.getTotal() > 30)
                        tccart.sendEmail("Credit", t);
                    tccart.setCurrentTransaction();
                    toast = Toast.makeText(ctx, getString(R.string.payment_processed), Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    //Add Customer Dialog
    public AlertDialog.Builder customerDialog(Context context, String function){
        if (function.contentEquals("edit") && tccart.getCurrentCustomer() == null){
            Toast toast = Toast.makeText(context, getString(R.string.no_customer_error), Toast.LENGTH_SHORT);
            toast.show();
        }
        final Context ctx = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = View.inflate(this, R.layout.dialog_layout, null);
        txtCustomerfName = (EditText)layout.findViewById(R.id.txtCustomerfName);
        txtCustomerlName = (EditText)layout.findViewById(R.id.txtCustomerlName);
        txtCustomerEmail = (EditText)layout.findViewById(R.id.txtCustomerEmail);

        //Add customer dialog
        if (function.contentEquals("add")) {
           // builder.setTitle("Add Customer");
            builder.setTitle("Add Customer");
            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (txtCustomerfName.getText().toString().contentEquals("") || txtCustomerlName.getText().toString().contentEquals("") || txtCustomerEmail.getText().toString().contentEquals("")) {
                        Toast toast = Toast.makeText(ctx, getString(R.string.blank_fields_error), Toast.LENGTH_SHORT);
                        toast.show();
                    } else if (!txtCustomerEmail.getText().toString().contentEquals("") && !isEmail(txtCustomerEmail.getText().toString())) {
                        Toast toast = Toast.makeText(ctx, getString(R.string.invalid_email_error), Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Customer c = tccart.createCustomer(tccart, txtCustomerfName.getText().toString(), txtCustomerlName.getText().toString(), txtCustomerEmail.getText().toString());
                        Toast toast = Toast.makeText(ctx, getString(R.string.customer_added), Toast.LENGTH_SHORT);
                        txtCurrCustomer.setText(c.getfName() +" "+ c.getlName());
                        txtTotalDue.setText(getString(R.string.zero_total));
                        toast.show();
                    }
                }
            });
        }
        //Edit customer dialog
        else if(function.contentEquals("edit")){
            txtCustomerfName.setText(tccart.getCurrentCustomer().getfName());
            txtCustomerlName.setText(tccart.getCurrentCustomer().getlName());
            txtCustomerEmail.setText(tccart.getCurrentCustomer().getEmail());
            builder.setTitle("Edit Customer");
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String customerfName = null, customerlName = null, customerEmail = null;
                    if (!txtCustomerEmail.getText().toString().contentEquals("") &&
                            !isEmail(txtCustomerEmail.getText().toString())) {
                        Toast toast = Toast.makeText(ctx, "Invalid email!", Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        if (!txtCustomerEmail.getText().toString().contentEquals(""))
                            customerEmail = txtCustomerEmail.getText().toString();
                        if (!txtCustomerfName.getText().toString().contentEquals(""))
                            customerfName = txtCustomerfName.getText().toString();
                        if (!txtCustomerlName.getText().toString().contentEquals(""))
                            customerlName = txtCustomerlName.getText().toString();

                        //TODO: should we really be calling createCustomer???
                        tccart.editCustomerInfo(customerfName, customerlName, customerEmail);
                        txtCurrCustomer.setText(tccart.getCurrentCustomer().getfName() +" "+ tccart.getCurrentCustomer().getlName());
                        Toast toast = Toast.makeText(ctx, getString(R.string.info_saved), Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                    }
                });
        builder.setView(layout);
        return builder;
    }

    //Process Transaction Dialog
    public AlertDialog.Builder transactionDialog(Context context){
        final Context ctx = context;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = View.inflate(this, R.layout.transaction_dialog, null);
        txtSubtotal = (EditText)layout.findViewById(R.id.txtSubtotal);
        builder.setTitle(getString(R.string.process_transaction));
        builder.setPositiveButton(getString(R.string.process), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Transaction t;
                if (txtSubtotal.getText().toString().contentEquals("")) {
                    Toast toast = Toast.makeText(ctx, getString(R.string.enter_subtotal_error), Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    t = tccart.processTransaction(Double.parseDouble(txtSubtotal.getText().toString()));
                    DecimalFormat dc = new DecimalFormat("#0.00");
                    txtTotalDue.setText("$"+dc.format(t.getTotal()));
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setView(layout);
        return builder;
    }

    public static boolean isEmail(String email) {
        boolean isValid = false;
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public void showCustList(){
        Intent intent = new Intent(this, CustList.class);
        startActivityForResult(intent, CUSTOMER_LIST_CODE);
    }
}
