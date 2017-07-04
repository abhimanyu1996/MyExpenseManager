package com.example.kapils.myexpensemanager;

/**
 * Created by Priyanjul on 10-06-2017.
 */

public class Task {
    String Desc;
    String Dnt;
    Boolean selected;
    Task(String Desc, String Dnt, Boolean selected)
    {
        this.Desc = Desc;
        this.Dnt = Dnt;
        this.selected = selected;
    }
    Task()
    {
        Desc = "";
        Dnt = "";
    }

}
