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
package de.bxservice.bxpos.logic.tasks;

import android.content.Context;
import android.os.AsyncTask;

import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.webservices.UpdateTableStatusWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;

/**
 * Created by Diego Ruiz on 5/24/16.
 */
public class UpdateTableStatusTask extends AsyncTask<Table, Void, Boolean> {

    private Context ctx;

    public UpdateTableStatusTask(Context context) {
        ctx = context;
    }

    @Override
    protected Boolean doInBackground(Table... tables) {

        boolean success = true;

        WebServiceRequestData wsData = new WebServiceRequestData(ctx);

        if (wsData.isDataComplete()) {
            for (Table table : tables) {

                UpdateTableStatusWebServiceAdapter updateTableStatusWebServiceAdapter = new UpdateTableStatusWebServiceAdapter(wsData, table);
                if (!updateTableStatusWebServiceAdapter.isSuccess() && updateTableStatusWebServiceAdapter.isConnectionError()) {
                    success = false;
                    break;
                }
            }
        } else {
            success = false;
        }

        return success;
    }

}
