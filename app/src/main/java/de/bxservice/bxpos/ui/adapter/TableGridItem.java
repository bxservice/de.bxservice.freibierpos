package de.bxservice.bxpos.ui.adapter;

/**
 * This represents every TableGridItem
 * it is written like this to allow future images of tables
 * instead of only names
 * Created by Diego Ruiz on 18/11/15.
 */
public class TableGridItem {

    private String title;
    private String image;


    public TableGridItem() {
        super();
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}