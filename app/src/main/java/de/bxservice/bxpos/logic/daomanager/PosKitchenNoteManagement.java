package de.bxservice.bxpos.logic.daomanager;

import android.content.Context;

import java.io.Serializable;
import java.util.ArrayList;

import de.bxservice.bxpos.persistence.DataMapper;

/**
 * Created by Diego Ruiz on 11/05/16.
 */
public class PosKitchenNoteManagement implements ObjectManagement, Serializable {

    //Object that writes to the db
    private DataMapper dataMapper;

    public PosKitchenNoteManagement(Context ctx) {
        dataMapper = new DataMapper(ctx);
    }

    @Override
    public boolean update(Object object) {
        return false;
    }

    @Override
    public boolean create(Object object) {
        //if the note already exists - don't do anything
        if(dataMapper.noteExist((String) object))
            return true;

        return dataMapper.save(object);
    }

    @Override
    public String get(long id){
        return null;
    }

    @Override
    public boolean remove(Object object) {
        return false;
    }

    public boolean noteExist(String note) {
        return dataMapper.noteExist(note);
    }

    public ArrayList<String> getKitchenNotes() {
        return dataMapper.getKitchenNotes();
    }

}