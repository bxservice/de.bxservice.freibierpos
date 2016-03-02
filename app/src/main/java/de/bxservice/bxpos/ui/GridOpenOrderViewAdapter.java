package de.bxservice.bxpos.ui;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.OpenOrderGridItem;

/**
 * Created by Diego Ruiz on 27/10/15.
 */
public class GridOpenOrderViewAdapter extends ArrayAdapter<OpenOrderGridItem> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<OpenOrderGridItem> mGridData = new ArrayList<>();

    public GridOpenOrderViewAdapter(Context mContext, int layoutResourceId, ArrayList<OpenOrderGridItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<OpenOrderGridItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.orderNumberTextView = (TextView) row.findViewById(R.id.order_number);
            holder.tableView = (TextView) row.findViewById(R.id.order_table);
            holder.totalView = (TextView) row.findViewById(R.id.order_total);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        OpenOrderGridItem item = mGridData.get(position);
        holder.orderNumberTextView.setText(Html.fromHtml(item.getOrderNo()));
        holder.tableView.setText(Html.fromHtml(item.getTable()));
        holder.totalView.setText(Html.fromHtml(item.getPrice()));

        return row;
    }

    static class ViewHolder {
        TextView orderNumberTextView;
        TextView tableView;
        TextView totalView;
    }
}
