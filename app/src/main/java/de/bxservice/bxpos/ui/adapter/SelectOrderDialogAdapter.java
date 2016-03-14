package de.bxservice.bxpos.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrder;

/**
 * Created by Diego Ruiz on 11/03/16.
 */
public class SelectOrderDialogAdapter extends RecyclerView.Adapter<SelectOrderDialogAdapter.JoinOrdersDialogViewHolder>
        implements View.OnClickListener {

    private ArrayList<POSOrder> mDataset;
    private View.OnClickListener listener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class JoinOrdersDialogViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtOrder;
        public TextView txtTable;

        public JoinOrdersDialogViewHolder(View v) {
            super(v);

            txtOrder = (TextView) itemView.findViewById(R.id.order_title);
            txtTable = (TextView) itemView.findViewById(R.id.table_name);
        }

        public void bindTable(POSOrder order) {
            txtOrder.setText(String.valueOf(order.getOrderId()));
            if (order.getTable() != null)
                txtTable.setText(order.getTable().getTableName());
            else
                txtTable.setText(itemView.getResources().getString(R.string.unset_table));
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SelectOrderDialogAdapter(ArrayList<POSOrder> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public JoinOrdersDialogViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.select_order_grid_item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        v.setOnClickListener(this);

        return new JoinOrdersDialogViewHolder(v);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null)
            listener.onClick(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(JoinOrdersDialogViewHolder holder, int position) {
        POSOrder order = mDataset.get(position);
        holder.bindTable(order);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}