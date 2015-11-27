package de.bxservice.bxpos.logic.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This represents the draft order - contains
 * everything that is selected and set before sending
 * the order
 * Created by Diego Ruiz on 25/11/15.
 */
public class POSOrder {

    //Order status
    public static final String DRAFT_STATUS    = "DRAFT";
    public static final String SENT_STATUS     = "SENT";
    public static final String COMPLETE_STATUS = "COMPLETE";

    private HashMap<MProduct, OrderProduct> orderProductHashMap = new HashMap<MProduct, OrderProduct>();

    private ArrayList<OrderProduct> preOrderedProducts = new ArrayList<OrderProduct>();
    private String orderRemark;
    private int guestNumber;
    private String status;

    public void addItem(MProduct product) {

        boolean newItem = true;

        //Check if the product was ordered before
        if ( !orderProductHashMap.isEmpty() ){

            OrderProduct orderProduct = orderProductHashMap.get(product);
            if( orderProduct != null ){
                orderProduct.setQty( orderProduct.getQty() + 1 ); //add 1 to the qty previously ordered
                newItem = false;
            }

        }

        if( newItem ){

            OrderProduct orderProduct = new OrderProduct();
            orderProduct.setProduct(product);
            orderProduct.setQty(1); //If new item - is the first item that is added

            preOrderedProducts.add(orderProduct);
            orderProductHashMap.put(product, orderProduct);
        }

    }

    public int getProductQtyOrdered(MProduct product) {

        if ( orderProductHashMap.get(product) != null )
            return orderProductHashMap.get(product).getQty();

        return 0;
    }

    public ArrayList<OrderProduct> getPreOrderedProducts() {
        return preOrderedProducts;
    }

    public void setPreOrderedProducts(ArrayList<OrderProduct> preOrderedProducts) {
        this.preOrderedProducts = preOrderedProducts;
    }

    public String getOrderRemark() {
        return orderRemark;
    }

    public void setOrderRemark(String orderRemark) {
        this.orderRemark = orderRemark;
    }

    public int getGuestNumber() {
        return guestNumber;
    }

    public void setGuestNumber(int guestNumber) {
        this.guestNumber = guestNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {

        if ( status.equals(DRAFT_STATUS) ||
                status.equals(SENT_STATUS) ||
                status.equals(COMPLETE_STATUS) )
        this.status = status;

    }
}
