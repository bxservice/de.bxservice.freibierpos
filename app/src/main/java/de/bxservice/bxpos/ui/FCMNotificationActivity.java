package de.bxservice.bxpos.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import de.bxservice.bxpos.R;
import de.bxservice.bxpos.fcm.BXPOSNotificationCode;
import de.bxservice.bxpos.logic.tasks.ReadServerDataTask;

public class FCMNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcmnotification);

        String clickAction = getIntent().getAction();

        if(BXPOSNotificationCode.MANDATORY_UPDATE_ACTION.equals(clickAction)) {
            // Read the data needed - Products. MProduct Category - Table ...
            ReadServerDataTask initiateData = new ReadServerDataTask(this);
            initiateData.execute();
        }
    }

    /**
     * Called when the read data task finishes
     * @param result
     */
    public void postExecuteReadDataTask() {
        onBackPressed();
    }
}
