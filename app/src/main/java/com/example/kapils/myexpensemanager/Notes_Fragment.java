package com.example.kapils.myexpensemanager;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Priyanjul on 20-06-2017.
 */

public class Notes_Fragment extends Fragment {

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<MyNote> notesList;
    private FloatingActionButton add;
    public MyDBHandler dbHandler;
    private int flag;
    public int pos;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_view);
        add = (FloatingActionButton)view.findViewById(R.id.btn_add);
        notesList = new ArrayList<>();


        adapter = new NotesAdapter(getActivity(),notesList);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        dbHandler = new MyDBHandler(getContext(),null,null,1);
        //addNotes();

        //Toast.makeText(getContext(), "initiating", Toast.LENGTH_SHORT).show();
        try{
            //Toast.makeText(getContext(), "inside try", Toast.LENGTH_SHORT).show();
            Cursor c = dbHandler.getAllNotes();
            //Toast.makeText(getContext(), "value of c = ", Toast.LENGTH_SHORT).show();

            if(c!=null)
            {
                //Toast.makeText(getContext(), "c!=Null", Toast.LENGTH_SHORT).show();
                if(c.moveToFirst()){
                    do{
                        String db_title = c.getString(c.getColumnIndex(dbHandler.COLUMN_NOTE_TITLE));
                        String db_note = c.getString(c.getColumnIndex(dbHandler.COLUMN_NOTE));
                        String db_dt = c.getString(c.getColumnIndex(dbHandler.COLUMN_DT));
                        addNotes(db_note, db_title, db_dt, 0);
                    }while (c.moveToNext());
                }
            }
        }catch (SQLException se){
            Log.e(getClass().getSimpleName(), "Could not create or delete database");
        }



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Notes_Fragment.this.getActivity(), DisplayNote.class);
                i.putExtra("TITLE","init");
                i.putExtra("NOTE", "init");
                startActivityForResult(i, 1);
                //startActivityForResult(new Intent(Notes_Fragment.this.getActivity(), DisplayNote.class),1);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                //Movie movie = movieList.get(position);
                MyNote note = notesList.get(position);
                Intent i = new Intent(Notes_Fragment.this.getActivity(), DisplayNote.class);
                i.putExtra("TITLE",note.getTitle());
                i.putExtra("NOTE", note.getNote());
                i.putExtra("POSITION",Integer.toString(position));
                startActivityForResult(i, 2);
                //Toast.makeText(getContext(), note.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        registerForContextMenu(recyclerView);


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String response = data.getStringExtra("IsDELETED");

            if(requestCode == 1) //create new note
            {
                if(response.equals("n")){
                    String fnote = data.getStringExtra("NOTE");
                    String ftitle = data.getStringExtra("TITLE");
                    addNotes(fnote, ftitle, DateFormat.getDateTimeInstance().format(new Date()), 1);
                }
                if(response.equals("y")){
                //do nothing
                }
            }
            if(requestCode == 2)  //update existing note
            {
                int position = Integer.parseInt(data.getStringExtra("POSITION"));
                if(response.equals("n")){
                    String fnote = data.getStringExtra("NOTE");
                    String ftitle = data.getStringExtra("TITLE");
                    int check = dbHandler.updateNote(ftitle.toUpperCase(),fnote, notesList.get(position).getDt());
                    if(check==0)
                        Toast.makeText(getActivity(), "Task cannot be updated !", Toast.LENGTH_SHORT).show();
                    else
                        //Toast.makeText(getActivity(), "Task updated !", Toast.LENGTH_SHORT).show();
                    notesList.set(position, new MyNote(ftitle.toUpperCase(), fnote, DateFormat.getDateTimeInstance().format(new Date())));
                    adapter.notifyDataSetChanged();

                }
                if(response.equals("y")){
                    pos = position;
                    int check = dbHandler.deleteNote(notesList.get(position).getDt());
                    if(check==0)
                        Toast.makeText(getActivity(), "Task cannot be deleted !", Toast.LENGTH_SHORT).show();
                    else{
                        //Toast.makeText(getActivity(), "Task deleted !", Toast.LENGTH_SHORT).show();
                        Snackbar snackbar = Snackbar.make(recyclerView, notesList.get(position).getTitle()+" note deleted !", Snackbar.LENGTH_SHORT);
                        snackbar.show();

                    }
                    notesList.remove(position);
                    adapter.notifyDataSetChanged();
                }
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if(v.getId()== R.id.recycler_view){
            menu.add(0,0,0,"Edit");
            menu.add(0,1,1,"Delete");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = ((NotesAdapter)recyclerView.getAdapter()).getPosition();
        switch (item.getItemId()){
            //edit context item
            case 0:
                MyNote note = notesList.get(position);
                Intent i = new Intent(Notes_Fragment.this.getActivity(), DisplayNote.class);
                i.putExtra("TITLE",note.getTitle());
                i.putExtra("NOTE", note.getNote());
                i.putExtra("POSITION",Integer.toString(position));
                startActivityForResult(i, 2);
                //Toast.makeText(getActivity(), "Edit clicked"+position, Toast.LENGTH_SHORT).show();

                break;
            //delete context item
            case 1:
                final AlertDialog.Builder builder = new AlertDialog.Builder(Notes_Fragment.this.getActivity());
                View mview = getActivity().getLayoutInflater().inflate(R.layout.notes_alert_box, null);
                Button can = (Button)mview.findViewById(R.id.btn_cancel);
                Button del = (Button)mview.findViewById(R.id.btn_delete);
                builder.setView(mview);
                final AlertDialog dialog = builder.create();
                can.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getContext(), "Cancel", Toast.LENGTH_SHORT).show();
                        //AlertDialog
                        dialog.dismiss();
                    }
                });

                del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                        int check = dbHandler.deleteNote(notesList.get(getPosition()).getDt());
                        if(check==0)
                            Toast.makeText(getActivity(), "Task cannot be deleted !", Toast.LENGTH_SHORT).show();
                        else{
                            //Toast.makeText(getActivity(), "Task deleted !", Toast.LENGTH_SHORT).show();
                            Snackbar snackbar = Snackbar.make(recyclerView, notesList.get(getPosition()).getTitle()+" note deleted !", Snackbar.LENGTH_SHORT);
                            snackbar.show();

                        }
                        notesList.remove(getPosition());
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();


                    }
                });


                dialog.show();
                //Toast.makeText(getActivity(), "delete clicked"+position, Toast.LENGTH_SHORT).show();

                break;
        }

        return true;

    }


    public int getPosition()
    {
        return pos;
    }


    private void deleteNotes(int position)
    {
        int check = dbHandler.deleteNote(notesList.get(position).getDt());
        if(check==0)
            Toast.makeText(getActivity(), "Task cannot be deleted !", Toast.LENGTH_SHORT).show();
        else{
            //Toast.makeText(getActivity(), "Task deleted !", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(recyclerView, notesList.get(position).getTitle()+" note deleted !", Snackbar.LENGTH_SHORT);
            snackbar.show();

        }
        notesList.remove(position);
        adapter.notifyDataSetChanged();
    }

    private void addNotes(String note, String title, String dt, int flag){
        if(flag==1) {
            boolean check = dbHandler.add_dbNote(title, note, dt);
            if (check) {
                //Toast.makeText(getContext(), "Data Added", Toast.LENGTH_LONG).show();
                MyNote myNote = new MyNote(title.toUpperCase(), note, dt);
                notesList.add(myNote);
            } else {
                Toast.makeText(getContext(), "Error occured data not added", Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            MyNote myNote = new MyNote(title.toUpperCase(), note, dt);
            notesList.add(myNote);
        }




        /*myNote = new MyNote("Title2", "huvbjwdsmkcnjeidwml smcjfehiwjdkns mcjbfehijdskm cjbdbl,.d ,vl,vdfehijksncjfehij", DateFormat.getDateTimeInstance().format(new Date()));
        notesList.add(myNote);

        myNote = new MyNote("Title3", "huvbjwdsmkcnjeidwmkcl,.xl smcjfehiwjdkns mcjbfehijdskm cjbdbl,.d ,vl,vdfehijksncjfehij", DateFormat.getDateTimeInstance().format(new Date()));
        notesList.add(myNote);

        myNote = new MyNote("Title4", "qwertyuiopasdfghjklzxcvbnm", DateFormat.getDateTimeInstance().format(new Date()));
        notesList.add(myNote);

        myNote = new MyNote("Title5", "huvbjwdsmkcnjmx,eidwml smcjfehiwjdkns mcjbfehijdskm cjbdbl,.d ,vl,vdfehijksncjfehij", DateFormat.getDateTimeInstance().format(new Date()));
        notesList.add(myNote);*/

        adapter.notifyDataSetChanged();


    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Notes");
        registerForContextMenu(recyclerView);
    }

    /*public static class AlertBoxFragment extends DialogFragment{
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setIcon(R.drawable.ic_delete_note)
                    .setTitle("Kar dein fir delete ???")
                    .setMessage("Are you sure a**hole ?")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something else
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            // Do something else
                        }
                    }).create();

            }
        }*/
    }

