package com.example.kapils.myexpensemanager;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class Summary_Allinone_Fragment extends Fragment {

    private String query;
    private ListView sumlv;
    private TextView totaltv;
    private MyDBHandler dbHandler;

    SimpleCursorAdapter adapter;


    public Summary_Allinone_Fragment(String sq) {
        if(sq==null){
            query = "";
        }
        else{
            query=sq;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary__allinone_, container, false);
        dbHandler = new MyDBHandler(getContext(),null,null,1);

        //initialize variables
        sumlv = (ListView)view.findViewById(R.id.sumallinonelv);
        totaltv = (TextView)view.findViewById(R.id.sumaiototal);

        //get cursor
        Cursor querycur = dbHandler.getQueryExpense(query);

        //loop to get total and set value of total tv
        double sumtotal=0;
        if(querycur.moveToFirst()) {
            do {
                sumtotal += querycur.getDouble(querycur.getColumnIndex(dbHandler.COLUMN_AMOUNT));
            } while (querycur.moveToNext());
        }
        totaltv.setText("Total: "+((double)Math.round(sumtotal*100)/100));


        //initialize cursor adapter
        adapter= new SimpleCursorAdapter(getContext(),
                R.layout.listview_item_layout,
                querycur,
                new String[]{dbHandler.COLUMN_TITLE, ""+dbHandler.COLUMN_AMOUNT,dbHandler.COLUMN_DATE, dbHandler.COLUMN_CATEGORY},
                new int[]{R.id.itemtitle,R.id.itemamount,R.id.itemdate, R.id.itemcategory},
                0);
        sumlv.setAdapter(adapter);

        //set on click
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
                //get cursor
                Cursor querycur = dbHandler.getQueryExpense(query);

                //loop to get total and set value of total tv
                double sumtotal=0;
                if(querycur.moveToFirst()) {
                    do {
                        sumtotal += querycur.getDouble(querycur.getColumnIndex(dbHandler.COLUMN_AMOUNT));
                    } while (querycur.moveToNext());
                }

                adapter.changeCursor(querycur);
                adapter.notifyDataSetChanged();
            }
        }
    }

}
