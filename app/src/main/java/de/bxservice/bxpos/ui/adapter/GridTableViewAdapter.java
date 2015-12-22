package de.bxservice.bxpos.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.idempiere.Table;

/**
 * Created by Diego Ruiz on 18/11/15.
 */
public class GridTableViewAdapter extends ArrayAdapter<TableGridItem> {

    private Context mContext;
    private int layoutResourceId;
    private ArrayList<TableGridItem> mGridData = new ArrayList<TableGridItem>();

    public GridTableViewAdapter(Context mContext, int layoutResourceId, ArrayList<TableGridItem> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Updates grid data and refresh grid items.
     * @param mGridData
     */
    public void setGridData(ArrayList<TableGridItem> mGridData) {
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
            holder.titleTextView = (TextView) row.findViewById(R.id.grid_item_title);
            holder.imageView = (ImageView) row.findViewById(R.id.grid_item_image);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        TableGridItem item = mGridData.get(position);
        if (Table.BUSY_STATUS.equals(item.getTable().getStatus())) {
            row.setBackgroundColor(Color.parseColor("#FFFB980D"));
            //holder.imageView.setImageResource(R.drawable.cutlery23_blank);
            //holder.titleTextView.setTextColor(Color.WHITE);
        }

        holder.titleTextView.setText(Html.fromHtml(item.getTitle()));

        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }

}
