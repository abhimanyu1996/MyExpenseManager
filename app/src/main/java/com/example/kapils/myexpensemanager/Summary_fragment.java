package com.example.kapils.myexpensemanager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Summary_fragment extends Fragment {


    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.summaryviewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.summarytabs);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        Calendar c = Calendar.getInstance();
        Date datenow=c.getTime();

        adapter.addFragment(new Summary_Allinone_Fragment("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2))='"+sdformat.format(datenow)+"'"), "Today");

        //week fragment
        c.add(Calendar.DATE, -7);
        Date dnew = c.getTime();
        adapter.addFragment(new Summary_Allinone_Fragment("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2)) between '"+sdformat.format(dnew)+"' and '"+sdformat.format(datenow)+"'"), "Week");

        //month
        c.add(Calendar.DATE, -30);
        dnew = c.getTime();
        adapter.addFragment(new Summary_Allinone_Fragment("where (substr(tdate,7,4)||'-'||substr(tdate,4,2)||'-'||substr(tdate,1,2)) between '"+sdformat.format(dnew)+"' and '"+sdformat.format(datenow)+"'"), "Month");

        //custom fragment
        adapter.addFragment(new Summary_Custom_Fragment(), "Custom");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Summary");
    }
}
