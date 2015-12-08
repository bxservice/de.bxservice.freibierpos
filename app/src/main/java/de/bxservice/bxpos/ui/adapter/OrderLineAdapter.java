package de.bxservice.bxpos.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.POSOrderLine;

/**
 * Created by Diego Ruiz on 8/12/15.
 */

public class OrderLineAdapter extends RecyclerView.Adapter<OrderLineAdapter.OrderLineViewHolder> {

    private ArrayList<POSOrderLine> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class OrderLineViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtQty;
        public TextView txtProductName;
        public TextView txtPtice;

        public OrderLineViewHolder(View v) {
            super(v);

            txtQty         = (TextView) itemView.findViewById(R.id.lblQty);
            txtProductName = (TextView) itemView.findViewById(R.id.lblName);
            txtPtice       = (TextView) itemView.findViewById(R.id.lblpriceline);

        }

        public void bindOrderLine(POSOrderLine orderLine) {
            txtQty.setText(String.valueOf(orderLine.getQtyOrdered()));
            txtProductName.setText(orderLine.getProduct().getProductName());
            txtPtice.setText(orderLine.getLineNetAmt().toString()); //TODO; add currency format
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderLineAdapter(ArrayList<POSOrderLine> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public OrderLineViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ordering_items, parent, false);
        // set the view's size, margins, paddings and layout parameters

        OrderLineViewHolder vh = new OrderLineViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(OrderLineViewHolder holder, int position) {
        POSOrderLine orderLine = mDataset.get(position);

        holder.bindOrderLine(orderLine);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}