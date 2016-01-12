package de.bxservice.bxpos.ui.adapter;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;

/**
 * Created by Diego Ruiz on 8/12/15.
 */

public class OrderingLineAdapter extends RecyclerView.Adapter<OrderingLineAdapter.OrderingLineViewHolder>
        implements View.OnClickListener, ItemTouchHelperAdapter {

    private ArrayList<POSOrderLine> mDataset;
    private View mainLayout;
    private SparseBooleanArray selectedItems;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class OrderingLineViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        // each data item is just a string in this case
        public TextView txtQty;
        public TextView txtProductName;
        public TextView txtPrice;

        public OrderingLineViewHolder(View v) {
            super(v);

            txtQty         = (TextView) itemView.findViewById(R.id.lblQty);
            txtProductName = (TextView) itemView.findViewById(R.id.lblName);
            txtPrice = (TextView) itemView.findViewById(R.id.lblpriceline);

        }

        public void bindOrderLine(POSOrderLine orderLine) {
            txtQty.setText(String.valueOf(orderLine.getQtyOrdered()));
            txtProductName.setText(orderLine.getProduct().getProductName());
            txtPrice.setText(orderLine.getLineTotalAmt());
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            TypedValue outValue = new TypedValue();
            itemView.getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            itemView.setBackgroundResource(outValue.resourceId);
        }
    }

    /**
     * An item changes its selection state
     * @param pos
     */
    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        }
        else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<Integer>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public OrderingLineAdapter(ArrayList<POSOrderLine> myDataset) {
        mDataset = myDataset;
        selectedItems = new SparseBooleanArray();
    }

    public POSOrderLine getSelectedItem(int position) {
        return mDataset.get(position);
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

        mainLayout = (View) parent.getParent().getParent().getParent();

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

    @Override
    public void onItemDismiss(final int position) {

        final POSOrderLine orderLine = mDataset.get(position);
        Snackbar snackbar = Snackbar
                .make(mainLayout, mainLayout.getResources().getString(R.string.item_removed), Snackbar.LENGTH_LONG)
                .setAction(mainLayout.getResources().getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDataset.add(position, orderLine);
                        notifyItemInserted(position);
                        if(mOnDataChangeListener != null){
                            mOnDataChangeListener.onItemAdded(position, orderLine);
                        }
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();

        mDataset.remove(position);
        notifyItemRemoved(position);

        if(mOnDataChangeListener != null){
            mOnDataChangeListener.onItemDeleted(position);
        }

    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        /*if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(mDataset, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(mDataset, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);*/
    }

    OnDataChangeListener mOnDataChangeListener;

    public void setOnDataChangeListener(OnDataChangeListener onDataChangeListener){
        mOnDataChangeListener = onDataChangeListener;
    }
}