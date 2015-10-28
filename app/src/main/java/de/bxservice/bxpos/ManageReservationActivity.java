package de.bxservice.bxpos;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.expandableListView);

        // preparing list data
        prepareListData();

        listAdapter = new ReservationExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.new_reservation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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
        today.add("Civil");
        today.add("Electronics and Communication");
        today.add("Electrical and Electronics");
        today.add("Information science");
        today.add("Industrial Production");
        today.add("Mechanical");
        today.add("Basic Sciences");

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

}
