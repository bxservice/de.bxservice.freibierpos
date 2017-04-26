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
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.idempiere.Table;

/**
 * Created by Diego Ruiz on 18/11/15.
 */
public class GridTableViewAdapter extends ArrayAdapter<Table> {

    private Context mContext;
    private int layoutResourceId;
    private List<Table> mGridData = new ArrayList<>();

    public GridTableViewAdapter(Context mContext, int layoutResourceId, ArrayList<Table> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Updates grid data and refresh grid items.
     * @param newList
     */
    public void setGridData(ArrayList<Table> newList) {
       mGridData = newList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        LinearLayout linear;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);
            holder.serverName = (TextView) row.findViewById(R.id.server_name);
            holder.orderTime = (TextView) row.findViewById(R.id.time);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        linear = (LinearLayout) row.findViewById(R.id.table_layout);

        if (Table.BUSY_STATUS.equals(mGridData.get(position).getStatus())) {
            row.setBackgroundColor(ContextCompat.getColor(holder.titleTextView.getContext(), R.color.busy_table_color));

            String occupiedHour = "";
            try {
                //Get the date in format yyyymmddhhmm
                String dateString = mGridData.get(position).getOrderTime();
                SimpleDateFormat originalFormat = new SimpleDateFormat("yyyymmddHHmm");

                Date date = originalFormat.parse(dateString);
                DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(holder.titleTextView.getContext());
                occupiedHour = dateFormat.format(date);

            } catch(Exception e) {}
            holder.serverName.setText(mGridData.get(position).getServerName());
            holder.orderTime.setText(occupiedHour);

            //If the table is busy center relative to linear layout
            if (linear != null) {
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) linear.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                linear.setLayoutParams(layoutParams);
            }

            //holder.imageView.setImageResource(R.drawable.cutlery23_blank);
            //holder.titleTextView.setTextColor(Color.WHITE);
        } else {
            row.setBackgroundColor(ContextCompat.getColor(holder.titleTextView.getContext(), R.color.empty_table_color));
            holder.serverName.setText("");
            holder.orderTime.setText("");

            //If the table is empty, center the table name relative to the total button
            if (linear != null) {
                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) linear.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                linear.setLayoutParams(layoutParams);
            }

        }

        holder.titleTextView.setText(Html.fromHtml(mGridData.get(position).getTableName()));

        return row;
    }

    static class ViewHolder {
        TextView serverName;
        TextView titleTextView;
        TextView orderTime;
        ImageView imageView;
    }

}
