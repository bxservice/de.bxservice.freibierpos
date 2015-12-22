package de.bxservice.bxpos.logic.model.pos;

import de.bxservice.bxpos.persistence.dbcontract.UserContract;

/**
 * Created by Diego Ruiz on 15/12/15.
 */
public class PosUser extends UserContract.User {

    int id;
    String username;
    String password;

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
}
