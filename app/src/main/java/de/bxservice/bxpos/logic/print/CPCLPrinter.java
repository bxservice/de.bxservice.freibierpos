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
package de.bxservice.bxpos.logic.print;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.bxservice.bxpos.logic.daomanager.PosDefaultDataManagement;
import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.logic.model.pos.PosProperties;
import de.bxservice.bxpos.logic.model.report.ReportGenericObject;

/**
 * Created by Diego Ruiz on 21/04/16.
 */
public class CPCLPrinter extends AbstractPOSPrinter {

    public CPCLPrinter(POSOrder order) {
        super(order);
    }

    /**
     * Print the tickets
     * Returns a string with %s
     * 1 - Order string
     * 2 - Table
     * 3 - Table Number
     * 3 - Server
     * 4 - Guests
     */
    @Override
    public String printTicket(String target) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        StringBuilder ticket = new StringBuilder();

        ticket.append("! U1 JOURNAL\r\n");
        ticket.append("! U1 SETLP 5 3 86\r\n");
        ticket.append("\r\n");
        ticket.append("%s #: "+ order.getOrderId() +"\r\n");
        ticket.append( "! U1 SETLP 7 0 24\r\n");
        ticket.append( "%s: %s\r\n");
        ticket.append("%s: " + order.getServerName(null) +"\r\n");
        ticket.append( "%s: "+ order.getGuestNumber() +"\r\n");
        ticket.append("\r\n");
        ticket.append( "! U1 SETLP 5 2 46\r\n");

        List<POSOrderLine> lines = new ArrayList<>();

        if(target.equals(KITCHEN_RECEIPT)) {
            lines = order.getPrintKitchenLines(null);
        } else if (target.equals(BAR_RECEIPT)) {
            lines = order.getPrintBarLines(null);
        }

        for(POSOrderLine line : lines) {
            ticket.append(line.getQtyOrdered() + "  " + line.getProduct().getProductName() + "\r\n");
            if(line.getProductRemark() != null && !line.getProductRemark().isEmpty())
                ticket.append("    " + line.getProductRemark() + "\r\n");
            line.setPrinted(true);
            line.updateLine(null);
        }

        ticket.append("\r\n");
        ticket.append( "! U1 SETLP 7 0 24\r\n");
        ticket.append(dateFormat.format(cal.getTime())+"\r\n");//2014/08/06 16:00:22
        ticket.append("! U1 PRESENT-AT\r\n");
        ticket.append("! U1 PRINT\r\n");

        return ticket.toString();
    }

    /**
     * Returns a string with several %s to format
     * 0 - Page Width
     * 1 - Restaurant Name
     * 2 - Address
     * 3 - City
     * 4 - Receipt label
     * 5 - Receipt Number
     * 6 - Table string
     * 7 - Table name
     * 8 - Server string
     * 9 - Guests string
     * 10 - Amount String
     * 11 - Charges String
     * 12 - Total String
     * 13 - Cash string
     * 14 - Back string
     * 15 - Footer description
     * @return String for receipt printing
     */
    @Override
    public String printReceipt() {
        StringBuilder ticket = new StringBuilder();

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(PosProperties.getInstance().getLocale());
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        //header T font size x y data
        String label = "! 0 200 200 250 1\r\n" +
                "PW %s\r\n" +
                "COUNTRY GERMANY\r\n" +
                "CENTER\r\n" +
                "T 4 0 25 20 %s\r\n" + //Restaurant name
                "T 5 1 25 66 %s\r\n" + //Restaurant address
                "T 5 1 25 112 %s\r\n" + //Restaurant city
                "LEFT\r\n" +
                "T 7 0 10 162 %s: %s\r\n" + //Receipt Number
                "RIGHT\r\n" +
                "T 7 0 400 162 %s: %s\r\n" + //Table Number
                "LEFT\r\n" +
                "T 7 0 10 186 %s: "+ order.getServerName(null) +"\r\n" + //Server name
                "RIGHT\r\n" +
                "T 7 0 400 186 %s: "+ order.getGuestNumber() +"\r\n" +  //# of guests
                "RIGHT\r\n" +
                "T 0 2 10 210 "+ dateFormat.format(cal.getTime()) +"\r\n" +  //Date
                "LINE 0 245 550 245 1\r\n" +
                "POSTFEED 0\r\n\r\n" +
                "PRINT\r\n";

        ticket.append(label);
        ticket.append( "! U1 SETLP 7 0 24\r\n");

        DefaultPosData defaultPosData = new PosDefaultDataManagement(null).getDefaultData();

        //If single lines for every product -> Configurable
        if(!defaultPosData.isCombineItems()) {
            for(POSOrderLine line : order.getOrderedLines()) {
                ticket.append(line.getQtyOrdered() + "  " + line.getProduct().getProductName() + "            "+
                        currencyFormat.format(line.getLineNetAmt()) + "\r\n");
                if(line.getProductRemark() != null && !line.getProductRemark().isEmpty())
                    ticket.append("    " + line.getProductRemark() + "\r\n");
            }
        } else {
            //If summarized lines for receipts
            String format = "  %-3s%-32s%7s";
            for(ReportGenericObject line : order.getSummarizeLines()) {
                ticket.append(String.format(format, line.getQuantity(), line.getDescription(),currencyFormat.format(line.getAmount())) + "\r\n");
            }
        }

        //footer
        StringBuilder footer = new StringBuilder();
        int height = 300;

        int posY = 15;

        footer.append("! 0 200 200 " + height +" 1\r\n"); //offset - 200 - 200 - height -qty
        footer.append("LEFT\r\n");
        footer.append("LINE 0 0 550 0 2\r\n");

        footer.append("T 7 1 25 " + posY + " %s\r\n"); //Amount label
        footer.append("RIGHT\r\n");
        footer.append("T 7 1 400 " + posY + " " + currencyFormat.format(order.getTotallines()) +"\r\n"); //Amount of the lines
        posY = posY + 40;

        footer.append("LEFT\r\n");
        footer.append("T 7 1 25 " + posY + " %s\r\n"); //Charge label
        footer.append("RIGHT\r\n");
        footer.append("T 7 1 400 " + posY + " " + currencyFormat.format(order.getSurcharge()) +"\r\n"); //Charge amount
        posY = posY + 40;

        footer.append("LEFT\r\n");
        footer.append("T 7 1 25 " + posY + " %s\r\n"); //Total label
        footer.append("RIGHT\r\n");
        footer.append("T 7 1 400 " + posY + " " + currencyFormat.format(order.getTotallines().add(order.getSurcharge())) +"\r\n"); //Total amount

        posY = posY + 40;
        footer.append("LEFT\r\n");
        footer.append("T 7 1 25 " + posY + " %s\r\n");
        footer.append("RIGHT\r\n");
        footer.append("T 7 1 400 " + posY + " " + currencyFormat.format(order.getCashAmt()) +"\r\n"); //Received amount

        posY = posY + 40;
        footer.append("LEFT\r\n");
        footer.append("T 7 1 25 " + posY + " %s\r\n"); //back label
        footer.append("RIGHT\r\n");
        footer.append("T 7 1 400 " + posY + " " + currencyFormat.format(order.getChangeAmt()) +"\r\n"); //back amount

        posY = posY + 45;
        footer.append("CENTER\r\n"); //back amount
        footer.append("T 7 1 10 "+ posY + " %s\r\n"); //Footer message
        footer.append("POSTFEED 20\r\n");
        footer.append("FORM \r\n\r\n");
        footer.append("PRINT\r\n");

        /*label = "! 0 200 200 200 1\r\n" +
                "LEFT\r\n" +
                "LINE 0 0 550 0 2\r\n" +
                "T 7 1 25 15 %s\r\n" + //Total label
                "RIGHT\r\n" +
                "T 7 1 400 15 "+ currencyFormat.format(order.getTotallines()) +"\r\n" + //Total value
                "LEFT\r\n" +
                "T 7 1 25 55 %s\r\n" + //Received
                "RIGHT\r\n" +
                "T 7 1 400 55 " + currencyFormat.format(order.getCashAmt()) +"\r\n" + //Received value
                "LEFT\r\n" +
                "T 7 1 25 95 %s\r\n" + //back label
                "RIGHT\r\n" +
                "T 7 1 400 95 " + currencyFormat.format(order.getChangeAmt()) +"\r\n" + //back
                "CENTER\r\n" +
                "T 7 1 10 150 %s\r\n" + //Footer message
                "POSTFEED 20\r\n" +
                "FORM \r\n\r\n"+
                "PRINT\r\n";*/

        ticket.append(footer.toString());

        return ticket.toString();
    }
}
