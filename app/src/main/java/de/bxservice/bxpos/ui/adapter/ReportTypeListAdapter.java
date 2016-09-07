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
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.report.Report;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public class ReportTypeListAdapter extends RecyclerView.Adapter<ReportTypeListAdapter.ReportTypeListViewHolder>
        implements View.OnClickListener {

    private ArrayList<Report> mDataset;
    private View.OnClickListener listener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ReportTypeListViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public CheckBox reportType;

        public ReportTypeListViewHolder(View v) {
            super(v);

            reportType = (CheckBox) itemView.findViewById(R.id.checkBox1);
        }

        public void bindReportType(final Report report) {
            reportType.setText(report.getName());
            reportType.setChecked(report.isSelected());

            //in some cases, it will prevent unwanted situations
            reportType.setOnCheckedChangeListener(null);

            //if true, your checkbox will be selected, else unselected
            reportType.setChecked(report.isSelected());

            reportType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //set your object's last status
                    report.setIsSelected(isChecked);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReportTypeListAdapter(ArrayList<Report> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReportTypeListViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_list_type_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        v.setOnClickListener(this);

        return new ReportTypeListViewHolder(v);
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
    public void onBindViewHolder(ReportTypeListViewHolder holder, int position) {
        Report report = mDataset.get(position);
        holder.bindReportType(report);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<Report> getReports() {
        return mDataset;
    }

}