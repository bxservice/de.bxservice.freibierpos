package de.bxservice.bxpos.logic;

import android.content.Context;

import java.util.List;
import java.util.Locale;

import de.bxservice.bxpos.logic.model.idempiere.ProductCategory;
import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Class in charge to provide the necessary objects to the UI
 * it comunicates to the database via Data Mapper
 * Created by Diego Ruiz on 30/12/15.
 */
public class DataProvider {

    private DataMapper dataMapper;
    private Context mContext;
    public static final Locale LOCALE = Locale.GERMANY;

    public DataProvider(Context ctx) {
        mContext = ctx;
        dataMapper = new DataMapper(mContext);
    }

    public long getTotalTableGroups() {
        return dataMapper.getTotalTableGroups();
    }

    public List<TableGroup> getAllTableGroups() {
        return dataMapper.getAllTableGroups();
    }

    public long getTotalCategories() {
        return dataMapper.getTotalCategories();
    }

    public List<ProductCategory> getAllCategories() {
        return dataMapper.getAllCategories();
    }
}
