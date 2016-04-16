package edu.gatech.seclass.tccart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Collections;
import tccart.seclass.gatech.edu.tccart.R;

public class TransactionList extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Customer> customers;
    private ArrayList<Transaction> transactions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        listView = (ListView)findViewById(R.id.transactionList);
        customers = TCCart.getInstance().getCustomerList();

        //Register for context menu
        registerForContextMenu(listView);
        customers = TCCart.getInstance().getCustomerList();

        //Get position
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        Log.d("TRANSACTIONS", "Getting Extra...Position: " + position);
        transactions = customers.get(position).getTransactions();
        Log.d("TRANSACTIONS", "Transaction List Size: "+transactions.size());

        //Sort Descending Order dates
        Collections.sort(transactions, Transaction.dateCompare());

        //Standard Adapter
        listView.setAdapter(
                new ArrayAdapter<>(this, R.layout.transaction, transactions));
    }

}
