package de.bxservice.bxpos.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.DraftOrder;
import de.bxservice.bxpos.logic.model.MProduct;
import de.bxservice.bxpos.ui.adapter.CreateOrderPagerAdapter;
import de.bxservice.bxpos.ui.dialog.GuestNumberDialogFragment;
import de.bxservice.bxpos.ui.dialog.RemarkDialogFragment;

public class CreateOrderActivity extends AppCompatActivity implements GuestNumberDialogFragment.GuestNumberDialogListener,
        RemarkDialogFragment.RemarkDialogListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private CreateOrderPagerAdapter mCreateOrderPagerAdapter;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
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

    }

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

        return true;
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
        if(id == R.id.items_search){
            Intent intent = new Intent(this, SearchMenuItemActivity.class);
            startActivity(intent);
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
