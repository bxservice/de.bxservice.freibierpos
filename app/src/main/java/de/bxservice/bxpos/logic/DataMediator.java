package de.bxservice.bxpos.logic;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
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

    static final String LOG_TAG = "Data Mediator";

    public static final Locale LOCALE = Locale.GERMANY;

    private static volatile DataMediator instance = null;

    private List<ProductCategory> productCategoryList = new ArrayList<>();
    private List<TableGroup> tableGroupList = new ArrayList<>();
    private List<MProduct> productList = new ArrayList<>();
    private List<ProductPrice> productPriceList = new ArrayList<>();
    private boolean error = false;
    private Context mContext;


    private DataMediator(Context ctx) {

        mContext = ctx;

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
                persistProductAttributes();
            }
        });

        productCategoryThread.run();

        Thread tableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                TableWebServiceAdapter tableWS = new TableWebServiceAdapter();
                tableGroupList = tableWS.getTableGroupList();
                persistTables();
            }
        });

        tableThread.run();

    }

    /**
     * Save tables in the database
     */
    public void persistTables() {
        for(TableGroup tg : tableGroupList) {
            tg.createTableGroup(mContext);
            for(Table table : tg.getTables()) {
                table.createTable(mContext);
            }
        }
    }

    /**
     * Save product attributes in the database
     */
    public void persistProductAttributes() {
        for(ProductCategory productCategory : productCategoryList)
            productCategory.createProductCategory(mContext);

        for(MProduct product : productList)
            product.createProduct(mContext);

        for(ProductPrice productPrice : productPriceList)
            productPrice.createProductPrice(mContext);
    }

    public static synchronized DataMediator getInstance() {
        if (instance == null) {
            instance = new DataMediator(null);
        }

        return instance;
    }

    public static synchronized DataMediator getInstance(Context ctx) {
        if (instance == null) {
            instance = new DataMediator(ctx);
        }

        return instance;
    }

    public boolean isError() {
        return error;
    }

    public boolean isDataComplete(){
        if(productCategoryList  != null && !productCategoryList.isEmpty() &&
                productList      != null && !productList.isEmpty() &&
                tableGroupList   != null && !tableGroupList.isEmpty() &&
                productPriceList != null && !productPriceList.isEmpty())
            return true;

        return false;
    }

    /**
     * Set the relation between product category and its respective products
     */
    private void setProductRelations(){

        //Relation between product category and product
        if(productCategoryList != null && !productCategoryList.isEmpty() &&
                productList != null && !productList.isEmpty()) {

            int productCategoryId;
            int childProductCategoryId;
            for(ProductCategory productCategory : productCategoryList) {

                productCategoryId = productCategory.getProductCategoryID();
                for(MProduct product : productList) {
                    childProductCategoryId = product.getProductCategoryId();
                    if(childProductCategoryId == productCategoryId)
                        productCategory.getProducts().add(product);
                }
            }
        }
        else {
            Log.i(LOG_TAG, "missing products");
            error = true;
        }

        //Relation between products and prices - the list has to be the same long. One price per every product
        if( productPriceList != null && !productPriceList.isEmpty() &&
                productList != null && !productList.isEmpty() &&
                productPriceList.size() == productList.size() ){

            int productId;
            int priceProductId;
            for(ProductPrice productPrice : productPriceList) {

                priceProductId = productPrice.getProductID();
                for(MProduct product : productList) {
                    productId = product.getProductID();
                    if(priceProductId == productId) {
                        productPrice.setProduct(product);
                    }
                }
            }
        }
        else {
            Log.e(LOG_TAG, "missing price products");
            error = true;
        }

    }
}
