package de.bxservice.bxpos.fcm;

/**
 * Created by Diego Ruiz on 5/24/16.
 */
public interface BXPOSNotificationCode {

    /**Type of update request*/
    String REQUEST_TYPE = "RT";

    /**Request that is not mandatory*/
    int SUGGESTED_REQUEST_CODE = 100;

    /**Request that is mandatory*/
    int MANDATORY_REQUEST_CODE = 200;
}
