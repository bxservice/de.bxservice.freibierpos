package de.bxservice.bxpos.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.model.Table;
import de.bxservice.bxpos.logic.model.TableGroup;
import de.bxservice.bxpos.ui.MainActivity;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 * Created by Diego Ruiz on 18/11/15.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    DataMediator dataProvider = DataMediator.getInstance();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a FoodMenuFragment (defined as a static inner class below).
        return MainTableFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return dataProvider.getTableGroupList().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return dataProvider.getTableGroupList().get(position).getName();
    }


    /**
     * Table Fragment
     */
    public static class MainTableFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        List<String> list;
        GridView grid;
        GridTableViewAdapter mGridAdapter;
        ArrayList<GridItem> mGridData;

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
            list=new ArrayList<String>();

            GridItem item;
            for ( Table table : tableGroup.getTables() ){
                item = new GridItem();
                item.setTitle(table.getTableName());
                //item.setImage(R.drawable.ic_local_dining_white_24dp);
                mGridData.add(item);
            }
                //list.add(table.getTableName());


            grid.setGravity(Gravity.CENTER_HORIZONTAL);

            mGridAdapter = new GridTableViewAdapter(this.getContext(), R.layout.table_grid_item_layout, mGridData);

            //ArrayAdapter<String> adp = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_dropdown_item_1line,list);
            grid.setAdapter(mGridAdapter);

            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                    //Get item at position
                    GridItem item = (GridItem) parent.getItemAtPosition(position);

                    ((MainActivity)getActivity()).setSelectedTable(item.getTitle());
                    ((MainActivity)getActivity()).showGuestNumberDialog();
                }
            });

            return rootView;
        }
    }

}

