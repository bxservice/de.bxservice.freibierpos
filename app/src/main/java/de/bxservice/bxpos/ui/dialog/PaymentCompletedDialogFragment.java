package de.bxservice.bxpos.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataProvider;

/**
 * Created by Diego Ruiz on 22/02/16.
 */
public class PaymentCompletedDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface PaymentCompletedListener {
        void onDialogPositiveClick(PaymentCompletedDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    PaymentCompletedListener mListener;

    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal paidAmount = BigDecimal.ZERO;
    private BigDecimal changeAmount = BigDecimal.ZERO;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false); //No back button - it's not a confirmation dialog, only an informative one

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.payment_complete_dialog, null);

        final TextView totalText = (TextView) view.findViewById(R.id.total);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DataProvider.LOCALE);

        totalText.setText(getString(R.string.total_value, currencyFormat.format(getTotal())));

        final TextView paidText = (TextView) view.findViewById(R.id.paid);
        paidText.setText(getString(R.string.paid_value, currencyFormat.format(getPaidAmount())));


        final TextView changeText = (TextView) view.findViewById(R.id.change);
        changeText.setText(getString(R.string.change_value, currencyFormat.format(getChangeAmount())));

        builder.setTitle(R.string.pay);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(PaymentCompletedDialogFragment.this);
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
            mListener = (PaymentCompletedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement PaymentCompletedListener");
        }
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(BigDecimal changeAmount) {
        this.changeAmount = changeAmount;
    }
}
