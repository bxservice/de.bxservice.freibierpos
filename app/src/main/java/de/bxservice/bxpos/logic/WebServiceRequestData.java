package de.bxservice.bxpos.logic;

import android.content.Context;

import java.util.Properties;

/**
 * Created by Diego Ruiz on 6/11/15.
 */
public class WebServiceRequestData {

    private static volatile WebServiceRequestData instance = null;

    private String username;
    private String password;
    private String clientId;
    private String roleId;
    private String orgId;
    private String attemptsNo;
    private String timeout;
    private String attemptsTimeout;
    private String urlBase;

    //Properties reader
    private AssetsPropertyReader assetsPropertyReader;
    private Properties properties;


    private WebServiceRequestData() {    }

    public static synchronized WebServiceRequestData getInstance() {
        if (instance == null) {
            instance = new WebServiceRequestData();
        }

        return instance;
    }

    public void readValues(Context context){

        assetsPropertyReader = new AssetsPropertyReader(context);
        properties = assetsPropertyReader.getProperties("bxpos.properties");

        username = properties.getProperty("username");
        password = properties.getProperty("password");
        clientId = properties.getProperty("clientId");
        roleId = properties.getProperty("roleID");
        orgId = properties.getProperty("orgId");
        attemptsNo = properties.getProperty("attemtpsno");
        timeout = properties.getProperty("timeout");
        attemptsTimeout = properties.getProperty("attemptsTimeout");
        urlBase = properties.getProperty("urlBase");

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
