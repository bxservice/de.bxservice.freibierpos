package de.bxservice.bxpos.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Diego Ruiz on 5/20/16.
 */
public class BxPosFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "BxPosFirebaseIIDService";

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
        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setDeviceToken(token);
        deviceToken.setSynchonized(false);
        deviceToken.createToken(null);
    }
}
