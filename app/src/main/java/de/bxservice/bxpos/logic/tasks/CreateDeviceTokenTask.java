package de.bxservice.bxpos.logic.tasks;

import android.os.AsyncTask;

import de.bxservice.bxpos.fcm.DeviceToken;
import de.bxservice.bxpos.logic.DataWriter;

/**
 * Created by Diego Ruiz on 5/23/16.
 */
public class CreateDeviceTokenTask extends AsyncTask<DeviceToken, Void, Boolean> {

    @Override
    protected Boolean doInBackground(DeviceToken... tokens) {

        DataWriter writer = new DataWriter();
        boolean success = true;

        for(DeviceToken token : tokens) {

            writer.writeDeviceToken(token.getDeviceToken());
            if (!writer.isSuccess() && writer.isConnectionError()) {
                success = false;
                break;
            }

            token.setSynchonized(true);
            token.update(null);
        }

        return success;
    }

}
