package de.bxservice.bxpos.ui.adapter;

/**
 * This represents every GridItem
 * it is written like this to allow future images of tables
 * instead of only names
 * Created by Diego Ruiz on 18/11/15.
 */
public class GridItem {

    private String title;

    public GridItem() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}