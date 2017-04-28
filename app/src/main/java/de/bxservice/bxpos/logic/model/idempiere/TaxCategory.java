/**********************************************************************
 * This file is part of Freibier POS                                   *
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

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.daomanager.TaxCategoryManagement;

/**
 * Created by Diego Ruiz on 11/11/16.
 */
public class TaxCategory {

    private List<Tax> taxes = new ArrayList<>();
    private int taxCategoryID;
    private String name;
    private TaxCategoryManagement taxCategoryManager;

    public int getTaxCategoryID() {
        return taxCategoryID;
    }

    public void setTaxCategoryID(int taxCategoryID) {
        this.taxCategoryID = taxCategoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Tax> taxes) {
        this.taxes = taxes;
    }

    public boolean save(Context ctx) {

        //No save in the database if the Product Category does not have products
        if (taxes == null || taxes.isEmpty())
            return false;

        taxCategoryManager = new TaxCategoryManagement(ctx);

        if (taxCategoryManager.get(taxCategoryID) == null)
            return createTaxCategory();
        else
            return updateTaxCategory();
    }

    private boolean updateTaxCategory() {
        return taxCategoryManager.update(this);
    }

    private boolean createTaxCategory() {
        return taxCategoryManager.create(this);
    }
}
