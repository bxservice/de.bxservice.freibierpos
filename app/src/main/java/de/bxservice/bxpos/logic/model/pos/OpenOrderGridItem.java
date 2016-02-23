package de.bxservice.bxpos.logic.model.pos;

/**
 * This represents every Open order GridItem
 * it is written like this to allow future images of tables
 * instead of only names
 * Created by Diego Ruiz on 23/02/16.
 */
public class OpenOrderGridItem {

    private String orderNo;
    private String price;
    private String table;


    public OpenOrderGridItem() {
        super();
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }
}
