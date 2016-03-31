package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public class SalesReport extends Report {

    private List<POSOrder> paidOrders;
    private ReportHtmlTemplate htmlTemplate;

    public SalesReport(Context mContext) {
        super(mContext);
        htmlTemplate = new ReportHtmlTemplate();
    }

    @Override
    public void performReport() {
        paidOrders = new DataProvider(mContext).getPaidOrders(fromDate, toDate);
        setReportResult();
    }

    /**
     * Set the result with the html template
     */
    private void setReportResult() {

        htmlResult.append(htmlTemplate.getHtmlTemplate().replace(ReportHtmlTemplate.TITLE_TAG, name));
        if(paidOrders != null) {

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            //Here is to remove the € sign because it has problems in HTML
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormat).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) currencyFormat).setDecimalFormatSymbols(decimalFormatSymbols);


            BigDecimal totalSold   = BigDecimal.ZERO;
            BigDecimal totalVoided = BigDecimal.ZERO;
            //TODO add discounted

            for(POSOrder order : paidOrders) {
                totalSold = totalSold.add(order.getTotallines());

                for (POSOrderLine orderLine : order.getOrderedLines()) {
                    if(orderLine.getLineStatus().equals(POSOrderLine.VOIDED))
                        totalVoided = totalVoided.add(orderLine.getLineNetAmt());
                }

            }

            //TODO: Remove hard coded strings
            //First row Net Sales
            htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, "<p style=\"text-align:left;\"> Net Sales: <span style=\"float:right;\">"
                    + currencyFormat.format(totalSold).trim() + " &euro;</span> </p>"));

            //Second row Voided items
            htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, "<p style=\"text-align:left;\"> Voided Items: <span style=\"float:right;\">"
                    + currencyFormat.format(totalVoided).trim() +" &euro;</span> </p>"));

            //Total line
            htmlResult.append(htmlTemplate.getTotalLine(mContext).replace(ReportHtmlTemplate.ROW_TAG, "<span style=\"float:right;\">"
                    + currencyFormat.format(totalSold.subtract(totalVoided)).trim() + " &euro; </span> </p>"));

        }
        else {
            htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, "No records"));
        }

    }
}
