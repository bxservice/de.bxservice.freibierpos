package de.bxservice.bxpos.logic.model;

/**
 * Created by diego on 9/11/15.
 */
public class Product {

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
