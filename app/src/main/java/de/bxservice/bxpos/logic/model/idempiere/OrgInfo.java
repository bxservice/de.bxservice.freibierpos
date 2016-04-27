package de.bxservice.bxpos.logic.model.idempiere;

import android.content.Context;

import de.bxservice.bxpos.logic.daomanager.PosOrgInfoManagement;

/**
 * Created by Diego Ruiz on 27/04/16.
 */
public class OrgInfo {

    private PosOrgInfoManagement dataManager;

    private String name;
    private String address1;
    private String address2;
    private String city;
    private String postalCode;
    private String phone;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Save data - if the default data was not previously saved it creates it
     * otherwise it updates it
     * @param ctx
     * @return
     */
    public boolean saveData (Context ctx) {
        dataManager = new PosOrgInfoManagement(ctx);

        if (dataManager.get(1) == null)
            return createData();
        else
            return updateData();
    }

    private boolean createData () {
        return dataManager.create(this);
    }

    private boolean updateData () {
        return dataManager.update(this);
    }
}
