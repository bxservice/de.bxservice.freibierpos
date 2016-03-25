package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public class ReportFactory {

    //This values have to match the values in string.xml
    private static final int SALES_CODE       = 0;
    private static final int VOID_ITEMS_CODE  = 1;
    private static final int TABLE_SALES_CODE = 2;

    private ArrayList<Report> reports = new ArrayList<>();

    public ReportFactory(Context mContext, String[] names, String[] values) {

        Report report = null;
        int reportValue;
        for(int i = 0; i < names.length; i++) {
            reportValue = Integer.parseInt(values[i]);

            switch (reportValue) {
                case SALES_CODE:
                    report = new SalesReport(mContext);
                    report.setCode(SALES_CODE);
                    report.setName(names[i]);
                    break;
                case VOID_ITEMS_CODE:
                    report = new VoidItemsReport(mContext);
                    report.setCode(VOID_ITEMS_CODE);
                    report.setName(names[i]);
                    break;
                case TABLE_SALES_CODE:
                    report = new TableSalesReport(mContext);
                    report.setCode(TABLE_SALES_CODE);
                    report.setName(names[i]);
                    break;
            }

            if(report != null)
                reports.add(report);
        }

    }

    public ArrayList<Report> getReports() {
        return reports;
    }

}
