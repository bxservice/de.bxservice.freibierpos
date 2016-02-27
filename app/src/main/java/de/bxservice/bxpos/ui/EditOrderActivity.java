package de.bxservice.bxpos.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.DataWritter;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;
import de.bxservice.bxpos.ui.adapter.EditPagerAdapter;
import de.bxservice.bxpos.ui.dialog.GuestNumberDialogFragment;
import de.bxservice.bxpos.ui.dialog.KitchenNoteDialogFragment;
import de.bxservice.bxpos.ui.dialog.RemarkDialogFragment;
import de.bxservice.bxpos.ui.fragment.OrderingItemsFragment;

public class EditOrderActivity extends AppCompatActivity implements GuestNumberDialogFragment.GuestNumberDialogListener,
        RemarkDialogFragment.RemarkDialogListener, KitchenNoteDialogFragment.KitchenDialogListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private EditPagerAdapter mEditPagerAdapter;

    public final static String EXTRA_ORDER   = "de.bxservice.bxpos.ORDER";
    static final int ADD_ITEMS_REQUEST  = 1;  // The request code
    static final int PAY_REQUEST = 2;  // The request code

    private View mainView;
    private POSOrder order;
    private String   caller; //Indicates the activity that called it
    private static HashMap<FloatingActionButton, Boolean> fabVisible = new HashMap<>();

    private FloatingActionButton sendButton;
    private FloatingActionButton payButton;

    private TextView qtyTextView;
    private TextView totalTextView;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private boolean addNewItemsOnBack = false;
    private boolean orderSent = false;

    private ActionMode mActionMode;

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
                if (order.sendOrder(getApplicationContext())) {
                    orderSent = true;
                    finish();
                } else
                    Snackbar.make(mainView, "Error", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
        });

        fabVisible.put(sendButton, true);

        payButton = (FloatingActionButton) findViewById(R.id.fabPay);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPaymentActivity();
            }
        });
        payButton.hide();
        fabVisible.put(payButton, false);


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

        updateSummary(EditPagerAdapter.ORDERING_POSITION);

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
            if(caller.equals("CreateOrderActivity")) {
                addNewItemsOnBack = true;
                onBackPressed();
                return true;
            }else if (caller.equals("MainActivity") ||
                    caller.equals("ViewOpenOrdersActivity")) {
                openAddItemsActivity();
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
        //TODO: Add options to split the check and to Join the orders from different tables

        return super.onOptionsItemSelected(item);
    }

    private void showRemarkDialog() {
        RemarkDialogFragment remarkDialog = new RemarkDialogFragment();
        remarkDialog.setNote(order.getOrderRemark());
        remarkDialog.show(getFragmentManager(), "RemarkDialogFragment");
    }

    private void showGuestNumberDialog() {
        // Create an instance of the dialog fragment and show it
        GuestNumberDialogFragment guestDialog = new GuestNumberDialogFragment();
        guestDialog.setNumberOfGuests(order.getGuestNumber());
        guestDialog.show(getFragmentManager(), "NumberOfGuestDialogFragment");
    }

    private void showKithenNoteDialog() {
        KitchenNoteDialogFragment kitchenNoteDialog = new KitchenNoteDialogFragment();
        List<Integer> selectedItemPositions = getSelectedItems();
        if(selectedItemPositions != null && selectedItemPositions.size() == 1) {
            POSOrderLine orderLine = order.getOrderLines().get(selectedItemPositions.get(0));
            kitchenNoteDialog.setOrderLine(orderLine);
            kitchenNoteDialog.setNote(orderLine.getProductRemark());
            kitchenNoteDialog.show(getFragmentManager(), "KitchenDialogFragment");
        }
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
        order.updateOrder(getBaseContext());
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
        order.updateOrder(getBaseContext());
    }

    /**
     * Click add on add kitchen note
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(KitchenNoteDialogFragment dialog) {
        // User touched the dialog's positive button
        String note = dialog.getNote();
        dialog.getOrderLine().setProductRemark(note);
        dialog.getOrderLine().updateLine(getBaseContext());
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
                !order.getStatus().equals(POSOrder.DRAFT_STATUS) &&
                caller.equals("CreateOrderActivity")) {
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
                fabVisible.put(sendButton, true);
                fabVisible.put(payButton, false);
                sendButton.show();
                payButton.hide();
                break;

            case EditPagerAdapter.ORDERED_POSITION:
                fabVisible.put(sendButton, false);
                fabVisible.put(payButton, true);
                payButton.show();
                sendButton.hide();
                break;

            default:
                sendButton.show();
                payButton.hide();
                break;
        }
    }

    public static Boolean isFabVisible(FloatingActionButton child) {
        return fabVisible.get(child);
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

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);
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
        updateSummary(EditPagerAdapter.ORDERING_POSITION);
    }

    /**
     * Undo the delete
     * @param position
     * @param orderLine
     */
    public void addItem(int  position, POSOrderLine orderLine) {
        order.addItem(position, orderLine);
        updateSummary(EditPagerAdapter.ORDERING_POSITION);
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
        else if (orderSent)
            setResult(2); //Everything ok - the order was created and the create Activity should be closed

        super.finish();
    }

    public void openAddItemsActivity() {
        Intent intent = new Intent(this, CreateOrderActivity.class);
        intent.putExtra("caller","EditOrderActivity");
        intent.putExtra(EXTRA_ORDER, order);

        startActivityForResult(intent, ADD_ITEMS_REQUEST);
    }

    /**
     * When it comes back from the create order Activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == ADD_ITEMS_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                ArrayList<POSOrderLine> orderLines = (ArrayList<POSOrderLine>) data.getExtras().get("orderLines");

                if (orderLines != null && !orderLines.isEmpty() && orderLines.size() != order.getOrderLines().size()) {
                    addnewOrderLines(orderLines);
                }
                this.recreate();

            }
        } else if(requestCode == PAY_REQUEST ) {
            if (resultCode == 2) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public void addnewOrderLines(ArrayList<POSOrderLine> orderLines) {
        for(POSOrderLine line : orderLines)
            order.getOrderLines().add(line);
    }

    /**
     * When Long click - shows the CAB and perform the corresponding action
     * @param idx
     */
    public void onLongPressed(int idx){
        if(mActionMode != null) {
            myToggleSelection(idx);
            return;
        }
        mActionMode = EditOrderActivity.this.startSupportActionMode(new ActionBarCallBack());
        myToggleSelection(idx);
    }

    /**
     * Only works when long click was performed previosuly
     * @param idx
     */
    public void onClickPressed(int idx){
        if(mActionMode != null) {
            myToggleSelection(idx);
            return;
        }
    }

    private void myToggleSelection(int idx) {
        OrderingItemsFragment itemsFragment = (OrderingItemsFragment) getFragment(mEditPagerAdapter.ORDERING_POSITION);

        if(itemsFragment != null) {
            itemsFragment.getmAdapter().toggleSelection(idx);
            String title = getString(R.string.selected_count, itemsFragment.getmAdapter().getSelectedItemCount());
            mActionMode.setTitle(title);
        }
    }

    private void clearSelections() {
        OrderingItemsFragment itemsFragment = (OrderingItemsFragment) getFragment(mEditPagerAdapter.ORDERING_POSITION);
        if(itemsFragment != null)
            itemsFragment.getmAdapter().clearSelections();
    }

    private void deleteSelectedItems() {
        OrderingItemsFragment itemsFragment = (OrderingItemsFragment) getFragment(mEditPagerAdapter.ORDERING_POSITION);

        if(itemsFragment != null) {
            itemsFragment.getmAdapter().removeSelectedItems();
        }
    }

    private void copySelectedItems() {
        OrderingItemsFragment itemsFragment = (OrderingItemsFragment) getFragment(mEditPagerAdapter.ORDERING_POSITION);

        if(itemsFragment != null) {
            itemsFragment.getmAdapter().copySelectedItems();
        }
    }

    private List<Integer> getSelectedItems() {
        OrderingItemsFragment itemsFragment = (OrderingItemsFragment) getFragment(mEditPagerAdapter.ORDERING_POSITION);

        if(itemsFragment != null) {
            return itemsFragment.getmAdapter().getSelectedItems();
        }

        return null;
    }

    public Fragment getFragment(int position) {
        return getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + mViewPager.getId() + ":"
                        + position);
    }

    /**
     * Class in charge of the CAB
     */
    class ActionBarCallBack implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_ordering_menu, menu);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            clearSelections();
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.ctx_item_delete:
                    deleteSelectedItems();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.ctx_item_copy:
                    copySelectedItems();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                case R.id.ctx_item_note:
                    //shareCurrentItem();
                    showKithenNoteDialog();
                    System.out.println("Clicked add note");
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

    }

    private void openPaymentActivity() {
        Intent intent = new Intent(this, PayOrderActivity.class);
        intent.putExtra("completedOrder", order);
        startActivityForResult(intent, PAY_REQUEST);
    }


}
