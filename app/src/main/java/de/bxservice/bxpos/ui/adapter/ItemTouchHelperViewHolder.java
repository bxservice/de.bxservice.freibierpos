package de.bxservice.bxpos.ui.adapter;

/**
 * Created by Diego Ruiz on 17/12/15.
 */
public interface ItemTouchHelperViewHolder {

    /**
     * Called when the {@link ItemTouchHelperViewHolder} first registers an
     * item as being moved or swiped.
     * Implementations should update the item view to indicate
     * it's active state.
     */
    void onItemSelected();


    /**
     * Called when the {@link ItemTouchHelperViewHolder} has completed the
     * move or swipe, and the active item state should be cleared.
     */
    void onItemClear();
}