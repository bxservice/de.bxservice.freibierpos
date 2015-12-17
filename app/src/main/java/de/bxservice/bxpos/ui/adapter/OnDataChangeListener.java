package de.bxservice.bxpos.ui.adapter;

/**
 * Created by Diego Ruiz on 17/12/15.
 */
public interface OnDataChangeListener{
    void onDataChanged(int position);
    void onItemDeleted(int position);
    void onItemAdded(int position, Object object);

}