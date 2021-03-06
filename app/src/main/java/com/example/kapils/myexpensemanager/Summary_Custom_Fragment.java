package com.example.kapils.myexpensemanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Summary_Custom_Fragment extends Fragment {

    private ListView sumlv;
    private MyDBHandler dbHandler;
    private Button frombtn, tobtn;
    private TextView totaltv;
    private RadioGroup typerg;

    private Calendar todate, fromdate;
    private static SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
    private static SimpleDateFormat sdrevformat = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleCursorAdapter adapter;
    private String mtype;


    public Summary_Custom_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary_custom, container, false);

        //initialize all
        sumlv = (ListView)view.findViewById(R.id.sumcustomlv);
        dbHandler = new MyDBHandler(getContext(),null,null,1);
        frombtn = (Button) view.findViewById(R.id.sumfrombutton);
        tobtn = (Button) view.findViewById(R.id.sumtobtn);
        totaltv = (TextView)view.findViewById(R.id.sumcustotal);
        typerg = (RadioGroup)view.findViewById(R.id.sumtyperadiogroup);
        mtype = "";

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
                newFragment.setTargetFragment(Summary_Custom_Fragment.this,4);
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });
        tobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SelectDateFragment(todate,tobtn,fromdate,null);
                newFragment.setTargetFragment(Summary_Custom_Fragment.this,4);
                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });

        //radiogroup type listener
        typerg.check(R.id.allradiobtn);
        typerg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.expenseradiobtn){
                    mtype = "Expense";
                }
                else if(checkedId == R.id.incomeradiobtn){
                    mtype = "Income";
                }
                else{
                    mtype = "";
                }

                updatelistandtotal();
            }
        });


        //get cursor
        Cursor querycur =changeAdapterCursor();
        //set adapter for list view
        adapter = new CustomSimpleCursorAdapter(getContext(),
                R.layout.listview_item_layout,
                querycur,
                new String[]{dbHandler.COLUMN_TITLE, ""+dbHandler.COLUMN_AMOUNT,dbHandler.COLUMN_DATE,dbHandler.COLUMN_CAT_NAME},
                new int[]{R.id.itemtitle,R.id.itemamount,R.id.itemdate, R.id.itemcategory},
                0);
        sumlv.setAdapter(adapter);

        updatelistandtotal();

        registerForContextMenu(sumlv);

        //let item click listener for listview
        sumlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = (Cursor) adapter.getItem(position);
                Intent intent = new Intent(getActivity(),ItemPopupActivity.class);
                intent.putExtra("cursorid",c.getInt(c.getColumnIndex(dbHandler.COLUMN_ID)));
                startActivityForResult(intent,8);
            }
        });

        return view;
    }

    private Cursor changeAdapterCursor() {
        if(!mtype.equals(""))
            return dbHandler.getQueryExpense("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2)) between '"+sdrevformat.format(fromdate.getTime())+"' and '"+sdrevformat.format(todate.getTime())+"' and "+dbHandler.COLUMN_TYPE+"='"+mtype+"'");
        else
            return dbHandler.getQueryExpense("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2)) between '"+sdrevformat.format(fromdate.getTime())+"' and '"+sdrevformat.format(todate.getTime())+"'");

    }


    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId()== sumlv.getId()){
            menu.add(1,0,0,"Edit");
            menu.add(1,1,1,"Delete");


            //context menu listener for tihis fragment
            MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    int position = menuInfo.position;
                    Cursor c = (Cursor) adapter.getItem(position);

                    if(item.getGroupId()==1) {
                        switch (item.getItemId()) {
                            //edit context item
                            case 0:
                                Intent intent = new Intent(getActivity(), ItemPopupActivity.class);
                                intent.putExtra("cursorid", c.getInt(c.getColumnIndex(dbHandler.COLUMN_ID)));
                                startActivityForResult(intent, 8);

                                break;
                            //delete context item
                            case 1:
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
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = menuInfo.position;
        Cursor c = (Cursor) adapter.getItem(position);

        if(item.getGroupId()==1) {
            switch (item.getItemId()) {
                //edit context item
                case 0:
                    Intent intent = new Intent(getActivity(), ItemPopupActivity.class);
                    intent.putExtra("cursorid", c.getInt(c.getColumnIndex(dbHandler.COLUMN_ID)));
                    startActivityForResult(intent, 8);

                    break;
                //delete context item
                case 1:
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
        /*//update listview after exit datedialog box
        else if(requestCode ==4 && resultCode==4){
            //get cursor
            Cursor querycur =dbHandler.getQueryExpense("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2)) between '"+sdrevformat.format(fromdate.getTime())+"' and '"+sdrevformat.format(todate.getTime())+"'");

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
            totaltv.setText("Total: "+((double)Math.round(sumtotal*100)/100));

            adapter.changeCursor(querycur);
            adapter.notifyDataSetChanged();

        }*/
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
        String total = "Total: "+(Math.round(sumtotal*100)/100);

        totaltv.setText(total);

        adapter.changeCursor(querycur);
        adapter.notifyDataSetChanged();

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
            Summary_Custom_Fragment activity = (Summary_Custom_Fragment) getTargetFragment();
            activity.updatelistandtotal();
        }
    }

}
