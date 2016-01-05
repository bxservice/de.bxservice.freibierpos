package de.bxservice.bxpos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.persistence.helper.PosObjectHelper;
import de.bxservice.bxpos.ui.adapter.MainPagerAdapter;
import de.bxservice.bxpos.ui.dialog.GuestNumberDialogFragment;

/**
 * First Activity after login
 * It displays the tables in a tabbed activity with the different groups
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GuestNumberDialogFragment.GuestNumberDialogListener {

    static final int NEW_ORDER_REQUEST = 1;  // The request code
    public final static String EXTRA_NUMBER_OF_GUESTS = "de.bxservice.bxpos.GUESTS";
    public final static String EXTRA_ASSIGNED_TABLE   = "de.bxservice.bxpos.TABLE";

    private MainPagerAdapter mMainPagerAdapter;
    private ViewPager mViewPager;

    private int numberOfGuests = 0;
    private Table selectedTable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mMainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager(), getBaseContext());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.mainTabViewPager);
        mViewPager.setAdapter(mMainPagerAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.mainTabs);
        tabs.setViewPager(mViewPager);

        FloatingActionButton newOrderButton = (FloatingActionButton) findViewById(R.id.new_order_button);
        newOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                           /* Toast.makeText(getBaseContext(), Integer.toString(getNumberOfGuests())+" "+getSelectedTable(),
                        Toast.LENGTH_SHORT).show();*/
                setNumberOfGuests(0);
                setSelectedTable(null);
                createOrder();
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

    public Table getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(Table selectedTable) {
        this.selectedTable = selectedTable;
    }

    // The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    @Override
    public void onDialogPositiveClick(GuestNumberDialogFragment dialog) {

        // User touched the dialog's positive button
        int guests = dialog.getNumberOfGuests();
        setNumberOfGuests(guests);
        createOrder();

    }

    public void createOrder(){

        Intent intent = new Intent(this, CreateOrderActivity.class);
        intent.putExtra(EXTRA_NUMBER_OF_GUESTS, getNumberOfGuests());
        intent.putExtra(EXTRA_ASSIGNED_TABLE, getSelectedTable());

        startActivityForResult(intent, NEW_ORDER_REQUEST);
    }

    /**
     * On destroy closes the db connection
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        PosObjectHelper.closeDB(getBaseContext());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_ORDER_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                this.recreate();
            }
        }
    }

}
