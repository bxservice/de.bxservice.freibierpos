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

            String tableContent = htmlTemplate.getHtmlTable(voidedLines.size()+1);

            int i = 0;
            for(ReportGenericObject genericObject : voidedLines) {
                totalVoided = totalVoided.add(genericObject.getAmount());
                totalQty = totalQty + Integer.parseInt(genericObject.getQuantity());

                tableContent = tableContent.replace(ReportHtmlTemplate.ROW_TAG + i, genericObject.getDescription());
                i = i+1;
                tableContent = tableContent.replace(ReportHtmlTemplate.ROW_TAG + i, genericObject.getQuantity());
                i = i+1;
                tableContent = tableContent.replace(ReportHtmlTemplate.ROW_TAG + i, currencyFormat.format(genericObject.getAmount()).trim() + " &euro;");
                i = i+1;
            }

            //Total row
            tableContent = tableContent.replace(ReportHtmlTemplate.ROW_TAG + i, mContext.getString(R.string.total));
            i = i+1;
            tableContent = tableContent.replace(ReportHtmlTemplate.ROW_TAG + i, String.valueOf(totalQty));
            i = i+1;
            tableContent = tableContent.replace(ReportHtmlTemplate.ROW_TAG + i, currencyFormat.format(totalVoided).trim() + " &euro;");

            htmlResult.append(tableContent);

        }
        else {
            htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.no_records)));
        }

    }

}
