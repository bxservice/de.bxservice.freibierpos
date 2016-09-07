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
package de.bxservice.bxpos.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.bxservice.bxpos.R;

/**
 * Created by Diego Ruiz on 7/04/16.
 */
public class PaymentTypeAdapter extends RecyclerView.Adapter<PaymentTypeAdapter.PaymentTypeViewHolder> implements View.OnClickListener {

    private ArrayList<String> mDataset;
    private View.OnClickListener listener;

    public static class PaymentTypeViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtPayment;

        public PaymentTypeViewHolder(View v) {
            super(v);

            txtPayment = (TextView) itemView.findViewById(R.id.type);
        }

        public void bindPaymentType(String paymentType, int position) {
            txtPayment.setText(paymentType);
            if (position == 0)
                txtPayment.setSelected(true);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PaymentTypeAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        //Allow only one selected at the same time
        ViewGroup row = (ViewGroup) view.getParent();
        for(int itemPos = 0; itemPos < row.getChildCount(); itemPos++) {
            row.getChildAt(itemPos).setSelected(false);
        }
        view.setSelected(true);
        if (listener != null)
            listener.onClick(view);
    }

    public String getSelectedItem(int position) {
        return mDataset.get(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public PaymentTypeViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.payment_type_item_layout, parent, false);

        v.setOnClickListener(this);

        //Half of the screen width
        v.getLayoutParams().width = parent.getWidth()/2;

        return new PaymentTypeViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PaymentTypeViewHolder holder, int position) {
        String result = mDataset.get(position);
        holder.bindPaymentType(result, position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}