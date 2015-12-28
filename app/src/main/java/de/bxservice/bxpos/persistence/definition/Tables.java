package de.bxservice.bxpos.persistence.definition;

import de.bxservice.bxpos.persistence.dbcontract.GroupTableContract;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderContract;
import de.bxservice.bxpos.persistence.dbcontract.PosOrderLineContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductCategoryContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductContract;
import de.bxservice.bxpos.persistence.dbcontract.ProductPriceContract;
import de.bxservice.bxpos.persistence.dbcontract.TableContract;
import de.bxservice.bxpos.persistence.dbcontract.UserContract;

/**
 * Created by Diego Ruiz on 23/12/15.
 */
public interface Tables {

    //Controls the database version
    String TABLE_META_INDEX = "meta_index";

    //Access tables
    String TABLE_USER          = UserContract.User.TABLE_NAME;

    //Physical space tables
    String TABLE_TABLE         = TableContract.TableDB.TABLE_NAME;
    String TABLE_TABLE_GROUP   = GroupTableContract.GroupTableDB.TABLE_NAME;

    //Order management tables
    String TABLE_POSORDER      = PosOrderContract.POSOrderDB.TABLE_NAME;
    String TABLE_POSORDER_LINE = PosOrderLineContract.POSOrderLineDB.TABLE_NAME;

    //Product management tables
    String TABLE_PRODUCT = ProductContract.ProductDB.TABLE_NAME;
    String TABLE_PRODUCT_CATEGORY = ProductCategoryContract.ProductCategoryDB.TABLE_NAME;
    String TABLE_PRODUCT_PRICE = ProductPriceContract.ProductPriceDB.TABLE_NAME;

}