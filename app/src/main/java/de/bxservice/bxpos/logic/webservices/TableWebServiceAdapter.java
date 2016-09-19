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
package de.bxservice.bxpos.logic.webservices;

import android.util.Log;

import org.idempiere.webservice.client.base.Enums;
import org.idempiere.webservice.client.base.Field;
import org.idempiere.webservice.client.net.WebServiceConnection;
import org.idempiere.webservice.client.request.QueryDataRequest;
import org.idempiere.webservice.client.response.WindowTabDataResponse;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;

/**
 * Created by Diego Ruiz on 9/11/15.
 */
public class TableWebServiceAdapter extends AbstractWSObject{

    //Associated record in Web Service Security in iDempiere
    private static final String SERVICE_TYPE = "QueryTable";

    private List<Table> tableList;
    private List <TableGroup> tableGroupList;

    public TableWebServiceAdapter(WebServiceRequestData wsData) {
        super(wsData);
    }

    @Override
    public String getServiceType() {
        return SERVICE_TYPE;
    }

    @Override
    public void queryPerformed() {

        QueryDataRequest ws = new QueryDataRequest();
        ws.setWebServiceType(getServiceType());
        ws.setLogin(getLogin());

        WebServiceConnection client = getClient();

        tableList = new ArrayList<>();
        tableGroupList = new ArrayList<>();

        try {
            WindowTabDataResponse response = client.sendRequest(ws);

            if (response.getStatus() == Enums.WebServiceResponseStatus.Error) {
                Log.e("Error ws response", response.getErrorMessage());
            } else {

                Log.i("info", "Total rows: " + response.getNumRows());
                String name;
                int tableId;
                boolean isSummary;
                String value;
                String status;

                for (int i = 0; i < response.getDataSet().getRowsCount(); i++) {

                    Log.i("info", "Row: " + (i + 1));
                    name = null;
                    tableId = 0;
                    isSummary = false;
                    value = null;
                    status = Table.FREE_STATUS;

                    for (int j = 0; j < response.getDataSet().getRow(i).getFieldsCount(); j++) {

                        Field field = response.getDataSet().getRow(i).getFields().get(j);
                        Log.i("info", "Column: " + field.getColumn() + " = " + field.getValue());

                        if("Name".equalsIgnoreCase(field.getColumn()))
                            name = field.getStringValue();
                        else if (Table.BAY_Table_ID.equalsIgnoreCase(field.getColumn()))
                            tableId = Integer.valueOf(field.getStringValue());
                        else if ("IsSummary".equalsIgnoreCase(field.getColumn())) {
                            if("Y".equalsIgnoreCase(field.getStringValue()))
                                isSummary = true;
                        }
                        else if ("Value".equalsIgnoreCase(field.getColumn()))
                            value = field.getStringValue();
                        else if ("BXS_IsBusy".equalsIgnoreCase(field.getColumn())) {
                            if("Y".equalsIgnoreCase(field.getStringValue()))
                                status = Table.BUSY_STATUS;
                        }

                    }

                    if(name != null &&  tableId!= 0 && value != null) {
                        // If isSummary is a table group else is a table
                        if(isSummary) {
                            TableGroup tableGroup =  new TableGroup();
                            tableGroup.setTableGroupID(tableId);
                            tableGroup.setValue(value);
                            tableGroup.setName(name);
                            tableGroupList.add(tableGroup);
                        }
                        else{
                            Table table = new Table();
                            table.setTableID(tableId);
                            table.setTableName(name);
                            table.setValue(value);
                            table.setStatus(status);
                            tableList.add(table);
                        }
                    }

                }

                associateGroupsAndTables();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Associate the tables to their corresponding group based on search key
     * If there are no groups - a default one is created
     */
    private void associateGroupsAndTables(){

        if (tableList != null && !tableList.isEmpty()) {
            //If there are no table groups - create a default one
            if ( tableGroupList.isEmpty() ){
                TableGroup tableGroup =  new TableGroup();
                tableGroup.setValue("default");
                tableGroup.setName("default");

                for ( Table table : tableList ){
                    table.setBelongingGroup(tableGroup);
                    tableGroup.getTables().add(table);
                }

                tableGroupList.add(tableGroup);
            } else {

                String groupValue;
                String tableValue;
                for ( TableGroup tableGroup : tableGroupList){

                    groupValue = tableGroup.getValue();
                    for ( Table table : tableList ){

                        tableValue = table.getValue().substring(0,2);
                        if (tableValue.equals(groupValue)){
                            tableGroup.getTables().add(table);
                            table.setBelongingGroup(tableGroup);
                        }
                    }
                }
            }
        }
    }

    public List<TableGroup> getTableGroupList() {
        return tableGroupList;
    }

}
