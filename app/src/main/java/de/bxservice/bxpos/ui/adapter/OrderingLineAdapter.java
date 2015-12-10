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

public class OrderingLineAdapter extends RecyclerView.Adapter<OrderingLineAdapter.OrderingLineViewHolder>
        implements View.OnClickListener {

    private ArrayList<POSOrderLine> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class OrderingLineViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtQty;
        public TextView txtProductName;
        public TextView txtPtice;

        public OrderingLineViewHolder(View v) {
            super(v);

            txtQty         = (TextView) itemView.findViewById(R.id.lblQty);
            txtProductName = (TextView) itemView.findViewById(R.id.lblName);
            txtPtice       = (TextView) itemView.findViewById(R.id.lblpriceline);

        }

        public void bindOrderLine(POSOrderLine orderLine) {
            txtQty.setText(String.valueOf(orderLine.getQtyOrdered()));
            txtProductName.setText(orderLine.getProduct().getProductName());
            txtPtice.setText(orderLine.getLineTotalAmt());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderingLineAdapter(ArrayList<POSOrderLine> myDataset) {
        mDataset = myDataset;
    }

    private View.OnClickListener listener;

    // Create new views (invoked by the layout manager)
    @Override
    public OrderingLineViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ordering_items, parent, false);
        // set the view's size, margins, paddings and layout parameters

        v.setOnClickListener(this);

        OrderingLineViewHolder vh = new OrderingLineViewHolder(v);
        return vh;
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(OrderingLineViewHolder holder, int position) {
        POSOrderLine orderLine = mDataset.get(position);

        holder.bindOrderLine(orderLine);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}