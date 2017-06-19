package com.example.kapils.myexpensemanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class Add_Fragment_Expense extends Fragment {

    EditText title, desc , amount;
    Button add;
    Spinner addcatspinner;
    MyDBHandler dbHandler;
    static Button expdatebtn;

    private static int year;
    private static int month;
    private static int day;
    private static SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_expense, container, false);

        //initiate all
        title = (EditText) view.findViewById(R.id.exptitle);
        desc = (EditText) view.findViewById(R.id.expdesc);
        amount = (EditText) view.findViewById(R.id.expamount);
        add = (Button) view.findViewById(R.id.expadd);
        expdatebtn = (Button) view.findViewById(R.id.expchgdate);

        //initiate date variables
        final Calendar c = Calendar.getInstance();
        Date date=c.getTime();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        //set initial now value of date
        expdatebtn.setText(sdformat.format(date));

        //date button listener
        expdatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });


        //database handler
        dbHandler = new MyDBHandler(getContext(),null,null,1);

        //for categories spinner
        addcatspinner = (Spinner) view.findViewById(R.id.addcatspinner);

        //set an adapter for spinner listener
        SimpleCursorAdapter SpinnerAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                dbHandler.getAllCategories(),
                new String[]{dbHandler.COLUMN_CAT_NAME},
                new int[]{android.R.id.text1},
                0
        );
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addcatspinner.setAdapter(SpinnerAdapter);
        addcatspinner.setSelection(0,true);

        //add button click listener
        add.setOnClickListener(

            new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mtitle = title.getText().toString();
                    String mdesc = desc.getText().toString();
                    String mamount = amount.getText().toString();
                    String mcat = ((Cursor)addcatspinner.getSelectedItem()).getString(1);
                    String mdate = expdatebtn.getText().toString();

                    if(mtitle.isEmpty()){
                        Toast.makeText(getContext(),"Please Enter Title",Toast.LENGTH_LONG).show();
                    }
                    else if(mamount.isEmpty()){
                        Toast.makeText(getContext(),"Please Enter Amount",Toast.LENGTH_LONG).show();
                    }
                    else if(mcat.isEmpty()){
                        Toast.makeText(getContext(),"Please Enter Title",Toast.LENGTH_LONG).show();
                    }
                    else if(mdate.isEmpty()){
                        Toast.makeText(getContext(),"Please Enter Title",Toast.LENGTH_LONG).show();
                    }
                    else{
                        float famt = Float.parseFloat(mamount);


                        boolean check = dbHandler.addExpense(mtitle,mdesc, "Expense", famt,mcat,mdate);

                        //check if data added ??
                        if(check){
                            Toast.makeText(getContext(),"Data Added",Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(getContext(),"Error occured data not added",Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        );

        return view;
    }


    //date fragment class..!!
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog datePickerDialog =  new DatePickerDialog(getActivity(),this,year,month,day);
            Calendar cal = Calendar.getInstance();
            datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int syear, int smonth, int sdayOfMonth) {
            year = syear;
            month = smonth;
            day = sdayOfMonth;

            Calendar cal = Calendar.getInstance();
            cal.set(syear,smonth,sdayOfMonth);
            Date date=cal.getTime();
            expdatebtn.setText(sdformat.format(date));
        }
    }


}
