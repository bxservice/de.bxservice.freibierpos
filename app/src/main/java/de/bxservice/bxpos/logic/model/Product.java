package de.bxservice.bxpos.logic.model;

/**
 * Created by diego on 9/11/15.
 */
public class Product {

    public static final String M_Product = "M_Product";

    private int productID;
    private String productName;

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
}
