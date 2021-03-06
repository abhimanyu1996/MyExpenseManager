package com.example.kapils.myexpensemanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Manage_Search_Category extends Fragment {

    private ListView zsumlv;
    private MyDBHandler dbHandler;
    private Button frombtn, tobtn;
    private TextView totaltv;
    private Spinner categoryspinner;

    private Calendar todate, fromdate;
    private static SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
    private static SimpleDateFormat sdrevformat = new SimpleDateFormat("yyyy-MM-dd");

    private CustomSimpleCursorAdapter madapter;
    private int mcategory = -1;

    public Manage_Search_Category() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_search_category, container, false);

        //initialize all
        zsumlv = (ListView)view.findViewById(R.id.managecustomsearchlv);
        dbHandler = new MyDBHandler(getContext(),null,null,1);
        frombtn = (Button) view.findViewById(R.id.managefrombutton);
        tobtn = (Button) view.findViewById(R.id.managetobtn);
        totaltv = (TextView)view.findViewById(R.id.managecustotal);
        categoryspinner = (Spinner)view.findViewById(R.id.managecatspinner);

        //initalize calender variables
        todate = Calendar.getInstance();
        fromdate = Calendar.getInstance();
        fromdate.add(Calendar.DATE, -30);

        //set button text
        frombtn.setText(sdformat.format(fromdate.getTime()));
        tobtn.setText(sdformat.format(todate.getTime()));

        //set to and from button listeners
        frombtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment(fromdate,frombtn,null,todate);
                newFragment.setTargetFragment(Manage_Search_Category.this,4);
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        tobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment(todate,tobtn,fromdate,null);
                newFragment.setTargetFragment(Manage_Search_Category.this,4);
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        //set an adapter for spinner listener
        Cursor allcats = dbHandler.getAllCategories();
        MatrixCursor extra = new MatrixCursor(new String[]{"_id",dbHandler.COLUMN_CAT_NAME});
        extra.addRow(new String[]{"-1","All"});
        Cursor[] cursors = {extra,allcats};
        final Cursor extendedcur = new MergeCursor(cursors);

        SimpleCursorAdapter SpinnerAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_spinner_item,
                extendedcur,
                new String[]{dbHandler.COLUMN_CAT_NAME},
                new int[]{android.R.id.text1},
                0
        );
        SpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryspinner.setAdapter(SpinnerAdapter);
        categoryspinner.setSelection(0,true);

        //categoryspinner on select listener
        categoryspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                extendedcur.moveToPosition(position);

                mcategory=extendedcur.getInt(0);

                updatelistandtotal();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        //get cursor
        Cursor querycur =changeAdapterCursor();
        //set adapter for list view
        madapter = new CustomSimpleCursorAdapter(getContext(),
                R.layout.listview_item_layout,
                querycur,
                new String[]{dbHandler.COLUMN_TITLE, ""+dbHandler.COLUMN_AMOUNT,dbHandler.COLUMN_DATE, dbHandler.COLUMN_CAT_NAME},
                new int[]{R.id.itemtitle,R.id.itemamount,R.id.itemdate, R.id.itemcategory},
                0);
        zsumlv.setAdapter(madapter);

        updatelistandtotal();
        registerForContextMenu(zsumlv);

        //let item click listener for listview
        zsumlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) madapter.getItem(position);
                Intent intent = new Intent(getActivity(),ItemPopupActivity.class);
                intent.putExtra("cursorid",c.getInt(c.getColumnIndex(dbHandler.COLUMN_ID)));
                startActivityForResult(intent,8);
            }
        });



        return view;
    }

    private Cursor changeAdapterCursor() {
        if(mcategory!=-1)
            return dbHandler.getQueryExpense("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2)) between '"+sdrevformat.format(fromdate.getTime())+"' and '"+sdrevformat.format(todate.getTime())+"' and "+dbHandler.COLUMN_CATEGORY+"='"+mcategory+"'");
        else
            return dbHandler.getQueryExpense("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2)) between '"+sdrevformat.format(fromdate.getTime())+"' and '"+sdrevformat.format(todate.getTime())+"'");
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId()== zsumlv.getId()){
            menu.add(1,2,2,"Edit");
            menu.add(1,3,3,"Delete");

            MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    int position = menuInfo.position;
                    Cursor c = (Cursor) madapter.getItem(position);


                    if(item.getGroupId()==1) {
                        switch (item.getItemId()) {
                            //edit context item
                            case 2:
                                Intent intent = new Intent(getActivity(), ItemPopupActivity.class);
                                intent.putExtra("cursorid", c.getInt(c.getColumnIndex(dbHandler.COLUMN_ID)));
                                startActivityForResult(intent, 8);

                                break;
                            //delete context item
                            case 3:
                                boolean check = dbHandler.deleteExpense(c.getInt(0));
                                //check if data deleted ??
                                if (check) {
                                    Toast.makeText(getContext(), "Data Deleted Successfully", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getContext(), "Error occured, data not deleted", Toast.LENGTH_LONG).show();
                                }

                                updatelistandtotal();

                                break;
                        }
                    }
                    return true;
                }
            };

            for(int i=0;i<menu.size();i++){
                menu.getItem(i).setOnMenuItemClickListener(listener);
            }
        }


    }

    /*
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint())
            return false;

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        Cursor c = (Cursor) madapter.getItem(position);


        if(item.getGroupId()==1) {
            switch (item.getItemId()) {
                //edit context item
                case 2:
                    Intent intent = new Intent(getActivity(), ItemPopupActivity.class);
                    intent.putExtra("cursorid", c.getInt(c.getColumnIndex(dbHandler.COLUMN_ID)));
                    startActivityForResult(intent, 8);

                    break;
                //delete context item
                case 3:
                    boolean check = dbHandler.deleteExpense(c.getInt(0));
                    //check if data deleted ??
                    if (check) {
                        Toast.makeText(getContext(), "Data Deleted Successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Error occured, data not deleted", Toast.LENGTH_LONG).show();
                    }

                    updatelistandtotal();

                    break;
            }
        }

        return true;

    }
    */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //update listview after update command
        if(requestCode==8){
            boolean check;

            try{
                check = data.getBooleanExtra("done",false);
            }catch (NullPointerException e){
                check = false;
            }

            if(check) {
                updatelistandtotal();
            }
        }
    }

    public void updatelistandtotal(){
        //get cursor
        Cursor querycur =changeAdapterCursor();

        //loop to get total and set value of total tv
        double sumtotal=0;
        boolean exporinc;

        if(querycur.moveToFirst()) {
            do {
                exporinc = querycur.getString(querycur.getColumnIndex(dbHandler.COLUMN_TYPE)).equals("Expense");

                if(exporinc)
                    sumtotal -= querycur.getDouble(querycur.getColumnIndex(dbHandler.COLUMN_AMOUNT));
                else
                    sumtotal += querycur.getDouble(querycur.getColumnIndex(dbHandler.COLUMN_AMOUNT));

            } while (querycur.moveToNext());
        }
        String total = "Total: "+((double)Math.round(sumtotal*100)/100);
        totaltv.setText(total);

        madapter.changeCursor(querycur);
        madapter.notifyDataSetChanged();

    }

    //date fragment class..!!
    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{

        //to update and get value
        private Calendar date;
        private Button btn;
        private Calendar minval, maxval;

        public SelectDateFragment(Calendar c, Button b, Calendar min, Calendar max) {
            date = c;
            btn = b;
            minval = min;
            maxval = max;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog datePickerDialog =  new DatePickerDialog(getActivity(),this,date.get(Calendar.YEAR),date.get(Calendar.MONTH),date.get(Calendar.DAY_OF_MONTH));
            Calendar cal = Calendar.getInstance();

            if(maxval==null)
                datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
            else
                datePickerDialog.getDatePicker().setMaxDate(maxval.getTimeInMillis());


            if(minval != null){
                datePickerDialog.getDatePicker().setMinDate(minval.getTimeInMillis());
            }

            return datePickerDialog;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            //update date
            date.set(year,month,dayOfMonth);
            //reset button value
            btn.setText(sdformat.format(date.getTime()));
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            Manage_Search_Category activity = (Manage_Search_Category) getTargetFragment();
            activity.updatelistandtotal();
        }
    }
}
