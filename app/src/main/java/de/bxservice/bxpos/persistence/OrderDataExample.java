package de.bxservice.bxpos.persistence;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.Order;

/**
 * Created by diego on 29/10/15.
 */
public class OrderDataExample {
    public static List ORDERS = new ArrayList<Order>();

    static{

        ORDERS.add(new Order("Caesar Salad","€7"));
        ORDERS.add(new Order("Chicken Nuggets","€8"));
        ORDERS.add(new Order("Duff Beer","€2"));
        ORDERS.add(new Order("Wine","€23"));
        ORDERS.add(new Order("Dessert","€5"));

    }
}
