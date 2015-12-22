package de.bxservice.bxpos.logic.model.idempiere;

import java.math.BigDecimal;

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
}
