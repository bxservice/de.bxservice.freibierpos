package de.bxservice.bxpos.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;

/**
 * Created by Diego Ruiz on 22/02/16.
 */
public class DiscountDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface DiscountDialogListener {
        void onDialogPositiveClick(DiscountDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private DiscountDialogListener mListener;

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal discountPercent = BigDecimal.ZERO;
    private String     reason = "";

    private TextWatcher percentWatcher;
    private TextWatcher amountWatcher;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.discount_dialog, null);

        final TextView subTotalLabel = (TextView) view.findViewById(R.id.sub_total);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DefaultPosData.LOCALE);

        subTotalLabel.setText(getString(R.string.subtotal_value, currencyFormat.format(subtotal)));

        final EditText discountPercentText = (EditText) view.findViewById(R.id.discount_percent);
        final EditText discountAmountText = (EditText) view.findViewById(R.id.discount_amount);
        final EditText reasonText = (EditText) view.findViewById(R.id.reason_text);

        reasonText.setText(reason);

        percentWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
                discountAmountText.removeTextChangedListener(amountWatcher);
                discountAmountText.setText(discountAmount.toString());
                discountAmountText.addTextChangedListener(amountWatcher);
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                try {
                    BigDecimal number = new BigDecimal(s.toString());

                    BigDecimal n2 = subtotal.multiply(number);

                    discountAmount = n2.divide(ONE_HUNDRED, 2, BigDecimal.ROUND_HALF_UP); //total*percentage/100
                } catch (NumberFormatException e) {
                    discountAmount = BigDecimal.ZERO;
                }

            }
        };

        discountPercentText.addTextChangedListener(percentWatcher);

        amountWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
                discountPercentText.removeTextChangedListener(percentWatcher);
                discountPercentText.setText(discountPercent.toString());
                discountPercentText.addTextChangedListener(percentWatcher);
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                try {
                    BigDecimal number = new BigDecimal(s.toString());

                    BigDecimal n2 = number.multiply(ONE_HUNDRED);

                    discountPercent = n2.divide(subtotal, 2, BigDecimal.ROUND_HALF_UP);  //Number*100/total
                }catch (NumberFormatException e) {
                    discountPercent = BigDecimal.ZERO;
                }
            }
        };

        discountAmountText.addTextChangedListener(amountWatcher);

        if (!discountAmount.equals(BigDecimal.ZERO))
            discountAmountText.setText(discountAmount.toString());

        builder.setTitle(R.string.add_discount);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        discountAmount = new BigDecimal(discountAmountText.getText().toString());
                        reason = reasonText.getText().toString();
                        mListener.onDialogPositiveClick(DiscountDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DiscountDialogFragment.this.getDialog().cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    // Override the Fragment.onAttach() method to instantiate the GuestNumberDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (DiscountDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement DiscountDialogListener");
        }
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
