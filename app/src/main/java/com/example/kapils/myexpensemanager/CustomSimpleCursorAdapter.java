package com.example.kapils.myexpensemanager;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;


public class CustomSimpleCursorAdapter extends SimpleCursorAdapter {

    Cursor cur;
    public CustomSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        cur = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);

        if(cur.moveToPosition(position)){
            String type = cur.getString(cur.getColumnIndex("type"));
            if(type.equals("Expense")){
                v.setBackgroundColor(Color.parseColor("#ff8080"));
            }
            else{
                v.setBackgroundColor(Color.parseColor("#00b300"));
            }
        }
        return v;
    }

    @Override
    public void changeCursor(Cursor cursor) {
        cur = cursor;
        super.changeCursor(cursor);
    }
}
