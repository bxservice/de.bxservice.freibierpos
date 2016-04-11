package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.daomanager.PosReportManagement;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public class TableSalesReport extends Report {

    private List<ReportGenericObject> tableSalesOrders;
    private ReportHtmlTemplate htmlTemplate;

    public TableSalesReport(Context mContext) {
        super(mContext);
        htmlTemplate = new ReportHtmlTemplate();
    }

    /**
     * Get paid orders and display them by table afterwards
     * @return
     */
    @Override
    protected void performReport() {
        tableSalesOrders = new PosReportManagement(mContext).getTableSalesReportRows(fromDate, toDate);
    }

    /**
     * Set the result with the html template
     */
    @Override
    protected void setReportResult() {

        htmlResult.append(htmlTemplate.getHtmlTemplate().replace(ReportHtmlTemplate.TITLE_TAG, name));
        if(tableSalesOrders != null && !tableSalesOrders.isEmpty()) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            //Here is to remove the € sign because it has problems in HTML
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormat).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) currencyFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            BigDecimal totalSold = BigDecimal.ZERO;
            int totalQty = 0;

            //Open HTML table
            htmlResult.append(htmlTemplate.getHtmlTableHeader());

            //Every iteration creates a table row
            for(ReportGenericObject genericObject : tableSalesOrders) {

                htmlResult.append(htmlTemplate.getHtmlRowOpen());

                totalSold = totalSold.add(genericObject.getAmount());
                totalQty = totalQty + Integer.parseInt(genericObject.getQuantity());

                htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, genericObject.getDescription()));
                htmlResult.append(htmlTemplate.getHtmlColumn("center").replace(ReportHtmlTemplate.ROW_TAG, genericObject.getQuantity()));
                htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, currencyFormat.format(genericObject.getAmount()).trim() + " &euro;"));

                htmlResult.append(htmlTemplate.getHtmlRowClose());
            }
            //Total row
            htmlResult.append(htmlTemplate.getHtmlRowOpen());

            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.total)));
            htmlResult.append(htmlTemplate.getHtmlColumn("center").replace(ReportHtmlTemplate.ROW_TAG, String.valueOf(totalQty)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, currencyFormat.format(totalSold).trim() + " &euro;"));

            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Close HTML table
            htmlResult.append(htmlTemplate.getHtmlTableClose());
        }
        else {
            htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.no_records)));
        }

    }
}
