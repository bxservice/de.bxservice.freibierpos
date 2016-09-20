/**********************************************************************
 * This file is part of FreiBier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
package de.bxservice.bxpos.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.daomanager.PosOutputDeviceManagement;
import de.bxservice.bxpos.logic.model.idempiere.IOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSPayment;
import de.bxservice.bxpos.logic.model.pos.PosProperties;
import de.bxservice.bxpos.logic.print.POSOutputDevice;
import de.bxservice.bxpos.logic.print.POSOutputDeviceValues;
import de.bxservice.bxpos.logic.tasks.CreateOrderTask;
import de.bxservice.bxpos.logic.tasks.PrintOrderTask;
import de.bxservice.bxpos.ui.adapter.PaymentTypeAdapter;
import de.bxservice.bxpos.ui.decorator.DividerItemDecoration;
import de.bxservice.bxpos.ui.dialog.SurchargeDialogFragment;
import de.bxservice.bxpos.ui.dialog.DiscountDialogFragment;
import de.bxservice.bxpos.ui.dialog.PaymentCompletedDialogFragment;
import de.bxservice.bxpos.ui.dialog.RemarkDialogFragment;


public class PayOrderActivity extends AppCompatActivity implements RemarkDialogFragment.RemarkDialogListener,
        SurchargeDialogFragment.CourtesyDialogListener, DiscountDialogFragment.DiscountDialogListener,
        PaymentCompletedDialogFragment.PaymentCompletedListener, View.OnLongClickListener {

    private static final String LOG_TAG = "Pay Order Activity";

    private POSOrder order;

    private View mPayFormView;
    private View mProgressView;
    private RecyclerView recyclerView;

    private TextView subTotalTextView;
    private TextView surchargeTextView;
    private TextView discountTextView;
    private TextView totalTextView;
    private TextView payTextView;
    private TextView paidTextView;
    private TextView changeTextView;

    private BigDecimal subtotal;
    private BigDecimal total;
    private BigDecimal surcharge;
    private BigDecimal discount;
    private BigDecimal paidAmount;
    private BigDecimal amountToPay;
    private BigDecimal changeAmount;

    //Numeric pad buttons
    private View deleteButton;

    //Payment options
    private ArrayList<POSPayment> payments;
    private ArrayList<String> paymentTypes;
    private HashMap<String, String> paymentNamesValues;
    private String selectedPaymentType;

    private CreateOrderTask createOrderTask;


    private boolean orderPaid = false;
    private String discountReason;
    private StringBuilder payAmount = new StringBuilder("");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_order);
        Toolbar toolbar = (Toolbar) findViewById(R.id.PaymentToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getExtras();

        subTotalTextView   = (TextView) findViewById(R.id.subTotal_textview);
        surchargeTextView = (TextView) findViewById(R.id.extra_textView);
        totalTextView = (TextView) findViewById(R.id.total_textview);
        payTextView = (TextView) findViewById(R.id.pay_textView);
        paidTextView = (TextView) findViewById(R.id.paid_textview);
        changeTextView = (TextView) findViewById(R.id.change_textView);
        discountTextView = (TextView) findViewById(R.id.discount_textview);

        recyclerView = (RecyclerView) findViewById(R.id.payment_types);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));

        paymentTypes = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.payment_types_names)));
        paymentNamesValues = new HashMap<>();

        paymentNamesValues.put(paymentTypes.get(0), IOrder.PAYMENTRULE_Cash);
        paymentNamesValues.put(paymentTypes.get(1), IOrder.PAYMENTRULE_CreditCard);
        selectedPaymentType = IOrder.PAYMENTRULE_Cash; //Cash by default

        final PaymentTypeAdapter mAdapter = new PaymentTypeAdapter(paymentTypes);

        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int position = recyclerView.getChildAdapterPosition(v);

                String selectedItem = mAdapter.getSelectedItem(position);
                selectedPaymentType = paymentNamesValues.get(selectedItem);
            }
        });

        recyclerView.setAdapter(mAdapter);

        deleteButton = findViewById(R.id.del);
        deleteButton.setOnLongClickListener(this);

        initAmounts();
        fillPaymentDisplay();

        mPayFormView = findViewById(R.id.pay_form);
        mProgressView = findViewById(R.id.pay_progress);
    }

    private void initAmounts() {
        subtotal = order.getTotallines();
        surcharge = BigDecimal.ZERO;
        discount = BigDecimal.ZERO;
        paidAmount = BigDecimal.ZERO;
        amountToPay = getAmountToPay();
        total = getTotal();
        changeAmount = getChange();
    }

    /**
     * Get extras from the previous activity
     * - Order
     */
    private void getExtras() {
        Intent intent = getIntent();

        if(intent != null) {

            order = (POSOrder)intent.getSerializableExtra("completedOrder");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pay_order, menu);
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
        if (id == R.id.set_extra) {
            showCourtesyDialog();
            return true;
        }
        if (id == R.id.add_note) {
            showRemarkDialog();
            return true;
        }
        if (id == R.id.add_discount) {
            showDiscountDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * On Button listener defined in the xml file
     * @param view
     */
    public void onButtonClick(View view) {
        switch (view.getId()) {

            case R.id.del:
                onDelete();
                break;
            case R.id.quickPay:
                onQuickPay();
                break;
            case R.id.pay:
                onPay();
                break;
            default:
                payAmount.append(((Button) view).getText().toString());
                updatePayField();
                break;
        }
    }

    /**
     * Fill the parameters of the payment display
     * with the info of the order
     */
    private void fillPaymentDisplay() {

        subTotalTextView.setText(getSubtotalText());
        surchargeTextView.setText(getSurchargeText());
        totalTextView.setText(getTotalText());
        discountTextView.setText(getDiscountText());
        payTextView.setText(getAmountToPayText());
        paidTextView.setText(getPaidAmountText());
        changeTextView.setText(getChangeText());
    }

    /**
     * Fill subtotal text view with the current amount
     * of the order
     * @return
     */
    private String getSubtotalText() {
        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();

        return getString(R.string.subtotal_value, currencyFormat.format(subtotal));
    }

    /**
     * Fill amount to Pay text view with the current amount
     * to pay
     * @return
     */
    private String getAmountToPayText() {
        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();

        return getString(R.string.to_pay, currencyFormat.format(getAmountToPay()));
    }

    /**
     * Fill surcharge text view with the assigned surcharge
     * of the order
     * @return
     */
    private String getSurchargeText() {
        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();

        return getString(R.string.surcharge_value, currencyFormat.format(surcharge));
    }

    /**
     * Fill surcharge text view with the assigned surcharge
     * of the order
     * @return
     */
    private String getDiscountText() {
        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();

        return getString(R.string.discount_value, currencyFormat.format(discount.negate()));
    }

    /**
     * Fill change text view with the amount
     * that has to be given back to the customer
     * @return
     */
    private String getChangeText() {
        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();

        return getString(R.string.change_value, currencyFormat.format(getChange()));
    }

    /**
     * Fill change text view with the amount
     * that has to be given back to the customer
     * @return
     */
    private String getPaidAmountText() {
        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();

        return getString(R.string.paid_value, currencyFormat.format(paidAmount));
    }

    private BigDecimal getChange() {
        changeAmount = total.subtract(paidAmount);
        changeAmount = changeAmount.subtract(amountToPay);
        return changeAmount.negate();
    }

    /**
     * Fill total text view with the total
     * of the order
     * @return
     */
    private String getTotalText() {
        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();

        return getString(R.string.total_value, currencyFormat.format(getTotal()));
    }

    private BigDecimal getTotal() {
        total = subtotal.add(surcharge);
        total = total.subtract(discount);
        return total;
    }

    private BigDecimal getAmountToPay() {
        if(payAmount.toString().equals(""))
            amountToPay = BigDecimal.ZERO;
        else {
            amountToPay = new BigDecimal(payAmount.toString());
        }
        return amountToPay;
    }

    private void showRemarkDialog() {
        RemarkDialogFragment remarkDialog = new RemarkDialogFragment();
        remarkDialog.setNote(order.getOrderRemark());
        remarkDialog.show(getFragmentManager(), "RemarkDialogFragment");
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

    private void showCourtesyDialog() {
        SurchargeDialogFragment courtesyDialog = new SurchargeDialogFragment();
        courtesyDialog.setSubtotal(subtotal);
        courtesyDialog.setSurchargeAmount(surcharge);
        courtesyDialog.show(getFragmentManager(), "SurchargeDialogFragment");
    }

    /**
     * Click add on add remark dialog
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(SurchargeDialogFragment dialog) {
        // User touched the dialog's positive button
        surcharge = dialog.getSurchargeAmount();
        order.setSurcharge(surcharge);
        updateSurchargeField();
    }

    private void showDiscountDialog() {
        DiscountDialogFragment courtesyDialog = new DiscountDialogFragment();
        courtesyDialog.setSubtotal(subtotal);
        courtesyDialog.setDiscountAmount(discount);
        courtesyDialog.setReason(discountReason);
        courtesyDialog.show(getFragmentManager(), "DiscountDialogFragment");
    }

    /**
     * Click add on add remark dialog
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(DiscountDialogFragment dialog) {
        // User touched the dialog's positive button
        discount = dialog.getDiscountAmount();
        discountReason = dialog.getReason();
        order.setDiscount(discount);
        order.setDiscountReason(discountReason);
        updateDiscountField();
        dialog.dismiss();
    }

    private void updateSurchargeField() {
        surchargeTextView.setText(getSurchargeText());
        updateTotalField();
    }

    private void updateTotalField() {
        totalTextView.setText(getTotalText());
        updateChangeField();
    }

    private void updateDiscountField() {
        discountTextView.setText(getDiscountText());
        updateTotalField();
    }

    private void updatePayField() {
        payTextView.setText(getAmountToPayText());
        updateChangeField();
    }

    private void updatePaidField() {
        paidTextView.setText(getPaidAmountText());
        updateChangeField();
    }

    private void updateChangeField() {
        changeTextView.setText(getChangeText());
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.del) {
            onClear();
            return true;
        }
        return false;
    }

    private void onClear() {
        payAmount = new StringBuilder("");
        updatePayField();
    }

    private void onDelete() {
        if(payAmount.length() > 0) {
            payAmount.delete(payAmount.length() - 1, payAmount.length());
            updatePayField();
        }
    }

    private void onQuickPay() {
        paidAmount = getTotal();
        onClear();
        updatePaidField();
        completePayment();
    }

    private void onPay() {

        BigDecimal partialPayment = getAmountToPay();
        paidAmount = paidAmount.add(partialPayment);

        //Paid amount not bigger than the total
        if (paidAmount.compareTo(getTotal()) == -1) {
            onClear();
            performPartialPayment(partialPayment);
            updatePaidField();
        }
        //Paid amount bigger than the total or same as total
        if (paidAmount.compareTo(getTotal()) == 1 || paidAmount.compareTo(getTotal()) == 0) {
            onClear();
            //If there were smaller payments before
            if(payments != null)
                performPartialPayment(partialPayment);

            updatePaidField();
            completePayment();
        }
    }

    /**
     * Perform a partial payment
     * @param partialAmount
     */
    private void performPartialPayment(BigDecimal partialAmount) {

        if(payments == null)
            payments = new ArrayList<>();

        //Change -> if the last payment exceeds the amount needed, place the right amount in the payment
        BigDecimal change = BigDecimal.ZERO;
        if(getChange().compareTo(BigDecimal.ZERO) == 1) {
            change = getChange();
        }

        POSPayment previousPayment = null;

        if(!payments.isEmpty()) {
            for(POSPayment oldPayment : payments) {
                if(oldPayment.getPaymentRule().equals(selectedPaymentType)) {
                    previousPayment = oldPayment;
                }
            }
        }

        if(previousPayment == null) {
            POSPayment partialPayment = new POSPayment();
            partialPayment.setPaymentAmount(partialAmount.subtract(change));
            partialPayment.setTenderType(selectedPaymentType, getBaseContext());
            payments.add(partialPayment);
        }
        else {
            previousPayment.setPaymentAmount(previousPayment.getPaymentAmount().add(partialAmount.subtract(change)));
        }

    }

    private void completePayment() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.complete_order)
                .setMessage(R.string.no_undone_operation)
                .setNegativeButton(R.string.no, null)
                .setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        showPaymentDialog();
                    }
                }).create().show();
    }

    private void showPaymentDialog() {
        PaymentCompletedDialogFragment paymentDialog = new PaymentCompletedDialogFragment();
        paymentDialog.setTotal(getTotal());
        paymentDialog.setPaidAmount(paidAmount);
        paymentDialog.setChangeAmount(getChange());
        paymentDialog.show(getFragmentManager(), "PaymentDialogFragment");
    }

    /**
     * Click add on add remark dialog
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(PaymentCompletedDialogFragment dialog) {
        order.setCashAmt(paidAmount);
        order.setChangeAmt(getChange());
        attemptSynchronizeOrder();
    }

    private void printOrder() {
        PosOutputDeviceManagement outputDeviceManager = new PosOutputDeviceManagement(getBaseContext());
        POSOutputDevice printReceiptDevice = outputDeviceManager.getDevice(POSOutputDeviceValues.TARGET_RECEIPT);

        if(printReceiptDevice != null && printReceiptDevice.getDeviceType().equalsIgnoreCase(POSOutputDeviceValues.DEVICE_PRINTER)) {
            PrintOrderTask createOrderTask = new PrintOrderTask(this, printReceiptDevice);
            createOrderTask.execute(order);
        }

    }

    /**
     * Attempts to create order in iDempiere.
     * If there are errors (iDempiere server returns an error), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSynchronizeOrder() {
        if (createOrderTask != null) {
            return;
        }

        orderPaid = true;
        order.setPayments(payments);
        order.setPaymentRule(selectedPaymentType);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String syncConnPref = sharedPref.getString(OfflineAdminSettingsActivity.KEY_PREF_SYNC_CONN, "");

        //If the synchronization settings are configured as Never
        if (!"0".equals(syncConnPref)) {
            Log.i(LOG_TAG, "Sync configuration - order sent to queue");
            order.payOrder(false, getBaseContext());
            printOrder();
            finish();
            return;
        }

        //Check if network connection is available
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //Check if there is internet connection
        if (networkInfo != null && networkInfo.isConnected()) {

            showProgress(true);
            if(order.getPayments() != null && order.getPayments().size() > 1)
                order.setPaymentRule(IOrder.PAYMENTRULE_MixedPOSPayment);
            createOrderTask = new CreateOrderTask(this);
            createOrderTask.execute(order);

        }else { //No internet connection

            // If the sync configuration chosen was Always the order has to be send
            if("0".equals(syncConnPref)) {
                Snackbar snackbar = Snackbar
                        .make(mPayFormView, getString(R.string.error_no_connection), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.action_retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                attemptSynchronizeOrder();
                            }
                        });

                // Changing message text color
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
            } else {

                Snackbar snackbar = Snackbar
                        .make(mPayFormView, getString(R.string.error_no_connection_on_pay_order), Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                order.payOrder(false, getBaseContext());
                                printOrder();
                                finish();
                            }
                        });

                // Changing message text color
                snackbar.setActionTextColor(Color.GREEN);
                snackbar.show();
            }
        }

    }

    @Override
    public void finish() {
        if(orderPaid)
            setResult(RESULT_OK); //Everything ok - the order was created and the create Activity should be closed
        else
            setResult(RESULT_CANCELED);
        super.finish();
    }

    /**
     * Shows the progress UI and hides the payment form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mPayFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mPayFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mPayFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mPayFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * gets call after the create order task finishes
     * @param success
     */
    public void postExecuteTask(boolean success, boolean connectionError, String errorMessage) {
        if(success) {
            order.payOrder(true, getBaseContext());
            printOrder();
            finish();
        }
        else {

            if (connectionError) {
                Snackbar snackbar = Snackbar
                        .make(mPayFormView, getString(R.string.no_connection_on_sync_order), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.action_retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                attemptSynchronizeOrder();
                            }
                        });

                // Changing message text color
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();

            } else {
                Toast.makeText(getBaseContext(), getString(R.string.no_success_on_sync_order) + errorMessage,
                        Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

}
