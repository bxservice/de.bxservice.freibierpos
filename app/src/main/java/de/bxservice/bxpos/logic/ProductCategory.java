package de.bxservice.bxpos.logic;

/**
 * Created by diego on 5/11/15.
 */
public class ProductCategory {

    public static final String M_Product_Category_ID = "M_Product_Category_ID";

    private int productCategoryID;
    private String name;

    public ProductCategory(int id, String name){
        productCategoryID = id;
        this.name = name;

    }

    public int getProductCategoryID() {
        return productCategoryID;
    }

    public void setProductCategoryID(int productCategoryID) {
        this.productCategoryID = productCategoryID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
