package de.bxservice.bxpos.ui.adapter;

/**
 * Listens a move or dismissal event from a RecyclerView
 * Created by Diego Ruiz on 17/12/15.
 */
public interface ItemTouchHelperAdapter {

    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);

}
