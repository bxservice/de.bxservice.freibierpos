package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.util.List;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public class TableSalesReport extends Report {

    public TableSalesReport(Context mContext) {
        super(mContext);
    }

    @Override
    public List<?> reportPerformed() {
        return null;
    }
}
