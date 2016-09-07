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
package de.bxservice.bxpos.logic;

import android.content.Context;
import android.util.Log;

import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.webservices.CreateDeviceTokenWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.CreateOrderWebServiceAdapter;

/**
 * Class that writes in iDempiere the data
 * and call the necessary methods
 * to persist it in the database
 * Created by Diego Ruiz on 6/11/15.
 */
public class DataWriter {

    private static final String LOG_TAG = "Data Writer";

    private boolean success = false;
    private boolean connectionError = false;
    private String  errorMessage = "";

    public void writeOrder(final POSOrder order, final Context context) {
        Log.i(LOG_TAG, "Write order");

        Thread createOrderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                CreateOrderWebServiceAdapter createOrderWS = new CreateOrderWebServiceAdapter(order, context);
                success = createOrderWS.isSuccess();
                connectionError = createOrderWS.isConnectionError();
                errorMessage = createOrderWS.getErrorMessage();
            }
        });

        createOrderThread.run();
    }

    public void writeDeviceToken(final String token) {
        Log.i(LOG_TAG, "Write token");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                CreateDeviceTokenWebServiceAdapter createTokenWS = new CreateDeviceTokenWebServiceAdapter(token);
                success = createTokenWS.isSuccess();
                connectionError = createTokenWS.isConnectionError();
            }
        });

        thread.run();
    }

    public boolean isConnectionError() {
        return connectionError;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
