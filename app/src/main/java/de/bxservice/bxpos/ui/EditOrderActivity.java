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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import java.math.BigDecimal;
import java.text.NumberFormat;

import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.R;
import de.bxservice.bxpos.ui.adapter.EditPagerAdapter;
import de.bxservice.bxpos.ui.dialog.GuestNumberDialogFragment;
import de.bxservice.bxpos.ui.dialog.RemarkDialogFragment;

public class EditOrderActivity extends AppCompatActivity implements GuestNumberDialogFragment.GuestNumberDialogListener,
        RemarkDialogFragment.RemarkDialogListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private EditPagerAdapter mEditPagerAdapter;

    private View mainView;
    private POSOrder order;
    private String   caller; //Indicates the activity that called it

    private FloatingActionButton sendButton;
    private FloatingActionButton payButton;

    private TextView qtyTextView;
    private TextView totalTextView;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private boolean addNewItemsOnBack = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_order_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getExtras();

        mainView = findViewById(R.id.main_layout);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mEditPagerAdapter = new EditPagerAdapter(getSupportFragmentManager(), getBaseContext(), order);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.editTabViewPager);
        mViewPager.setAdapter(mEditPagerAdapter);

        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.openOrderTabs);
        tabs.setViewPager(mViewPager);

        sendButton = (FloatingActionButton) findViewById(R.id.fabSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (order.sendOrder(getApplicationContext()))
                    Snackbar.make(mainView, "Order created", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                else
                    Snackbar.make(mainView, "Error", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
        });

        payButton = (FloatingActionButton) findViewById(R.id.fabPay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, order.getStatus(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        payButton.hide();

        //Listen to tab swipes
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                animateFab(position);
                updateSummary(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        mViewPager.addOnPageChangeListener(onPageChangeListener);

        qtyTextView   = (TextView) findViewById(R.id.qty_textView);
        totalTextView = (TextView) findViewById(R.id.total_textView);

        updateSummary(0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_order, menu);
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
        if (id == R.id.add_item) {
            if( caller.equals("CreateOrderActivity") ) {
                addNewItemsOnBack = true;
                onBackPressed();
                return true;
            }else {
                //TODO: add the logic to call the interface when it is call from somewhere else
                return true;
            }
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
        remarktDialog.setNote(order.getOrderRemark());
        remarktDialog.show(getFragmentManager(), "RemarkDialogFragment");
    }

    private void showGuestNumberDialog() {
        // Create an instance of the dialog fragment and show it
        GuestNumberDialogFragment guestDialog = new GuestNumberDialogFragment();
        guestDialog.setNumberOfGuests(order.getGuestNumber());
        guestDialog.show(getFragmentManager(), "NumberOfGuestDialogFragment");
    }

    /**
     * Click set on guest number dialog
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(GuestNumberDialogFragment dialog) {
        // User touched the dialog's positive button
        int guests = dialog.getNumberOfGuests();
        order.setGuestNumber(guests);
    }

    /**
     * Click add on add remark dialog
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(RemarkDialogFragment dialog) {
        // User touched the dialog's positive button
        String note = dialog.getNote();
        order.setOrderRemark(note);
    }

    /**
     * Get extras from the previous activity
     * - Order
     */
    public void getExtras() {

        Intent intent = getIntent();

        if(intent != null) {

            order = (POSOrder)intent.getSerializableExtra("draftOrder");
            caller = intent.getStringExtra("caller");

        }
    }

    public void onBackPressed() {

        if(order != null &&
                !order.getStatus().equals(POSOrder.DRAFT_STATUS)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.discard_draft_order)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            EditOrderActivity.super.onBackPressed();
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
     * Fab options to hide and show on each tab
     * @param position
     */
    private void animateFab(int position) {

        switch (position) {

            case EditPagerAdapter.ORDERING_POSITION:
                sendButton.show();
                payButton.hide();
                break;

            case EditPagerAdapter.ORDERED_POSITION:
                payButton.show();
                sendButton.hide();
                break;

            default:
                sendButton.show();
                payButton.hide();
                break;
        }
    }

    /**
     * Displays the qty and the total in each tab
     * @param position
     */
    private void updateSummary(int position) {

        switch (position) {

            case EditPagerAdapter.ORDERING_POSITION:
                qtyTextView.setText(getTotalQuantity(POSOrderLine.ORDERING));
                totalTextView.setText(getTotalAmount(POSOrderLine.ORDERING));
                break;

            case EditPagerAdapter.ORDERED_POSITION:
                qtyTextView.setText(getTotalQuantity(POSOrderLine.ORDERED));
                totalTextView.setText(getTotalAmount(POSOrderLine.ORDERED));
                break;

            default:
                qtyTextView.setText(getTotalQuantity(POSOrderLine.ORDERING));
                totalTextView.setText(getTotalAmount(POSOrderLine.ORDERING));
                break;
        }

    }

    /**
     * Returns total amount of items depending
     * on the tab focus
     * @param status
     * @return
     */
    public String getTotalQuantity(String status) {

        if (order == null)
            return "";

        StringBuilder totalQtyString = new StringBuilder( getString(R.string.quantity) + ": " );
        int totalQty = 0;

        for(POSOrderLine orderLine : order.getOrderLines()) {

            if (status.equals(orderLine.getLineStatus())) {
                totalQty = totalQty + orderLine.getQtyOrdered();
            }
        }

        totalQtyString.append(totalQty);

        return totalQtyString.toString();
    }

    /**
     * Returns total amount to pay depending
     * on the tab focus
     * @param status
     * @return
     */
    public String getTotalAmount(String status) {

        if (order == null)
            return "";

        StringBuilder totalString = new StringBuilder();
        BigDecimal total = BigDecimal.ZERO;

        if (status. equals(POSOrderLine.ORDERING))
            totalString.append(getString(R.string.subtotal));
        else if (status.equals(POSOrderLine.ORDERED))
            totalString.append(getString(R.string.total));

        totalString.append(": ");

        for(POSOrderLine orderLine : order.getOrderLines()) {

            if (status.equals( orderLine.getLineStatus())) {
                total = total.add(orderLine.getLineNetAmt());
            }
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataMediator.LOCALE);
        totalString.append(currencyFormat.format(total));

        return totalString.toString();

    }

    /**
     * Removes an item when swipe in the list view
     * updates the qty and subtotal
     * @param position
     */
    public void removeItem(int  position) {
        order.removeItem(position);
        updateSummary(0);
    }

    /**
     * Undo the delete
     * @param position
     * @param orderLine
     */
    public void addItem(int  position, POSOrderLine orderLine) {
        order.addItem(position, orderLine);
        updateSummary(0);
    }

    @Override
    public void finish() {
        //When new items want to be added - persist the changes in guests and notes
        if (caller.equals("CreateOrderActivity") && addNewItemsOnBack) {

            Intent data = new Intent();
            data.putExtra("remark",order.getOrderRemark());
            data.putExtra("guests", order.getGuestNumber());
            data.putExtra("orderLines", order.getOrderLines());
            setResult(RESULT_OK, data);

        }
        super.finish();
    }


}
