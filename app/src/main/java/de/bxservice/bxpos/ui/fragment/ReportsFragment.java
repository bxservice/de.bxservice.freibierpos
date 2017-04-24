/**********************************************************************
 * This file is part of Freibier POS                                   *
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
package de.bxservice.bxpos.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Calendar;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.report.Report;
import de.bxservice.bxpos.logic.model.report.ReportFactory;
import de.bxservice.bxpos.ui.adapter.ReportTypeListAdapter;
import de.bxservice.bxpos.ui.decorator.DividerItemDecoration;
import de.bxservice.bxpos.ui.dialog.DatePickerFragment;

public class ReportsFragment extends Fragment implements View.OnClickListener {

    //View components
    private Button fromButton, toButton;
    private ReportTypeListAdapter mAdapter;

    //The dates will are stored as int in yyyymmdd format
    private long fromDate, toDate;
    private boolean isFromDate;

    public ReportsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reports, container, false);

        fromButton   = (Button) view.findViewById(R.id.from_button);
        toButton     = (Button) view.findViewById(R.id.to_button);
        RecyclerView  recyclerView = (RecyclerView) view.findViewById(R.id.report_types);

        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1; //Calendar month returns the position of the month 0 being January
        int day = c.get(Calendar.DAY_OF_MONTH);

        setFromButtonText(year, month, day);
        setToButtonText(year, month, day);
        fromDate = getFormattedDate(year, month, day, 0, 0); //From the beginning of the day
        toDate   = getFormattedDate(year, month, day,23,59); //To the end of the day

        String[] reportTypeNames  = getResources().getStringArray(R.array.report_types_titles);
        String[] reportTypeValues = getResources().getStringArray(R.array.report_types_values);

        ReportFactory reports = new ReportFactory(getActivity(), reportTypeNames, reportTypeValues);
        mAdapter = new ReportTypeListAdapter(reports.getReports());

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        recyclerView.setAdapter(mAdapter);

        fromButton.setOnClickListener(this);
        toButton.setOnClickListener(this);

        return view;
    }

    public void onDateSet(int year, int month, int day) {

        if (isFromDate) {
            setFromButtonText(year, month+1, day);
            fromDate = getFormattedDate(year, month+1, day, 0, 0); //From the beginning of the day
        }
        else {
            setToButtonText(year, month+1, day);
            toDate = getFormattedDate(year, month+1, day, 23, 59); //To the end of the day
        }

    }

    private void setFromButtonText(int year, int month, int day) {
        StringBuilder date = new StringBuilder();
        if(day < 10)
            date.append("0");
        date.append(day).append(".");
        if(month < 10)
            date.append("0");
        date.append(month).append(".").append(year);

        fromButton.setText(getString(R.string.from_date, date));
    }

    private void setToButtonText(int year, int month, int day) {
        StringBuilder date = new StringBuilder();
        if(day < 10)
            date.append("0");
        date.append(day).append(".");
        if(month < 10)
            date.append("0");
        date.append(month).append(".").append(year);
        toButton.setText(getString(R.string.to_date, date));
    }

    private long getFormattedDate(int year, int month, int day, int hour, int minute) {
        StringBuilder date = new StringBuilder();

        date.append(year);

        if(month < 10)
            date.append("0");
        date.append(month);

        if(day < 10)
            date.append("0");
        date.append(day);

        if(hour < 10)
            date.append("0");
        date.append(hour);

        if(minute < 10)
            date.append("0");
        date.append(minute);

        return Long.parseLong(date.toString());
    }

    private void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.from_button:
                isFromDate = true;
                showDatePickerDialog();
                break;

            case R.id.to_button:
                isFromDate = false;
                showDatePickerDialog();
                break;
        }
    }

    public ArrayList<Report> getSelectedReports() {
        return mAdapter.getSelectedReports();
    }

    public long getFromDate() {
        return fromDate;
    }

    public long getToDate() {
        return toDate;
    }
}
