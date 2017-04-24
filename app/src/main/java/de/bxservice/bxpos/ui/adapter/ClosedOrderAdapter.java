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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrder;

public class ClosedOrderAdapter extends RecyclerView.Adapter<ClosedOrderAdapter.ClosedOrderViewHolder>
        implements View.OnClickListener, Filterable {

    private List<POSOrder> mDataset;
    private List<POSOrder> orig;
    private View.OnClickListener listener;

    public static class ClosedOrderViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtOrderNumber;
        public TextView txtTableName;
        public TextView txtTotal;
        public TextView txtStatus;

        public ClosedOrderViewHolder(View v) {
            super(v);

            txtOrderNumber = (TextView) itemView.findViewById(R.id.order_number);
            txtTableName   = (TextView) itemView.findViewById(R.id.order_table);
            txtTotal       = (TextView) itemView.findViewById(R.id.order_total);
            txtStatus      = (TextView) itemView.findViewById(R.id.order_status);

        }

        public void bindOrder(POSOrder order) {
            txtOrderNumber.setText(itemView.getResources().getString(R.string.order_no, order.getDocumentNo()));
            txtTableName.setText(order.getTable() != null ? order.getTable().getTableName() : itemView.getResources().getString(R.string.unset_table));
            txtTotal.setText(itemView.getResources().getString(R.string.total_value, order.getTotal()));

            String status = order.getStatus();
            if (POSOrder.COMPLETE_STATUS.equals(status))
                status = itemView.getResources().getString(R.string.paid);
            else if (POSOrder.VOID_STATUS.equals(status)) {
                //To make it bold the recycler must be lost, ask if it's worthy
                /*txtStatus.setTypeface(null, Typeface.BOLD_ITALIC);
                txtOrderNumber.setTypeface(null, Typeface.BOLD_ITALIC);
                txtTableName.setTypeface(null, Typeface.BOLD_ITALIC);
                txtTotal.setTypeface(null, Typeface.BOLD_ITALIC);*/
                status = itemView.getResources().getString(R.string.voided);
            }

            txtStatus.setText(status);
        }

    }

    public ClosedOrderAdapter(List<POSOrder> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ClosedOrderViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.closed_order_grid_item_layout, parent, false);

        v.setOnClickListener(this);

        return new ClosedOrderViewHolder(v);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick(view);
    }

    @Override
    public void onBindViewHolder(ClosedOrderViewHolder holder, int position) {
        POSOrder order = mDataset.get(position);
        holder.bindOrder(order);
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public List<POSOrder> getOrders() {
        return mDataset;
    }

    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                final FilterResults oReturn = new FilterResults();
                final ArrayList<POSOrder> results = new ArrayList<>();

                if (orig == null)
                    orig = mDataset;

                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final POSOrder order : orig) {
                            if (order.getDocumentNo().toLowerCase().contains(constraint.toString()))
                                results.add(order);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDataset = (ArrayList<POSOrder>) results.values;
                notifyDataSetChanged();
            }
        };

    }

}
