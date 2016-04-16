package edu.gatech.seclass.tccart;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import tccart.seclass.gatech.edu.tccart.R;

public class CustList extends AppCompatActivity {
    private ListView listView;
    private ArrayList<Customer> customers;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_list);

        listView = (ListView)findViewById(R.id.customer_list);

        //Register for context menu
        registerForContextMenu(listView);
        customers = TCCart.getInstance().getCustomerList();
        Collections.sort(customers, Customer.nameCompare());

        //Custom Adapter
        listView.setAdapter(
                new CustomerAdapter(this, R.layout.customer, customers));

        //Set OnClickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
                Log.d("CUSTLIST", "Clicked Customer: " + customers.get(position));
                //Start new intent here. Need to display all transactions in separate ArrayAdapter.
                //Use the transaction toString() method!
                showTransactions(position);
            }
        });
    }

    public void showTransactions(int position){
        Intent intent = new Intent(MainActivity.getContextInstance(), TransactionList.class);
        Log.d("TRANSACTIONS", "Putting Extra: " + position);
        intent.putExtra("position", position);//Pass the index position
        startActivityForResult(intent, MainActivity.TRANSACTION_LIST_CODE);
    }

    public void showInfo(Customer c){
        AlertDialog.Builder builder = new AlertDialog.Builder(CustList.this);
        View layout = View.inflate(this, R.layout.customer_info, null);
        TextView name = (TextView)layout.findViewById(R.id.txtName);
        TextView email = (TextView)layout.findViewById(R.id.txtEmail);
        TextView id = (TextView)layout.findViewById(R.id.txtId);
        TextView vip = (TextView)layout.findViewById(R.id.txtVip);
        TextView credits = (TextView)layout.findViewById(R.id.txtCredits);
        TextView vipNextYear = (TextView)layout.findViewById(R.id.txtVipNextYear);
        DecimalFormat df = new DecimalFormat("$#,##0.00");

        //Customer Info Dialog
        builder.setTitle("Customer Info");
        builder.setCancelable(true);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        name.setText(c.getfName() + " " + c.getlName());
        email.setText(c.getEmail());
        id.setText(c.getId());
        if (c.isVipStatus())
            vip.setText(getString(R.string.customer_is_vip));
        if (!c.isVipStatus())
            vip.setText(getString(R.string.not_vip));
        credits.setText(df.format(c.getCurrentCredit()));
        if (c.isEarnedVipNextYear())
            vipNextYear.setText(getString(R.string.vip_next_year));
        else
            vipNextYear.setText(df.format(c.getAmountUntilVip())+ " "+ getString(R.string.more_for_vip));
        builder.setView(layout);
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.show_info:
                showInfo(TCCart.getInstance().getCustomerList().get(info.position));
                return true;
            case R.id.show_transactions:
                showTransactions(info.position);
                return true;
            case R.id.delete_customer:
                TCCart.getInstance().getCustomerList().remove(info.position);
                listView.invalidateViews();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
