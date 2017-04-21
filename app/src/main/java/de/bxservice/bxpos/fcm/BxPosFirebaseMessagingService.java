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

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.tasks.ReadServerDataTask;
import de.bxservice.bxpos.ui.FCMNotificationActivity;
import de.bxservice.bxpos.ui.MainActivity;

/**
 * Created by Diego Ruiz on 5/20/16.
 */
public class BxPosFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "Messaging Service";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.}
        Map<String, String> data = remoteMessage.getData();
        String notificationTitle = "";
        String notificationBody  = "";
        if(remoteMessage.getNotification() != null) {
            notificationBody  = remoteMessage.getNotification().getBody();
            notificationTitle = remoteMessage.getNotification().getTitle();
        }


        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + notificationBody);
        Log.d(TAG, "FCM Data Message: " + data);

        if(data != null) {
            //If the request is suggested
            if (String.valueOf(BXPOSNotificationCode.RECOMMENDED_REQUEST_CODE).equals(data.get(BXPOSNotificationCode.REQUEST_TYPE))) {
                sendNotification(notificationBody, notificationTitle, BXPOSNotificationCode.RECOMMENDED_REQUEST_CODE);
            }
            else if (String.valueOf(BXPOSNotificationCode.MANDATORY_REQUEST_CODE).equals(data.get(BXPOSNotificationCode.REQUEST_TYPE))) {
                sendNotification(notificationBody, notificationTitle, BXPOSNotificationCode.MANDATORY_REQUEST_CODE);
            }
            else if (String.valueOf(BXPOSNotificationCode.TABLE_STATUS_CHANGED_CODE).equals(data.get(BXPOSNotificationCode.REQUEST_TYPE))) {
                Log.d(TAG, "Request to change table " + data.get(BXPOSNotificationCode.CHANGED_TABLE_ID));
                updateTable(data.get(BXPOSNotificationCode.CHANGED_TABLE_ID), data.get(BXPOSNotificationCode.NEW_TABLE_STATUS).equals("Y"), data.get(BXPOSNotificationCode.SERVER_NAME));
            }

        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * @param messageBody  FCM message body received.
     * @param messageTitle FCM message title received.
     */
    private void sendNotification(String messageBody, String messageTitle, int requestCode) {
        Intent intent = new Intent(this, FCMNotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Setting the clickAction
        String clickAction = "";
        switch(requestCode) {
            case BXPOSNotificationCode.RECOMMENDED_REQUEST_CODE:
                clickAction = BXPOSNotificationCode.RECOMMENDED_UPDATE_ACTION;
                break;
            case BXPOSNotificationCode.MANDATORY_REQUEST_CODE:
                clickAction = BXPOSNotificationCode.MANDATORY_UPDATE_ACTION;
        }
        intent.setAction(clickAction);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationBuilder.setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void updateTable(String tableId, Boolean isBusy, String serverName) {
        Table table = Table.getTable(this, Long.parseLong(tableId));

        if (table != null) {

            String status = isBusy ? Table.BUSY_STATUS : Table.FREE_STATUS;

            //Check if the status is the same as in the database
            if(status.equals(table.getStatus()))
                return;

            if(isBusy) {
                if(serverName != null)
                    table.setServerName(serverName);

                table.occupyTable(this, false);
            }
            else
                table.freeTable(this, false);

            updateMainActivity(table);
        }
    }

    private void updateMainActivity(Table table) {
        Log.d(TAG, "Broadcasting message");
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MainActivity.TABLE_UPDATED_ACTION);
        broadcastIntent.putExtra(MainActivity.EXTRA_UPDATED_TABLE, table);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }
}
