package de.bxservice.bxpos.ui.adapter;

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

/**
 * Created by Diego Ruiz on 19/11/15.
 */
public class GridOrderViewAdapter extends ArrayAdapter<NewOrderGridItem> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<NewOrderGridItem> mGridData = new ArrayList<NewOrderGridItem>();

    public GridOrderViewAdapter(Context mContext, int layoutResourceId, ArrayList<NewOrderGridItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }

    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<NewOrderGridItem> mGridData) {
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
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_name);
            holder.priceTextView = (TextView) row.findViewById(R.id.grid_item_price);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        NewOrderGridItem item = mGridData.get(position);
        holder.titleTextView.setText(Html.fromHtml(item.getName()));
        holder.priceTextView.setText(Html.fromHtml(item.getPrice()));

        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        TextView priceTextView;
    }
}
