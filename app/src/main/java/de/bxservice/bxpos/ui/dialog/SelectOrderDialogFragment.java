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
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.ui.RecyclerItemsListener;
import de.bxservice.bxpos.ui.adapter.SelectOrderDialogAdapter;

/**
 * Dialog fragment used to select another order
 * it is used in join and split options
 * Created by Diego Ruiz on 11/03/16.
 */
public class SelectOrderDialogFragment extends DialogFragment {

    private static final String IS_JOIN = "isJoin";
    private static final String ORDER = "order";

    public interface SelectOrderDialogListener {
        void onDialogPositiveClick(SelectOrderDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    private SelectOrderDialogListener mListener;
    private ArrayList<POSOrder> mGridData;
    private RecyclerView recyclerView;
    private POSOrder order;
    private boolean isJoin = true; //flag to check if it is call to join or split

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //On rotation screen
        if (savedInstanceState != null) {
            isJoin = savedInstanceState.getBoolean(IS_JOIN);
            order = (POSOrder) savedInstanceState.getSerializable(ORDER);
        }

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.select_order_dialog, null);

        recyclerView = (RecyclerView) view.findViewById(R.id.open_order_list);

        // use a grid layout manager with 2 columns
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity().getBaseContext(), 3);
        recyclerView.setLayoutManager(mLayoutManager);

        initGridData();

        SelectOrderDialogAdapter mGridAdapter = new SelectOrderDialogAdapter(mGridData);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemsListener(getActivity().getBaseContext(), recyclerView, new RecyclerItemsListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        order = mGridData.get(position);
                        mListener.onDialogPositiveClick(SelectOrderDialogFragment.this);
                        SelectOrderDialogFragment.this.getDialog().dismiss();
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
                        SelectOrderDialogFragment.this.getDialog().cancel();
                    }
                });

        if(!isJoin)
            builder.setPositiveButton(R.string.new_order, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            order = null;
                            mListener.onDialogPositiveClick(SelectOrderDialogFragment.this);
                            SelectOrderDialogFragment.this.getDialog().dismiss();
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
            mGridData = new ArrayList<>();
            for (POSOrder currentOrder : POSOrder.getOpenOrders(getActivity().getBaseContext())) {
                if(order.getOrderId() != currentOrder.getOrderId())
                    mGridData.add(currentOrder);
            }
        } else {
            mGridData = new ArrayList<>(POSOrder.getOpenOrders(getActivity().getBaseContext()));
        }
    }

    public POSOrder getOrder() {
        return order;
    }

    public void setOrder(POSOrder order) {
        this.order = order;
    }

    public boolean isJoin() {
        return isJoin;
    }

    public void setIsJoin(boolean isJoin) {
        this.isJoin = isJoin;
    }

    // Override the Fragment.onAttach() method to instantiate the GuestNumberDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SelectOrderDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SelectOrderDialogListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_JOIN, isJoin);
        outState.putSerializable(ORDER, order);
    }

}
