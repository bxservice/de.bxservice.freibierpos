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
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.PosProperties;

/**
 * Created by Diego Ruiz on 22/02/16.
 */
public class SurchargeDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface CourtesyDialogListener {
        void onDialogPositiveClick(SurchargeDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private CourtesyDialogListener mListener;

    private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal surchargeAmount = BigDecimal.ZERO;
    private BigDecimal surchargePercent = BigDecimal.ZERO;

    private TextWatcher percentWatcher;
    private TextWatcher amountWatcher;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        setRetainInstance(true);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.courtesy_dialog, null);

        final TextView subTotalLabel = (TextView) view.findViewById(R.id.sub_total);

        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();

        subTotalLabel.setText(getString(R.string.subtotal_value, currencyFormat.format(subtotal)));

        final EditText surchargePercentText = (EditText) view.findViewById(R.id.surcharge_percent);
        final EditText surchargeAmountText = (EditText) view.findViewById(R.id.surcharge_amount);

        percentWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
                surchargeAmountText.removeTextChangedListener(amountWatcher);
                surchargeAmountText.setText(surchargeAmount.toString());
                surchargeAmountText.addTextChangedListener(amountWatcher);
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {

                try {
                    BigDecimal number = new BigDecimal(s.toString());

                    BigDecimal n2 = subtotal.multiply(number);

                    surchargeAmount = n2.divide(ONE_HUNDRED, 2, BigDecimal.ROUND_HALF_UP); //total*percentage/100
                } catch (NumberFormatException e) {
                    surchargeAmount = BigDecimal.ZERO;
                }

            }
        };

        surchargePercentText.addTextChangedListener(percentWatcher);

        amountWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {
                surchargePercentText.removeTextChangedListener(percentWatcher);
                surchargePercentText.setText(surchargePercent.toString());
                surchargePercentText.addTextChangedListener(percentWatcher);
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                try {
                    BigDecimal number = new BigDecimal(s.toString());

                    BigDecimal n2 = number.multiply(ONE_HUNDRED);

                    if (subtotal.compareTo(BigDecimal.ZERO) != 0)
                        surchargePercent = n2.divide(subtotal, 2, BigDecimal.ROUND_HALF_UP);  //Number*100/total
                }catch (NumberFormatException e) {
                    surchargePercent = BigDecimal.ZERO;
                }
            }
        };

        surchargeAmountText.addTextChangedListener(amountWatcher);

        if (!surchargeAmount.equals(BigDecimal.ZERO))
            surchargeAmountText.setText(surchargeAmount.toString());

        builder.setTitle(R.string.set_extra);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        surchargeAmount = new BigDecimal(surchargeAmountText.getText().toString());
                        mListener.onDialogPositiveClick(SurchargeDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SurchargeDialogFragment.this.getDialog().cancel();
                    }
                });

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        return dialog;
    }

    // Override the Fragment.onAttach() method to instantiate the GuestNumberDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (CourtesyDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement CourtesyDialogListener");
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

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getSurchargeAmount() {
        return surchargeAmount;
    }

    public void setSurchargeAmount(BigDecimal surchargeAmount) {
        this.surchargeAmount = surchargeAmount;
    }

}
