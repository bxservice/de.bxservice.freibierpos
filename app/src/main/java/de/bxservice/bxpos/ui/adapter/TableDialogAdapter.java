package de.bxservice.bxpos.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.idempiere.Table;

/**
 * Created by Diego Ruiz on 10/03/16.
 */
public class TableDialogAdapter extends RecyclerView.Adapter<TableDialogAdapter.TableDialogViewHolder>
        implements View.OnClickListener {

    private ArrayList<Table> mDataset;
    private View.OnClickListener listener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class TableDialogViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtTable;

        public TableDialogViewHolder(View v) {
            super(v);

            txtTable = (TextView) itemView.findViewById(R.id.table_title);
        }

        public void bindTable(Table table) {
            txtTable.setText(String.valueOf(table.getTableName()));
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TableDialogAdapter(ArrayList<Table> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TableDialogViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.switch_table_grid_item_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters

        v.setOnClickListener(this);

        return new TableDialogViewHolder(v);
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
    public void onBindViewHolder(TableDialogViewHolder holder, int position) {
        Table table = mDataset.get(position);
        holder.bindTable(table);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
