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
package de.bxservice.bxpos.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.PosProperties;
import de.bxservice.bxpos.ui.adapter.ReviewOrderLinesAdapter;
import de.bxservice.bxpos.ui.decorator.DividerItemDecoration;

public class ReviewOrderFragment extends Fragment {

    public static final String REVIEWED_ORDER = "REVIEW_ORDER";

    private POSOrder order;

    private TextView receiptTextView;
    private TextView tableTextView;
    private TextView subtotalTextView;
    private TextView serverTextView;
    private TextView dateTextView;
    private TextView totalTextView;
    private TextView totalAmtTextView;
    private TextView subtotalAmtTextView;
    private TextView vatTextView;
    private TextView vatAmtTextView;
    private TextView vat2TextView;
    private TextView vat2AmtTextView;
    private TextView surchargeTextView;
    private TextView surchargeAmtTextView;
    private TextView discountTextView;
    private TextView discountAmtTextView;

    public ReviewOrderFragment() {
    }

    public static ReviewOrderFragment newInstance(POSOrder order) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(ReviewOrderFragment.REVIEWED_ORDER, order);

        ReviewOrderFragment fragment = new ReviewOrderFragment();
        fragment.setArguments(arguments);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_review_closed_order, container, false);

        Bundle arguments = getArguments();

        if (arguments == null) {
            return view;
        }

        order = (POSOrder) arguments.getSerializable(REVIEWED_ORDER);

        //Header
        receiptTextView = (TextView) view.findViewById(R.id.receipt_textview);
        tableTextView = (TextView) view.findViewById(R.id.table_textview);
        serverTextView = (TextView) view.findViewById(R.id.server_textview);
        dateTextView = (TextView) view.findViewById(R.id.date_textView);

        //Footer
        totalTextView = (TextView) view.findViewById(R.id.total_textView);
        totalAmtTextView = (TextView) view.findViewById(R.id.total_amount_textview);
        subtotalTextView = (TextView) view.findViewById(R.id.subtotal_textview);
        subtotalAmtTextView = (TextView) view.findViewById(R.id.subtotal_amount_textView);
        vatTextView = (TextView) view.findViewById(R.id.vat_textview);
        vatAmtTextView = (TextView) view.findViewById(R.id.vat_amount_textView);
        vat2TextView = (TextView) view.findViewById(R.id.vat2_textview);
        vat2AmtTextView = (TextView) view.findViewById(R.id.vat2_amount_textView);
        surchargeTextView = (TextView) view.findViewById(R.id.surcharge_textview);
        surchargeAmtTextView = (TextView) view.findViewById(R.id.surcharge_amount_textView);
        discountTextView = (TextView) view.findViewById(R.id.discount_textview);
        discountAmtTextView = (TextView) view.findViewById(R.id.discount_amount_textView);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.ordered_lines);
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));

        ReviewOrderLinesAdapter adapter = new ReviewOrderLinesAdapter(order.getOrderedLines());

        recyclerView.setAdapter(adapter);

        fillHeaderDisplay();
        fillFooterDisplay();

        // Inflate the layout for this fragment
        return view;
    }

    private void fillHeaderDisplay() {

        receiptTextView.setText(getString(R.string.order_no, order.getDocumentNo()));
        serverTextView.setText(getString(R.string.waiter_role) + ": " + order.getServerName(getContext()));
        tableTextView.setText(order.getTable() != null ? getString(R.string.table) + ": " + order.getTable().getTableName() : getString(R.string.unset_table));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm", PosProperties.getInstance().getLocale());
        String parsedDate = "";
        try {
            Date orderDate = dateFormat.parse(Long.toString(order.getOrderDate(getContext())));
            DateFormat format = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, PosProperties.getInstance().getLocale());
            parsedDate = format.format(orderDate);
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        dateTextView.setText(parsedDate);
    }

    private void fillFooterDisplay() {

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(PosProperties.getInstance().getLocale());
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);

        totalTextView.setText(getString(R.string.total));
        totalAmtTextView.setText(currencyFormat.format(order.getTotallines().add(order.getSurcharge())));
        subtotalTextView.setText(getString(R.string.net_label));
        subtotalAmtTextView.setText(currencyFormat.format(order.getTotallines().subtract(order.getTotalTaxes())));

        if (order.getTaxRates().size() == 1) {
            vat2TextView.setVisibility(View.GONE);
            vat2AmtTextView.setVisibility(View.GONE);
        }

        int taxNo = 1;
        for (Map.Entry<Integer, BigDecimal> entry : order.getTaxRates().entrySet()) {
            if (taxNo == 1) {
                vatTextView.setText(getString(R.string.tax_label) + " " + entry.getKey() + " %");
                vatAmtTextView.setText(currencyFormat.format(entry.getValue()));
            } else {
                vat2TextView.setText(getString(R.string.tax_label) + " " + entry.getKey() + " %");
                vat2AmtTextView.setText(currencyFormat.format(entry.getValue()));
            }
            taxNo++;
        }

        surchargeTextView.setText(getString(R.string.set_extra));
        surchargeAmtTextView.setText(currencyFormat.format(order.getSurcharge()));
        discountTextView.setText(getString(R.string.add_discount));
        discountAmtTextView.setText(currencyFormat.format(order.getDiscount()));
    }

    public POSOrder getOrder() {
        return order;
    }

}
