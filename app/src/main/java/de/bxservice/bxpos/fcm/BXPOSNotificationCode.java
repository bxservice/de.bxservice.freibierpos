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

/**
 * Created by Diego Ruiz on 5/24/16.
 */
public interface BXPOSNotificationCode {

    /**Type of update request*/
    String REQUEST_TYPE = "RT";

    /**Request that is not mandatory*/
    int RECOMMENDED_REQUEST_CODE = 100;

    /**Request that is mandatory*/
    int MANDATORY_REQUEST_CODE = 200;

    /**Table status changed request*/
    int TABLE_STATUS_CHANGED_CODE = 300;

    /**Request actions send as click_action to perform on click in the notification*/
    /**Mandatory request action*/
    String MANDATORY_UPDATE_ACTION = "LOAD_DATA";
    String RECOMMENDED_UPDATE_ACTION = "OPEN_ACTIVITY";

    /**Data tags for table change status*/
    String CHANGED_TABLE_ID = "TableId";
    String NEW_TABLE_STATUS = "TableStatus";
    String SERVER_NAME = "ServerName";

}
