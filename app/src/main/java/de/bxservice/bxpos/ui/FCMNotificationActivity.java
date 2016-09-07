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

        if (BXPOSNotificationCode.MANDATORY_UPDATE_ACTION.equals(clickAction)) {
            // Read the data needed - Products. MProduct Category - Table ...
            ReadServerDataTask initiateData = new ReadServerDataTask(this);
            initiateData.execute();
        } else if (BXPOSNotificationCode.RECOMMENDED_UPDATE_ACTION.equals(clickAction)) {
            onBackPressed();
        } else {
            onBackPressed();
        }
    }

    /**
     * Called when the read data task finishes
     */
    public void postExecuteReadDataTask() {
        onBackPressed();
    }
}
