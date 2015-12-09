package de.bxservice.bxpos.logic;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.bxservice.bxpos.logic.model.MProduct;
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

    public static final Locale LOCALE = Locale.GERMANY;

    private static volatile DataMediator instance = null;

    private List<ProductCategory> productCategoryList = new ArrayList<>();
    private List<TableGroup> tableGroupList = new ArrayList<>();
    private List<MProduct> productList = new ArrayList<>();
    private List<ProductPrice> productPriceList = new ArrayList<>();
    private HashMap<Integer, ProductPrice> productPriceHashMap = new HashMap<>();
    private boolean error = false;


    private DataMediator() {
        Thread productCategoryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProductCategoryWebServiceAdapter productCategoryWS = new ProductCategoryWebServiceAdapter();
                productCategoryList = productCategoryWS.getProductCategoryList();

                ProductWebServiceAdapter productWS = new ProductWebServiceAdapter();
                productList = productWS.getProductList();

                ProductPriceWebServiceAdapter productPriceWS = new ProductPriceWebServiceAdapter();
                productPriceList = productPriceWS.getProductPriceList();

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

    public List<TableGroup> getTableGroupList() {
        return tableGroupList;
    }

    public List<MProduct> getProductList() {
        return productList;
    }

    public List<ProductPrice> getProductPriceList() {
        return productPriceList;
    }

    public boolean isError() {
        return error;
    }

    public boolean isDataComplete(){
        if( productCategoryList  != null && !productCategoryList.isEmpty() &&
                productList      != null && !productList.isEmpty() &&
                tableGroupList   != null && !tableGroupList.isEmpty() &&
                productPriceList != null && !productPriceList.isEmpty() )
            return true;

        return false;
    }

    public HashMap<Integer, ProductPrice> getProductPriceHashMap() {
        return productPriceHashMap;
    }

    /**
     * Set the relation between product category and its respective products
     */
    private void setProductRelations(){

        Collections.sort(productList, new ProductComparator());

        //Relation between product category and product
        if( productCategoryList != null && !productCategoryList.isEmpty() &&
                productList != null && !productList.isEmpty() ){

            int productCategoryId;
            int childProductCategoryId;
            for( ProductCategory productCategory : productCategoryList ){

                productCategoryId = productCategory.getProductCategoryID();
                for( MProduct product : productList ){
                    childProductCategoryId = product.getProductCategoryId();
                    if( childProductCategoryId == productCategoryId )
                        productCategory.getProducts().add(product);
                }
            }
        }
        else {
            Log.i("Error: ", "missing products");
            error = true;
        }

        //Relation between products and prices - the list has to be the same long. One price per every product
        if( productPriceList != null && !productPriceList.isEmpty() &&
                productList != null && !productList.isEmpty() &&
                productPriceList.size() == productList.size() ){

            int productId;
            int priceProductId;
            for( ProductPrice productPrice : productPriceList ){

                priceProductId = productPrice.getProductID();
                for( MProduct product : productList ){
                    productId = product.getProductID();
                    if( priceProductId == productId ) {
                        productPrice.setProduct(product);
                        productPriceHashMap.put(product.getProductID(),productPrice);
                    }
                }
            }
        }
        else {
            Log.i("Error: ", "missing price products");
            error = true;
        }

    }

    public class ProductComparator implements Comparator<MProduct> {
        @Override
        public int compare(MProduct o1, MProduct o2) {
            return o1.getProductName().compareTo(o2.getProductName());
        }
    }

}
