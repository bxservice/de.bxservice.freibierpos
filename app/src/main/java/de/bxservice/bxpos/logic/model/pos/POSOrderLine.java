package de.bxservice.bxpos.logic.model.pos;

import android.content.Context;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;

import de.bxservice.bxpos.logic.daomanager.PosOrderLineManagement;
import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;

/**
 * This is the line that is added to the orders
 * it has extra attributes that will not be sent to iDempiere
 * Created by Diego Ruiz on 25/11/15.
 */
public class POSOrderLine implements Serializable {

    //Order Line status
    public static final String ORDERING    = "ORDERING";
    public static final String ORDERED     = "ORDERED";
    public static final String VOIDED = "VOIDED";

    private PosOrderLineManagement lineManager;
    //Order that the line belongs to
    private POSOrder order;

    private int orderLineId;
    private int lineNo;
    private MProduct product;
    private int qtyOrdered;
    private String productRemark = "";
    private boolean marked = false;
    private String lineStatus;
    private BigDecimal lineNetAmt = BigDecimal.ZERO; //qty*StpPrice

    //Defines if the order line is a free product
    private boolean isComplimentaryProduct = false;


    public MProduct getProduct() {
        return product;
    }

    public void setProduct(MProduct product) {
        this.product = product;
    }

    public int getQtyOrdered() {
        return qtyOrdered;
    }

    public void setQtyOrdered(int qtyOrdered) {
        this.qtyOrdered = qtyOrdered;
    }

    public String getProductRemark() {
        return productRemark;
    }

    public void setProductRemark(String productRemark) {
        this.productRemark = productRemark;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public String getLineStatus() {
        return lineStatus;
    }

    public void setLineStatus(String lineStatus) {
        this.lineStatus = lineStatus;
    }

    public BigDecimal getLineNetAmt() {

        if(product != null)
            lineNetAmt = product.getProductPriceValue().multiply(BigDecimal.valueOf(qtyOrdered));

        return lineNetAmt;
    }

    /**
     * Returns the total amt of the line
     * in an integer to be save in the database
     * @return
     */
    public Integer getLineNetAmtInteger() {
        return getLineNetAmt().multiply(BigDecimal.valueOf(100)).intValue(); //total * 100
    }

    /**
     * Gets an integer value from the db and converts it to a BigDecimal
     * last two digits are decimals
     * @param total
     */
    public void setLineTotalFromInt(Integer total) {
        double doubleValue = (double) total / 100;
        lineNetAmt = BigDecimal.valueOf(doubleValue);
    }

    public void setLineNetAmt(BigDecimal lineNetAmt) {
        this.lineNetAmt = lineNetAmt;
    }

    public String getLineTotalAmt() {

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(DefaultPosData.LOCALE);
        return currencyFormat.format(getLineNetAmt());
    }

    public POSOrder getOrder() {
        return order;
    }

    public void setOrder(POSOrder order) {
        this.order = order;
    }

    public int getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(int orderLineId) {
        this.orderLineId = orderLineId;
    }

    public int getLineNo() {
        return lineNo;
    }

    public void setLineNo(int lineNo) {
        this.lineNo = lineNo;
    }

    public boolean isComplimentaryProduct() {
        return isComplimentaryProduct;
    }

    public void setComplimentaryProduct(boolean complimentaryProduct) {
        isComplimentaryProduct = complimentaryProduct;
    }

    public boolean sendOrder (Context ctx) {

        lineManager = new PosOrderLineManagement(ctx);
        boolean result;

        completeLine();

        result = lineManager.create(this);

        if(!result)
            uncompleteLine();

        return result;

    }

    public void completeLine() {
        lineStatus = ORDERED;
    }

    public void uncompleteLine() {
        lineStatus = ORDERING;
    }

    public void voidLine(String reason) {
        lineStatus = VOIDED;
        productRemark = productRemark + " ***VOIDED*** " + reason; //Description in iDempiere
    }

    public boolean updateLine(Context ctx) {

        lineManager = new PosOrderLineManagement(ctx);
        return lineManager.update(this);

    }

    public boolean createLine(Context ctx) {
        lineManager = new PosOrderLineManagement(ctx);
        return lineManager.create(this);
    }

    public boolean remove(Context ctx) {
        lineManager = new PosOrderLineManagement(ctx);
        return lineManager.remove(this);
    }

    /**
     * Return if a line is voidable or not
     * based on the conditions of not having
     * VOID status and qty greater than 0
     * @return
     */
    public boolean isVoidable() {
        if(lineStatus.equals(VOIDED) || qtyOrdered < 0)
            return false;

        return true;
    }

}
