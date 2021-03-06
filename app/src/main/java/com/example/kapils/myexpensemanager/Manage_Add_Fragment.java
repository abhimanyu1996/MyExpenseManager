package com.example.kapils.myexpensemanager;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Manage_Add_Fragment extends Fragment {

    Button categorybtn;
    EditText categortet;
    ListView managelistview;
    MyDBHandler dbHandler;
    SimpleCursorAdapter adapter;

    public Manage_Add_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_add, container, false);
        dbHandler = new MyDBHandler(getContext(),null,null,1);

        //initialize
        managelistview = (ListView)view.findViewById(R.id.managelistview);
        categortet = (EditText) view.findViewById(R.id.addcategoryet);
        categorybtn= (Button) view.findViewById(R.id.addcategorybtn);

        //set list view adapter
        adapter = new SimpleCursorAdapter(getContext(),
                android.R.layout.simple_list_item_1,
                dbHandler.getAllCategories(),
                new String[]{dbHandler.COLUMN_CAT_NAME},
                new int[]{android.R.id.text1},
                0);
        managelistview.setAdapter(adapter);

        registerForContextMenu(managelistview);
        //set long press listener on listview

        //set add button listener
        categorybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cattext = categortet.getText().toString();

                if(cattext.isEmpty()){
                    Toast.makeText(getContext(),"Add Category field empty",Toast.LENGTH_LONG).show();
                }
                else{
                    dbHandler.addCategory(cattext,null);
                    adapter.changeCursor(dbHandler.getAllCategories());
                    adapter.notifyDataSetChanged();
                    categortet.setText("");
                    Toast.makeText(getContext(),"Category added",Toast.LENGTH_LONG).show();
                }

            }
        });


        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId()==R.id.managelistview){
            menu.add(0,0,0,"Delete");

            //context menu listener for tihis fragment
            MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    int position = menuInfo.position;
                    Cursor c = (Cursor) adapter.getItem(position);

                    if(item.getGroupId()==0) {
                        switch (item.getItemId()) {
                            case 0:
                                boolean chk = dbHandler.deleteCategory(c.getInt(0));


                                if (chk) {
                                    Toast.makeText(getContext(), "Category Deleted", Toast.LENGTH_LONG).show();
                                    adapter.changeCursor(dbHandler.getAllCategories());
                                    adapter.notifyDataSetChanged();
                                } else
                                    Toast.makeText(getContext(), "There was some problem", Toast.LENGTH_LONG).show();

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
                case 0:
                    boolean chk = dbHandler.deleteCategory(c.getInt(0));


                    if (chk) {
                        Toast.makeText(getContext(), "Category Deleted", Toast.LENGTH_LONG).show();
                        adapter.changeCursor(dbHandler.getAllCategories());
                        adapter.notifyDataSetChanged();
                    } else
                        Toast.makeText(getContext(), "There was some problem", Toast.LENGTH_LONG).show();

                    break;
            }
        }
        return true;

    }*/
}
