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
package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;
import java.io.Serializable;
import de.bxservice.bxpos.logic.daomanager.PosTenderTypeManagement;

/**
 * Created by Diego Ruiz on 9/14/16.
 */
public class PosTenderType implements Serializable {

    //Constant values in iDempiere
    public static final String CREDIT_CARD_PAYMENT_TENDER_TYPE_VALUE = "C";
    public static final String CASH_PAYMENT_TENDER_TYPE_VALUE        = "X";

    private PosTenderTypeManagement tenderTypeManager;

    private int    C_POSTenderType_ID;
    private String tenderType;

    public int getC_POSTenderType_ID() {
        return C_POSTenderType_ID;
    }

    public void setC_POSTenderType_ID(int c_POSTenderType_ID) {
        C_POSTenderType_ID = c_POSTenderType_ID;
    }

    public String getTenderType() {
        return tenderType;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    public boolean save(Context ctx) {
        tenderTypeManager = new PosTenderTypeManagement(ctx);

        if (tenderTypeManager.get(C_POSTenderType_ID) == null)
            return createTenderType();
        else
            return updateTenderType();
    }

    private boolean updateTenderType() {
        return tenderTypeManager.update(this);
    }

    /**
     * Communicates with the manager to create the product in the database
     * @return
     */
    private boolean createTenderType() {
        return tenderTypeManager.create(this);
    }

    public static PosTenderType get(Context ctx, String tenderType) {
        PosTenderTypeManagement tenderTypeManager = new PosTenderTypeManagement(ctx);
        return tenderTypeManager.get(tenderType);
    }
}
