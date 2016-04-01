package de.bxservice.bxpos.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.tasks.CreateOrderTask;
import de.bxservice.bxpos.persistence.helper.PosObjectHelper;
import de.bxservice.bxpos.ui.adapter.MainPagerAdapter;
import de.bxservice.bxpos.ui.dialog.GuestNumberDialogFragment;
import de.bxservice.bxpos.ui.dialog.MultipleOrdersTableDialogFragment;

/**
 * First Activity after login
 * It displays the tables in a tabbed activity with the different groups
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GuestNumberDialogFragment.GuestNumberDialogListener,
        MultipleOrdersTableDialogFragment.MultipleOrdersTableDialogListener {

    private static final String LOG_TAG = "Main Activity";

    static final int NEW_ORDER_REQUEST  = 1;  // The request code
    static final int EDIT_ORDER_REQUEST = 2;  // The request code
    static final int OPEN_ORDER_REQUEST = 3;  // The request code

    public final static String EXTRA_NUMBER_OF_GUESTS = "de.bxservice.bxpos.GUESTS";
    public final static String EXTRA_ASSIGNED_TABLE   = "de.bxservice.bxpos.TABLE";

    private MainPagerAdapter mMainPagerAdapter;
    private ViewPager mViewPager;

    private int numberOfGuests = 0;
    private Table selectedTable = null;

    private SharedPreferences sharedPref;
    private String syncConnPref;

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
                numberOfGuests = 0;
                selectedTable = null;
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

        sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        syncConnPref = sharedPref.getString(OfflineAdminSettingsActivity.KEY_PREF_SYNC_CONN, "");
        callAsynchronousTask();
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
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.open_orders) {

            Intent intent = new Intent(this, ViewOpenOrdersActivity.class);
            startActivityForResult(intent, OPEN_ORDER_REQUEST);

        } else if (id == R.id.nav_report) {
            Intent intent = new Intent(this, ReportsActivity.class);
            startActivity(intent);
        } /*else if (id == R.id.nav_reservation) {

            Intent intent = new Intent(this, ManageReservationActivity.class);
            startActivity(intent);

        }  else if (id == R.id.nav_settings) {

        }*/ else if (id == R.id.nav_send) {
            DataProvider dataProvider = new DataProvider(getBaseContext());

            final List<POSOrder> unsynchronizedOrders = dataProvider.getUnsynchronizedOrders();

            if (unsynchronizedOrders != null && unsynchronizedOrders.size() != 0) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.synchronize_orders_title)
                        .setMessage(getString(R.string.synchronize_orders_message, unsynchronizedOrders.size()))
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.synchronize, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                synchronizePendingOrders(unsynchronizedOrders, false);
                            }
                        }).create().show();
            }
            else
                Toast.makeText(getBaseContext(), getString(R.string.no_unsync_orders),
                        Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * If there are orders to be synchronized. Checks if there is internet connection
     * and synchronizes them
     * @param unsynchronizedOrders
     */
    private void synchronizePendingOrders(List<POSOrder> unsynchronizedOrders, boolean automatic) {
        //Check if network connection is available
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //When no internet connection
        if (networkInfo != null && networkInfo.isConnected()) {
            CreateOrderTask createOrderTask = new CreateOrderTask(this);
            //Convert from List to POSOrder[] to send as a parameter to the async task
            POSOrder[] orderArray = unsynchronizedOrders.toArray(new POSOrder[unsynchronizedOrders.size()]);
            createOrderTask.execute(orderArray);
        }
        else if (!automatic)
            Toast.makeText(getBaseContext(), getString(R.string.error_no_connection_on_sync_order),
                    Toast.LENGTH_SHORT).show();
    }

    public void showGuestNumberDialog() {
        // Create an instance of the dialog fragment and show it
        GuestNumberDialogFragment guestDialog = new GuestNumberDialogFragment();
        guestDialog.show(getFragmentManager(), "NumberOfGuestDialogFragment");
    }

    public void showSelectOrderDialog(List<POSOrder> orders) {
        MultipleOrdersTableDialogFragment ordersDialog = new MultipleOrdersTableDialogFragment();
        ordersDialog.setTableOrders(orders);
        ordersDialog.show(getFragmentManager(), "MultipleOrdersTableDialogFragment");
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
        numberOfGuests = dialog.getNumberOfGuests();
        createOrder();
    }

    @Override
    public void onDialogPositiveClick(MultipleOrdersTableDialogFragment dialog) {
        if(dialog.getOrder() != null)
            editOrder(dialog.getOrder());
    }

    public void createOrder(){

        Intent intent = new Intent(this, CreateOrderActivity.class);
        intent.putExtra("caller","MainActivity");
        intent.putExtra(EXTRA_NUMBER_OF_GUESTS, numberOfGuests);
        intent.putExtra(EXTRA_ASSIGNED_TABLE, selectedTable);

        startActivityForResult(intent, NEW_ORDER_REQUEST);
    }

    public void editOrder(List<POSOrder> orders){

        //No orders in the array -> free table - new order
        if (orders.isEmpty())
            createOrder();
        //Only one order in the table -> Open edit activity
        else if (orders.size() == 1) {
            editOrder(orders.get(0));
        } else {
            showSelectOrderDialog(orders);
        }
    }

    public void editOrder(POSOrder order) {
        Intent intent = new Intent(this, EditOrderActivity.class);
        intent.putExtra("caller","MainActivity");
        intent.putExtra("draftOrder", order);
        startActivityForResult(intent, EDIT_ORDER_REQUEST);
    }


    /**
     * On destroy closes the db connection
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        PosObjectHelper.closeDB(getBaseContext());
    }

    /**
     * Close the drawer when coming back from
     * other activities
     */
    @Override
    public void onResume(){
        super.onResume();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == NEW_ORDER_REQUEST ||
                requestCode == EDIT_ORDER_REQUEST ||
                requestCode == OPEN_ORDER_REQUEST) {
            // Make sure the request was successful
            this.recreate();
        }
    }

    /**
     * gets call after the create order task finishes
     * @param success
     */
    public void postExecuteTask(boolean success) {
        if(success)
            Toast.makeText(getBaseContext(), getString(R.string.success_on_sync_order),
                    Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getBaseContext(), getString(R.string.no_success_on_sync_order),
                    Toast.LENGTH_LONG).show();
    }

    /**
     * Calls the sync order task every x amount of time
     * as chosen in the settings
     */
    public void callAsynchronousTask() {

        //If the settings are set as always or never sync do not create the timer
        if("-1".equals(syncConnPref) || "0".equals(syncConnPref))
            return;

        int syncTime = Integer.parseInt(syncConnPref);

        final Handler handler = new Handler();
        Timer timer = new Timer();

        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            DataProvider dataProvider = new DataProvider(getBaseContext());
                            final List<POSOrder> unsynchronizedOrders = dataProvider.getUnsynchronizedOrders();

                            if (unsynchronizedOrders != null && unsynchronizedOrders.size() != 0) {
                                synchronizePendingOrders(unsynchronizedOrders, true);
                            }

                        } catch (Exception e) {
                            Log.e(LOG_TAG, e.toString());
                        }
                    }
                });
            }
        };

        timer.schedule(doAsynchronousTask, syncTime * 60000, syncTime * 60000 /*one minute in ms*/);
    }

}
