package de.bxservice.bxpos.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ListView;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;

import de.bxservice.bxpos.logic.model.POSOrder;
import de.bxservice.bxpos.persistence.OrderDataExample;
import de.bxservice.bxpos.R;
import de.bxservice.bxpos.ui.adapter.EditPagerAdapter;
import de.bxservice.bxpos.ui.adapter.OrderArrayAdapter;

public class EditOrderActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private EditPagerAdapter mEditPagerAdapter;

    private POSOrder order;

    private FloatingActionButton sendButton;
    private FloatingActionButton payButton;

    private TextView qtyTextView;
    private TextView totalTextView;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_order_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getExtras();

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
                Snackbar.make(view, order.getStatus(), Snackbar.LENGTH_LONG)
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
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };

        mViewPager.addOnPageChangeListener(onPageChangeListener);

        qtyTextView   = (TextView) findViewById(R.id.qty_textView);
        totalTextView = (TextView) findViewById(R.id.total_textView);

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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Get extras from the previous activity
     * - Order
     */
    public void getExtras() {

        Intent intent = getIntent();

        if( intent != null ){

            order = (POSOrder)intent.getSerializableExtra("draftOrder");

        }
    }

    public void onBackPressed() {

        if( order != null &&
                !order.getStatus().equals(POSOrder.DRAFT_STATUS) ){
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

    private void updateSummary(int position) {

        switch (position) {

            case EditPagerAdapter.ORDERING_POSITION:

                break;

            case EditPagerAdapter.ORDERED_POSITION:

                break;

            default:

                break;
        }

    }



}
