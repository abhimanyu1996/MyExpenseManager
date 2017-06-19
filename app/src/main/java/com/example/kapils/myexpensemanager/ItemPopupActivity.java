package com.example.kapils.myexpensemanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecurityPermission;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ItemPopupActivity extends AppCompatActivity {

    private TextView titletv, desctv, amounttv;
    private Spinner catspinner;
    private Button datebtn,updatebtn, deletebtn;
    private MyDBHandler dbHandler;

    private Calendar datecal;
    private static SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_popup);
        dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);

        //set screen size
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.7));

        //get cursor data
        Intent i = getIntent();
        final int itemid = i.getIntExtra("cursorid",0);

        //get item data from database
        Cursor c = dbHandler.getQueryExpense("where "+dbHandler.COLUMN_ID+"="+itemid);

        //initialize variables
        titletv = (TextView) findViewById(R.id.poptitle);
        desctv = (TextView) findViewById(R.id.popdesc);
        amounttv = (TextView) findViewById(R.id.popamount);
        catspinner = (Spinner) findViewById(R.id.popcategory);
        datebtn = (Button) findViewById(R.id.popdatebtn);
        updatebtn = (Button) findViewById(R.id.popupdate);
        deletebtn = (Button) findViewById(R.id.popdeletebtn);

        //set values equal to selecte item
        if(c.moveToFirst()) {
            titletv.setText(c.getString(c.getColumnIndex(dbHandler.COLUMN_TITLE)));
            desctv.setText(c.getString(c.getColumnIndex(dbHandler.COLUMN_DESC)));
            amounttv.setText(c.getString(c.getColumnIndex(dbHandler.COLUMN_AMOUNT)));

            //set spinner adapter
            Cursor spinnercursor = dbHandler.getAllCategories();
            SimpleCursorAdapter SpinnerAdapter = new SimpleCursorAdapter(
                    getApplicationContext(),
                    android.R.layout.simple_spinner_item,
                    spinnercursor,
                    new String[]{dbHandler.COLUMN_CAT_NAME},
                    new int[]{android.R.id.text1},
                    0
            );
            SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            catspinner.setAdapter(SpinnerAdapter);

            //set spinner selected item
            String itemcategory = c.getString(c.getColumnIndex(dbHandler.COLUMN_CATEGORY));
            int cpos = 0;
            for(int x=0; x<SpinnerAdapter.getCount();x++){
                spinnercursor.moveToPosition(x);
                String Temp = spinnercursor.getString(spinnercursor.getColumnIndex(dbHandler.COLUMN_CAT_NAME));
                if(Temp.contentEquals(itemcategory)){
                    cpos = x;
                    break;
                }
            }
            catspinner.setSelection(cpos);


        }

        //set date initial value
        datecal = Calendar.getInstance();
        String itemdatestr="";
        try {
            itemdatestr = c.getString(c.getColumnIndex(dbHandler.COLUMN_DATE));
        }catch (CursorIndexOutOfBoundsException e){
            Toast.makeText(getApplicationContext(),"Either the Transaction Deleted or Updated",Toast.LENGTH_LONG).show();

            Intent intent = new Intent();
            intent.putExtra("done",true);
            setResult(8,intent);
            finish();
        }

        try {
            datecal.setTime(sdformat.parse(itemdatestr));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        datebtn.setText(itemdatestr);

        //set date listener
        datebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment(datecal,datebtn);
                newFragment.show(getSupportFragmentManager(), "DatePicker");
            }
        });

        //update button click listener
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mtitle = titletv.getText().toString();
                String mdesc = desctv.getText().toString();
                String mamount = amounttv.getText().toString();
                String mcat = ((Cursor)catspinner.getSelectedItem()).getString(1);
                String mdate = datebtn.getText().toString();

                if(mtitle.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Enter Title",Toast.LENGTH_LONG).show();
                }
                else if(mamount.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Enter Amount",Toast.LENGTH_LONG).show();
                }
                else if(mcat.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Enter Title",Toast.LENGTH_LONG).show();
                }
                else if(mdate.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Enter Title",Toast.LENGTH_LONG).show();
                }
                else{
                    float famt = Float.parseFloat(mamount);


                    boolean check = dbHandler.updateExpense(itemid,mtitle,mdesc, "Expense", famt,mcat,mdate);

                    //check if data added ??
                    if(check){
                        Toast.makeText(getApplicationContext(),"Data Updated",Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Error occured data not updated",Toast.LENGTH_LONG).show();
                    }

                    Intent intent = new Intent();
                    intent.putExtra("done",check);
                    setResult(8,intent);
                    finish();
                }
            }
        });

        //set listener for delete button
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean check = dbHandler.deleteExpense(itemid);
                //check if data deleted ??
                if(check){
                    Toast.makeText(getApplicationContext(),"Data Deleted Successfully",Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Error occured, data not deleted",Toast.LENGTH_LONG).show();
                }

                Intent intent = new Intent();
                intent.putExtra("done",check);
                setResult(8,intent);
                finish();
            }
        });
    }

    //date fragment class
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        Calendar cal;
        Button btn;

        public SelectDateFragment(Calendar c, Button b) {
            cal=c;
            btn=b;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog datePickerDialog =  new DatePickerDialog(getActivity(),this,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
            Calendar cal = Calendar.getInstance();
            datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int syear, int smonth, int sdayOfMonth) {
            cal.set(syear,smonth,sdayOfMonth);
            btn.setText(sdformat.format(cal.getTime()));
        }
    }
}
