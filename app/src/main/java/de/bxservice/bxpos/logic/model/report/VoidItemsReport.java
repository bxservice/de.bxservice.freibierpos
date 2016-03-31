package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public class VoidItemsReport extends Report {

    private List<ReportGenericObject> voidedLines;
    private ReportHtmlTemplate htmlTemplate;

    public VoidItemsReport(Context mContext) {
        super(mContext);
        htmlTemplate = new ReportHtmlTemplate();
    }

    @Override
    public void performReport() {
        voidedLines = new DataProvider(mContext).getVoidedReportRows(fromDate, toDate);
        setReportResult();
    }

    /**
     * Set the result with the html template
     */
    private void setReportResult() {

        htmlResult.append(htmlTemplate.getHtmlTemplate().replace(ReportHtmlTemplate.TITLE_TAG, name));
        if(voidedLines != null && !voidedLines.isEmpty()) {

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
            //Here is to remove the € sign because it has problems in HTML
            DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currencyFormat).getDecimalFormatSymbols();
            decimalFormatSymbols.setCurrencySymbol("");
            ((DecimalFormat) currencyFormat).setDecimalFormatSymbols(decimalFormatSymbols);

            BigDecimal totalVoided = BigDecimal.ZERO;
            int totalQty = 0;

            for(ReportGenericObject o : voidedLines) {
                totalVoided = totalVoided.add(o.getAmount());
                totalQty = totalQty + Integer.parseInt(o.getQuantity());

                htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, "<p style=\"text-align:left;\">" + o.getDescription() + //left
                        "<span style=\"text-align:center;\"> " + o.getQuantity() + //Center
                        "<span style=\"text-align:right;\"> " + currencyFormat.format(o.getAmount()).trim() + " &euro;</span> </p>"));//Right
            }

            //Total line
            htmlResult.append(htmlTemplate.getTotalLine(mContext).replace(ReportHtmlTemplate.ROW_TAG, "<span style=\"text-align:center;\"> " + String.valueOf(totalQty) + //Center
                    "<span style=\"text-align:right;\"> " + currencyFormat.format(totalVoided).trim() + " &euro;</span> </p>"));//Right

        }
        else {
            htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, "No records"));
        }

    }

}
