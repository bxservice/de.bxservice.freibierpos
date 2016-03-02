package de.bxservice.bxpos.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.DataWriter;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.ui.dialog.CourtesyDialogFragment;
import de.bxservice.bxpos.ui.dialog.DiscountDialogFragment;
import de.bxservice.bxpos.ui.dialog.PaymentCompletedDialogFragment;
import de.bxservice.bxpos.ui.dialog.RemarkDialogFragment;


public class PayOrderActivity extends AppCompatActivity implements RemarkDialogFragment.RemarkDialogListener,
        CourtesyDialogFragment.CourtesyDialogListener, DiscountDialogFragment.DiscountDialogListener,
        PaymentCompletedDialogFragment.PaymentCompletedListener, View.OnLongClickListener {

    private POSOrder order;

    private View mPayFormView;
    private View mProgressView;

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

    //TODO: Add the payment option methods supported by iDempiere

    private CreateOrderTask createOrderTask;



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

        deleteButton = findViewById(R.id.del);

        deleteButton.setOnLongClickListener(this);

        initAmounts();
        fillPaymentDisplay();

        mPayFormView = findViewById(R.id.pay_form);
        mProgressView = findViewById(R.id.pay_progress);
    }

    public void initAmounts() {
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
    public void getExtras() {
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
            //TODO: Customize tip as a charge in iDempiere (Configurable)
            showCourtesyDialog();
            return true;
        }
        if (id == R.id.add_note) {
            showRemarkDialog();
            return true;
        }
        if (id == R.id.add_discount) {
            //TODO: Discount as a new line in iDempiere - Configurable
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
    public void fillPaymentDisplay() {

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
    public String getSubtotalText() {

        StringBuilder subTotalString = new StringBuilder();
        subTotalString.append(getString(R.string.subtotal));
        subTotalString.append(": ");

        BigDecimal total = subtotal;

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);
        subTotalString.append(currencyFormat.format(total));

        return subTotalString.toString();
    }

    /**
     * Fill amount to Pay text view with the current amount
     * to pay
     * @return
     */
    public String getAmountToPayText() {

        StringBuilder amountString = new StringBuilder();
        amountString.append(getString(R.string.to_pay));
        amountString.append(": ");

        BigDecimal total = getAmountToPay();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);
        amountString.append(currencyFormat.format(total));

        return amountString.toString();
    }

    /**
     * Fill surcharge text view with the assigned surcharge
     * of the order
     * @return
     */
    public String getSurchargeText() {

        StringBuilder surchargeString = new StringBuilder();
        surchargeString.append(getString(R.string.set_extra));
        surchargeString.append(": ");

        BigDecimal total = getSurcharge();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);
        surchargeString.append(currencyFormat.format(total));

        return surchargeString.toString();
    }

    /**
     * Fill surcharge text view with the assigned surcharge
     * of the order
     * @return
     */
    public String getDiscountText() {

        StringBuilder discountString = new StringBuilder();
        discountString.append(getString(R.string.add_discount));
        discountString.append(": ");

        BigDecimal total = getDiscount().negate();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);
        discountString.append(currencyFormat.format(total));

        return discountString.toString();
    }

    /**
     * Fill change text view with the amount
     * that has to be given back to the customer
     * @return
     */
    public String getChangeText() {

        StringBuilder changeString = new StringBuilder();
        changeString.append(getString(R.string.change));
        changeString.append(": ");

        BigDecimal total = getChange();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);
        changeString.append(currencyFormat.format(total));

        return changeString.toString();
    }

    /**
     * Fill change text view with the amount
     * that has to be given back to the customer
     * @return
     */
    public String getPaidAmountText() {

        StringBuilder paidString = new StringBuilder();
        paidString.append(getString(R.string.paid));
        paidString.append(": ");

        BigDecimal total = getPaidAmount();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);
        paidString.append(currencyFormat.format(total));

        return paidString.toString();
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getSurcharge() {
        return surcharge;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getChange() {
        changeAmount = total.subtract(paidAmount);
        changeAmount = changeAmount.subtract(amountToPay);
        return changeAmount.negate();
    }

    public void setSurcharge(BigDecimal surcharge) {
        this.surcharge = surcharge;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    /**
     * Fill surcharge text view with the assigned surcharge
     * of the order
     * @return
     */
    public String getTotalText() {

        StringBuilder totalString = new StringBuilder();
        totalString.append(getString(R.string.total));
        totalString.append(": ");

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);
        totalString.append(currencyFormat.format(getTotal()));

        return totalString.toString();
    }

    public BigDecimal getTotal() {
        total = subtotal.add(getSurcharge());
        total = total.subtract(getDiscount());
        return total;
    }

    public BigDecimal getAmountToPay() {
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
        CourtesyDialogFragment courtesyDialog = new CourtesyDialogFragment();
        courtesyDialog.setSubtotal(subtotal);
        courtesyDialog.setSurchargeAmount(surcharge);
        courtesyDialog.show(getFragmentManager(), "CourtesyDialogFragment");
    }

    /**
     * Click add on add remark dialog
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(CourtesyDialogFragment dialog) {
        // User touched the dialog's positive button
        surcharge = dialog.getSurchargeAmount();
        //order.setSurcharge(); //TODO Create
        //order.updateOrder(getBaseContext());
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
        //order.setSurcharge(); //TODO Create
        //order.updateOrder(getBaseContext());
        updateDiscountField();
    }

    public void updateSurchargeField() {
        surchargeTextView.setText(getSurchargeText());
        updateTotalField();
    }

    public void updateTotalField() {
        totalTextView.setText(getTotalText());
        updateChangeField();
    }

    public void updateDiscountField() {
        discountTextView.setText(getDiscountText());
        updateTotalField();
    }

    public void updatePayField() {
        payTextView.setText(getAmountToPayText());
        updateChangeField();
    }

    public void updatePaidField() {
        paidTextView.setText(getPaidAmountText());
        updateChangeField();
    }

    public void updateChangeField() {
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
        completePayment();
    }

    private void onPay() {

        paidAmount = paidAmount.add(getAmountToPay());

        //Paid amount not bigger than the total
        if (paidAmount.compareTo(getTotal()) == -1) {
            onClear();
            updatePaidField();
        }
        //Paid amount bigger than the total or same as total
        if (paidAmount.compareTo(getTotal()) == 1 || paidAmount.compareTo(getTotal()) == 0) {
            onClear();
            completePayment();
        }
    }

    private void completePayment() {
        showPaymentDialog();
    }

    private void showPaymentDialog() {
        PaymentCompletedDialogFragment paymentDialog = new PaymentCompletedDialogFragment();
        paymentDialog.setTotal(getTotal());
        paymentDialog.setPaidAmount(getPaidAmount());
        paymentDialog.setChangeAmount(getChange());
        paymentDialog.show(getFragmentManager(), "PaymentDialogFragment");
    }

    /**
     * Click add on add remark dialog
     * @param dialog
     */
    @Override
    public void onDialogPositiveClick(PaymentCompletedDialogFragment dialog) {
        // User touched the dialog's positive button
        attemptSynchronizeOrder();
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

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String syncConnPref = sharedPref.getString(OfflineAdminSettingsActivity.KEY_PREF_SYNC_CONN, "");

        //Check if network connection is available
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) { //TODO: Add when preferences

            showProgress(true);
            createOrderTask = new CreateOrderTask(order);
            createOrderTask.execute((Void) null);

        }else { //No internet connection
        /*
            // If the sync configuration chosen was Always the order cannot be unsynchronized
            if("0".equals(syncConnPref)) {
                Snackbar snackbar = Snackbar
                        .make(mLoginFormView, getString(R.string.error_no_connection), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.action_retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                attemptLogin();
                            }
                        });

                // Changing message text color
                snackbar.setActionTextColor(Color.RED);
                snackbar.show();
            } else {

                PosUser loggedUser = getLoggedUser(username);

                //Username does not exist and no internet connection
                if(loggedUser == null) {
                    Snackbar snackbar = Snackbar
                            .make(mLoginFormView, getString(R.string.error_no_connection_username), Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.action_retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    attemptLogin();
                                }
                            });

                    // Changing message text color
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                } else {
                    //No internet connection but the user is known
                    offlineLogin(loggedUser);
                }
            }*/
        }

    }

    /**
     * When the pay button is clicked completes the order
     * set status complete and free the table
     * Synchronized flag depends on the success of the creation in
     * iDempiere
     * @param isSynchronized
     */
    public void completeOrder(boolean isSynchronized) {

        order.setStatus(POSOrder.COMPLETE_STATUS);
        order.setSync(isSynchronized);
        order.updateOrder(getBaseContext());

        if( order.getTable() != null) {
            order.getTable().setStatus(Table.FREE_STATUS);
            order.getTable().updateTable(getBaseContext());
        }
    }

    @Override
    public void finish() {
        setResult(2); //Everything ok - the order was created and the create Activity should be closed
        super.finish();
    }

    /**
     * Shows the progress UI and hides the login form.
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
     * Represents an asynchronous creating task used to send
     * the order to iDempiere
     */
    public class CreateOrderTask extends AsyncTask<Void, Void, Boolean> {


        private POSOrder order;

        CreateOrderTask(POSOrder order) {
            this.order = order;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            DataWriter write = new DataWriter(getBaseContext(), order);
            return write.isSuccess();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            //TODO: Add when not success because not connection or because an error ocurred while saving
            completeOrder(true);
            finish();
        }
    }

}
