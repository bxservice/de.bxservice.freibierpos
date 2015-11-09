package de.bxservice.bxpos.logic.model;

/**
 * Created by diego on 9/11/15.
 */
public class Table {

    //TODO: Create the table in iDempiere and fill this variable
    public static final String M_Product = "?????";

    private int tableID;
    private String tableName;

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
