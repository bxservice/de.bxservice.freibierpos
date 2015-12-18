package de.bxservice.bxpos.logic.model;

import java.io.Serializable;

/**
 * Created by Diego Ruiz on 9/11/15.
 */
public class Table implements Serializable {

    public static final String BAY_Table_ID = "BAY_Table_ID";

    private int tableID;
    private String tableName;
    private String value;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
