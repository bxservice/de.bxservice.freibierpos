package de.bxservice.bxpos.logic.model.pos;

import android.content.Context;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

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

    private ArrayList<POSOrderLine> orderingLines = new ArrayList<>();
    private ArrayList<POSOrderLine> orderedLines = new ArrayList<>();
    private String orderRemark = "";

    private int currentLineNo = 10;
    private long orderId;
    private Table table;
    private int guestNumber = 0;
    private String status;
    private BigDecimal totallines = BigDecimal.ZERO;
    private boolean sync = false;

    public void addItem(MProduct product, Context ctx) {

        boolean newItem = true;

        if (!isAlwaysOneLine) {

            //Check if the product was ordered before
            if (!orderlineProductHashMap.isEmpty()) {

                POSOrderLine orderLine = orderlineProductHashMap.get(product);
                if(orderLine != null){
                    orderLine.setQtyOrdered(orderLine.getQtyOrdered() + 1); //add 1 to the qty previously ordered
                    newItem = false;
                    orderLine.createLine(ctx);
                }

            }
        }

        if(newItem) {

            POSOrderLine posOrderLine = new POSOrderLine();
            posOrderLine.setOrder(this);
            posOrderLine.setProduct(product);
            posOrderLine.setQtyOrdered(1); //If new item - is the first item that is added
            posOrderLine.setLineStatus(POSOrderLine.ORDERING);
            posOrderLine.setLineNo(currentLineNo);

            orderingLines.add(posOrderLine);
            posOrderLine.createLine(ctx);

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
     * Remove all items that were not send
     */
    public void removeOrderingItems() {
        int orderingSize = orderingLines.size();
        for (int i = 0; i < orderingSize; i++)
            removeItem(0);
    }

    /**
     * Removes an item from the list
     * @param position
     */
    public void removeItem (int position) {

        POSOrderLine orderLine = orderingLines.get(position);
        MProduct product = orderLine.getProduct();

        if (isAlwaysOneLine && orderlineProductQtyHashMap.get(product.getProductID()) != null) {
            //If there was one product - remove it
            if (orderlineProductQtyHashMap.get(product.getProductID()) == 1) {
                orderlineProductQtyHashMap.remove(product.getProductID());
            } else {
                orderlineProductQtyHashMap.put(product.getProductID(), orderlineProductQtyHashMap.get(product.getProductID()) - 1);
            }

        } else {
            orderlineProductHashMap.remove(product);
        }

        orderingLines.remove(position);
        orderLine.remove(null);
    }

    /**
     * Called when undo deleting
     * @param position
     * @param orderLine
     */
    public void addItem (int position, POSOrderLine orderLine) {

        //Copy the orderLine
        if(orderingLines.contains(orderLine)) {
            addItem(orderLine.getProduct(), null);
            return;
        }

        MProduct product = orderLine.getProduct();

        orderingLines.add(position, orderLine);

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

    public ArrayList<POSOrderLine> getOrderingLines() {
        return orderingLines;
    }

    public void setOrderingLines(ArrayList<POSOrderLine> orderLines) {
        this.orderingLines = orderLines;
    }

    public ArrayList<POSOrderLine> getOrderedLines() {
        return orderedLines;
    }

    public void setOrderedLines(ArrayList<POSOrderLine> orderedLines) {
        this.orderedLines = orderedLines;
    }

    /**
     * Return all the ordered lines that has not been voided
     * @return
     */
    public ArrayList<POSOrderLine> getOrderedLinesNoVoid() {
        ArrayList<POSOrderLine> noVoidedLines = new ArrayList<>();

        for(POSOrderLine line : orderedLines) {
            if(!line.getLineStatus().equals(POSOrderLine.VOIDED) && line.getQtyOrdered() > 0)
                noVoidedLines.add(line);
        }

        return noVoidedLines;
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

    public boolean isSync() {
        return sync;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public void setCurrentLineNo() {
        if(orderedLines != null && !orderedLines.isEmpty())
            currentLineNo = orderedLines.get(orderedLines.size() - 1).getLineNo() + 10;
    }

    /**
     * Set table from Id
     * @param tableId
     */
    public void setTable(long tableId) {

        if(orderManager == null)
            orderManager = new PosOrderManagement(null);

        Table table = orderManager.getTable(tableId);
        if (table != null)
            this.table = table;
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
            addItem(orderLine.getProduct(), null);
        }
    }

    /**
     * Clear all the objects related to order lines
     */

    private void clearOrderLines() {
        orderingLines.clear();
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
        totallines = BigDecimal.ZERO;
        for (POSOrderLine orderLine : orderedLines) {
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
        double doubleValue = (double) total / 100;
        this.totallines = BigDecimal.valueOf(doubleValue);
    }

    public boolean sendOrder (Context ctx) {

        orderManager = new PosOrderManagement(ctx);
        boolean result;

        completeOrder();

        result = orderManager.update(this);

        if(!result)
            uncompleteOrder();
        else {
            if(table != null)
                table.occupyTable(ctx);
        }

        return result;

    }

    public boolean createOrder (Context ctx) {
        orderManager = new PosOrderManagement(ctx);
        return orderManager.create(this);
    }

    public boolean updateOrder (Context ctx) {
        orderManager = new PosOrderManagement(ctx);
        return orderManager.update(this);
    }

    public void completeOrder() {
        status = SENT_STATUS;
        for (POSOrderLine orderLine : orderingLines) {
            orderLine.completeLine();
            orderLine.updateLine(null);
            orderedLines.add(orderLine);
        }
        orderingLines.clear();
    }

    /**
     * When the pay button is clicked completes the order
     * set status complete and free the table
     * Synchronized flag depends on the success of the creation in
     * iDempiere
     * @param isSynchronized
     */
    public void payOrder(boolean isSynchronized, Context ctx) {
        status = COMPLETE_STATUS;
        sync = isSynchronized;
        updateOrder(ctx);

        //TODO: Wrong freeing table when offline sync -> multi order on table different behaviour
        if(table != null) {
            table.freeTable(ctx);
        }

    }

    public void uncompleteOrder() {
        status = DRAFT_STATUS;
        for (POSOrderLine orderLine : orderedLines) {
            orderLine.uncompleteLine();
            orderLine.updateLine(null);
            orderingLines.add(orderLine);
        }
        orderedLines.clear();
    }

    public boolean remove(Context ctx) {
        orderManager = new PosOrderManagement(ctx);
        return orderManager.remove(this);
    }

    /**
     * Receive another order and joins them
     * @param originOrder
     * @param ctx
     */
    public void joinOrders(POSOrder originOrder, Context ctx) {

        //Join guests - Sum up the guests
        guestNumber = guestNumber + originOrder.getGuestNumber();

        //Join remarks - Concatenate the remarks
        orderRemark = orderRemark  + " " + originOrder.getOrderRemark();

        //Join the ordered lines from the two orders
        for (POSOrderLine originLine : originOrder.getOrderedLines()) {
            originLine.setOrder(this);
            originLine.updateLine(ctx);
            orderedLines.add(originLine);
        }

        //Join the ordering lines from the two orders
        for (POSOrderLine originLine : originOrder.getOrderingLines()) {
            originLine.setOrder(this);
            originLine.updateLine(ctx);
            orderingLines.add(originLine);
        }

        updateOrder(ctx);

        // Free the table of the merged order
        if (originOrder.getTable() != null) {
            originOrder.getTable().freeTable(ctx);
        }

        //Delete the merged order from the db
        originOrder.remove(ctx);
    }

    /**
     * Split one order into two
     * @param destinationOrder
     * @param toSplitLines
     * @param ctx
     */
    public void splitOrder(POSOrder destinationOrder, ArrayList<POSOrderLine> toSplitLines, Context ctx) {

        if(toSplitLines == null || toSplitLines.isEmpty())
            return;

        //If order is null -> new order
        if(destinationOrder == null) {
            POSOrder newOrder = new POSOrder();

            newOrder.createOrder(ctx);
            newOrder.setStatus(SENT_STATUS);
            newOrder.setOrderedLines(toSplitLines);

            newOrder.createOrder(ctx);

            for (POSOrderLine orderLine : toSplitLines) {
                orderLine.setOrder(newOrder);
                orderLine.updateLine(ctx);
            }
            //TODO: When split left the same table -> multiple tanlñes
            //newOrder.setTable(table);

        } else {
            for (POSOrderLine orderLine : toSplitLines) {
                orderLine.setOrder(destinationOrder);
                orderLine.updateLine(ctx);
                destinationOrder.getOrderedLines().add(orderLine);
            }
        }

    }

    /**
     * Void line by creating a copy of the original line
     * but with negative qty
     */
    public boolean voidLine(int position, String reason) {

        POSOrderLine voidedLine = orderedLines.get(position); //Original line to be voided

        //If the selected line is voided or is a voiding error
        if(!voidedLine.isVoidable())
            return false;

        POSOrderLine voidLine = new POSOrderLine(); //Line that voids with -quantity

        //Copy all the values from the original line
        voidLine.setProductRemark(voidedLine.getProductRemark());
        voidLine.setProduct(voidedLine.getProduct());
        voidLine.setOrder(voidedLine.getOrder());
        voidLine.setQtyOrdered(-1 * voidedLine.getQtyOrdered()); //Same quantity opposite sign
        voidLine.setLineNo(voidedLine.getLineNo() + 5); //Normal lines raise from 10 to 10
        voidLine.completeLine();

        voidedLine.voidLine(reason);
        voidedLine.updateLine(null);

        orderedLines.add(position + 1, voidLine);
        voidLine.createLine(null);

        return true;
    }

}
