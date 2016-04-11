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

}
