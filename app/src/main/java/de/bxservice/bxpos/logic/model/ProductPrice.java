package de.bxservice.bxpos.logic.model;

import java.math.BigDecimal;

/**
 * Created by diego on 9/11/15.
 */
public class ProductPrice {

    public static final String M_ProductPrice = "M_ProductPrice";

    private int priceListVersionID;
    private int productPriceID;
    private Product product;
    private BigDecimal sellingPrice;


}
