/**********************************************************************
 * This file is part of FreiBier POS                                   *
 *                                                                     *
 *                                                                     *
 * Copyright (C) Contributors                                          *
 *                                                                     *
 * This program is free software; you can redistribute it and/or       *
 * modify it under the terms of the GNU General Public License         *
 * as published by the Free Software Foundation; either version 2      *
 * of the License, or (at your option) any later version.              *
 *                                                                     *
 * This program is distributed in the hope that it will be useful,     *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of      *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the        *
 * GNU General Public License for more details.                        *
 *                                                                     *
 * You should have received a copy of the GNU General Public License   *
 * along with this program; if not, write to the Free Software         *
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,          *
 * MA 02110-1301, USA.                                                 *
 *                                                                     *
 * Contributors:                                                       *
 * - Diego Ruiz - Bx Service GmbH                                      *
 **********************************************************************/
package de.bxservice.bxpos.logic;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.DefaultPosData;
import de.bxservice.bxpos.logic.model.idempiere.MProduct;
import de.bxservice.bxpos.logic.model.idempiere.PosTenderType;
import de.bxservice.bxpos.logic.model.idempiere.RestaurantInfo;
import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.logic.model.idempiere.ProductPrice;
import de.bxservice.bxpos.logic.model.idempiere.Table;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
import de.bxservice.bxpos.logic.model.idempiere.TaxCategory;
import de.bxservice.bxpos.logic.model.pos.PosUser;
import de.bxservice.bxpos.logic.print.POSOutputDevice;
import de.bxservice.bxpos.logic.webservices.DefaultPosDataWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.OrgInfoWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.OutputDeviceWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.PosTenderTypeWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.PosUserWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.ProductCategoryWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.ProductPriceWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.ProductWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.TableWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.TaxInfoWebServiceAdapter;
import de.bxservice.bxpos.logic.webservices.WebServiceRequestData;

/**
 * Class that reads from iDempiere the data and call the necessary methods
 * to persist it in the database
 * Created by Diego Ruiz on 6/11/15.
 */
public class DataReader {

    private static final String LOG_TAG = "Data Reader";

    private List<ProductCategory> productCategoryList = new ArrayList<>();
    private List<TableGroup> tableGroupList = new ArrayList<>();
    private List<TaxCategory> taxCategoryList = new ArrayList<>();
    private List<MProduct> productList = new ArrayList<>();
    private List<ProductPrice> productPriceList = new ArrayList<>();
    private DefaultPosData defaultData = null;
    private RestaurantInfo restaurantInfo = null;
    private PosUser userInfo = null;
    private List<POSOutputDevice> outputDeviceList = new ArrayList<>();
    private List<PosTenderType> tenderTypeList = new ArrayList<>();
    private boolean error = false;
    private Context mContext;


    public DataReader(final WebServiceRequestData wsData, Context ctx) {

        mContext = ctx;

        Thread dataUserThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PosUserWebServiceAdapter userWS = new PosUserWebServiceAdapter(wsData);
                if (userWS.isSuccess()) {
                    userInfo = userWS.getUser();
                    persistUserData();
                }
            }
        });

        dataUserThread.run();

        Thread outputDeviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                OutputDeviceWebServiceAdapter deviceWS = new OutputDeviceWebServiceAdapter(wsData);
                outputDeviceList = deviceWS.getOutputDeviceList();
                persistDeviceList();
            }
        });

        outputDeviceThread.run();

        Thread taxInfoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                TaxInfoWebServiceAdapter taxInfoWS = new TaxInfoWebServiceAdapter(wsData);
                if (taxInfoWS.isSuccess()) {
                    taxCategoryList = taxInfoWS.getTaxCategoryList();
                    persistTaxCategory();
                }
            }
        });

        taxInfoThread.run();

        Thread productCategoryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ProductCategoryWebServiceAdapter productCategoryWS = new ProductCategoryWebServiceAdapter(wsData);
                productCategoryList = productCategoryWS.getProductCategoryList();

                ProductWebServiceAdapter productWS = new ProductWebServiceAdapter(wsData);
                productList = productWS.getProductList();

                ProductPriceWebServiceAdapter productPriceWS = new ProductPriceWebServiceAdapter(wsData);
                productPriceList = productPriceWS.getProductPriceList();

                setProductRelations();
                if (!error)
                    persistProductAttributes();
            }
        });

        productCategoryThread.run();

        Thread tableThread = new Thread(new Runnable() {
            @Override
            public void run() {
                TableWebServiceAdapter tableWS = new TableWebServiceAdapter(wsData);
                tableGroupList = tableWS.getTableGroupList();
                persistTables();
            }
        });

        tableThread.run();

        Thread tablePosData = new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultPosDataWebServiceAdapter dataWS = new DefaultPosDataWebServiceAdapter(wsData);
                defaultData = dataWS.getDefaultPosData();
                persistPosData();
            }
        });

        tablePosData.run();

        Thread orgInfoThread = new Thread(new Runnable() {
            @Override
            public void run() {
                OrgInfoWebServiceAdapter dataWS = new OrgInfoWebServiceAdapter(wsData);
                restaurantInfo = dataWS.getRestaurantInfo();
                persistOrgInfo();
            }
        });

        orgInfoThread.run();

        Thread tenderTypesThread = new Thread(new Runnable() {
            @Override
            public void run() {
                PosTenderTypeWebServiceAdapter tenderTypeWS = new PosTenderTypeWebServiceAdapter(wsData);
                tenderTypeList = tenderTypeWS.getTenderTypeList();
                persistTenderTypes();
            }
        });

        tenderTypesThread.run();

    }

    private void persistUserData() {
        userInfo.updateUserInfo(mContext);
    }

    /**
     * Save default data in the database
     */
    private void persistPosData() {
        defaultData.saveData(mContext);
    }

    /**
     * Save Org info in the database
     */
    private void persistOrgInfo() {
        restaurantInfo.saveData(mContext);
    }

    private void persistDeviceList() {
        for(POSOutputDevice device : outputDeviceList) {
            device.save(mContext);
        }

    }

    /**
     * Save tables in the database
     */
    private void persistTables() {
        for(TableGroup tg : tableGroupList) {
            tg.save(mContext);
            for(Table table : tg.getTables()) {
                table.save(mContext);
            }
        }
    }

    private void persistTaxCategory() {
        for(TaxCategory taxCategory : taxCategoryList)
            taxCategory.save(mContext);
    }

    /**
     * Save product attributes in the database
     */
    private void persistProductAttributes() {
        for(ProductCategory productCategory : productCategoryList)
            productCategory.save(mContext);

        for(MProduct product : productList)
            product.save(mContext);

        for(ProductPrice productPrice : productPriceList)
            productPrice.save(mContext);
    }

    /**
     * Save tender type in the database
     */
    private void persistTenderTypes() {
        for (PosTenderType tenderType: tenderTypeList)
            tenderType.save(mContext);
    }

    public boolean isError() {
        return error;
    }

    public boolean isDataComplete(){

        if(productCategoryList   != null && !productCategoryList.isEmpty() &&
                productList      != null && !productList.isEmpty() &&
                tableGroupList   != null && !tableGroupList.isEmpty() &&
                productPriceList != null && !productPriceList.isEmpty() &&
                tenderTypeList   != null && !tenderTypeList.isEmpty() &&
                restaurantInfo   != null &&  defaultData != null &&
                userInfo != null) {
            return true;
        }

        Log.e(LOG_TAG, "missing data");

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
            Log.e(LOG_TAG, "missing products");
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
