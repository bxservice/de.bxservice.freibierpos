package de.bxservice.bxpos.logic.print;

import de.bxservice.bxpos.logic.model.pos.POSOrder;

/**
 * Created by Diego Ruiz on 22/04/16.
 */
public abstract class AbstractPOSPrinter implements POSPrinter{

    protected POSOrder order;

    public AbstractPOSPrinter(POSOrder order) {
        this.order = order;
    }

}
