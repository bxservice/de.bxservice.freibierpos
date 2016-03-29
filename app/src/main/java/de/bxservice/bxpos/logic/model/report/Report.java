package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public abstract class Report {

    String name;
    int code;
    long fromDate, toDate;
    Context mContext;
    boolean isSelected;

    public Report(Context mContext) {
        this.mContext = mContext;
    }

    public List<?> runReport() {
        return reportPerformed();
    }

    public abstract List<?> reportPerformed();

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFromDate() {
        return fromDate;
    }

    public void setFromDate(long fromDate) {
        this.fromDate = fromDate;
    }

    public long getToDate() {
        return toDate;
    }

    public void setToDate(long toDate) {
        this.toDate = toDate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
