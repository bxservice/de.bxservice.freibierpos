package de.bxservice.bxpos.logic.model.pos;

import android.content.Context;

import de.bxservice.bxpos.logic.daomanager.PosUserManagement;
import de.bxservice.bxpos.persistence.dbcontract.UserContract;

/**
 * Created by Diego Ruiz on 15/12/15.
 */
public class PosUser extends UserContract.User {

    private int id;
    private String username;
    private String password;
    private PosUserManagement userManager;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Communicates with the manager to create the user in the database
     * @param ctx
     * @return
     */
    public boolean createUser(Context ctx) {

        userManager = new PosUserManagement(ctx);
        boolean result;

        result = userManager.create(this);

        return result;

    }

    public boolean changePassword(String password, Context ctx) {
        this.password = password;
        return updateUser(ctx);
    }

    public boolean updateUser(Context ctx) {
        userManager = new PosUserManagement(ctx);
        return userManager.update(this);
    }

}
