package de.bxservice.bxpos.logic;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.Product;
import de.bxservice.bxpos.logic.model.ProductCategory;
import de.bxservice.bxpos.logic.model.ProductPrice;
import de.bxservice.bxpos.logic.model.TableGroup;
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
    private List<TableGroup> tableGroupList = new ArrayList<TableGroup>();
    private List<Product> productList = new ArrayList<Product>();
    private List<ProductPrice> productPriceList = new ArrayList<ProductPrice>();


    private DataMediator() {
        Thread productCategoryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProductCategoryWebServiceAdapter productCategoryWS = new ProductCategoryWebServiceAdapter();
                productCategoryList = productCategoryWS.getProductCategoryList();

                ProductWebServiceAdapter productWS = new ProductWebServiceAdapter();
                productList = productWS.getProductList();

                setProductRelations();

            }
        });

        productCategoryThread.run();

        Thread tableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                TableWebServiceAdapter tableWS = new TableWebServiceAdapter();
                tableGroupList = tableWS.getTableGroupList();

            }
        });

        tableThread.run();


        Thread productPriceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProductPriceWebServiceAdapter productPriceWS = new ProductPriceWebServiceAdapter();
                productPriceList = productPriceWS.getProductPriceList();
            }
        });

        productPriceThread.run();
    }


    public static synchronized DataMediator getInstance() {
        if (instance == null) {
            instance = new DataMediator();
        }

        return instance;
    }

    public List<ProductCategory> getProductCategoryList() {
        return productCategoryList;
    }

    public void setProductCategoryList(List<ProductCategory> productCategoryList) {
        this.productCategoryList = productCategoryList;
    }

    public List<TableGroup> getTableGroupList() {
        return tableGroupList;
    }

    public void setTableGroupList(List<TableGroup> tableGroupList) {
        this.tableGroupList = tableGroupList;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public List<ProductPrice> getProductPriceList() {
        return productPriceList;
    }

    public void setProductPriceList(List<ProductPrice> productPriceList) {
        this.productPriceList = productPriceList;
    }

    public boolean isDataComplete(){
        if( productCategoryList  != null && !productCategoryList.isEmpty() &&
                productList      != null && !productList.isEmpty() &&
                tableGroupList   != null && !tableGroupList.isEmpty() &&
                productPriceList != null && !productPriceList.isEmpty() )
            return true;

        return false;
    }

    /**
     * Set the relation between product category and its respective products
     */
    private void setProductRelations(){

        if( productCategoryList != null && !productCategoryList.isEmpty() &&
                productList != null && !productList.isEmpty() ){

            int productCategoryId;
            int childProductCategoryId;
            for( ProductCategory pc : productCategoryList ){

                productCategoryId = pc.getProductCategoryID();
                for( Product p : productList ){
                    childProductCategoryId = p.getProductCategoryId();
                    if( childProductCategoryId == productCategoryId )
                        pc.getProducts().add(p);
                }
            }

        }

    }

}
