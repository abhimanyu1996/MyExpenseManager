package com.example.kapils.myexpensemanager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


/**
 * Created by Priyanjul on 22-06-2017.
 */

public class DisplayNote extends AppCompatActivity {

    EditText note;
    AutoCompleteTextView title;
    String ftitle, fnote, position;
    int requestcode;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_notepad);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        note = (EditText)findViewById(R.id.et_note);
        title = (AutoCompleteTextView)findViewById(R.id.et_title);
        relativeLayout = (RelativeLayout)findViewById(R.id.relativelayout);

        ftitle = getIntent().getStringExtra("TITLE");
        fnote = getIntent().getStringExtra("NOTE");
        if(ftitle.equals("init") && fnote.equals("init")) {
            requestcode =1;
        }
        else {
            position = getIntent().getStringExtra("POSITION");
            title.setText(ftitle);
            note.setText(fnote);
            requestcode = 2;
        }

        title.setTextSize(title.getTextSize()+1);
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_menu, menu);
       /* Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int Value = extras.getInt("id");
            getMenuInflater().inflate(R.menu.display_menu, menu);
        }*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        final Intent i = new Intent();
        switch (item.getItemId()){
            case android.R.id.home:
                if(title.getText().toString().equals("")) {
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Please provide a title to your note", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else {
                    Intent it = new Intent();
                    it.putExtra("IsDELETED", "n");
                    it.putExtra("NOTE", note.getText().toString());
                    it.putExtra("TITLE", title.getText().toString());
                    if (requestcode == 1) {
                        setResult(requestcode, it);
                    } else {
                        it.putExtra("POSITION", position);
                        setResult(requestcode, it);
                    }
                    //setResult(1,i);
                    //Toast.makeText(DisplayNote.this, "Save", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return true;

            case R.id.Delete:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayNote.this);

                // Setting Dialog Title
                alertDialog.setTitle("  Delete Note");

                // Setting Dialog Message
                alertDialog.setMessage("Are you sure you want delete this?");

                // Setting Icon to Dialog
                alertDialog.setIcon(R.drawable.ic_delete_note);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke YES event
                        i.putExtra("IsDELETED","y");
                        if(requestcode == 1){
                            Toast.makeText(getApplicationContext(), "Note not created..", Toast.LENGTH_SHORT).show();
                            setResult(requestcode,i);
                        }
                        else {
                            i.putExtra("POSITION",position);
                            setResult(requestcode, i);
                        }
                        finish();
                    }
                });

                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke NO event
                        //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
                // Showing Alert Message
                alertDialog.show();
                return true;

            case R.id.Save:
                String inp_title = title.getText().toString();
                if(inp_title.equals("")) {
                    Snackbar snackbar = Snackbar.make(relativeLayout, "Please provide a title to your note", Snackbar.LENGTH_SHORT);
                    snackbar.show();
                }
                else {
                    i.putExtra("IsDELETED", "n");
                    i.putExtra("NOTE", note.getText().toString());
                    i.putExtra("TITLE", inp_title);
                    if (requestcode == 1) {
                        setResult(requestcode, i);
                    } else {
                        i.putExtra("POSITION", position);
                        setResult(requestcode, i);
                    }
                    //setResult(1,i);
                    //Toast.makeText(DisplayNote.this, "Save", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return true;

            case R.id.Share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, title.getText().toString().trim());
                sharingIntent.putExtra(Intent.EXTRA_TEXT, note.getText().toString().trim());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onBackPressed() {
        if(title.getText().toString().equals("")) {
            Snackbar snackbar = Snackbar.make(relativeLayout, "Please provide a title to your note", Snackbar.LENGTH_SHORT);
            snackbar.show();
        }
        else {
            Intent i = new Intent();
            i.putExtra("IsDELETED", "n");
            i.putExtra("NOTE", note.getText().toString());
            i.putExtra("TITLE", title.getText().toString());
            if (requestcode == 1) {
                setResult(requestcode, i);
            } else {
                i.putExtra("POSITION", position);
                setResult(requestcode, i);
            }
            //setResult(1,i);
            //Toast.makeText(DisplayNote.this, "Save", Toast.LENGTH_SHORT).show();
            finish();
        }
        return;
    }
}