/**********************************************************************
 * This file is part of FreiBier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.math.BigDecimal;
import java.util.List;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public class SalesReport extends Report {

    private List<POSOrder> paidOrders;
    private ReportHtmlTemplate htmlTemplate;
    private boolean reportByUser = false;

    public SalesReport(Context mContext, boolean reportByUser) {
        super(mContext);
        htmlTemplate = new ReportHtmlTemplate();
        this.reportByUser = reportByUser;
    }

    @Override
    protected void performReport() {
        paidOrders = POSOrder.getPaidOrders(mContext, fromDate, toDate, reportByUser);
    }

    /**
     * Set the result with the html template
     */
    @Override
    protected void setReportResult() {

        htmlResult.append(htmlTemplate.getHtmlTemplate().replace(ReportHtmlTemplate.TITLE_TAG, name));
        if(paidOrders != null) {

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
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, getFormattedValue(totalSold.add(totalDiscounted))));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Discounted items row
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.add_discount)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, getFormattedValue(totalDiscounted)));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Net sales row -> Sales - discounted
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.net_sales)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, getFormattedValue(totalSold)));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Surcharged items row
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.set_extra)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG,getFormattedValue(totalSurcharged)));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Total row -> net sales + surcharges
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.total)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, getFormattedValue(totalSold.add(totalSurcharged))));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Voided items row -> only informative
            htmlResult.append(htmlTemplate.getHtmlRowOpen());
            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.void_item)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, getFormattedValue(totalVoided)));
            htmlResult.append(htmlTemplate.getHtmlRowClose());

            //Close HTML table
            htmlResult.append(htmlTemplate.getHtmlTableClose());
            htmlResult.append(htmlTemplate.getHtmlClose());
        }
        else {
            htmlResult.append(htmlTemplate.getRowText().replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.no_records)));
            htmlResult.append(htmlTemplate.getHtmlClose());
        }

    }
}
