package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import java.io.Serializable;
import java.math.BigDecimal;

import de.bxservice.bxpos.logic.daomanager.PosProductManagement;

/**
 * This is the product that is read from iDempiere
 * Created by Diego Ruiz on 9/11/15.
 */
public class MProduct implements Serializable {

    public static final String M_Product_ID = "M_Product_ID";

    private int productID;
    private String productName;
    private int productCategoryId;
    private PosProductManagement productManager;

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

    public BigDecimal getProductPriceValue() {
        ProductPrice productPrice = getProductPrice(null);
        return productPrice.getStdPrice();
    }

    public ProductPrice getProductPrice(Context ctx) {
        productManager = new PosProductManagement(ctx);
        return productManager.getProductPrice(this);
    }

    /**
     * Communicates with the manager to create the product in the database
     * @param ctx
     * @return
     */
    public boolean createProduct(Context ctx) {
        productManager = new PosProductManagement(ctx);
        return productManager.create(this);
    }

}
