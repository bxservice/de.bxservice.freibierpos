package de.bxservice.bxpos.logic.model.idempiere;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diego Ruiz on 13/11/15.
 */
public class TableGroup implements Serializable {

    private int tableGroupID;
    private String value;
    private String name;
    private List<Table> tables = new ArrayList<Table>();

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<Table> getTables() {
        return tables;
    }

    public void setTables(List<Table> tables) {
        this.tables = tables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTableGroupID() {
        return tableGroupID;
    }

    public void setTableGroupID(int tableGroupID) {
        this.tableGroupID = tableGroupID;
    }
}
