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

import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 11/05/16.
 */
public class PosKitchenNoteManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosKitchenNoteManagement(Context ctx) {
        dataMapper = new DataMapper(ctx);
    }

    @Override
    public boolean update(Object object) {
        return false;
    }

    @Override
    public boolean create(Object object) {
        //if the note already exists - don't do anything
        if(dataMapper.noteExist((String) object))
            return true;

        return dataMapper.save(object);
    }

    @Override
    public String get(long id){
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return false;
    }

    public boolean noteExist(String note) {
        return dataMapper.noteExist(note);
    }

    public ArrayList<String> getKitchenNotes() {
        return dataMapper.getKitchenNotes();
    }

}