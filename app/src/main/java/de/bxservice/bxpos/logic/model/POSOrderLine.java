package de.bxservice.bxpos.logic.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * This is the product that is added to the draft orders
 * it has extra attributes that will not be sent to iDempiere
 * Created by Diego Ruiz on 25/11/15.
 */
public class POSOrderLine implements Serializable{

    //Order Line status
    public static final String ORDERING    = "ORDERING";
    public static final String ORDERED     = "ORDERED";

    //Order that the line belongs to
    POSOrder order;

    private MProduct product;
    private int qtyOrdered;
    private String productRemark;
    private boolean marked = false;
    private String lineStatus;
    private BigDecimal lineNetAmt = BigDecimal.ZERO; //qty*StpPrice


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

        if( product != null )
            lineNetAmt = product.getProductPrice().multiply(BigDecimal.valueOf(qtyOrdered));

        return lineNetAmt;
    }

    public POSOrder getOrder() {
        return order;
    }

    public void setOrder(POSOrder order) {
        this.order = order;
    }
}
