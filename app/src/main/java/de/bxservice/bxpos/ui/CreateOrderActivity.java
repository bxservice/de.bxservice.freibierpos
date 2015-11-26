package de.bxservice.bxpos.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.model.DraftOrder;
import de.bxservice.bxpos.logic.model.MProduct;
import de.bxservice.bxpos.logic.model.Order;
import de.bxservice.bxpos.logic.model.ProductPrice;
import de.bxservice.bxpos.ui.adapter.CreateOrderPagerAdapter;
import de.bxservice.bxpos.ui.adapter.OrderArrayAdapter;
import de.bxservice.bxpos.ui.dialog.GuestNumberDialogFragment;
import de.bxservice.bxpos.ui.dialog.RemarkDialogFragment;

public class CreateOrderActivity extends AppCompatActivity implements GuestNumberDialogFragment.GuestNumberDialogListener,
        RemarkDialogFragment.RemarkDialogListener, SearchView.OnQueryTextListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private CreateOrderPagerAdapter mCreateOrderPagerAdapter;

    private PagerSlidingTabStrip tabs;
    private View mTabFormView;

    //ListView attributes for search functionality
    private ListView listView;
    private OrderArrayAdapter<String> mAdapter;
    private List items = new ArrayList<Order>();
    private SearchView mSearchView;

    //order attributes
    private DraftOrder draftOrder = null;
    private int numberOfGuests = 0;
    private String selectedTable = "";
    private String remarkNote = "";

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.create_order_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get Table # and # of guests
        getExtras();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mCreateOrderPagerAdapter = new CreateOrderPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.createOrderContainer);
        mViewPager.setAdapter(mCreateOrderPagerAdapter);


        listView = (ListView) findViewById(R.id.list_search_View2);

        initSearchListItems();

        mAdapter = new OrderArrayAdapter<>(this, items);

        listView.setAdapter(mAdapter);
        listView.setVisibility(View.GONE);


        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);

        Toast.makeText(getBaseContext(), Integer.toString(getNumberOfGuests()) + " " + getSelectedTable(),
                Toast.LENGTH_SHORT).show();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mTabFormView = findViewById(R.id.createOrderContainer);

    }

    /**
     * Creates the list view that will be used when click on search
     */
    public void initSearchListItems() {

        ProductPrice productPrice;
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataMediator.LOCALE);

        for( MProduct product : DataMediator.getInstance().getProductList() ){
            productPrice = DataMediator.getInstance().getProductPriceHashMap().get(product);
            items.add(new Order(product.getProductName(), currencyFormat.format(productPrice.getStdPrice())));
        }
    }

    /**
     * Method that allows to capture the text that is being
     * introduced in the search field
     * @param newText
     * @return
     */
    public boolean onQueryTextChange(String newText) {
        OrderArrayAdapter adapter = (OrderArrayAdapter)listView.getAdapter();

        if ( TextUtils.isEmpty(newText) ) {
            adapter.getFilter().filter(null);
        } else {
            showSearchList(true);
            adapter.getFilter().filter(newText);
        }
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**
     * When the search button is pressed - the listview is shown and the tabs are hidden
     * When the search button loses focuses - listview hidden and tabs are shown
     * @param show
     */
    private void showSearchList(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mTabFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mTabFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mTabFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            tabs.setVisibility(show ? View.GONE : View.VISIBLE);
            tabs.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    tabs.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            listView.setVisibility(show ? View.VISIBLE : View.GONE);
            listView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    listView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            listView.setVisibility(show ? View.VISIBLE : View.GONE);
            mTabFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            tabs.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }//showSearchList

    /**
     * Get extras from the previous activity
     * - Table number
     * - Number of guests
     */
    public void getExtras() {
        Bundle extras = getIntent().getExtras();

        if( extras != null ){

            if( extras.getString(MainActivity.EXTRA_ASSIGNED_TABLE) != null )
                setSelectedTable(extras.getString(MainActivity.EXTRA_ASSIGNED_TABLE));

             setNumberOfGuests(extras.getInt(MainActivity.EXTRA_NUMBER_OF_GUESTS));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_order_activity, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView = (SearchView) menu.findItem(R.id.items_search).getActionView();

        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        setupSearchView();

        return true;
    }

    /**
     * Add the needed congiguration to the search view
     */
    private void setupSearchView() {
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSearchList(true);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == android.R.id.home) {
            //If the searchview mode is displayed, only go back to the tab
            if ( listView.isShown() ) {
                mSearchView.onActionViewCollapsed();
                showSearchList(false);
                return true;
            }
            onBackPressed();
            return true;
        }
        if (id == R.id.set_guests) {
            showGuestNumberDialog();
            return true;
        }
        if (id == R.id.add_note) {
            showRemarkDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showRemarkDialog() {
        RemarkDialogFragment remarktDialog = new RemarkDialogFragment();
        remarktDialog.show(getFragmentManager(), "RemarkDialogFragment");
    }

    private void showGuestNumberDialog() {
        // Create an instance of the dialog fragment and show it
        GuestNumberDialogFragment guestDialog = new GuestNumberDialogFragment();
        guestDialog.setNumberOfGuests(getNumberOfGuests());
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

    public String getRemarkNote() {
        return remarkNote;
    }

    public void setRemarkNote(String remarkNote) {
        this.remarkNote = remarkNote;
    }

    /**
     * Click set on guest number dialog
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(GuestNumberDialogFragment dialog) {
        // User touched the dialog's positive button
        int guests = dialog.getNumberOfGuests();
        setNumberOfGuests(guests);
        updateDraftOrder();
    }

    /**
     * Click add on add remark dialog
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(RemarkDialogFragment dialog) {
        // User touched the dialog's positive button
        String note = dialog.getNote();
        setRemarkNote(note);
        updateDraftOrder();
    }

    public void addOrderItem(MProduct product) {

        if( draftOrder == null ){
            draftOrder = new DraftOrder();
            draftOrder.setGuestNumber(getNumberOfGuests());
            draftOrder.setOrderRemark(getRemarkNote());
        }

        draftOrder.addItem(product);

    }

    public void updateDraftOrder() {

        if( draftOrder!= null ){

            if( !remarkNote.equals(draftOrder.getOrderRemark()) ){
                draftOrder.setOrderRemark(remarkNote);
            }
            if( numberOfGuests != draftOrder.getGuestNumber() ){
                draftOrder.setGuestNumber(numberOfGuests);
            }

        }

    }

    public int getProductQtyOrdered(MProduct product) {
        if( draftOrder == null )
            return 0;
        return draftOrder.getProductQtyOrdered(product);
    }


    @Override
    /**
     * When the back button is pressed and something
     * was ordered show a confirmation dialog
     */
    public void onBackPressed() {

        if( draftOrder != null ){
            new AlertDialog.Builder(this)
                    .setTitle(R.string.discard_draft_order)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            CreateOrderActivity.super.onBackPressed();
                        }
                    }).create().show();
        }
        else
            super .onBackPressed();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            onBackPressed();
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }

}
