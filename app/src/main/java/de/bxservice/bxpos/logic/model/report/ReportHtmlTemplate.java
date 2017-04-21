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
package de.bxservice.bxpos.logic.model.report;

/**
 * Default template for reports in the app
 * Created by Diego Ruiz on 30/03/16.
 */
public class ReportHtmlTemplate {

    public static final String TITLE_TAG  = "TITLE";
    public static final String ROW_TAG    = "ROW";

    private StringBuilder htmlTemplate;

    /**
     * Gets the initial html template
     * with the header
     * @return
     */
    public String getHtmlTemplate() {
        htmlTemplate = new StringBuilder();
        htmlTemplate.append("<h4 align=\"center\"><u>"+ TITLE_TAG +"</u></h4>"); //Title
        return htmlTemplate.toString();
    }

    public String getRowText() {
        return "<p>"+ ROW_TAG +"</p>";
    }

    /**
     * Get header of a table
     * @return
     */
    public String getHtmlTableHeader() {
        return "<table style=\"width:100%\" border=\"0\">"; //Table with no border
    }

    /**
     * Get closing tag of a table
     * @return
     */
    public String getHtmlTableClose() {
        return "</table>";
    }

    /**
     * Open a table row
     * @return
     */
    public String getHtmlRowOpen() {
        return "<tr>";
    }

    /**
     * Close a table row
     * @return
     */
    public String getHtmlRowClose() {
        return "</tr>";
    }

    /**
     * Get a column
     * @param alignment it must be left - center or right
     * @return
     */
    public String getHtmlColumn(String alignment) {
        return "<td align=\""+ alignment +"\">" + ROW_TAG + "</td>";
    }

}
