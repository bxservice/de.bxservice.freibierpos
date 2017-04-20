package de.bxservice.bxpos.persistence.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import de.bxservice.bxpos.logic.model.idempiere.TaxCategory;
import de.bxservice.bxpos.persistence.dbcontract.TaxCategoryContract;
import de.bxservice.bxpos.persistence.definition.Tables;

/**
 * Created by Diego Ruiz on 11/11/16.
 */
public class PosTaxCategoryHelper extends PosObjectHelper {

    private static final String LOG_TAG = "Tax Category Helper";

    public PosTaxCategoryHelper(Context mContext) {
        super(mContext);
    }

    /*
    * Creating a tax category
    */
    public long createTaxCategory(TaxCategory category) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaxCategoryContract.TaxCategoryDB.COLUMN_NAME_TAX_CATEGORY_ID, category.getTaxCategoryID());
        values.put(TaxCategoryContract.TaxCategoryDB.COLUMN_NAME_NAME, category.getName());

        // insert row
        return db.insert(Tables.TABLE_TAX_CATEGORY, null, values);
    }

    /*
    * get single tax category
    */
    public TaxCategory getTaxCategory(long taxCategoryID) {
        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + Tables.TABLE_TAX_CATEGORY + " WHERE "
                + TaxCategoryContract.TaxCategoryDB.COLUMN_NAME_TAX_CATEGORY_ID + " = ?";

        Log.d(LOG_TAG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, new String[] { String.valueOf(taxCategoryID) });

        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else {
            if (c != null)
                c.close();
            return null;
        }

        TaxCategory taxCategory = new TaxCategory();
        taxCategory.setTaxCategoryID(c.getInt(c.getColumnIndex(TaxCategoryContract.TaxCategoryDB.COLUMN_NAME_TAX_CATEGORY_ID)));
        taxCategory.setName(c.getString(c.getColumnIndex(TaxCategoryContract.TaxCategoryDB.COLUMN_NAME_NAME)));

        PosTaxHelper taxHelper = new PosTaxHelper(mContext);
        taxCategory.setTaxes(taxHelper.getTaxes(taxCategory));

        c.close();

        return taxCategory;
    }

    /*
    * Updating a tax category
    */
    public int updateTaxCategory(TaxCategory category) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaxCategoryContract.TaxCategoryDB.COLUMN_NAME_NAME, category.getName());

        // updating row
        return db.update(Tables.TABLE_TAX_CATEGORY, values, TaxCategoryContract.TaxCategoryDB.COLUMN_NAME_TAX_CATEGORY_ID + " = ?",
                new String[] { String.valueOf(category.getTaxCategoryID()) });
    }

}
