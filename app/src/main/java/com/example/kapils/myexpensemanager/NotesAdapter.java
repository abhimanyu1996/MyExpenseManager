package com.example.kapils.myexpensemanager;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Priyanjul on 23-06-2017.
 */

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private int pos;
    private Context mContext;
    private List<MyNote> notesList;
    private String bg_colors[] = {"#D7BDE2" , "#AED6F1" , "#A9CCE3" , "#A3E4D7" , "#F9E79F" , "#FAD7A0" , "#F5B7B1"};
    private String title_colors[]={"#9B59B6" , "#5DADE2" , "#5499C7" , "#48C9B0" , "#F4D03F" , "#F5B041" , "#EC7063"};

    public class MyViewHolder extends RecyclerView.ViewHolder  //implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener
    {

        public TextView title, note, dt;
        //public ImageView overflow;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.tv_title);
            note = (TextView)itemView.findViewById(R.id.tv_note);
            dt = (TextView)itemView.findViewById(R.id.tv_dt);
            //overflow = (ImageView)itemView.findViewById(R.id.overflow);
            //itemView.setOnCreateContextMenuListener(this);
        }


    }




    public NotesAdapter(Context mContext, List<MyNote> notesList)
    {
        this.mContext = mContext;
        this.notesList = notesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card, parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        pos = position;
        MyNote note = notesList.get(position);
        holder.note.setText(note.getNote());
        holder.note.setBackgroundColor(Color.parseColor(bg_colors[position%(bg_colors.length)]));
        holder.title.setText(note.getTitle());
        holder.title.setTypeface(Typeface.DEFAULT_BOLD);
        holder.dt.setText(note.getDt());
        holder.title.setTextColor(Color.parseColor(title_colors[position%(title_colors.length)]));

        /*holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return ;
            }
        });*/

        /*holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(holder.overflow);
            }
        });*/

    }

    public int getPosition(){
        return pos;
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        //inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_note, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();

    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener{

        public MyMenuItemClickListener(){}

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){
                case R.id.action_delete:
                    Toast.makeText(mContext,"Deleted", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_share:
                    Toast.makeText(mContext, "Shared", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

}
