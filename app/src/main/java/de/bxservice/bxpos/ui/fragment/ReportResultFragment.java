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
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.ui.adapter.ReportResultAdapter;
import de.bxservice.bxpos.ui.decorator.DividerItemDecoration;

public class ReportResultFragment extends Fragment {

    public final static String REPORT_RESULTS = "de.bxservice.bxpos.REPORT_RESULTS";

    private RecyclerView recyclerView;

    public ReportResultFragment() {
    }

    public static ReportResultFragment newInstance(ArrayList<String> reportResults) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(REPORT_RESULTS, reportResults);

        ReportResultFragment fragment = new ReportResultFragment();
        fragment.setArguments(arguments);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_result_report, container, false);

        Bundle arguments = getArguments();

        if (arguments == null) {
            return view;
        }

        ArrayList<String> reportResults = (ArrayList<String>) arguments.getSerializable(REPORT_RESULTS);

        recyclerView = (RecyclerView) view.findViewById(R.id.report_result);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        ReportResultAdapter mGridAdapter = new ReportResultAdapter(reportResults);
        recyclerView.setAdapter(mGridAdapter);

        // Inflate the layout for this fragment
        return view;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }
}
