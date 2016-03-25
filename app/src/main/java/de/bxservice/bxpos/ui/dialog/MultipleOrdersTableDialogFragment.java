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
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.ui.RecyclerItemsListener;
import de.bxservice.bxpos.ui.adapter.MultipleOrderTableDialogAdapter;
import de.bxservice.bxpos.ui.decorator.DividerItemDecoration;

/**
 * Created by Diego Ruiz on 21/03/16.
 */
public class MultipleOrdersTableDialogFragment extends DialogFragment {



    public interface MultipleOrdersTableDialogListener {
        void onDialogPositiveClick(MultipleOrdersTableDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private MultipleOrdersTableDialogListener mListener;
    private ArrayList<POSOrder> mGridData;
    private POSOrder selectedOrder;
    private RecyclerView recyclerView;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.select_order_table_dialog, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.multiple_orders_list);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getBaseContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), DividerItemDecoration.VERTICAL_LIST));

        final MultipleOrderTableDialogAdapter mGridAdapter = new MultipleOrderTableDialogAdapter(mGridData);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemsListener(getActivity().getBaseContext(), recyclerView, new RecyclerItemsListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        selectedOrder = mGridData.get(position);
                        mListener.onDialogPositiveClick(MultipleOrdersTableDialogFragment.this);
                        MultipleOrdersTableDialogFragment.this.getDialog().dismiss();
                    }

                    @Override
                    public void onItemLongClick(View view, int position) {
                    }

                })
        );

        recyclerView.setAdapter(mGridAdapter);

        builder.setTitle(R.string.select_order);
        builder.setView(view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MultipleOrdersTableDialogFragment.this.getDialog().cancel();
                    }
                });

        // Create the AlertDialog object and return it
        return builder.create();
    }

    public POSOrder getOrder() {
        return selectedOrder;
    }

    public void setTableOrders(List<POSOrder> orders) {
        mGridData = new ArrayList<>(orders);
    }

    // Override the Fragment.onAttach() method to instantiate the GuestNumberDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (MultipleOrdersTableDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement MultipleOrdersTableDialogListener");
        }
    }

}
