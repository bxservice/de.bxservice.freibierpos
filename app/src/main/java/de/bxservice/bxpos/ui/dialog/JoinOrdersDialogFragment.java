package de.bxservice.bxpos.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.ui.RecyclerItemsListener;
import de.bxservice.bxpos.ui.adapter.JoinOrdersDialogAdapter;

/**
 * Created by Diego Ruiz on 11/03/16.
 */
public class JoinOrdersDialogFragment extends DialogFragment {



    public interface JoinOrdersDialogListener {
        void onDialogPositiveClick(JoinOrdersDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    JoinOrdersDialogListener mListener;
    private ArrayList<POSOrder> mGridData;
    private RecyclerView recyclerView;
    private POSOrder order;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.join_orders_dialog, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.open_order_list);

        // use a grid layout manager with 2 columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity().getBaseContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);

        initGridData();

        JoinOrdersDialogAdapter mGridAdapter = new JoinOrdersDialogAdapter(mGridData);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemsListener(getActivity().getBaseContext(), recyclerView, new RecyclerItemsListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        order = mGridData.get(position);
                        mListener.onDialogPositiveClick(JoinOrdersDialogFragment.this);
                        JoinOrdersDialogFragment.this.getDialog().dismiss();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }

                })
        );

        recyclerView.setAdapter(mGridAdapter);

        builder.setTitle(R.string.join_orders);
        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        JoinOrdersDialogFragment.this.getDialog().cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * init the grid data with all the tables
     */
    private void initGridData() {
        DataProvider dataProvider = new DataProvider(getActivity().getBaseContext());
        //If the order is != null don't add it to the array. Avoid join an order with itself
        if (order != null) {
            mGridData = new ArrayList<>();
            for (POSOrder currentOrder : dataProvider.getAllOpenOrders()) {
                if(order.getOrderId() != currentOrder.getOrderId())
                    mGridData.add(currentOrder);
            }
        } else {
            mGridData = new ArrayList<>(dataProvider.getAllOpenOrders());
        }
    }

    public POSOrder getOrder() {
        return order;
    }

    public void setOrder(POSOrder order) {
        this.order = order;
    }

    // Override the Fragment.onAttach() method to instantiate the GuestNumberDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (JoinOrdersDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement JoinOrdersDialogListener");
        }
    }

}
