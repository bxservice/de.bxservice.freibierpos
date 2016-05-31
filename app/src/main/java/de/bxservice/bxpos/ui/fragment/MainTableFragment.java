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
import de.bxservice.bxpos.logic.daomanager.PosOrderManagement;
import de.bxservice.bxpos.logic.daomanager.PosTableGroupManagement;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
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
    private GridView grid;
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

        grid = (GridView) rootView.findViewById(R.id.tableView);

        PosTableGroupManagement tableGroupManager = new PosTableGroupManagement(getActivity().getBaseContext());

        List<TableGroup> tableGroupList = tableGroupManager.getAllTableGroups();
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

                if (item.getStatus().equals(Table.FREE_STATUS))
                    ((MainActivity) getActivity()).showGuestNumberDialog();
                else if (item.getStatus().equals(Table.BUSY_STATUS)) {
                    PosOrderManagement orderManager = new PosOrderManagement(getActivity().getBaseContext());
                    ((MainActivity) getActivity()).editOrder(orderManager.getTableOrders(item));
                }
            }
        });

        return rootView;
    }

    public void updateTableStatus(int position, Table table) {
        mGridData.get(position).setStatus(table.getStatus());
        mGridData.get(position).setServerName(table.getServerName());
        mGridAdapter.setGridData((ArrayList<Table>) mGridData);
        mGridAdapter.notifyDataSetChanged();
    }
}