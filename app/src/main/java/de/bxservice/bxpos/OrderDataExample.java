package de.bxservice.bxpos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by diego on 29/10/15.
 */
public class OrderDataExample {
    static List ORDERS = new ArrayList<Order>();

    static{

        ORDERS.add(new Order("Caesar Salad","€7"));
        ORDERS.add(new Order("Chicken Nuggets","€8"));
        ORDERS.add(new Order("Duff Beer","€2"));
        ORDERS.add(new Order("Wine","€23"));
        ORDERS.add(new Order("Dessert","€5"));

    }
}
