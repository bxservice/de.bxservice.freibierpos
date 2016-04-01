package de.bxservice.bxpos.logic.webservices;

import org.idempiere.webservice.client.base.LoginRequest;
import org.idempiere.webservice.client.net.WebServiceClient;

/**
 * Created by Diego Ruiz on 4/11/15.
 */
abstract class AbstractWSObject {

    private LoginRequest login;
    private WebServiceClient client;
    private WebServiceRequestData wsData;
    private Object parameter;

    protected AbstractWSObject() {

        wsData = WebServiceRequestData.getInstance();

        if (wsData.isDataComplete()) {
            initLogin();
            initClient();
            runWebService();
        }
    }

    public AbstractWSObject(Object parameter) {

        wsData = WebServiceRequestData.getInstance();

        if (wsData.isDataComplete()) {
            initLogin();
            initClient();
            setParameter(parameter);
            runWebService();
        }

    }

    private void initLogin(){
        login = new LoginRequest();

        login.setUser(wsData.getUsername());
        login.setPass(wsData.getPassword());
        login.setClientID(Integer.parseInt(wsData.getClientId()));
        login.setRoleID(Integer.parseInt(wsData.getRoleId()));
        login.setOrgID(Integer.parseInt(wsData.getOrgId()));
        login.setWarehouseID(Integer.parseInt(wsData.getWarehouseId()));
    }

    private void initClient(){
        client = new WebServiceClient();

        client.setAttempts(Integer.parseInt(wsData.getAttemptsNo()));
        client.setTimeout(Integer.parseInt(wsData.getTimeout()));
        client.setAttemptsTimeout(Integer.parseInt(wsData.getAttemptsTimeout()));
        client.setWebServiceUrl(wsData.getUrlBase());
        client.setUserAgentProduct("Android Test WS Client");
    }

    protected LoginRequest getLogin() {
        return login;
    }

    protected WebServiceClient getClient() {
        return client;
    }

    private void runWebService() {
        queryPerformed();
        /*saveRequestResponse();
        printTotal();
        System.out.println();*/
    }

    public abstract String getServiceType();

    protected abstract void queryPerformed();

    private void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    protected Object getParameter() {
        return parameter;
    }

}
