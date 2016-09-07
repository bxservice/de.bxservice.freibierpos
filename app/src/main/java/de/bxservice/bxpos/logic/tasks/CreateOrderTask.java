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

import android.app.Activity;
import android.os.AsyncTask;

import de.bxservice.bxpos.logic.DataWriter;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.ui.MainActivity;
import de.bxservice.bxpos.ui.PayOrderActivity;

/**
 * Represents an asynchronous creating task used to send
 * the order to iDempiere
 * Created by Diego Ruiz on 2/03/16.
 */
public class CreateOrderTask extends AsyncTask<POSOrder, Void, Boolean> {

    private Activity mActivity;
    private boolean connectionError = false;
    private String  errorMessage = "";

    public CreateOrderTask(Activity callerActivity) {
        mActivity = callerActivity;
    }

    @Override
    protected Boolean doInBackground(POSOrder... orders) {

        DataWriter writer = new DataWriter();
        boolean success = true;

        for(POSOrder order : orders) {
            writer.writeOrder(order, mActivity.getBaseContext());
            //If no success creating the order in iDempiere and the problem is the connection with the server
            if (!writer.isSuccess()) {
                success = false;

                if (writer.isConnectionError())
                    connectionError = true;

                errorMessage = writer.getErrorMessage();
                break;

            }
            order.setSync(true);
            order.updateOrder(mActivity.getBaseContext());
            success = true;
        }

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (mActivity instanceof PayOrderActivity)
            ((PayOrderActivity) mActivity).postExecuteTask(success, connectionError, errorMessage);

        if (mActivity instanceof MainActivity)
            ((MainActivity) mActivity).postExecuteCreateOrderTask(success, connectionError, errorMessage);
    }
}