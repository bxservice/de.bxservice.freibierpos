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
package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.util.List;

import de.bxservice.bxpos.logic.model.report.ReportGenericObject;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Class in charge to provide the necessary info for the report creation
 * it communicates to the database via Data Mapper
 * Created by Diego Ruiz on 30/12/15.
 */
public class PosReportManagement {

    private DataMapper dataMapper;

    public PosReportManagement(Context ctx) {
        dataMapper = new DataMapper(ctx);
    }

    public List<ReportGenericObject> getVoidedReportRows(long fromDate, long toDate) {
        return dataMapper.getVoidedReportRows(fromDate, toDate);
    }

    public List<ReportGenericObject> getTableSalesReportRows(long fromDate, long toDate) {
        return dataMapper.getTableSalesReportRows(fromDate, toDate);
    }

    public List<ReportGenericObject> getProductSalesReportRows(long fromDate, long toDate) {
        return dataMapper.getProductSalesReportRows(fromDate, toDate);
    }

}
