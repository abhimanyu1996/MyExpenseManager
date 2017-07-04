package com.example.kapils.myexpensemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Priyanjul on 16-06-2017.
 */

public class PopUpdate extends AppCompatActivity {

    EditText input;
    FloatingActionButton btn;
    String filler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pop_update);

        input = (EditText)findViewById(R.id.et_input);
        btn = (FloatingActionButton)findViewById(R.id.btn_update);
        filler = getIntent().getExtras().getString("FILLER");
        Log.d("value of filler",""+filler);
        input.setText(filler);
        input.setSelectAllOnFocus(true);


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width*0.8),(int)(height*0.4));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = input.getText().toString().trim();
                Intent i = new Intent();
                i.putExtra("INPUT", msg);
                setResult(2,i);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        String msg = input.getText().toString().trim();
        Intent i = new Intent();
        i.putExtra("INPUT", msg);
        setResult(2,i);
        finish();
    }
}
