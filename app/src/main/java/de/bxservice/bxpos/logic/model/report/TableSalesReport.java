package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.DataProvider;

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
    public void performReport() {
        tableSalesOrders = new DataProvider(mContext).getTableSalesReportRows(fromDate, toDate);
        setReportResult();
    }

    /**
     * Set the result with the html template
     */
    private void setReportResult() {

        htmlResult.append(htmlTemplate.getHtmlTemplate().replace(ReportHtmlTemplate.TITLE_TAG, name));
        if(tableSalesOrders != null && !tableSalesOrders.isEmpty()) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            //Here is to remove the € sign because it has problems in HTML
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormat).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) currencyFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            BigDecimal totalVoided = BigDecimal.ZERO;
            int totalQty = 0;

            String tableContent = htmlTemplate.getHtmlTable(tableSalesOrders.size());

            int i = 0;
            for(ReportGenericObject genericObject : tableSalesOrders) {
                totalVoided = totalVoided.add(genericObject.getAmount());
                totalQty = totalQty + Integer.parseInt(genericObject.getQuantity());

                tableContent = tableContent.replace(ReportHtmlTemplate.ROW_TAG + i, genericObject.getDescription());
                i = i+1;
                tableContent = tableContent.replace(ReportHtmlTemplate.ROW_TAG + i, genericObject.getQuantity());
                i = i+1;
                tableContent = tableContent.replace(ReportHtmlTemplate.ROW_TAG + i, currencyFormat.format(genericObject.getAmount()).trim());
                i = i+1;

                /*htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, "<p style=\"text-align:center;\">" +
                                        "<span style=\"float:left;\">"  + genericObject.getDescription()  + "</span>"
                        + genericObject.getQuantity() +
                        "<span style=\"float:right;\"> " + currencyFormat.format(genericObject.getAmount()).trim() + " &euro;</span> </p>"));//Right*/
            }

            htmlResult.append(tableContent);

            //Total line
            htmlResult.append(htmlTemplate.getTotalLine(mContext).replace(ReportHtmlTemplate.ROW_TAG, "<span style=\"text-align:center;\"> " + String.valueOf(totalQty) + "</span>" + //Center
                    "<span style=\"float:right;\"> " + currencyFormat.format(totalVoided).trim() + " &euro;</span> </p>"));//Right

        }
        else {
            htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.no_records)));
        }

    }
}
