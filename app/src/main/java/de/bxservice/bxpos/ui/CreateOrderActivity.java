package de.bxservice.bxpos.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.model.pos.NewOrderGridItem;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.ui.adapter.CreateOrderPagerAdapter;
import de.bxservice.bxpos.ui.adapter.SearchItemAdapter;
import de.bxservice.bxpos.ui.decorator.DividerItemDecoration;
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
    static final int PICK_CONFIRMATION_REQUEST = 1;  // The request code


    private PagerSlidingTabStrip tabs;
    private View mTabFormView;

    //RecyclerView attributes for search functionality
    private RecyclerView recyclerView;
    private SearchItemAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<NewOrderGridItem> items = new ArrayList<>();
    private HashMap<NewOrderGridItem, MProduct> itemProductHashMap;
    private SearchView mSearchView;

    //order attributes
    private POSOrder posOrder = null;
    private int numberOfGuests = 0;
    private Table selectedTable = null;
    private String remarkNote = "";
    private FloatingActionButton sendActionButton;

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
        mCreateOrderPagerAdapter = new CreateOrderPagerAdapter(getSupportFragmentManager(), getBaseContext());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.createOrderContainer);
        mViewPager.setAdapter(mCreateOrderPagerAdapter);


        recyclerView = (RecyclerView) findViewById(R.id.search_item_view);

        initSearchListItems();

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SearchItemAdapter(items);
        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = recyclerView.getChildAdapterPosition(v);

                NewOrderGridItem selectedItem = mAdapter.getSelectedItem(position);
                addOrderItem(itemProductHashMap.get(selectedItem));

                mCreateOrderPagerAdapter.updateQty(getSupportFragmentManager(), selectedItem, getProductQtyOrdered(itemProductHashMap.get(selectedItem)));
                onBackPressed();

            }
        });

        recyclerView.setAdapter(mAdapter);
        recyclerView.setVisibility(View.GONE);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));


        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(mViewPager);

        String tableName = "";

        if (getSelectedTable() != null)
            tableName = getSelectedTable().getTableName();

        Toast.makeText(getBaseContext(), Integer.toString(getNumberOfGuests()) + " " + tableName,
                Toast.LENGTH_SHORT).show();


        sendActionButton = (FloatingActionButton) findViewById(R.id.fab);
        sendActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (posOrder == null)
                    Snackbar.make(view, R.string.empty_order, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                else
                    openConfirmationActivity();
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

        NewOrderGridItem gridItem;
        itemProductHashMap = new HashMap<>();

        for(MProduct product : DataMediator.getInstance().getProductList()) {
            gridItem = new NewOrderGridItem();
            productPrice = DataMediator.getInstance().getProductPriceHashMap().get(product.getProductID());

            gridItem.setName(product.getProductName());
            gridItem.setPrice(currencyFormat.format(productPrice.getStdPrice()));

            items.add(gridItem);
            itemProductHashMap.put(gridItem, product);
        }
    }

    /**
     * Method that allows to capture the text that is being
     * introduced in the search field
     * @param newText
     * @return
     */
    public boolean onQueryTextChange(String newText) {
        SearchItemAdapter adapter = (SearchItemAdapter) recyclerView.getAdapter();

        if (TextUtils.isEmpty(newText)) {
            adapter.getFilter().filter("");
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

            recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
            recyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });

            if (sendActionButton.getVisibility() == View.VISIBLE)
                sendActionButton.hide();
            else
                sendActionButton.show();

        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
            mTabFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            tabs.setVisibility(show ? View.GONE : View.VISIBLE);
            if (sendActionButton.getVisibility() == View.VISIBLE)
                sendActionButton.hide();
            else
                sendActionButton.show();
        }
    }//showSearchList

    /**
     * Get extras from the previous activity
     * - Table number
     * - Number of guests
     */
    public void getExtras() {
        Bundle extras = getIntent().getExtras();

        if(extras != null) {

            if(getIntent().getSerializableExtra(MainActivity.EXTRA_ASSIGNED_TABLE) != null)
                setSelectedTable((Table) getIntent().getSerializableExtra(MainActivity.EXTRA_ASSIGNED_TABLE));

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
        remarktDialog.setNote(getRemarkNote());
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

    public Table getSelectedTable() {
        return selectedTable;
    }

    public void setSelectedTable(Table selectedTable) {
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

        if(posOrder == null) {
            posOrder = new POSOrder();
            posOrder.setGuestNumber(getNumberOfGuests());
            posOrder.setOrderRemark(getRemarkNote());
            posOrder.setTable(selectedTable);
            posOrder.setStatus(POSOrder.DRAFT_STATUS);
        }

        posOrder.addItem(product);
    }

    public void updateDraftOrder() {

        if(posOrder != null) {

            if(!remarkNote.equals(posOrder.getOrderRemark())) {
                posOrder.setOrderRemark(remarkNote);
            }
            if(numberOfGuests != posOrder.getGuestNumber()) {
                posOrder.setGuestNumber(numberOfGuests);
            }

        }

    }

    public int getProductQtyOrdered(MProduct product) {
        if(posOrder == null)
            return 0;
        return posOrder.getProductQtyOrdered(product);
    }


    @Override
    /**
     * When the back button is pressed and something
     * was ordered show a confirmation dialog
     */
    public void onBackPressed() {

        //If the searchview mode is displayed, only go back to the tab
        if (recyclerView.isShown()) {
            mSearchView.onActionViewCollapsed();
            showSearchList(false);
        }
        else if(posOrder != null) {
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

    /**
     * Opens the confirmation activity passing the draft order
     * as parameter
     */
    public void openConfirmationActivity() {
        Intent intent = new Intent(this, EditOrderActivity.class);
        intent.putExtra("draftOrder", posOrder);
        intent.putExtra("caller","CreateOrderActivity");

        startActivityForResult(intent, PICK_CONFIRMATION_REQUEST);
    }

    public void updateOrderLines(ArrayList<POSOrderLine> orderLines) {
        posOrder.recreateOrderLines(orderLines);
        mCreateOrderPagerAdapter.refreshAllQty(getSupportFragmentManager());
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONFIRMATION_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                setRemarkNote(data.getExtras().get("remark").toString());
                setNumberOfGuests((Integer) data.getExtras().get("guests"));

                ArrayList<POSOrderLine> orderLines = (ArrayList<POSOrderLine>) data.getExtras().get("orderLines");

                if (orderLines != null && !orderLines.isEmpty() && orderLines.size() != posOrder.getOrderLines().size()) {
                    updateOrderLines(orderLines);
                }

                updateDraftOrder();

            }
        }
    }

}
