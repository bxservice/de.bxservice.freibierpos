package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import java.math.BigDecimal;

import de.bxservice.bxpos.logic.daomanager.PosProductPriceManagement;

/**
 * Created by Diego Ruiz on 9/11/15.
 */
public class ProductPrice {

    public static final String M_ProductPrice_ID = "M_ProductPrice_ID";

    private int priceListVersionID;
    private int productPriceID;
    private MProduct product;
    private BigDecimal stdPrice;
    private int productID;
    private PosProductPriceManagement productPriceManager;

    public int getPriceListVersionID() {
        return priceListVersionID;
    }

    public void setPriceListVersionID(int priceListVersionID) {
        this.priceListVersionID = priceListVersionID;
    }

    public int getProductPriceID() {
        return productPriceID;
    }

    public void setProductPriceID(int productPriceID) {
        this.productPriceID = productPriceID;
    }

    public MProduct getProduct() {
        return product;
    }

    public void setProduct(MProduct product) {
        this.product = product;
    }

    public BigDecimal getStdPrice() {
        return stdPrice;
    }

    public void setStdPrice(BigDecimal stdPrice) {
        this.stdPrice = stdPrice;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    /**
     * Returns the std price of the product
     * in an integer to be save in the database
     * @return
     */
    public Integer getIntegerStdPrice() {
        Integer total;
        total = Integer.valueOf(getStdPrice().multiply(BigDecimal.valueOf(100)).intValue()); //total * 100

        return total;
    }

    /**
     * Gets an integer value from the db and converts it to a BigDecimal
     * last two digits are decimals
     * @param total
     */
    public void setStdPriceFromInt(Integer total) {
        //TODO CHECK
        setStdPrice(BigDecimal.valueOf(total / 100));
        System.out.println(stdPrice);
        //this.totallines = totallines;
    }

    public boolean createProductPrice(Context ctx) {
        productPriceManager = new PosProductPriceManagement(ctx);
        return productPriceManager.create(this);
    }
}
