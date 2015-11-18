package de.bxservice.bxpos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.model.Table;
import de.bxservice.bxpos.logic.model.TableGroup;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GuestNumberDialogFragment.GuestNumberDialogListener {

    public final static String EXTRA_NUMBER_OF_GUESTS = "de.bxservice.bxpos.GUESTS";
    public final static String EXTRA_ASSIGNED_TABLE   = "de.bxservice.bxpos.TABLE";

    private MainPagerAdapter mMainPagerAdapter;
    private ViewPager mViewPager;

    private int numberOfGuests = 0;
    private String selectedTable = "";

    DataMediator dataProvider;

    List<String> list;
    GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.mainTabViewPager);
        mViewPager.setAdapter(mMainPagerAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.mainTabs);
        tabs.setViewPager(mViewPager);

        list=new ArrayList<String>();
        grid=(GridView) findViewById(R.id.tableView);

        dataProvider = DataMediator.getInstance();

        for ( TableGroup tg : dataProvider.getTableGroupList() ){

            Toast.makeText(getBaseContext(), tg.getName(),
                    Toast.LENGTH_SHORT).show();

            for (Table table : tg.getTables() )
                list.add(table.getTableName());

        }

        grid.setGravity(Gravity.CENTER_HORIZONTAL);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,list);
        grid.setAdapter(adp);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {


                /*Toast.makeText(getBaseContext(), list.get(arg2),
                        Toast.LENGTH_SHORT).show();*/
                /*Toast.makeText(getBaseContext(), "Hola",
                        Toast.LENGTH_SHORT).show();*/
                setSelectedTable(list.get(arg2));
                showGuestNumberDialog();
            }
        });

        FloatingActionButton newOrderButton = (FloatingActionButton) findViewById(R.id.new_order_button);
        newOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CreateOrderActivityOption2.class);
                intent.putExtra(EXTRA_NUMBER_OF_GUESTS, getNumberOfGuests());
                intent.putExtra(EXTRA_ASSIGNED_TABLE, getSelectedTable());

               /* Toast.makeText(getBaseContext(), Integer.toString(getNumberOfGuests())+" "+getSelectedTable(),
                        Toast.LENGTH_SHORT).show();*/


                startActivity(intent);
                //createOrder(view);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.open_orders) {

            Intent intent = new Intent(this, ViewOpenOrdersActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_reservation) {

            Intent intent = new Intent(this, ManageReservationActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_report) {

            Intent intent = new Intent(this, EditOrderActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showGuestNumberDialog() {
        // Create an instance of the dialog fragment and show it
        GuestNumberDialogFragment guestDialog = new GuestNumberDialogFragment();
        guestDialog.show(getFragmentManager(), "NumberOfGuestDialogFragment");
    }

    public int getNumberOfGuests() {
        return numberOfGuests;
    }

    public void setNumberOfGuests(int numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    public String getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(String selectedTable) {
        this.selectedTable = selectedTable;
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    @Override
    public void onDialogPositiveClick(GuestNumberDialogFragment dialog) {

        // User touched the dialog's positive button
        int guests = dialog.getNumberOfGuests();
        setNumberOfGuests(guests);
        createOrder(dialog.getView());

    }

    public void createOrder(View view){

        Intent intent = new Intent(this, CreateOrderActivity.class);
        intent.putExtra(EXTRA_NUMBER_OF_GUESTS, getNumberOfGuests());
        intent.putExtra(EXTRA_ASSIGNED_TABLE, getSelectedTable());

        Toast.makeText(getBaseContext(), Integer.toString(getNumberOfGuests())+" "+getSelectedTable(),
                Toast.LENGTH_SHORT).show();

        startActivity(intent);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class MainPagerAdapter extends FragmentPagerAdapter {

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a FoodMenuFragment (defined as a static inner class below).
            return MainTableFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 2 total pages. Ordered - Ordering.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ORDERING";
                case 1:
                    return "ORDERED";
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MainTableFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        ListView listView;
        OrderArrayAdapter<String> mAdapter;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static MainTableFragment newInstance(int sectionNumber) {
            MainTableFragment fragment = new MainTableFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public MainTableFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_edit_order, container, false);

            /*TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText("Caesar Salad  €10");

            TextView textView1 = (TextView) rootView.findViewById(R.id.section_label1);
            textView1.setText("Africola  €3");

            TextView textView2 = (TextView) rootView.findViewById(R.id.section_label2);
            textView2.setText("Desert €2");*/
            //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));


            /*listView = (ListView) rootView.findViewById(R.id.lista);
            mAdapter = new OrderArrayAdapter<>(this.getContext(), OrderDataExample.ORDERS);*/



            //listView.setAdapter(mAdapter);


            return rootView;
        }
    }

}
