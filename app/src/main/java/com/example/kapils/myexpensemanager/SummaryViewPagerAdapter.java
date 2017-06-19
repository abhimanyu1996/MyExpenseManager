package com.example.kapils.myexpensemanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by hp1 on 21-01-2015.
 */
public class SummaryViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    private static SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");

    // Build a Constructor and assign the passed Values to appropriate values in the class
    public SummaryViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);

        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;

    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Calendar c = Calendar.getInstance();
        Date datenow=c.getTime();

        if(position == 0) // if the position is 0 we are returning the First tab
        {
            Summary_Allinone_Fragment tab = new Summary_Allinone_Fragment("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2))='"+sdformat.format(datenow)+"'");
            return tab;
        }
        else if(position == 1) // if the position is 0 we are returning the First tab
        {
            c.add(Calendar.DATE, -7);
            Date dnew = c.getTime();
            Summary_Allinone_Fragment tab = new Summary_Allinone_Fragment("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2)) between '"+sdformat.format(dnew)+"' and '"+sdformat.format(datenow)+"'");//
            return tab;
        }
        else if(position == 2) // if the position is 0 we are returning the First tab
        {
            c.add(Calendar.DATE, -30);
            Date dnew = c.getTime();
            Summary_Allinone_Fragment tab = new Summary_Allinone_Fragment("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2)) between '"+sdformat.format(dnew)+"' and '"+sdformat.format(datenow)+"'");//
            return tab;
        }
        else             // As we are having 2 tabs if the position is now 0 it must be 1 so we are returning second tab
        {
            Summary_Custom_Fragment tab2 = new Summary_Custom_Fragment();
            return tab2;
        }
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}