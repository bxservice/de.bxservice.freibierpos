package de.bxservice.bxpos.logic;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.Product;
import de.bxservice.bxpos.logic.model.ProductCategory;
import de.bxservice.bxpos.logic.model.ProductPrice;
import de.bxservice.bxpos.logic.model.Table;
import de.bxservice.bxpos.logic.webservices.ProductCategoryWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.ProductPriceWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.ProductWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.TableWebServiceAdapter;

/**
 * Class in charge of work as the mediator between the data and the UI
 * it uses a singleton pattern to create the instance to secure that
 * the same object is references across the app
 * Created by Diego Ruiz on 6/11/15.
 */
public class DataMediator {

    private static volatile DataMediator instance = null;

    private List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
    private List<Table> tableList = new ArrayList<Table>();
    private List<Product> productList = new ArrayList<Product>();
    private List<ProductPrice> productPriceList = new ArrayList<ProductPrice>();


    private DataMediator(final Context ctx) {
        Thread productCategoryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProductCategoryWebServiceAdapter productCategoryWS = new ProductCategoryWebServiceAdapter(ctx);
                productCategoryList = productCategoryWS.getProductCategoryList();

            }
        });

        productCategoryThread.run();

        Thread tableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                TableWebServiceAdapter tableWS = new TableWebServiceAdapter(ctx);
                tableList = tableWS.getTableList();

            }
        });

        tableThread.run();

        Thread productThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProductWebServiceAdapter productWS = new ProductWebServiceAdapter(ctx);
                productList = productWS.getProductList();
            }
        });

        productThread.run();

        Thread productPriceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProductPriceWebServiceAdapter productPriceWS = new ProductPriceWebServiceAdapter(ctx);
                productPriceList = productPriceWS.getProductPriceList();
            }
        });

        productPriceThread.run();
        //TODO: Create threads for all the other MOdel classes
    }


    public static synchronized DataMediator getInstance(Context ctx) {
         if (instance == null) {
        instance = new DataMediator(ctx);
        }

        return instance;
    }


    public List<ProductCategory> getProductCategoryList() {
        return productCategoryList;
    }

    public void setProductCategoryList(List<ProductCategory> productCategoryList) {
        this.productCategoryList = productCategoryList;
    }
}
