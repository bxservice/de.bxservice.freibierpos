package de.bxservice.bxpos.logic.model.report;

import java.math.BigDecimal;

/**
 * This class is a generic class to load a report data
 * with 3 variables
 * Created by Diego Ruiz on 31/03/16.
 */
public class ReportGenericObject {

    private String description;
    private String quantity;
    private BigDecimal amount;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAmount(int amountInt) {
        double doubleValue = (double) amountInt / 100;
        amount = BigDecimal.valueOf(doubleValue);
    }
}
