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
package de.bxservice.bxpos.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Diego Ruiz on 5/20/16.
 */
public class BxPosFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "BxPosFirebaseIIDService";
    public static final String TOKEN_SHARED_PREF = "de.bxservice.token_preference";
    public static final String TOKEN_SYNC_PREF = "deviceTokenSync";


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token in the database to send it to iDempiere
     * when possible
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        Log.d(TAG, "New token created ----> " + token);
        SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(TOKEN_SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(TOKEN_SYNC_PREF, false);
        editor.apply();

    }

    public static String getToken(){
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "****TOKEN ----> " + refreshedToken);
        return refreshedToken;
    }
}
