package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.util.List;

import de.bxservice.bxpos.logic.DataProvider;
import de.bxservice.bxpos.logic.model.pos.POSOrder;

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
            for(POSOrder order : paidOrders) {
                htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, String.valueOf(order.getOrderId())));
            }
        }

        htmlResult.append(htmlTemplate.getTotalLine(mContext));

    }



}
