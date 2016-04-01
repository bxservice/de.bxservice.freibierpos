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
    protected void performReport() {
        paidOrders = new DataProvider(mContext).getPaidOrders(fromDate, toDate);
    }

    /**
     * Set the result with the html template
     */
    @Override
    protected void setReportResult() {

        htmlResult.append(htmlTemplate.getHtmlTemplate().replace(ReportHtmlTemplate.TITLE_TAG, name));
        if(paidOrders != null) {

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            //Here is to remove the € sign because it has problems in HTML
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormat).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) currencyFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            BigDecimal totalSold   = BigDecimal.ZERO;
            BigDecimal totalVoided = BigDecimal.ZERO;
            BigDecimal totalDiscounted = BigDecimal.ZERO;
            BigDecimal totalSurcharged = BigDecimal.ZERO;

            htmlResult.append(htmlTemplate.getHtmlTableHeader());

            for(POSOrder order : paidOrders) {
                totalSold = totalSold.add(order.getTotallines());
                totalDiscounted = totalDiscounted.add(order.getDiscount());
                totalSurcharged = totalSurcharged.add(order.getSurcharge());

                for (POSOrderLine orderLine : order.getOrderedLines()) {
                    if(orderLine.getLineStatus().equals(POSOrderLine.VOIDED))
                        totalVoided = totalVoided.add(orderLine.getLineNetAmt());
                }
            }

            //Net sales row
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.sales)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, currencyFormat.format(totalSold.add(totalDiscounted)).trim() + " &euro;"));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Discounted items row
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.add_discount)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, currencyFormat.format(totalDiscounted).trim() + " &euro;"));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Net sales row -> Sales - discounted
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.net_sales)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, currencyFormat.format(totalSold).trim() + " &euro;"));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Surcharged items row
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.set_extra)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, currencyFormat.format(totalSurcharged).trim() + " &euro;"));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Total row -> net sales + surcharges
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.total)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, currencyFormat.format(totalSold.add(totalSurcharged)).trim() + " &euro;"));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Voided items row -> only informative
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.void_item)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, currencyFormat.format(totalVoided).trim() + " &euro;"));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Close HTML table
            htmlResult.append(htmlTemplate.getHtmlTableClose());
        }
        else {
            htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.no_records)));
        }

    }
}
