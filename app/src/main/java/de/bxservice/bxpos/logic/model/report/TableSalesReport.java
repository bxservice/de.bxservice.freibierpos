package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.util.List;

import de.bxservice.bxpos.logic.DataProvider;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public class TableSalesReport extends Report {

    public TableSalesReport(Context mContext) {
        super(mContext);
    }

    /**
     * Get paid orders and display them by table afterwards
     * @return
     */
    @Override
    public void performReport() {
        new DataProvider(mContext).getPaidOrders(fromDate, toDate);
    }
}
