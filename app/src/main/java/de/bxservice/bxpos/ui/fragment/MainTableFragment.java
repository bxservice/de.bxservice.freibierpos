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
package de.bxservice.bxpos.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.ui.MainActivity;
import de.bxservice.bxpos.ui.adapter.GridTableViewAdapter;

/**
 * Created by Diego Ruiz on 18/11/15.
 */
public class MainTableFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private GridTableViewAdapter mGridAdapter;
    private List<Table> mGridData;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainTableFragment newInstance(int sectionNumber) {
        MainTableFragment fragment = new MainTableFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public List<Table> getmGridData() {
        return mGridData;
    }


    public MainTableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        GridView grid = (GridView) rootView.findViewById(R.id.tableView);

        List<TableGroup> tableGroupList = TableGroup.getAllTableGroups(getActivity().getBaseContext());
        TableGroup tableGroup = tableGroupList.get(sectionNumber);

        mGridData = tableGroup.getTables();
        grid.setGravity(Gravity.CENTER_HORIZONTAL);

        mGridAdapter = new GridTableViewAdapter(this.getContext(), R.layout.table_grid_item_layout, (ArrayList<Table>) mGridData);

        grid.setAdapter(mGridAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                Table item = (Table) parent.getItemAtPosition(position);

                ((MainActivity) getActivity()).setSelectedTable(item);

                if (item.getStatus().equals(Table.FREE_STATUS)) {
                    if (DefaultPosData.get(getActivity()).isShowGuestDialog())
                        ((MainActivity) getActivity()).showGuestNumberDialog();
                    else
                        ((MainActivity) getActivity()).createOrder();
                } else if (item.getStatus().equals(Table.BUSY_STATUS)) {
                    ((MainActivity) getActivity()).editOrder(POSOrder.getTableOrders(getActivity().getBaseContext(), item));
                }
            }
        });

        return rootView;
    }

    public void updateTableStatus(int position, Table table) {
        if (mGridAdapter == null || mGridData == null || table == null)
            return;

        mGridData.get(position).setStatus(table.getStatus());
        mGridData.get(position).setServerName(table.getServerName());
        mGridData.get(position).setStatusChangeTime(table.getStatusChangeTime());
        mGridAdapter.setGridData((ArrayList<Table>) mGridData);
        mGridAdapter.notifyDataSetChanged();
    }

    public void refreshAllTables() {

        if (mGridAdapter == null)
            return;

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        List<TableGroup> tableGroupList = TableGroup.getAllTableGroups(getActivity().getBaseContext());
        TableGroup tableGroup = tableGroupList.get(sectionNumber);

        if (mGridData == null)
            mGridData = tableGroup.getTables();

        List<Table> updatedTables = tableGroup.getTables();
        for (int i = 0; i < mGridData.size(); i++) {

            for (Table table : updatedTables) {
                if (table.getTableID() == mGridData.get(i).getTableID() && !table.getStatus().equals(mGridData.get(i).getStatus())) {
                    mGridData.get(i).setStatus(table.getStatus());
                    mGridData.get(i).setServerName(table.getServerName());
                    mGridData.get(i).setStatusChangeTime(table.getStatusChangeTime());
                }
            }
        }
        mGridAdapter.setGridData((ArrayList<Table>) mGridData);
        mGridAdapter.notifyDataSetChanged();
    }
}