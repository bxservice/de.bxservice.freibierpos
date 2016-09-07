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

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.NewOrderGridItem;

/**
 * Created by Diego Ruiz on 9/12/15.
 */
public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder> implements View.OnClickListener, Filterable {

    private ArrayList<NewOrderGridItem> mDataset;
    private ArrayList<NewOrderGridItem> orig;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SearchItemViewHolder extends RecyclerView.ViewHolder {

        public TextView txtProductName;
        public TextView txtPtice;

        public SearchItemViewHolder(View v) {
            super(v);

            txtProductName = (TextView) itemView.findViewById(R.id.search_name);
            txtPtice = (TextView) itemView.findViewById(R.id.search_price);

        }

        public void bindSearchItem(NewOrderGridItem item) {
            txtProductName.setText(item.getName());
            txtPtice.setText(item.getPrice());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchItemAdapter(ArrayList<NewOrderGridItem> myDataset) {
        mDataset = myDataset;
    }

    private View.OnClickListener listener;

    // Create new views (invoked by the layout manager)
    @Override
    public SearchItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_items, parent, false);
        // set the view's size, margins, paddings and layout parameters

        v.setOnClickListener(this);

        return new SearchItemViewHolder(v);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SearchItemViewHolder holder, int position) {
        NewOrderGridItem orderLine = mDataset.get(position);

        holder.bindSearchItem(orderLine);
    }

    public NewOrderGridItem getSelectedItem(int position) {
        return mDataset.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Filter getFilter() {

        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                final FilterResults oReturn = new FilterResults();
                final ArrayList<NewOrderGridItem> results = new ArrayList<>();

                if (orig == null)
                    orig = mDataset;

                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final NewOrderGridItem g : orig) {
                            if (g.getName().toLowerCase().contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mDataset = (ArrayList<NewOrderGridItem>) results.values;
                notifyDataSetChanged();

            }
        };

    }

}
