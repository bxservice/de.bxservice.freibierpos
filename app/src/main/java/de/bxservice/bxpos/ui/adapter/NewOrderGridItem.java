package de.bxservice.bxpos.ui.adapter;

/**
 * This represents every Food Menu GridItem
 * it is written like this to allow future images of tables
 * instead of only names
 * Created by Diego Ruiz on 19/11/15.
 */
public class NewOrderGridItem {

    private String name;
    private String price;
    private String qty;


    public NewOrderGridItem() {
        super();
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }
}
