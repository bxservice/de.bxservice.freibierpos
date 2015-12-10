package de.bxservice.bxpos.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.POSOrder;
import de.bxservice.bxpos.logic.model.POSOrderLine;
import de.bxservice.bxpos.ui.adapter.OrderLineAdapter;
import de.bxservice.bxpos.ui.decorator.DividerItemDecoration;

/**
 * Created by Diego Ruiz on 10/12/15.
 */
public class OrderingItemsFragment extends Fragment {

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String ARG_ORDER          = "related_order";

    POSOrder order;

    private RecyclerView mRecyclerView;
    private OrderLineAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OrderingItemsFragment newInstance(int sectionNumber, POSOrder order) {
        OrderingItemsFragment fragment = new OrderingItemsFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        args.putSerializable(ARG_ORDER, order);

        fragment.setArguments(args);
        return fragment;
    }

    public OrderingItemsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_order, container, false);


        int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);

        order = (POSOrder) getArguments().getSerializable(ARG_ORDER);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<POSOrderLine> myDataset = new ArrayList<>();

        if( sectionNumber == 0 ) {

            for( POSOrderLine orderLine : order.getOrderLines()){

                if ( orderLine.getLineStatus().equals(POSOrderLine.ORDERING) ) {
                    myDataset.add(orderLine);
                }
            }

        }
        else if ( sectionNumber == 1 ) {

            for( POSOrderLine orderLine : order.getOrderLines()){

                if ( orderLine.getLineStatus().equals(POSOrderLine.ORDERED) ) {
                    myDataset.add(orderLine);
                }
            }
        }

        // specify an adapter (and its listener)
        mAdapter = new OrderLineAdapter(myDataset);

        mAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("DemoRecView", "Pulsado el elemento " + mRecyclerView.getChildAdapterPosition(v));
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL_LIST));


        return rootView;
    }

}