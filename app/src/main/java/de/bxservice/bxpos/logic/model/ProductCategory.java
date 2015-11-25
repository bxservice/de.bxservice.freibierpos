package de.bxservice.bxpos.logic.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego on 5/11/15.
 */
public class ProductCategory {

    public static final String M_Product_Category_ID = "M_Product_Category_ID";

    private int productCategoryID;
    private String name;
    List<MProduct> products = new ArrayList<MProduct>();


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

    public List<MProduct> getProducts() {
        return products;
    }

    public void setProducts(List<MProduct> products) {
        this.products = products;
    }
}
