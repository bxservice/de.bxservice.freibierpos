package de.bxservice.bxpos.logic;

import android.content.Context;

import java.util.List;

import de.bxservice.bxpos.logic.model.idempiere.TableGroup;
import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 30/12/15.
 */
public class DataProvider {

    private DataMapper dataMapper;
    private Context mContext;

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
}
