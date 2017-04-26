package de.bxservice.bxpos.ui.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.NumberFormat;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.pos.PosProperties;

public class OverridePriceDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface OverridePriceDialogListener {
        void onDialogPositiveClick(OverridePriceDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private OverridePriceDialogFragment.OverridePriceDialogListener mListener;
    private MProduct product = null;
    private BigDecimal overridePrice;
    private EditText enteredPrice;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        setRetainInstance(true);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.override_price_dialog, null);

        final TextView productNameText = (TextView) view.findViewById(R.id.item_name);

        if (product != null)
            productNameText.setText(product.getProductName());

        enteredPrice = (EditText) view.findViewById(R.id.item_price);

        enteredPrice.requestFocus();

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Nothing on purpose to avoid closing the dialog when the price is lower than expected

                        if (!TextUtils.isEmpty(enteredPrice.getText().toString()))
                            overridePrice = new BigDecimal(enteredPrice.getText().toString());

                        //If the price is lower than the price limit -> error
                        if (overridePrice != null && overridePrice.compareTo(product.getProductPrice(getActivity()).getPriceLimit()) < 0)
                            Toast.makeText(getActivity().getBaseContext(), "Limit price violated",
                                    Toast.LENGTH_LONG).show();
                        else
                            mListener.onDialogPositiveClick(OverridePriceDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        OverridePriceDialogFragment.this.getDialog().cancel();
                    }
                });

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        return dialog;
    }

    @Override
    public void onStart()
    {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        final AlertDialog d = (AlertDialog) getDialog();
        if(d != null)
        {
            Button positiveButton = d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    BigDecimal priceLimit = product.getProductPrice(getActivity()).getPriceLimit();
                    if (!TextUtils.isEmpty(enteredPrice.getText().toString())) {
                        overridePrice = new BigDecimal(enteredPrice.getText().toString());
                        //If the price is lower than the price limit -> error
                        if (overridePrice != null && overridePrice.compareTo(priceLimit) < 0)
                            wrongPrice(priceLimit);
                        else
                            mListener.onDialogPositiveClick(OverridePriceDialogFragment.this);
                    }
                    else {
                        wrongPrice(priceLimit);
                    }
                }
            });
        }
    }

    public void wrongPrice(BigDecimal limitPrice) {
        if (enteredPrice != null) {
            NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();
            enteredPrice.setError(getString(R.string.error_invalid_price, currencyFormat.format(limitPrice)));
            enteredPrice.requestFocus();
        }
    }

    // Override the Fragment.onAttach() method to instantiate the OverridePriceDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OverridePriceDialogFragment.OverridePriceDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement OverridePriceDialogListener");
        }
    }

    @Override
    public void onDestroyView() {
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (getDialog() != null && getRetainInstance()) {
            getDialog().setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public MProduct getProduct() {
        return product;
    }

    public void setProduct(MProduct product) {
        this.product = product;
    }

    public BigDecimal getOverridePrice() {
        return overridePrice;
    }

}