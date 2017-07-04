package com.example.kapils.myexpensemanager;

/**
 * Created by Priyanjul on 23-06-2017.
 */

public class MyNote {

    private String title;
    private String note;
    private String dt;

    public MyNote(){}

    public MyNote(String title, String note, String dt){
        this.title = title;
        this.note = note;
        this.dt = dt;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public String getDt() {
        return dt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
}
