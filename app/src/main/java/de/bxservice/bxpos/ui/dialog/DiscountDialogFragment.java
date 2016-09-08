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
package de.bxservice.bxpos.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
    private EditText    reasonText;
    private EditText    discountAmountText;

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
        discountAmountText = (EditText) view.findViewById(R.id.discount_amount);
        reasonText  = (EditText) view.findViewById(R.id.reason_text);

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
                        //Nothing on purpose to avoid closing the dialog when the reason is not filled out
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
                    reason = reasonText.getText().toString();
                    if (TextUtils.isEmpty(reason)) {
                        reasonText.setError(getString(R.string.error_invalid_reason));
                        reasonText.requestFocus();
                    }
                    else {
                        discountAmount = new BigDecimal(discountAmountText.getText().toString());
                        mListener.onDialogPositiveClick(DiscountDialogFragment.this);
                    }
                }
            });
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
