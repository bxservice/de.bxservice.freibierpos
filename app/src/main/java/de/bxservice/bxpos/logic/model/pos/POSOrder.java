package de.bxservice.bxpos.logic.model.pos;

import android.content.Context;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

import de.bxservice.bxpos.logic.DataMediator;
import de.bxservice.bxpos.logic.daomanager.PosOrderManagement;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.Table;

/**
 * This represents the draft order - contains
 * everything that is selected and set before sending
 * the order
 * Created by Diego Ruiz on 25/11/15.
 */
public class POSOrder implements Serializable {

    //Manager in charge to communicate with the database - not mixing the model and db layers
    private PosOrderManagement orderManager;
    //Order status
    public static final String DRAFT_STATUS    = "DRAFT";
    public static final String SENT_STATUS     = "SENT";
    public static final String COMPLETE_STATUS = "COMPLETE";

    /**
     * Boolean that defines if the lines are shown as individual always
     * or as sum up lines. e.g. 2 Apples - instead of w lines with 1 apple
     *
     * By default it will always be single line, but this is left here for
     * future customization if wanted
     */
    private boolean isAlwaysOneLine = true;

    //Checks if the product was ordered before
    private HashMap<MProduct, POSOrderLine> orderlineProductHashMap = new HashMap<>();
    //Checks how many times a product has been ordered
    private HashMap<Integer, Integer> orderlineProductQtyHashMap = new HashMap<>();

    private ArrayList<POSOrderLine> orderLines = new ArrayList<>();
    private String orderRemark;

    private int currentLineNo = 10;
    private long orderId;
    private Table table;
    private int guestNumber;
    private String status;
    private BigDecimal totallines = BigDecimal.ZERO;

    public void addItem(MProduct product) {

        boolean newItem = true;

        if (!isAlwaysOneLine) {

            //Check if the product was ordered before
            if (!orderlineProductHashMap.isEmpty()) {

                POSOrderLine POSOrderLine = orderlineProductHashMap.get(product);
                if(POSOrderLine != null){
                    POSOrderLine.setQtyOrdered(POSOrderLine.getQtyOrdered() + 1); //add 1 to the qty previously ordered
                    newItem = false;
                }

            }
        }

        if(newItem) {

            POSOrderLine posOrderLine = new POSOrderLine();
            posOrderLine.setOrder(this);
            posOrderLine.setProduct(product);
            posOrderLine.setQtyOrdered(1); //If new item - is the first item that is added
            posOrderLine.setLineStatus(posOrderLine.ORDERING);
            posOrderLine.setLineNo(currentLineNo);

            orderLines.add(posOrderLine);

            if(isAlwaysOneLine) {

                //If the list is empty - is the first time the product is ordered
                if (orderlineProductQtyHashMap.isEmpty() || orderlineProductQtyHashMap.get(product.getProductID()) == null) {
                    orderlineProductQtyHashMap.put(product.getProductID(), 1);
                } else {
                     orderlineProductQtyHashMap.put(product.getProductID(), orderlineProductQtyHashMap.get(product.getProductID()) + 1);
                }

            }else {
                orderlineProductHashMap.put(product, posOrderLine);
            }

            currentLineNo += 10; //Sets the lineNo 10 by 10 like in iDempiere
        }

    }

    /**
     * Removes an item from the list
     * @param position
     */
    public void removeItem (int position) {

        POSOrderLine orderLine = orderLines.get(position);
        MProduct product = orderLine.getProduct();

        if (isAlwaysOneLine) {
            //If there was one product - remove it
            if (orderlineProductQtyHashMap.get(product.getProductID()) == 1 ) {
                orderlineProductQtyHashMap.remove(product.getProductID());
            } else {
                orderlineProductQtyHashMap.put(product.getProductID(), orderlineProductQtyHashMap.get(product.getProductID()) - 1);
            }

        } else {
            orderlineProductHashMap.remove(product);
        }

        orderLines.remove(position);

    }

    /**
     * Called when undo deleting
     * @param position
     * @param orderLine
     */
    public void addItem (int position, POSOrderLine orderLine) {

        MProduct product = orderLine.getProduct();

        orderLines.add(position, orderLine);

        if (isAlwaysOneLine) {
            //If the list is empty - is the first time the product is ordered
            if (orderlineProductQtyHashMap.isEmpty() || orderlineProductQtyHashMap.get(product.getProductID()) == null) {
                orderlineProductQtyHashMap.put(product.getProductID(), 1);
            } else {
                orderlineProductQtyHashMap.put(product.getProductID(), orderlineProductQtyHashMap.get(product.getProductID()) + 1);
            }
        } else {
            orderlineProductHashMap.put(product, orderLine);
        }

    }

    public int getProductQtyOrdered(MProduct product) {

        if (isAlwaysOneLine && orderlineProductQtyHashMap.get(product.getProductID()) != null) {
            return orderlineProductQtyHashMap.get(product.getProductID());

        }
        else if (orderlineProductHashMap.get(product) != null)
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

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    /**
     * Set table from Id
     * @param tableId
     */
    public void setTable(long tableId) {
        if(DataMediator.getInstance().getTableGroupList() != null) {

            for(int i = 0; i < DataMediator.getInstance().getTableGroupList().size() ; i++ ) {

                for (Table table : DataMediator.getInstance().getTableGroupList().get(i).getTables()) {
                    if (table.getTableID() == tableId) {
                        setTable(table);
                        return;
                    }
                }

            }
        }

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        if (status.equals(DRAFT_STATUS) ||
                status.equals(SENT_STATUS) ||
                status.equals(COMPLETE_STATUS))
        this.status = status;

    }

    /**
     * Recreates the orderLines array
     */
    public void recreateOrderLines(ArrayList<POSOrderLine> newOrderLines) {
        clearOrderLines();
        for (POSOrderLine orderLine: newOrderLines) {
            addItem(orderLine.getProduct());
        }
    }

    /**
     * Clear all the objects related to order lines
     */
    private void clearOrderLines() {
        orderLines.clear();
        orderlineProductQtyHashMap.clear();
        orderlineProductHashMap.clear();
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    /**
     * Returns the total sum of the order lines
     * @return
     */
    public BigDecimal getTotallines() {
        for (POSOrderLine orderLine : getOrderLines()) {
            totallines = orderLine.getLineNetAmt().add(totallines);
        }
        return totallines;
    }

    /**
     * Returns the total sum of the order lines
     * in an integer to be save in the database
     * @return
     */
    public Integer getTotallinesInteger() {
        Integer total;
        total = Integer.valueOf(getTotallines().multiply(BigDecimal.valueOf(100)).intValue()); //total * 100

        return total;
    }

    public void setTotallines(BigDecimal totallines) {
        this.totallines = totallines;
    }

    /**
     * Gets an integer value from the db and converts it to a BigDecimal
     * last two digits are decimals
     * @param total
     */
    public void setTotalFromInt(Integer total) {
        //TODO
        setTotallines(BigDecimal.valueOf(total / 100));
        System.out.println(totallines);
        //this.totallines = totallines;
    }

    public boolean sendOrder (Context ctx) {

        orderManager = new PosOrderManagement(ctx);
        boolean result;

        completeOrder();

        result = orderManager.create(this);

        if(!result)
            uncompleteOrder();
        else {
            if(table != null)
                table.occupyTable(ctx);
        }

        return result;

    }

    public void completeOrder() {
        setStatus(SENT_STATUS);
        for (POSOrderLine orderLine : getOrderLines()) {
            orderLine.completeLine();
        }
    }

    public void uncompleteOrder() {
        setStatus(DRAFT_STATUS);
        for (POSOrderLine orderLine : getOrderLines()) {
            orderLine.uncompleteLine();
        }
    }

}
