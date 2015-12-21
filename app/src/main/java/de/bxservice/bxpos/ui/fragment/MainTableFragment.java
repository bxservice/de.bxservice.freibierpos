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
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.model.Table;
import de.bxservice.bxpos.logic.model.TableGroup;
import de.bxservice.bxpos.ui.MainActivity;
import de.bxservice.bxpos.ui.adapter.GridTableViewAdapter;
import de.bxservice.bxpos.ui.adapter.TableGridItem;

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
    private ArrayList<TableGridItem> mGridData;

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

    public MainTableFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main_activity, container, false);

        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        grid = (GridView) rootView.findViewById(R.id.tableView);

        List<TableGroup> tableGroupList = DataMediator.getInstance().getTableGroupList();
        TableGroup tableGroup = tableGroupList.get(sectionNumber);

        mGridData = new ArrayList<>();

        TableGridItem item;
        for (Table table : tableGroup.getTables()) {
            item = new TableGridItem();
            item.setTitle(table.getTableName());
            item.setTable(table);
            //item.setImage(R.drawable.ic_local_dining_white_24dp);
            mGridData.add(item);
        }

        grid.setGravity(Gravity.CENTER_HORIZONTAL);

        mGridAdapter = new GridTableViewAdapter(this.getContext(), R.layout.table_grid_item_layout, mGridData);

        //ArrayAdapter<String> adp = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_dropdown_item_1line,list);
        grid.setAdapter(mGridAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //Get item at position
                TableGridItem item = (TableGridItem) parent.getItemAtPosition(position);

                ((MainActivity)getActivity()).setSelectedTable(item.getTable());
                ((MainActivity)getActivity()).showGuestNumberDialog();
            }
        });

        return rootView;
    }
}