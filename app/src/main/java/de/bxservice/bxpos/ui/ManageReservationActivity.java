package de.bxservice.bxpos.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.ui.adapter.ReservationExpandableListAdapter;

public class ManageReservationActivity extends AppCompatActivity {

    ExpandableListView expListView;
    private List<String> listDataHeader;
    // child data in format of header title, child title
    private HashMap<String, List<String>> listDataChild;
    ReservationExpandableListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_reservation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.reservation_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);

        // preparing list data
        prepareListData();

        listAdapter = new ReservationExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        FloatingActionButton newReservationBtn = (FloatingActionButton) findViewById(R.id.new_reservation);
        newReservationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createReservation(view);
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });
    }

    /*
 * Preparing the list data
 */
    private void prepareListData() {

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding head data
        listDataHeader.add("28.10.2015");
        listDataHeader.add("29.10.2015");
        listDataHeader.add("31.10.2015");
        listDataHeader.add("04.12.2015");

        // Adding child data
        List<String> today= new ArrayList<String>();
        today.add("Table 1  - 9:30");
        today.add("Table 25 - 12:00");
        today.add("eeee");
        today.add("eeee");
        today.add("eeee");
        today.add("eeee");
        today.add("eeee");
        today.add("eeee");


        List<String> tomorrow = new ArrayList<String>();
        tomorrow.add("Table 9 - 21:00");
        tomorrow.add("Table 1 - 21:30");

        List<String> pasadomanana = new ArrayList<String>();
        pasadomanana.add("iDempiere World Conference Event - 20:00");
        pasadomanana.add("Karaoke night");

        listDataChild.put(listDataHeader.get(0), today); // Header, Child data
        listDataChild.put(listDataHeader.get(1), tomorrow);
        listDataChild.put(listDataHeader.get(2), pasadomanana);

    }

    /**
     * Calls the create reservation activity
     * @param view
     */
    public void createReservation(View view){

        FragmentManager fragmentManager = getSupportFragmentManager();
        CreateReservationDialogFragment newFragment = new CreateReservationDialogFragment();
        newFragment.show(fragmentManager, "dialog");

        /*if (mIsLargeLayout) {
            // The device is using a large layout, so show the fragment as a dialog
            newFragment.show(fragmentManager, "dialog");
        } else {*/
            // The device is smaller, so show the fragment fullscreen
            //FragmentTransaction transaction = fragmentManager.beginTransaction();

            // For a little polish, specify a transition animation
            //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

            // To make it fullscreen, use the 'content' root view as the container
            // for the fragment, which is always the root view for the activity
           // transaction.add(android.R.id.content, newFragment)
                    //.addToBackStack(null).commit();
        //}

    }

}
