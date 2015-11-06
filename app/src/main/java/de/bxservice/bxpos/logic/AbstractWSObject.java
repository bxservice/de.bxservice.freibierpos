package de.bxservice.bxpos.logic;

import android.content.Context;

import org.idempiere.webservice.client.base.LoginRequest;
import org.idempiere.webservice.client.net.WebServiceClient;

import java.util.Properties;

/**
 * Created by Diego Ruiz on 4/11/15.
 */
public abstract class AbstractWSObject {

    private LoginRequest login;
    private WebServiceClient client;


    public AbstractWSObject() {

        login = new LoginRequest();
        login.setUser("SuperUser");
        login.setPass("System");
        login.setClientID(11);
        login.setRoleID(102);
        login.setOrgID(0);

        client = new WebServiceClient();
        client.setAttempts(3);
        client.setTimeout(2000);
        client.setAttemptsTimeout(2000);
        client.setWebServiceUrl(getUrlBase());
       // client.setUserAgentProduct("Android Test WS Client");

        runWebService();
    }

    public LoginRequest getLogin() {
        return login;
    }

    public String getUrlBase() {
        return "https://192.168.2.4:8443";
    }

    public WebServiceClient getClient() {
        return client;
    }

    public void runWebService() {
        queryPerformed();
        /*saveRequestResponse();
        printTotal();
        System.out.println();*/
    }

    public abstract String getServiceType();

    public abstract void queryPerformed();



}
