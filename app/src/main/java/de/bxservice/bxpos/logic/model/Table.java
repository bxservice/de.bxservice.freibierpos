package de.bxservice.bxpos.logic.model;

/**
 * Created by diego on 9/11/15.
 */
public class Table {

    public static final String BAY_Table_ID = "BAY_Table_ID";

    private int tableID;
    private String tableName;

    public boolean isSummary() {
        return isSummary;
    }

    public void setIsSummary(boolean isSummary) {
        this.isSummary = isSummary;
    }

    private boolean isSummary;

    public int getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
