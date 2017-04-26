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

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.NewOrderGridItem;

/**
 * Created by Diego Ruiz on 19/11/15.
 */
public class GridOrderViewAdapter extends ArrayAdapter<NewOrderGridItem> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<NewOrderGridItem> mGridData = new ArrayList<>();

    public GridOrderViewAdapter(Context mContext, int layoutResourceId, ArrayList<NewOrderGridItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<NewOrderGridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_name);
            holder.priceTextView = (TextView) row.findViewById(R.id.grid_item_price);
            holder.qtyTextView = (TextView) row.findViewById(R.id.qtyOrdered_text);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        NewOrderGridItem item = mGridData.get(position);
        if (item.getName() != null && !item.getName().isEmpty())
            holder.titleTextView.setText(Html.fromHtml(item.getName()));
        if (item.getPrice() != null && !item.getPrice().isEmpty())
            holder.priceTextView.setText(Html.fromHtml(item.getPrice()));
        holder.qtyTextView.setText(Html.fromHtml(item.getQty()));

        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView priceTextView;
        TextView qtyTextView;
    }
}
