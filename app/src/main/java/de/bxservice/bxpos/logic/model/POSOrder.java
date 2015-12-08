package de.bxservice.bxpos.logic.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This represents the draft order - contains
 * everything that is selected and set before sending
 * the order
 * Created by Diego Ruiz on 25/11/15.
 */
public class POSOrder implements Serializable {

    //Order status
    public static final String DRAFT_STATUS    = "DRAFT";
    public static final String SENT_STATUS     = "SENT";
    public static final String COMPLETE_STATUS = "COMPLETE";

    private HashMap<MProduct, POSOrderLine> orderlineProductHashMap = new HashMap<MProduct, POSOrderLine>();

    private ArrayList<POSOrderLine> orderLines = new ArrayList<POSOrderLine>();
    private String orderRemark;

    //TODO: Change for Table object reference
    private String table;
    private int guestNumber;
    private String status;

    public void addItem(MProduct product) {

        boolean newItem = true;

        //Check if the product was ordered before
        if ( !orderlineProductHashMap.isEmpty() ){

            POSOrderLine POSOrderLine = orderlineProductHashMap.get(product);
            if( POSOrderLine != null ){
                POSOrderLine.setQtyOrdered(POSOrderLine.getQtyOrdered() + 1); //add 1 to the qty previously ordered
                newItem = false;
            }

        }

        if( newItem ){

            POSOrderLine posOrderLine = new POSOrderLine();
            posOrderLine.setOrder(this);
            posOrderLine.setProduct(product);
            posOrderLine.setQtyOrdered(1); //If new item - is the first item that is added
            posOrderLine.setLineStatus(posOrderLine.ORDERING);

            orderLines.add(posOrderLine);
            orderlineProductHashMap.put(product, posOrderLine);
        }

    }

    public int getProductQtyOrdered(MProduct product) {

        if ( orderlineProductHashMap.get(product) != null )
            return orderlineProductHashMap.get(product).getQtyOrdered();

        return 0;
    }

    public ArrayList<POSOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(ArrayList<POSOrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public String getOrderRemark() {
        return orderRemark;
    }

    public void setOrderRemark(String orderRemark) {
        this.orderRemark = orderRemark;
    }

    public int getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(int guestNumber) {
        this.guestNumber = guestNumber;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        if ( status.equals(DRAFT_STATUS) ||
                status.equals(SENT_STATUS) ||
                status.equals(COMPLETE_STATUS) )
        this.status = status;

    }
}
