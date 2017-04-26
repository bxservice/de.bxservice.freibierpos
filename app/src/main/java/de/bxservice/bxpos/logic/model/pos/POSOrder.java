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

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.bxservice.bxpos.logic.daomanager.PosOrderManagement;
import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.idempiere.IOrder;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.StandardTaxProvider;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.report.ReportGenericObject;
import de.bxservice.bxpos.ui.utilities.PreferenceActivityHelper;

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
    public static final String VOID_STATUS     = "VOID";
    public static final String COMPLETE_STATUS = "COMPLETE";

    /**
     * Boolean that defines if the lines are shown as individual always
     * or as sum up lines. e.g. 2 Apples - instead of w lines with 1 apple
     */
    private boolean isAlwaysOneLine = true;

    //Checks how many times a product has been ordered
    private HashMap<Integer, Integer> orderlineProductQtyHashMap = new HashMap<>();

    private ArrayList<POSOrderLine> orderingLines = new ArrayList<>();
    private ArrayList<POSOrderLine> orderedLines  = new ArrayList<>();
    private ArrayList<POSPayment>   payments      = new ArrayList<>();
    private ArrayList<POSOrderTax>  orderTaxes;
    private String orderRemark = "";

    private int currentLineNo = 10;
    private long orderId;
    private String documentNo;
    private Table table;
    private int guestNumber = 0;
    private String status;
    private String discountReason = "";
    private String paymentRule = IOrder.PAYMENTRULE_Cash; //Default cash payments
    private BigDecimal totallines = BigDecimal.ZERO;
    private BigDecimal discount   = BigDecimal.ZERO;
    private BigDecimal surcharge  = BigDecimal.ZERO;
    private BigDecimal cashAmt    = BigDecimal.ZERO;
    private BigDecimal changeAmt  = BigDecimal.ZERO;
    private boolean sync = false;

    public POSOrder() {
        //Define if the items will be shown in single lines or summarized
        isAlwaysOneLine = DefaultPosData.get(null).isSeparateOrderItems();
    }

    public void addItem(MProduct product, Context ctx) {
        addItem(product, null, ctx);
    }

    /**
     * Add an order line to the order
     * if the product has related products to be added automatically, add those line also
     * @param product         Product in the line
     * @param overridePrice   Override product price with a different value
     * @param ctx             Context
     */
    public void addItem(MProduct product, @Nullable BigDecimal overridePrice, Context ctx) {

        boolean newItem = true;
        POSOrderLine orderLine = null;

        if (!isAlwaysOneLine) {

            //Check if the product was ordered before
            if (!orderingLines.isEmpty()) {

                orderLine = getOrderingLine(product);
                if(orderLine != null){
                    newItem = false;
                    orderLine.setQtyOrdered(orderLine.getQtyOrdered() + 1); //add 1 to the qty previously ordered
                    if (overridePrice != null) {
                        orderLine.setLineNetAmt(orderLine.getLineNetAmt().add(overridePrice));
                    }
                    orderLine.updateLine(ctx);
                }
            }
        }

        if (newItem) {

            orderLine = new POSOrderLine();
            orderLine.setOrder(this);
            orderLine.setProduct(product);
            orderLine.setQtyOrdered(1); //If new item - is the first item that is added
            orderLine.setLineStatus(POSOrderLine.ORDERING);
            orderLine.setLineNo(currentLineNo);
            if (overridePrice != null) {
                orderLine.setLineNetAmt(overridePrice);
            }

            orderingLines.add(orderLine);
            orderLine.createLine(ctx);

            if (isAlwaysOneLine) {

                //If the list is empty - is the first time the product is ordered
                if (orderlineProductQtyHashMap.isEmpty() || orderlineProductQtyHashMap.get(product.getProductID()) == null) {
                    orderlineProductQtyHashMap.put(product.getProductID(), 1);
                } else {
                     orderlineProductQtyHashMap.put(product.getProductID(), orderlineProductQtyHashMap.get(product.getProductID()) + 1);
                }
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
     * @param position of the item in the list
     */
    public void removeItem(int position) {

        POSOrderLine orderLine = orderingLines.get(position);
        removeItemFromHashMap(orderLine.getProduct());
        orderingLines.remove(position);
        orderLine.remove(null);
    }

    private void removeItemFromHashMap(MProduct product ) {

        if (isAlwaysOneLine && orderlineProductQtyHashMap.get(product.getProductID()) != null) {
            //If there was one product - remove it
            if (orderlineProductQtyHashMap.get(product.getProductID()) == 1) {
                orderlineProductQtyHashMap.remove(product.getProductID());
            } else {
                orderlineProductQtyHashMap.put(product.getProductID(), orderlineProductQtyHashMap.get(product.getProductID()) - 1);
            }
        }
    }

    /**
     * Called when undo deleting
     * @param position
     * @param orderLine
     */
    public void addItem(int position, POSOrderLine orderLine) {

        //Copy the orderLine
        if (orderingLines.contains(orderLine)) {
            addItem(orderLine.getProduct(), orderLine.getLineNetAmt(), null);
            return;
        }

        orderingLines.add(position, orderLine);

        if (isAlwaysOneLine) {
            MProduct product = orderLine.getProduct();
            //If the list is empty - is the first time the product is ordered
            if (orderlineProductQtyHashMap.isEmpty() || orderlineProductQtyHashMap.get(product.getProductID()) == null) {
                orderlineProductQtyHashMap.put(product.getProductID(), 1);
            } else {
                orderlineProductQtyHashMap.put(product.getProductID(), orderlineProductQtyHashMap.get(product.getProductID()) + 1);
            }
        }
    }

    public int getProductQtyOrdered(MProduct product) {

        if (isAlwaysOneLine && orderlineProductQtyHashMap.get(product.getProductID()) != null) {
            return orderlineProductQtyHashMap.get(product.getProductID());

        } else {
            POSOrderLine orderingLine = getOrderingLine(product);
            if (orderingLine != null)
                return orderingLine.getQtyOrdered();
        }

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

    public ArrayList<ReportGenericObject> getSummarizeLines() {
        ArrayList<ReportGenericObject> summarizeLines = new ArrayList<>();
        HashMap<String, Integer>    productQtyHashMap = new HashMap<>();
        HashMap<String, BigDecimal> productAmtHashMap = new HashMap<>();

        for(POSOrderLine line : orderedLines) {

            if (productQtyHashMap.isEmpty() || productQtyHashMap.get(line.getProduct().getProductName()) == null) {
                productQtyHashMap.put(line.getProduct().getProductName(), line.getQtyOrdered());
            } else {
                productQtyHashMap.put(line.getProduct().getProductName(), productQtyHashMap.get(line.getProduct().getProductName()) + line.getQtyOrdered());
            }

            if (productAmtHashMap.isEmpty() || productAmtHashMap.get(line.getProduct().getProductName()) == null) {
                productAmtHashMap.put(line.getProduct().getProductName(), line.getLineNetAmt());
            } else {
                productAmtHashMap.put(line.getProduct().getProductName(), productAmtHashMap.get(line.getProduct().getProductName()).add(line.getLineNetAmt()));
            }
        }

        for(String key : productQtyHashMap.keySet()) {
            ReportGenericObject line = new ReportGenericObject();
            line.setQuantity(String.valueOf(productQtyHashMap.get(key)));
            line.setDescription(key);
            line.setAmount(productAmtHashMap.get(key));
            summarizeLines.add(line);
        }

        return summarizeLines;
    }

    /**
     *
     * @return Array list with all the lines that are printed in the kitchen
     */
    public List<POSOrderLine> getPrintKitchenLines(Context ctx) {
        if(orderManager == null)
            orderManager = new PosOrderManagement(ctx);

        return orderManager.getPrintKitchenLines(this);
    }

    public List<POSOrderLine> getPrintBarLines(Context ctx) {
        if(orderManager == null)
            orderManager = new PosOrderManagement(ctx);

        return orderManager.getPrintBarLines(this);
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

    public String getDocumentNo() {
        return documentNo;
    }

    public void setDocumentNo(String documentNo) {
        this.documentNo = documentNo;
    }

    /**
     *  Get the numeric part of the document no
     * @return document number without the prefix
     */
    public String getOrderNo() {
        return documentNo.replaceAll("[^0-9]", "");
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
                status.equals(VOID_STATUS) ||
                status.equals(COMPLETE_STATUS))
        this.status = status;

    }

    public ArrayList<POSPayment> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<POSPayment> payments) {
        this.payments = payments;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getPaymentRule() {
        return paymentRule;
    }

    public void setPaymentRule(String paymentRule) {
        if (IOrder.PAYMENTRULE_Cash.equals(paymentRule) ||
                IOrder.PAYMENTRULE_CreditCard.equals(paymentRule) ||
                IOrder.PAYMENTRULE_MixedPOSPayment.equals(paymentRule))
            this.paymentRule = paymentRule;
    }

    /**
     * Returns the discount value
     * in an integer to be save in the database
     * @return
     */
    public Integer getDiscountInteger() {
        return discount.multiply(BigDecimal.valueOf(100)).intValue(); //total * 100
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    /**
     * Gets an integer value from the db and converts it to a BigDecimal
     * last two digits are decimals
     * @param discount
     */
    public void setDiscountFromInt(Integer discount) {
        double doubleValue = (double) discount / 100;
        this.discount = BigDecimal.valueOf(doubleValue);
    }

    /**
     * Returns the total surcharge
     * in an integer to be save in the database
     * @return
     */
    public Integer getSurchargeInteger() {
        return surcharge.multiply(BigDecimal.valueOf(100)).intValue(); //total * 100
    }

    public void setSurcharge(BigDecimal surcharge) {
        this.surcharge = surcharge;
    }

    public BigDecimal getSurcharge() {
        return surcharge;
    }

    /**
     * Gets an integer value from the db and converts it to a BigDecimal
     * last two digits are decimals
     * @param surcharge
     */
    public void setSurchargeFromInt(Integer surcharge) {
        double doubleValue = (double) surcharge / 100;
        this.surcharge = BigDecimal.valueOf(doubleValue);
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

    public String getTotal() {
        NumberFormat currencyFormat = PosProperties.getInstance().getCurrencyFormat();
        return currencyFormat.format(getTotallines());
    }

    public BigDecimal getChangeAmt() {
        return changeAmt;
    }

    public void setChangeAmt(BigDecimal changeAmt) {
        this.changeAmt = changeAmt;
    }

    public BigDecimal getCashAmt() {
        return cashAmt;
    }

    public void setCashAmt(BigDecimal cashAmt) {
        this.cashAmt = cashAmt;
    }

    public String getDiscountReason() {
        return discountReason;
    }

    public void setDiscountReason(String discountReason) {
        this.discountReason = discountReason;
    }

    public long getOrderDate(Context ctx) {
        if (orderManager == null)
            orderManager = new PosOrderManagement(ctx);

        return orderManager.getOrderDate(this);
    }

    /**
     * Returns the total sum of the ordering lines
     * @return
     */
    public BigDecimal getTotalOrderinglines() {
        totallines = BigDecimal.ZERO;
        for (POSOrderLine orderLine : orderingLines) {
            totallines = orderLine.getLineNetAmt().add(totallines);
        }
        return totallines;
    }

    public int getOrderedQty() {
        int totalQty = 0;
        for(POSOrderLine orderLine : orderedLines) {
            totalQty = totalQty + orderLine.getQtyOrdered();
        }

        return totalQty;
    }

    public int getOrderingQty() {
        int totalQty = 0;
        for(POSOrderLine orderLine : orderingLines) {
            totalQty = totalQty + orderLine.getQtyOrdered();
        }

        return totalQty;
    }


    /**
     * Returns the total sum of the order lines
     * in an integer to be save in the database
     * @return
     */
    public Integer getTotallinesInteger() {
        return getTotallines().multiply(BigDecimal.valueOf(100)).intValue(); //total * 100
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

    public boolean sendOrder(Context ctx) {

        orderManager = new PosOrderManagement(ctx);
        boolean result;

        completeOrder(ctx);

        result = orderManager.update(this);

        if(!result)
            uncompleteOrder(ctx);
        else if (table != null && !table.getStatus().equals(Table.BUSY_STATUS)) {
            table.setServerName(getServerName(ctx));
            table.occupyTable(ctx, true);
        }

        return result;

    }

    public String getServerName(Context ctx) {
        if (orderManager == null)
            orderManager = new PosOrderManagement(ctx);

        return orderManager.getServerName(this);
    }

    public boolean createOrder (Context ctx) {
        orderManager = new PosOrderManagement(ctx);

        //get values from preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        String orderPrefix = sharedPref.getString(PreferenceActivityHelper.KEY_ORDER_PREFIX, "");
        int orderNumber = Integer.parseInt(sharedPref.getString(PreferenceActivityHelper.KEY_ORDER_NUMBER, "1"));
        documentNo = orderPrefix + orderNumber;

        //If the order is created successfully - order Number +1
        if (orderManager.create(this)) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(PreferenceActivityHelper.KEY_ORDER_NUMBER, String.valueOf(orderNumber +1));
            editor.apply();
            return true;
        } else
            return false;
    }

    public boolean updateOrder (Context ctx) {
        orderManager = new PosOrderManagement(ctx);
        return orderManager.update(this);
    }

    private void completeOrder(Context ctx) {
        status = SENT_STATUS;

        if (isAlwaysOneLine || orderedLines.isEmpty()) {
            for (POSOrderLine orderLine : orderingLines) {
                orderLine.completeLine();
                orderLine.updateLine(ctx);
                orderedLines.add(orderLine);
            }
        } else {
            POSOrderLine previousOrderedLine;
            for (POSOrderLine orderLine : orderingLines) {
                previousOrderedLine = getOrderedLine(orderLine.getProduct());

                if (previousOrderedLine != null) {
                    previousOrderedLine.setQtyOrdered(previousOrderedLine.getQtyOrdered() + orderLine.getQtyOrdered());
                    orderLine.remove(ctx);
                    previousOrderedLine.updateLine(ctx);
                } else {
                    orderLine.completeLine();
                    orderLine.updateLine(ctx);
                    orderedLines.add(orderLine);
                }
            }
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

        if(payments != null && !payments.isEmpty()) {
            if(payments.size() == 1)
                paymentRule = payments.get(0).getPaymentRule();
            else {
                paymentRule = IOrder.PAYMENTRULE_MixedPOSPayment;
                for(POSPayment payment : payments) {
                    payment.setOrder(this);
                    payment.createPayment(ctx);
                }
            }
        }

        //Calculate tax
        calculateTaxTotal(ctx);

        status = COMPLETE_STATUS;
        sync = isSynchronized;
        updateOrder(ctx);

        if(table != null) {
            table.freeTable(ctx, true);
        }
    }

    private void uncompleteOrder(Context ctx) {
        status = DRAFT_STATUS;
        for (POSOrderLine orderLine : orderedLines) {
            orderLine.uncompleteLine();
            orderLine.updateLine(ctx);
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

        Table originTable = originOrder.getTable();

        //Delete the merged order from the db
        originOrder.remove(ctx);

        // Free the table of the merged order
        if (originTable != null) {
            originTable.freeTable(ctx, true);
        }
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

            newOrder.setStatus(SENT_STATUS);
            newOrder.setOrderedLines(toSplitLines);
            newOrder.setTable(table);

            newOrder.createOrder(ctx);

            for (POSOrderLine orderLine : toSplitLines) {
                orderLine.setOrder(newOrder);
                orderLine.updateLine(ctx);
            }

        } else {
            for (POSOrderLine orderLine : toSplitLines) {
                orderLine.setOrder(destinationOrder);
                orderLine.updateLine(ctx);
                destinationOrder.getOrderedLines().add(orderLine);
            }
        }

    }

    public ArrayList<POSOrderTax> getOrderTaxes() {

        if (orderTaxes == null)
            calculateTaxTotal(null);

        return orderTaxes;
    }

    /**
     * Returns the total sum of the taxes
     * @return
     */
    public BigDecimal getTotalTaxes() {
        BigDecimal totalTaxes = BigDecimal.ZERO;

        for (POSOrderTax tax : getOrderTaxes())
            totalTaxes = totalTaxes.add(tax.getTaxAmount());

        return totalTaxes;
    }

    public boolean calculateTaxTotal(Context ctx) {
        //Clean the existing taxes if any
        orderTaxes = new ArrayList<>();

        StandardTaxProvider calculator = new StandardTaxProvider();
        return calculator.calculateOrderTaxTotal(ctx, this);

    }	//	calculateTaxTotal

    private POSOrderLine getOrderingLine(MProduct product) {
        for (POSOrderLine line : orderingLines) {
            if (line.getProduct().getProductID() == product.getProductID())
                return line;
        }

        return null;
    }

    private POSOrderLine getOrderedLine(MProduct product) {
        for (POSOrderLine line : orderedLines) {
            if (line.getProduct().getProductID() == product.getProductID()
                    && !line.getLineStatus().equals(POSOrderLine.VOIDED)
                    && line.getQtyOrdered() > 0)
                return line;
        }

        return null;
    }

    /**
     * Get the tax amount grouped by tax rate
     * @return Map with the pairs rate - tax amount
     */
    public Map<Integer, BigDecimal> getTaxRates() {
        Map<Integer, BigDecimal> taxRates = new HashMap<>();

        for (POSOrderTax orderTax : getOrderTaxes()) {
            Integer rate = orderTax.getTax().getRate().intValue();
            if (!taxRates.containsKey(rate)) {
                taxRates.put(rate, orderTax.getTaxAmount());
            } else {
                BigDecimal oldTaxAmt = taxRates.get(rate);
                taxRates.put(rate, oldTaxAmt.add(orderTax.getTaxAmount()));
            }
        }

        return taxRates;
    }

    public static List<POSOrder> getUnsynchronizedOrders(Context ctx) {
        PosOrderManagement orderManager = new PosOrderManagement(ctx);
        return orderManager.getUnsynchronizedOrders();
    }

    public static List<POSOrder> getOpenOrders(Context ctx) {
        PosOrderManagement orderManager = new PosOrderManagement(ctx);
        return orderManager.getAllOpenOrders();
    }

    public static List<POSOrder> getClosedOrders(Context ctx) {
        PosOrderManagement orderManager = new PosOrderManagement(ctx);
        return orderManager.getClosedOrders();
    }

    public static List<POSOrder> getTableOrders(Context ctx, Table table) {
        PosOrderManagement orderManager = new PosOrderManagement(ctx);
        return orderManager.getTableOrders(table);
    }

    public static List<POSOrder> getPaidOrders(Context ctx, long fromDate, long toDate, boolean byUser) {
        PosOrderManagement orderManager = new PosOrderManagement(ctx);
        if (byUser)
            return orderManager.getUserPaidOrders(fromDate, toDate);
        else
            return orderManager.getPaidOrders(fromDate, toDate);
    }

    public boolean voidLine(int position, String reason, Context ctx) {
        return voidLine(orderedLines.get(position) /*Original line to be voided*/, reason, 1, ctx, false);
    }

    public boolean voidLine(int position, String reason, int qtyItemsToVoid, Context ctx) {
        return voidLine(orderedLines.get(position) /*Original line to be voided*/, reason, qtyItemsToVoid, ctx, false);
    }

    /**
     * Void line by creating a copy of the original line
     * but with negative qty
     */
    private boolean voidLine(POSOrderLine voidedLine, String reason, int qtyItemsToVoid, Context ctx, boolean forceVoid) {

        if (voidedLine == null)
            return false;

        //If the selected line is voided or is a voiding error
        if (!voidedLine.isVoidable() && !forceVoid)
            return false;

        if (isAlwaysOneLine || qtyItemsToVoid == voidedLine.getQtyOrdered()) {

            POSOrderLine voidLine = cloneLine(voidedLine, -1 * voidedLine.getQtyOrdered()); //Line that voids with -quantity
            voidLine.setLineNo(voidedLine.getLineNo() + 5); //Normal lines raise from 10 to 10
            voidLine.completeLine();

            voidedLine.voidLine(reason);
            voidedLine.updateLine(ctx);

            orderedLines.add(orderedLines.indexOf(voidedLine) + 1, voidLine);
            voidLine.createLine(ctx);

        } else {

            if (qtyItemsToVoid < 1)
                return false;

            POSOrderLine newVoidedLine = cloneLine(voidedLine, qtyItemsToVoid); //Copy of the original with the voided qty
            POSOrderLine voidLine = cloneLine(voidedLine, -1 * qtyItemsToVoid); //Line that voids with -quantity

            //Update the qty in the original line and leave it with complete status
            voidedLine.setQtyOrdered(voidedLine.getQtyOrdered() - qtyItemsToVoid);
            voidedLine.updateLine(ctx);

            //Create a new voided line with void status
            newVoidedLine.setLineNo(voidedLine.getLineNo() + 1); //Normal lines raise from 10 to 10
            newVoidedLine.voidLine(reason);
            newVoidedLine.createLine(ctx);

            //Copy all the values from the voided line and create the negative line
            voidLine.setLineNo(voidedLine.getLineNo() + 1); //Normal lines raise from 10 to 10
            voidLine.completeLine();
            voidLine.createLine(ctx);

            orderedLines.add(orderedLines.indexOf(voidedLine) + 1, newVoidedLine);
            orderedLines.add(orderedLines.indexOf(newVoidedLine) + 1, voidLine);

        }

        return true;
    } //voidLine

    private POSOrderLine cloneLine(POSOrderLine originalLine, int qty) {
        POSOrderLine clonedLine = new POSOrderLine();

        //Copy all the values from the original line
        clonedLine.setProductRemark(originalLine.getProductRemark());
        clonedLine.setProduct(originalLine.getProduct());
        clonedLine.setOrder(originalLine.getOrder());
        clonedLine.setQtyOrdered(qty);

        return clonedLine;
    } //cloneLine

    public boolean voidOrder(Context ctx, String reason) {
        status = VOID_STATUS;
        updateOrder(ctx);

        for(POSOrderLine orderingLine : orderingLines) {
            orderingLine.remove(ctx);
        }

        for(POSOrderLine orderedLine : orderedLines) {
            orderedLine.voidLine(reason);
            orderedLine.updateLine(ctx);
        }

        if(table != null) {
            table.freeTable(ctx, true);
        }

        return true;
    }

    public static int getMaxDocumentNo(Context ctx, String orderPrefix) {
        PosOrderManagement orderManager = new PosOrderManagement(ctx);
        return orderManager.getMaxDocumentNo(orderPrefix);
    }

}
