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

import java.util.ArrayList;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public class ReportFactory {

    //This values have to match the values in string.xml
    private static final int SALES_CODE         = 0;
    private static final int MY_SALES_CODE      = 1;
    private static final int VOID_ITEMS_CODE    = 2;
    private static final int TABLE_SALES_CODE   = 3;
    private static final int PRODUCT_SALES_CODE = 4;

    private ArrayList<Report> reports = new ArrayList<>();

    public ReportFactory(Context mContext, String[] names, String[] values) {

        Report report = null;
        int reportValue;
        for(int i = 0; i < names.length; i++) {
            reportValue = Integer.parseInt(values[i]);

            switch (reportValue) {
                case SALES_CODE:
                    report = new SalesReport(mContext, false);
                    report.setCode(SALES_CODE);
                    report.setName(names[i]);
                    break;
                case MY_SALES_CODE:
                    report = new SalesReport(mContext, true);
                    report.setCode(MY_SALES_CODE);
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
                case PRODUCT_SALES_CODE:
                    report = new ProductSalesReport(mContext);
                    report.setCode(PRODUCT_SALES_CODE);
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
