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
                htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, getFormattedValue(genericObject.getAmount())));

                htmlResult.append(htmlTemplate.getHtmlRowClose());
            }
            //Total row
            htmlResult.append(htmlTemplate.getHtmlRowOpen());

            htmlResult.append(htmlTemplate.getHtmlColumn("left").replace(ReportHtmlTemplate.ROW_TAG, mContext.getString(R.string.total)));
            htmlResult.append(htmlTemplate.getHtmlColumn("center").replace(ReportHtmlTemplate.ROW_TAG, String.valueOf(totalQty)));
            htmlResult.append(htmlTemplate.getHtmlColumn("right").replace(ReportHtmlTemplate.ROW_TAG, getFormattedValue(totalSold)));

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
