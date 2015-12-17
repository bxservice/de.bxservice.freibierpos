package de.bxservice.bxpos.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.NewOrderGridItem;
import de.bxservice.bxpos.logic.model.POSOrder;
import de.bxservice.bxpos.logic.model.POSOrderLine;
import de.bxservice.bxpos.ui.EditOrderActivity;
import de.bxservice.bxpos.ui.RecyclerOrderingItemsListener;
import de.bxservice.bxpos.ui.adapter.OnDataChangeListener;
import de.bxservice.bxpos.ui.adapter.OrderingLineAdapter;
import de.bxservice.bxpos.ui.adapter.SimpleItemTouchHelperCallback;
import de.bxservice.bxpos.ui.decorator.DividerItemDecoration;

/**
 * Created by Diego Ruiz on 10/12/15.
 */
public class OrderingItemsFragment extends Fragment {

    private static final String ARG_ORDER = "related_order";

    private POSOrder order;

    private RecyclerView mRecyclerView;
    private OrderingLineAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OrderingItemsFragment newInstance(POSOrder order) {
        OrderingItemsFragment fragment = new OrderingItemsFragment();
        Bundle args = new Bundle();

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

        order = (POSOrder) getArguments().getSerializable(ARG_ORDER);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(mLayoutManager);

        ArrayList<POSOrderLine> myDataset = new ArrayList<>();


        for(POSOrderLine orderLine : order.getOrderLines()) {

            if (orderLine.getLineStatus().equals(POSOrderLine.ORDERING)) {
                myDataset.add(orderLine);
            }
        }

        // specify an adapter (and its listener)
        mAdapter = new OrderingLineAdapter(myDataset);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL_LIST));

        mRecyclerView.addOnItemTouchListener(
                new RecyclerOrderingItemsListener(getActivity().getBaseContext(), new RecyclerOrderingItemsListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO Handle item click
                        POSOrderLine selectedItem = mAdapter.getSelectedItem(position);

                        System.out.println("clicked" + selectedItem.getProduct().getProductName());
                    }
                })
        );

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        mAdapter.setOnDataChangeListener(new OnDataChangeListener() {
            public void onDataChanged(int position) {
            }

            @Override
            public void onItemDeleted(int position) {
                ((EditOrderActivity) getActivity()).removeItem(position);
            }

            @Override
            public void onItemAdded(int position, Object object) {
                ((EditOrderActivity) getActivity()).addItem(position, (POSOrderLine) object);
            }
        });

        return rootView;
    }

}