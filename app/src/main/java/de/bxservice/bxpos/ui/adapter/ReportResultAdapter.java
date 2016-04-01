package de.bxservice.bxpos.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.util.ArrayList;

import de.bxservice.bxpos.R;

/**
 * Created by Diego Ruiz on 30/03/16.
 */
public class ReportResultAdapter extends RecyclerView.Adapter<ReportResultAdapter.ReportResultViewHolder> {

    private ArrayList<String> mDataset;
    public static class ReportResultViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public WebView txtResult;

        public ReportResultViewHolder(View v) {
            super(v);

            txtResult = (WebView) itemView.findViewById(R.id.result);
        }

        public void bindTable(String result) {
            //txtResult.setText(Html.fromHtml(result));
            txtResult.loadData(result, "text/html", null);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ReportResultAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ReportResultViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.report_result_item_layout, parent, false);

        return new ReportResultViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ReportResultViewHolder holder, int position) {
        String result = mDataset.get(position);
        holder.bindTable(result);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}