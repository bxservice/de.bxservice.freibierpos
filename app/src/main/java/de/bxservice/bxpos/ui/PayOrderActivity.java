package de.bxservice.bxpos.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.ui.dialog.CourtesyDialogFragment;
import de.bxservice.bxpos.ui.dialog.DiscountDialogFragment;
import de.bxservice.bxpos.ui.dialog.RemarkDialogFragment;


public class PayOrderActivity extends AppCompatActivity implements RemarkDialogFragment.RemarkDialogListener,
        CourtesyDialogFragment.CourtesyDialogListener, DiscountDialogFragment.DiscountDialogListener {

    private POSOrder order;

    private TextView subTotalTextView;
    private TextView surchargeTextView;
    private TextView discountTextView;
    private TextView totalTextView;
    private TextView payTextView;
    private TextView paidTextView;
    private TextView changeTextView;

    private BigDecimal subtotal;
    private BigDecimal surcharge;
    private BigDecimal discount;

    private String discountReason;

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

        initAmounts();
        fillPaymentDisplay();

    }

    public void initAmounts() {
        subtotal = order.getTotallines();
        surcharge = BigDecimal.ZERO;
        discount = BigDecimal.ZERO;
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
     * Fill the parameters of the payment display
     * with the info of the order
     */
    public void fillPaymentDisplay() {

        subTotalTextView.setText(getSubtotalText());
        surchargeTextView.setText(getSurchargeText());
        totalTextView.setText(getTotal());
        discountTextView.setText(getDiscountText());
        payTextView.setText("");
        paidTextView.setText("");
        changeTextView.setText("");
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

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getSurcharge() {
        return surcharge;
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
    public String getTotal() {

        StringBuilder totalString = new StringBuilder();
        totalString.append(getString(R.string.total));
        totalString.append(": ");

        BigDecimal total = getSurcharge().add(order.getTotallines());

        total = total.subtract(getDiscount());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);
        totalString.append(currencyFormat.format(total));

        return totalString.toString();
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
        courtesyDialog.show(getFragmentManager(), "RemarkDialogFragment");
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
        updateTotalField();
    }

    private void showDiscountDialog() {
        DiscountDialogFragment courtesyDialog = new DiscountDialogFragment();
        courtesyDialog.setSubtotal(subtotal);
        courtesyDialog.setDiscountAmount(discount);
        courtesyDialog.setReason(discountReason);
        courtesyDialog.show(getFragmentManager(), "RemarkDialogFragment");
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
        updateTotalField();
    }

    public void updateSurchargeField() {
        surchargeTextView.setText(getSurchargeText());
    }

    public void updateTotalField() {
        totalTextView.setText(getTotal());
    }

    public void updateDiscountField() {
        discountTextView.setText(getDiscountText());
    }


}
