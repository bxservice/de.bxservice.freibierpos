package de.bxservice.bxpos.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.logic.daomanager.PosTableManagement;
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
                sendNotification(notificationBody, notificationTitle);
            }
            else if (String.valueOf(BXPOSNotificationCode.MANDATORY_REQUEST_CODE).equals(data.get(BXPOSNotificationCode.REQUEST_TYPE))) {
                mandatoryUpdate();
            }
            else if (String.valueOf(BXPOSNotificationCode.TABLE_STATUS_CHANGED_CODE).equals(data.get(BXPOSNotificationCode.REQUEST_TYPE))) {
                //TODO: Refresh activity
                System.out.println("Table changed " + data.get("TableId") + " " + data.get("TableStatus"));
                updateTable(data.get("TableId"), data.get("TableStatus").equals("Y"));
            }

        }

    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * @param messageBody  FCM message body received.
     * @param messageTitle FCM message title received.
     */
    private void sendNotification(String messageBody, String messageTitle) {
        Intent intent = new Intent(this, FCMNotificationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    /**
     * Performs an update on the data on the background
     */
    private void mandatoryUpdate() {
        // Read the data needed - Products. MProduct Category - Table ...
        ReadServerDataTask initiateData = new ReadServerDataTask(null);
        initiateData.execute();
    }

    private void updateTable(String tableId, Boolean isBusy) {
        PosTableManagement tableManager = new PosTableManagement(this);
        Table table = tableManager.get(Long.parseLong(tableId));

        if (table != null) {
            if (isBusy)
                table.occupyTable(this);
            else
                table.freeTable(this);
        }
    }
}
