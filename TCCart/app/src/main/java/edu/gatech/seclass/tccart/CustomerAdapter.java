package edu.gatech.seclass.tccart;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import tccart.seclass.gatech.edu.tccart.R;

public class CustomerAdapter extends ArrayAdapter<Customer> {
    private Context context;
    private ArrayList<Customer> customers;

    public CustomerAdapter(Context context, int textViewResourceId, ArrayList<Customer> customers){
        super(context, textViewResourceId, customers);
        this.context = context;
        this.customers = customers;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;

        //In first run, row will be null. Expensive process we don't want to repeat every time.
        if (row == null){
            Log.d("CUSTADAPTER", "Row was null.");
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.customer, null);
        }

        //Variable Setup
        Customer c = customers.get(position);
        TextView name = (TextView)row.findViewById(R.id.customer_name);
        TextView email = (TextView)row.findViewById(R.id.customer_email);

        //Sanity Checking
        if (c == null || name == null || email == null)
            return row;

        //Set the TextViews
        name.setText(c.getfName() + " " + c.getlName());
        if (c.isVipStatus())
            name.setTextColor(MainActivity.getContextInstance().getResources().getColor(R.color.yellow));
        email.setText(c.getEmail());

        return row;
    }
}
