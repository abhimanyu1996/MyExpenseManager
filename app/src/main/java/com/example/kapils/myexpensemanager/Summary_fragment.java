package com.example.kapils.myexpensemanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class Summary_fragment extends Fragment {

    ListView smy;
    MyDBHandler dbHandler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        smy = (ListView) view.findViewById(R.id.summarylistview);
        dbHandler = new MyDBHandler(getContext(),null,null,1);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(),
                R.layout.listview_item_layout,
                dbHandler.getAllExpense(),
                new String[]{dbHandler.COLUMN_TITLE, ""+dbHandler.COLUMN_AMOUNT,dbHandler.COLUMN_DATE, dbHandler.COLUMN_CATEGORY},
                new int[]{R.id.itemtitle,R.id.itemamount,R.id.itemdate, R.id.itemcategory},
                0);

        smy.setAdapter(adapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Summary");
    }
}
