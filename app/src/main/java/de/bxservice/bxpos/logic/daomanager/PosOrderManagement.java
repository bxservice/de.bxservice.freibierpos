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
package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosOrderManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosOrderManagement(Context ctx) {
        dataMapper = new DataMapper(ctx);
    }

    @Override
    public boolean update(Object object) {
        return dataMapper.update(object);
    }

    @Override
    public boolean create(Object object) {
        return dataMapper.save(object);
    }

    @Override
    public POSOrder get(long id){
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return dataMapper.remove(object);
    }

    public Table getTable(long id){
        return dataMapper.getTable(id);
    }

    public List<POSOrder> getAllOpenOrders() {
        return dataMapper.getOpenOrders();
    }

    public List<POSOrder> getUnsynchronizedOrders() {
        return dataMapper.getUnsynchronizedOrders();
    }

    public List<POSOrder> getPaidOrders(long fromDate, long toDate) {
        return dataMapper.getPaidOrders(fromDate, toDate);
    }

    public List<POSOrder> getUserPaidOrders(long fromDate, long toDate) {
        return dataMapper.getUserPaidOrders(fromDate, toDate);
    }

    public POSOrder getPosOrder(Table table) {
        return dataMapper.getOpenPosOrder(table);
    }

    public List<POSOrder> getTableOrders (Table table) {
        return dataMapper.getTableOrders(table);
    }

    public List<POSOrderLine> getPrintKitchenLines(POSOrder order) {
        return dataMapper.getPrintKitchenLines(order);
    }

    public List<POSOrderLine> getPrintBarLines(POSOrder order) {
        return dataMapper.getPrintBarLines(order);
    }

    public String getServerName(POSOrder order) {
        return dataMapper.getServerName(order);
    }
}
