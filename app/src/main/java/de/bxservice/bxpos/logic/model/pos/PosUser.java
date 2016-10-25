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
package de.bxservice.bxpos.logic.model.pos;

import android.content.Context;

import de.bxservice.bxpos.logic.daomanager.PosUserManagement;
import de.bxservice.bxpos.logic.util.SecureEngine;
import de.bxservice.bxpos.persistence.dbcontract.UserContract;

/**
 * Created by Diego Ruiz on 15/12/15.
 */
public class PosUser extends UserContract.User {

    private int id;
    private String username;
    private String displayUsername;
    private String userPIN;
    private String password;
    private String salt;
    private PosUserManagement userManager;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayUsername() {
        return displayUsername;
    }

    public void setDisplayUsername(String displayUsername) {
        this.displayUsername = displayUsername;
    }

    public String getUserPIN() {
        return userPIN;
    }

    public void setUserPIN(String userPIN) {
        this.userPIN = userPIN;
    }

    /**
     * Communicates with the manager to create the user in the database
     * @param ctx
     * @return
     */
    public boolean createUser(Context ctx) {

        userManager = new PosUserManagement(ctx);
        boolean result;

        result = userManager.create(this);

        return result;

    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public boolean updateUser(Context ctx) {
        userManager = new PosUserManagement(ctx);
        return userManager.update(this);
    }

    public boolean updateUserInfo(Context ctx) {
        userManager = new PosUserManagement(ctx);
        return userManager.updateUserInfo(this);
    }

    /**
     * check if hashed password matches
     */
    public boolean authenticateHash (String password2)  {
        return SecureEngine.isMatchHash(password, salt, password2);
    }

}
