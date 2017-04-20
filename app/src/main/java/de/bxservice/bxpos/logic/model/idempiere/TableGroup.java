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
package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.daomanager.PosTableGroupManagement;

/**
 * Created by Diego Ruiz on 13/11/15.
 */
public class TableGroup implements Serializable {

    private int tableGroupID;
    private String value;
    private String name;
    private List<Table> tables = new ArrayList<>();
    private PosTableGroupManagement tableGroupManager;

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

    public boolean save(Context ctx) {
        tableGroupManager = new PosTableGroupManagement(ctx);

        if (tableGroupManager.get(tableGroupID) == null)
            return createTableGroup();
        else
            return updateTableGroup();
    }

    private boolean updateTableGroup() {
        return tableGroupManager.update(this);
    }

    private boolean createTableGroup() {
        return tableGroupManager.create(this);
    }

    public static List<TableGroup> getAllTableGroups(Context ctx) {
        PosTableGroupManagement tableGroupManager = new PosTableGroupManagement(ctx);
        return tableGroupManager.getAllTableGroups();
    }
}
