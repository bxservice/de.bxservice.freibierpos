package de.bxservice.bxpos.logic.model;

import java.io.Serializable;

/**
 * This is the product that is added to the draft orders
 * it has extra attributes that will not be sent to iDempiere
 * Created by Diego Ruiz on 25/11/15.
 */
public class OrderProduct implements Serializable{

    //Order status
    public static final String ORDERING    = "ORDERING";
    public static final String ORDERED     = "ORDERED";

    private MProduct product;
    private int qty;
    private String productRemark;
    private boolean marked = false;
    private String productStatus;

    public MProduct getProduct() {
        return product;
    }

    public void setProduct(MProduct product) {
        this.product = product;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
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

    public String getProductStatus() {
        return productStatus;
    }

    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }
}
