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
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import de.bxservice.bxpos.logic.DataReader;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;
import de.bxservice.bxpos.ui.FCMNotificationActivity;
import de.bxservice.bxpos.ui.LoginActivity;
import de.bxservice.bxpos.ui.MainActivity;

/**
 * Created by Diego Ruiz on 5/23/16.
 */
public class ReadServerDataTask extends AsyncTask<Void, Void, Boolean> {

    private Activity mActivity;
    private static final String TAG = "ReadServerDataTask";

    public ReadServerDataTask(Activity callerActivity) {
        mActivity = callerActivity;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        Context ctx = null;

        if(mActivity != null)
            ctx = mActivity.getBaseContext();

        WebServiceRequestData wsData = new WebServiceRequestData(ctx);

        DataReader data;
        //Check if the necessary data to perform a web service call exists
        if (wsData.isDataComplete()) {
            data = new DataReader(wsData, ctx);
        } else {
            Log.e(TAG, "Invalid Web service request data");
            return false;
        }


        return data.isDataComplete() && !data.isError();
    }

    @Override
    protected void onPostExecute(Boolean result) {

        if (mActivity instanceof MainActivity)
            ((MainActivity) mActivity).postExecuteReadDataTask(result);
        else if (mActivity instanceof LoginActivity)
            ((LoginActivity) mActivity).postExecuteReadDataTask(result);
        else if (mActivity instanceof FCMNotificationActivity)
            ((FCMNotificationActivity) mActivity).postExecuteReadDataTask();

    }
}