package de.bxservice.bxpos.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.NewOrderGridItem;

/**
 * Created by Diego Ruiz on 9/12/15.
 */
public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.SearchItemViewHolder> implements View.OnClickListener {

    private ArrayList<NewOrderGridItem> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class SearchItemViewHolder extends RecyclerView.ViewHolder {

        public TextView txtProductName;
        public TextView txtPtice;

        public SearchItemViewHolder(View v) {
            super(v);

            txtProductName = (TextView) itemView.findViewById(android.R.id.text1);
            txtPtice       = (TextView) itemView.findViewById(android.R.id.text2);

        }

        public void bindSearchItem(NewOrderGridItem item) {
            txtProductName.setText(item.getName());
            txtPtice.setText(item.getPrice());
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SearchItemAdapter(ArrayList<NewOrderGridItem> myDataset) {
        mDataset = myDataset;
    }

    private View.OnClickListener listener;

    // Create new views (invoked by the layout manager)
    @Override
    public SearchItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.two_line_list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        v.setOnClickListener(this);

        SearchItemViewHolder vh = new SearchItemViewHolder(v);
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
    public void onBindViewHolder(SearchItemViewHolder holder, int position) {
        NewOrderGridItem orderLine = mDataset.get(position);

        holder.bindSearchItem(orderLine);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
