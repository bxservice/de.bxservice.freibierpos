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
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.idempiere.Table;

/**
 * Created by Diego Ruiz on 18/11/15.
 */
public class GridTableViewAdapter extends ArrayAdapter<Table> {

    private Context mContext;
    private int layoutResourceId;
    private List<Table> mGridData = new ArrayList<>();

    public GridTableViewAdapter(Context mContext, int layoutResourceId, ArrayList<Table> mGridData) {
        super(mContext, layoutResourceId, mGridData);
        this.layoutResourceId = layoutResourceId;
        this.mContext = mContext;
        this.mGridData = mGridData;
    }


    /**
     * Updates grid data and refresh grid items.
     * @param newList
     */
    public void setGridData(ArrayList<Table> newList) {
       mGridData = newList;
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

        if (Table.BUSY_STATUS.equals(mGridData.get(position).getStatus())) {
            row.setBackgroundColor(Color.parseColor("#FFFB980D"));
            //holder.imageView.setImageResource(R.drawable.cutlery23_blank);
            //holder.titleTextView.setTextColor(Color.WHITE);
        } else {
            row.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        holder.titleTextView.setText(Html.fromHtml(mGridData.get(position).getTableName()));

        return row;
    }

    static class ViewHolder {
        TextView titleTextView;
        ImageView imageView;
    }

}
