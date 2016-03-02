package de.bxservice.bxpos.logic.webservices;

import android.content.Context;

import java.util.Properties;

import de.bxservice.bxpos.logic.AssetsPropertyReader;
import de.bxservice.bxpos.logic.PosProperties;

/**
 * Created by Diego Ruiz on 6/11/15.
 */
public class WebServiceRequestData {

    private static volatile WebServiceRequestData instance = null;

    private String username        = null;
    private String password        = null;
    private String clientId        = null;
    private String roleId          = null;
    private String orgId           = null;
    private String attemptsNo      = null;
    private String timeout         = null;
    private String attemptsTimeout = null;
    private String urlBase         = null;
    private String warehouseId = null;

    private WebServiceRequestData() {

    }

    public static synchronized WebServiceRequestData getInstance() {
        if (instance == null) {
            instance = new WebServiceRequestData();
        }

        return instance;
    }

    public void readValues(Context context) {
        //Properties reader
        AssetsPropertyReader assetsPropertyReader = new AssetsPropertyReader(context);
        Properties properties = assetsPropertyReader.getProperties();

        //clientId        = properties.getProperty(PosProperties.CLIENT_PROPERTY);
        //roleId          = properties.getProperty(role);
        //orgId           = properties.getProperty(PosProperties.ORG_PROPERTY);
        attemptsNo      = properties.getProperty(PosProperties.ATTEMPTS_PROPERTY);
        timeout         = properties.getProperty(PosProperties.TIMEOUT_PROPERTY);
        attemptsTimeout = properties.getProperty(PosProperties.ATTEMPTS_TIMEOUT_PROPERTY);

    }

    public boolean isDataComplete() {
        if (username != null && password != null &&
                clientId != null && roleId != null &&
                orgId != null && attemptsNo != null &&
                attemptsNo != null && timeout != null &&
                attemptsTimeout != null && urlBase != null)
            return true;

        return false;
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

    public void setWarehouseId(String warehouseId) {
        this.warehouseId = warehouseId;
    }

    public String getWarehouseId() {
        return warehouseId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAttemptsNo() {
        return attemptsNo;
    }

    public void setAttemptsNo(String attemptsNo) {
        this.attemptsNo = attemptsNo;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    public String getAttemptsTimeout() {
        return attemptsTimeout;
    }

    public void setAttemptsTimeout(String attemptsTimeout) {
        this.attemptsTimeout = attemptsTimeout;
    }

    public String getUrlBase() {
        return urlBase;
    }

    public void setUrlBase(String urlBase) {
        this.urlBase = urlBase;
    }
}
