package de.bxservice.bxpos.logic.print;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.pos.POSOrder;
import de.bxservice.bxpos.logic.model.pos.POSOrderLine;
import de.bxservice.bxpos.logic.model.report.ReportGenericObject;

/**
 * Created by Diego Ruiz on 21/04/16.
 */
public class CPCLPrinter extends AbstractPOSPrinter {

    private static final String KITCHEN_RECEIPT = "KITCHEN";
    private static final String BAR_RECEIPT     = "BAR";

    public CPCLPrinter(POSOrder order) {
        super(order);
    }


    @Override
    public String print() {

        /*return String.format("! 0 200 200 600 1\r\n" +
                "PW 550\r\n" +
                "TONE 0\r\n" +
                "SPEED 2\r\n" +
                "ON-FEED IGNORE\r\n" +
                "NO-PACE\r\n" +
                "POSTFEED 50\r\n" +
                "JOURNAL\r\n" +
                //"BOX 0 0 376 797 5\r\n" +
                "T 5 1 25 20 Type:\r\n" +
                "T 5 1 90 20 %s\r\n" +
                "T 5 1 25 75 Server:\r\n" +
                "T 5 1 110 75 %s\r\n" +
                "T 5 1 25 130 Guest:\r\n" +
                "T 5 1 100 130 %s\r\n" +
                "T 5 1 300 20 Table\r\n" +
                "T 5 3 300 75 %s\r\n" +
                "LINE 25 175 530 175 1\r\n" +
                "T 5 0 35 421 Address:\r\n" +
                "T 5 0 35 342 Last Name:\r\n" +
                "T 5 0 35 257 First Name:\r\n" +
                "T 5 0 35 175 Plate #:\r\n" +
                "T 5 0 64 290 %s\r\n" +
                "T 5 0 64 374 %s\r\n" +
                "T 5 0 64 466 %s\r\n" +
                "T 5 0 64 558 %s\r\n" +
                "FORM \r\n\r\n"+
                "PRINT\r\n", new Object[] { "Dine-in", "Garden", "5", "Table 1","Carlos", "Ruiz", "AAA 2356", "None" });

                /*return "! U1 JOURNAL\r\n" +
                "! U1 SETLP 5 2 24\r\n" +
                "Order #: "+ order.getOrderId() +"\r\n" +
                "Type: Dine-in\r\n" +
                "Server: Garden\r\n" +
                "Guests: "+ order.getGuestNumber() +"\r\n" +
                //"! U1 LINE 25 175 530 175 1\r\n" +
                "! U1 SETLP 5 0 24\r\n" +

                "! U1 SETLP 7 0 24\r\n" +
                order.getOrderedLines().get(0).getQtyOrdered() + "  " + order.getOrderedLines().get(0).getProduct().getProductName() + "\r\n" +
                order.getOrderedLines().get(1).getQtyOrdered() + "  " + order.getOrderedLines().get(1).getProduct().getProductName() + "\r\n" +
                "! U1 PRESENT-AT\r\n" +
                "! U1 PRINT\r\n";*/

        return null;
    }

    /**
     * Print the kitchen receipt
     * Returns a string with %s
     * 1 - Order string
     * 2 - Table
     * 3 - Table Number
     * 3 - Server
     * 4 - Guests
     */
    @Override
    public String printKitchen() {
        return getPrintout(KITCHEN_RECEIPT);

    }

    @Override
    public String printBar() {
        return getPrintout(BAR_RECEIPT);
    }

    private String getPrintout(String target) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        StringBuilder ticket = new StringBuilder();

        ticket.append("! U1 JOURNAL\r\n");
        ticket.append("! U1 SETLP 5 3 86\r\n");
        ticket.append("\r\n");
        ticket.append("%s #: "+ order.getOrderId() +"\r\n");
        ticket.append( "! U1 SETLP 7 0 24\r\n");
        ticket.append( "%s: %s\r\n");
        ticket.append("%s: " + order.getServerName() +"\r\n");
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
     * 10 - Total String
     * 11 - Cash string
     * 12 - Back string
     * 13 - Footer description
     * @return String for receipt printing
     */
    @Override
    public String printReceipt() {
        StringBuilder ticket = new StringBuilder();

        NumberFormat currencyFormat = NumberFormat.getNumberInstance(DefaultPosData.LOCALE);
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        //header
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
                "T 7 0 10 186 %s: "+ order.getServerName() +"\r\n" + //Server name
                "RIGHT\r\n" +
                "T 7 0 400 186 %s: "+ order.getGuestNumber() +"\r\n" +  //# of guests
                "RIGHT\r\n" +
                "T 0 2 10 210 "+ dateFormat.format(cal.getTime()) +"\r\n" +  //Date
                "LINE 0 245 550 245 1\r\n" +
                "POSTFEED 0\r\n\r\n" +
                "PRINT\r\n";

        ticket.append(label);

        ticket.append( "! U1 SETLP 7 0 24\r\n");
        //If single lines for every product -> Configurable
        /*for(POSOrderLine line : order.getOrderedLines()) {
            ticket.append(line.getQtyOrdered() + "  " + line.getProduct().getProductName() + "            "+
                    currencyFormat.format(line.getLineNetAmt()) + "\r\n");
            if(line.getProductRemark() != null && !line.getProductRemark().isEmpty())
                ticket.append("    " + line.getProductRemark() + "\r\n");
        }*/

        //If summarized lines for receipts
        String format = "  %-3s%-32s%7s";
        for(ReportGenericObject line : order.getSummarizeLines()) {
            ticket.append(String.format(format, line.getQuantity(), line.getDescription(),currencyFormat.format(line.getAmount())) + "\r\n");
        }


        //footer
        label = "! 0 200 200 200 1\r\n" +
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
                "PRINT\r\n";

        ticket.append(label);

        return ticket.toString();
    }
}
