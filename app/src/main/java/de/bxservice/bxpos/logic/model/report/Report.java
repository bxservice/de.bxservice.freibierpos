package de.bxservice.bxpos.logic.model.report;

import android.content.Context;

/**
 * Created by Diego Ruiz on 25/03/16.
 */
public abstract class Report {

    String name;
    int code;
    long fromDate, toDate;
    Context mContext;
    boolean isSelected;
    StringBuilder htmlResult;

    public Report(Context mContext) {
        this.mContext = mContext;
        htmlResult = new StringBuilder();
    }

    public void runReport() {
        performReport();
    }

    public abstract void performReport();

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

    public StringBuilder getHtmlResult() {
        return htmlResult;
    }

    public void setHtmlResult(StringBuilder htmlResult) {
        this.htmlResult = htmlResult;
    }
}
