package de.bxservice.bxpos.de.bxservice.bxpos.logic;

/**
 * Created by diego on 29/10/15.
 */
public class Order {

    private String orderName;
    private String value;

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Order(String order, String v){
        orderName= order;
        value = v;
    }

    @Override
    public String toString(){return orderName+","+value;}
}
