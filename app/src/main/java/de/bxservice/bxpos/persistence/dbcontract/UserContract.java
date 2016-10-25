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
package de.bxservice.bxpos.persistence.dbcontract;

import android.provider.BaseColumns;

/**
 * Created by Diego Ruiz on 15/12/15.
 */
public final class UserContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public UserContract() {}

    /* Inner class that defines the table contents */
    public static abstract class User implements BaseColumns {
        public static final String TABLE_NAME               = "pos_user";
        public static final String COLUMN_NAME_USER_ID      = "userid";
        public static final String COLUMN_NAME_USERNAME     = "username";
        public static final String COLUMN_NAME_DISPLAY_NAME = "displayName";
        public static final String COLUMN_NAME_USER_PIN     = "userPIN";
        public static final String COLUMN_NAME_PASSWORD     = "password";
        public static final String COLUMN_NAME_SALT         = "salt";
    }
}
