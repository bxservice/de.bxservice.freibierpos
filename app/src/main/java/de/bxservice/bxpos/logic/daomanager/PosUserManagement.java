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
package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public class PosUserManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosUserManagement(Context ctx) {
        dataMapper = new DataMapper(ctx);
    }

    @Override
    public boolean update(Object object) {
        return dataMapper.update(object);
    }

    @Override
    public boolean create(Object object) {
        return dataMapper.save(object);
    }

    @Override
    public PosUser get(long id){
        return dataMapper.getUser(id);
    }

    @Override
    public boolean remove(Object object) {
        return true;
    }

    /**
     * Get the user from the username
     * @param username
     * @return
     */
    public PosUser get(String username) {
        return dataMapper.getUser(username);
    }

    //return the usernames that have already logged in
    public ArrayList<String> getUsernameList() {
        return dataMapper.getUsernameList();
    }

}
