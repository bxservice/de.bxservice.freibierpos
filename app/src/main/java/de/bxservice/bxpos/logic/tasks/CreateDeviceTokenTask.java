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

import android.content.SharedPreferences;
import android.os.AsyncTask;

import de.bxservice.bxpos.fcm.BxPosFirebaseInstanceIDService;
import de.bxservice.bxpos.logic.DataWriter;

/**
 * Created by Diego Ruiz on 5/23/16.
 */
public class CreateDeviceTokenTask extends AsyncTask<String, Void, Boolean> {

    SharedPreferences sharedPref;

    public CreateDeviceTokenTask(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    @Override
    protected Boolean doInBackground(String... tokens) {

        DataWriter writer = new DataWriter();
        boolean success = true;

        for(String token : tokens) {

            writer.writeDeviceToken(token);
            if (!writer.isSuccess() && writer.isConnectionError()) {
                success = false;
                break;
            }
        }

        return success;
    }

    @Override
    protected void onPostExecute(final Boolean success) {

        if (sharedPref != null) {
            //Update value
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(BxPosFirebaseInstanceIDService.TOKEN_SYNC_PREF, success);
            editor.commit();
        }
    }

}
