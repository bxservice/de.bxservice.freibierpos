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

import java.util.ArrayList;

/**
 * Every new role supported by the app must be registered here
 * Created by Diego Ruiz on 17/11/15.
 */
public class PosRoles {

    public static final String WAITER_ROLE     = "waiter_role";
    public static final String SUPERVISOR_ROLE = "supervisor_role";

    public static ArrayList<String> getRoles(){
        ArrayList<String> roles = new ArrayList<>();

        roles.add(WAITER_ROLE);
        roles.add(SUPERVISOR_ROLE);

        return roles;
    }

}