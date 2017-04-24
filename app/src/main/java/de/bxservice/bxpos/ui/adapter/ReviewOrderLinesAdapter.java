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
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;

public class ReviewOrderLinesAdapter extends RecyclerView.Adapter<ReviewOrderLinesAdapter.ReviewOrderLinesViewHolder> {

    private ArrayList<POSOrderLine> mDataset;

    public static class ReviewOrderLinesViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtQty;
        public TextView txtProductName;
        public TextView txtPrice;

        public ReviewOrderLinesViewHolder(View v) {
            super(v);

            txtQty         = (TextView) itemView.findViewById(R.id.lblQty2);
            txtProductName = (TextView) itemView.findViewById(R.id.lblName2);
            txtPrice = (TextView) itemView.findViewById(R.id.lblpriceline2);

        }

        public void bindOrderLine(POSOrderLine orderLine) {
            txtQty.setText(String.valueOf(orderLine.getQtyOrdered()));
            txtProductName.setText(orderLine.getProduct().getProductKey());
            txtPrice.setText(orderLine.getLineTotalAmt());
        }
    }

    public ReviewOrderLinesAdapter(ArrayList<POSOrderLine> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReviewOrderLinesViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ordered_items, parent, false);

        return new ReviewOrderLinesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ReviewOrderLinesViewHolder holder, int position) {
        POSOrderLine orderLine = mDataset.get(position);
        holder.bindOrderLine(orderLine);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
