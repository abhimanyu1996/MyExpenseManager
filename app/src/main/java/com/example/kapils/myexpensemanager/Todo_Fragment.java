package com.example.kapils.myexpensemanager;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import at.markushi.ui.CircleButton;

public class Todo_Fragment extends Fragment {

    EditText task;
    Button add;
    CircleButton del, edit;
    ListView lv;
    MyCustomAdapter customAdapter;
    public MyDBHandler dbHandler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo_, container, false);

        //database handler
        dbHandler = new MyDBHandler(getContext(),null,null,1);

        task = (EditText)view.findViewById(R.id.et_task);
        add = (Button)view.findViewById(R.id.btn_add);
        lv= (ListView)view.findViewById(R.id.lv_tasks);


        /*final SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(),
                R.layout.list_row,
                dbHandler.getAllTasks(),
                new String[]{dbHandler.COLUMN_TASK, dbHandler.COLUMN_DNT},
                new int[]{R.id.tv_desc, R.id.tv_dnt},
                0);
        lv.setAdapter(adapter);*/

        final ArrayList<Task> tasks = new ArrayList<Task>();
        try{
            Cursor c = dbHandler.getAllTasks();
            if(c!=null)
            {
                if(c.moveToFirst()){
                    do{
                        String db_desc = c.getString(c.getColumnIndex(dbHandler.COLUMN_TASK));
                        String db_dnt = c.getString(c.getColumnIndex(dbHandler.COLUMN_DNT));
                        int db_temp = c.getInt(c.getColumnIndex(dbHandler.COLUMN_STATUS));
                        boolean db_status = (db_temp==0)?Boolean.FALSE:Boolean.TRUE;
                        tasks.add(new Task(db_desc, db_dnt, db_status));
                    }while (c.moveToNext());
                }
            }
        }catch (SQLException se){
            Log.e(getClass().getSimpleName(), "Could not create or delete database");
        }
        customAdapter = new MyCustomAdapter(tasks); //Loads custom listview first time
        lv.setAdapter(customAdapter);



        add.animate();
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask(v, tasks);
            }
        });



        return view;

    }


    public void addTask(View view, ArrayList<Task> tasks)
    {
        String todo = task.getText().toString().trim();
        if(todo.equals(""))
            Toast.makeText(getActivity().getApplicationContext(), "Please input some task !", Toast.LENGTH_SHORT).show();
        else {
            boolean check = dbHandler.addTask(task.getText().toString(), DateFormat.getDateTimeInstance().format(new Date()),0);
            //check if data added ??
            if(check){
               // Toast.makeText(getContext(),"Data Added",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getContext(),"Error occured data not added",Toast.LENGTH_LONG).show();
            }

            tasks.add(new Task(todo, DateFormat.getDateTimeInstance().format(new Date()),Boolean.FALSE));
            customAdapter = new MyCustomAdapter(tasks);
            lv.setAdapter(customAdapter);
            customAdapter.notifyDataSetChanged();
            task.setText("");
        }

    }

    class MyCustomAdapter extends BaseAdapter{

        public int pos; //To access 'position' inside onActivityResult
        ArrayList<Task> tasks = new ArrayList<Task>();
        MyCustomAdapter(ArrayList<Task> tasks)
        {
            this.tasks = tasks;
        }

        public ArrayList<Task> fetchTask()
        {
            return this.tasks;
        }
        @Override
        public int getCount() {
            return tasks.size();
        }

        @Override
        public String getItem(int position) {
            return tasks.get(position).Desc;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view1 = inflater.inflate(R.layout.list_row, null);
            final TextView desc = (TextView) view1.findViewById(R.id.tv_desc);
            TextView dnt = (TextView) view1.findViewById(R.id.tv_dnt);
            desc.setText(tasks.get(position).Desc);
            dnt.setText(tasks.get(position).Dnt);
            Switch btn = (Switch) view1.findViewById(R.id.state);
            btn.setChecked(tasks.get(position).selected); //maintain switch states as earlier
            if(tasks.get(position).selected) //maintain striked out text on view refresh
                desc.setPaintFlags(desc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            del = (CircleButton)view1.findViewById(R.id.btn_del);
            edit = (CircleButton)view1.findViewById(R.id.edit);


            btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        tasks.get(position).selected = Boolean.TRUE;
                        //saving current switch state in variable 'selected'
                        desc.setPaintFlags(desc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        int check = dbHandler.updateStatus(tasks.get(position).Dnt, 1, null, 's');
                        if(check==0)
                            Toast.makeText(getActivity(), "State cannot be updated !", Toast.LENGTH_SHORT).show();
                        else{
                            //Toast.makeText(getActivity(), "State updated !", Toast.LENGTH_SHORT).show();
                            }

                    }
                    else {
                        tasks.get(position).selected = Boolean.FALSE;
                        desc.setPaintFlags(0);
                        int check = dbHandler.updateStatus(tasks.get(position).Dnt, 0, null, 's');
                        if(check==0)
                            Toast.makeText(getActivity(), "State cannot be updated !", Toast.LENGTH_SHORT).show();
                        else {
                            //Toast.makeText(getActivity(), "State updated !", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int check = dbHandler.deleteTask(tasks.get(position).Dnt);
                    if(check==0)
                        Toast.makeText(getActivity(), "Task cannot be deleted !", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getActivity(), "Task deleted !", Toast.LENGTH_SHORT).show();
                    tasks.remove(position);
                    customAdapter.notifyDataSetChanged();
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pos = position;
                    Intent i = new Intent(Todo_Fragment.this.getActivity(),PopUpdate.class);
                    i.putExtra("FILLER",tasks.get(position).Desc);
                    startActivityForResult(i,2);
                }
            });

            return  view1;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<Task> tasks;
        if(requestCode == 2)  //Save data to db
        {
            tasks = customAdapter.fetchTask();
            int check = dbHandler.updateStatus(tasks.get(customAdapter.pos).Dnt, -1 ,data.getStringExtra("INPUT") , 't');
            if(check==0)
                Toast.makeText(getActivity(), "Task cannot be updated !", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getActivity(), "Task updated !", Toast.LENGTH_SHORT).show();
            //update listview
            tasks.set(customAdapter.pos, new Task(data.getStringExtra("INPUT"),DateFormat.getDateTimeInstance().format(new Date()),tasks.get(customAdapter.pos).selected));
            customAdapter.notifyDataSetChanged();

        }
    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("ToDoList");
    }
}
