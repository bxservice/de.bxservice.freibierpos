/**********************************************************************
 * This file is part of Freibier POS                                   *
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.PosProperties;
import de.bxservice.bxpos.ui.adapter.ReviewOrderLinesAdapter;
import de.bxservice.bxpos.ui.decorator.DividerItemDecoration;

public class CreateOrderDetailFragment extends Fragment {

    public static final String ORDER = "DETAIL_ORDER";

    private POSOrder mOrder;

    private RecyclerView mRecyclerView;
    private ReviewOrderLinesAdapter mAdapter;

    private TextView receiptTextView;
    private TextView tableTextView;
    private TextView subtotalTextView;
    private TextView qtyTextView;
    private TextView qtyValueTextView;
    private TextView subtotalAmtTextView;

    public CreateOrderDetailFragment() {
    }

    public static CreateOrderDetailFragment newInstance(POSOrder order) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ORDER, order);

        CreateOrderDetailFragment fragment = new CreateOrderDetailFragment();
        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_order_detail, container, false);

        Bundle arguments = getArguments();

        if (arguments == null) {
            return view;
        }

        mOrder = (POSOrder) arguments.getSerializable(ORDER);

        if (mOrder != null) {
            //Header
            receiptTextView = (TextView) view.findViewById(R.id.order_textview);
            tableTextView = (TextView) view.findViewById(R.id.table_textview);

            //Footer
            qtyTextView = (TextView) view.findViewById(R.id.qty_textview);
            qtyValueTextView = (TextView) view.findViewById(R.id.qty_value_textView);
            subtotalTextView = (TextView) view.findViewById(R.id.subtotal_textview);
            subtotalAmtTextView = (TextView) view.findViewById(R.id.subtotal_amount_textView);

            mRecyclerView = (RecyclerView) view.findViewById(R.id.ordering_lines);
            mRecyclerView.setHasFixedSize(true);

            // use a linear layout manager
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(mLayoutManager);

            mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

            View topSeparator = view.findViewById(R.id.top_separator);
            View footSeparator = view.findViewById(R.id.foot_separator);

            topSeparator.setVisibility(View.VISIBLE);
            footSeparator.setVisibility(View.VISIBLE);

            setupRecyclerView();
            fillHeaderDisplay();
            fillFooterDisplay();
        }

        // Inflate the layout for this fragment
        return view;
    }

    private void setupRecyclerView() {
        mAdapter = new ReviewOrderLinesAdapter(mOrder.getOrderingLines());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void fillHeaderDisplay() {
        receiptTextView.setText(getString(R.string.order_no, mOrder.getDocumentNo()));
        tableTextView.setText(mOrder.getTable() != null ? getString(R.string.table) + ": " + mOrder.getTable().getTableName() : getString(R.string.unset_table));
    }

    private void fillFooterDisplay() {
        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();

        qtyTextView.setText(getString(R.string.subtotal));
        subtotalTextView.setText(getString(R.string.quantity));
        qtyValueTextView.setText(currencyFormat.format(mOrder.getTotalOrderinglines()));
        subtotalAmtTextView.setText(String.valueOf(mOrder.getOrderingQty()));

    }

}
