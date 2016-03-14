package de.bxservice.bxpos.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.ui.RecyclerItemsListener;
import de.bxservice.bxpos.ui.adapter.OrderingLineAdapter;

/**
 * Created by Diego Ruiz on 14/03/16.
 */
public class SplitOrderDialogFragment extends DialogFragment {



    public interface SplitOrderDialogListener {
        void onDialogPositiveClick(SplitOrderDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    SplitOrderDialogListener mListener;
    private ArrayList<POSOrderLine> mGridData;
    private RecyclerView recyclerView;
    private POSOrder order;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.split_order_dialog, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.ordered_items_list);

        // use a grid layout manager with 2 columns
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        initGridData();

        OrderingLineAdapter mGridAdapter = new OrderingLineAdapter(mGridData);

        /*recyclerView.addOnItemTouchListener(
                new RecyclerItemsListener(getActivity().getBaseContext(), recyclerView, new RecyclerItemsListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //order = mGridData.get(position);
                        mListener.onDialogPositiveClick(SplitOrderDialogFragment.this);
                        SplitOrderDialogFragment.this.getDialog().dismiss();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }

                })
        );*/

        recyclerView.setAdapter(mGridAdapter);

        builder.setTitle(R.string.split_order);
        builder.setView(view)
                .setPositiveButton(R.string.split, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //note = remarkNote.getText().toString();
                        mListener.onDialogPositiveClick(SplitOrderDialogFragment.this);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SplitOrderDialogFragment.this.getDialog().cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    /**
     * init the grid data with all the tables
     */
    private void initGridData() {
        //If the order is != null don't add it to the array. Avoid join an order with itself
        if (order != null) {
            mGridData = order.getOrderedLines();
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
            mListener = (SplitOrderDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SplitOrderDialogListener");
        }
    }

}
