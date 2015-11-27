package de.bxservice.bxpos.logic.model;

import java.io.Serializable;

/**
 * This is the product that is read from iDempiere
 * Created by Diego Ruiz on 9/11/15.
 */
public class MProduct implements Serializable {

    public static final String M_Product_ID = "M_Product_ID";

    private int productID;
    private String productName;
    private int productCategoryId;

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(int productCategoryId) {
        this.productCategoryId = productCategoryId;
    }
}
