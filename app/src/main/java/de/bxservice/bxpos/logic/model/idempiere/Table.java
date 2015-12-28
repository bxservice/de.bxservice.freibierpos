package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import java.io.Serializable;

import de.bxservice.bxpos.logic.daomanager.PosTableManagement;

/**
 * Created by Diego Ruiz on 9/11/15.
 */
public class Table implements Serializable {

    //Table status
    public static final String FREE_STATUS     = "FREE";
    public static final String BUSY_STATUS     = "BUSY";
    public static final String RESERVED_STATUS = "RESERVED";

    public static final String BAY_Table_ID = "BAY_Table_ID";

    private PosTableManagement tableManager;
    private TableGroup belongingGroup;
    private int tableID;
    private String tableName;
    private String value;
    private String status;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (status.equals(FREE_STATUS) ||
                status.equals(BUSY_STATUS) ||
                status.equals(RESERVED_STATUS))
            this.status = status;
    }

    public TableGroup getBelongingGroup() {
        return belongingGroup;
    }

    public void setBelongingGroup(TableGroup belongingGroup) {
        this.belongingGroup = belongingGroup;
    }

    //TODO: Set table group by id
    public void setTableGroup(int tableGroup) {
        //this.tableGroup = tableGroup;
    }

    public boolean createTable(Context ctx) {
        tableManager = new PosTableManagement(ctx);
        return tableManager.create(this);
    }
}
