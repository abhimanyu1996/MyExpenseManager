package com.example.kapils.myexpensemanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Summary_Allinone_Fragment extends Fragment {

    private String query;
    private ListView sumlv;
    private TextView totaltv;
    private MyDBHandler dbHandler;
    private RadioGroup typerg;

    CustomSimpleCursorAdapter adapter;
    private String mtype;

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
        typerg = (RadioGroup)view.findViewById(R.id.aiosumtyperadiogroup);
        mtype = "";

        //radiogroup type listener
        typerg.check(R.id.aioallradiobtn);
        typerg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if(checkedId == R.id.aioexpenseradiobtn){
                    mtype = "Expense";
                }
                else if(checkedId == R.id.aioincomeradiobtn){
                    mtype = "Income";
                }
                else{
                    mtype = "";
                }

                updatelistandtotal();
            }
        });

        //get cursor
        Cursor querycur = dbHandler.getQueryExpense(query);

        //initialize cursor adapter
        adapter= new CustomSimpleCursorAdapter(getContext(),
                R.layout.listview_item_layout,
                querycur,
                new String[]{dbHandler.COLUMN_TITLE, ""+dbHandler.COLUMN_AMOUNT,dbHandler.COLUMN_DATE, dbHandler.COLUMN_CAT_NAME},
                new int[]{R.id.itemtitle,R.id.itemamount,R.id.itemdate, R.id.itemcategory},
                0);
        sumlv.setAdapter(adapter);

        updatelistandtotal();

        registerForContextMenu(sumlv);
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

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId()== sumlv.getId()){
            menu.add(0,0,0,"Edit");
            menu.add(0,1,1,"Delete");


            //context menu listener for tihis fragment
            MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    int position = menuInfo.position;
                    Cursor c = (Cursor) adapter.getItem(position);

                    if(item.getGroupId()==0) {
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

        if(item.getGroupId()==0) {
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
                //get cursor
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
        String total = "Total: "+(Math.round(sumtotal*100)/100);
        totaltv.setText(total);

        adapter.changeCursor(querycur);
        adapter.notifyDataSetChanged();

    }


    private Cursor changeAdapterCursor() {
        if(!mtype.equals(""))
            return dbHandler.getQueryExpense(query+" and "+dbHandler.COLUMN_TYPE+"='"+mtype+"'");
        else
            return dbHandler.getQueryExpense(query);

    }


}
