package de.bxservice.bxpos.fcm;

/**
 * Created by Diego Ruiz on 5/24/16.
 */
public interface BXPOSNotificationCode {

    /**Type of update request*/
    String REQUEST_TYPE = "RT";

    /**Request that is not mandatory*/
    int RECOMMENDED_REQUEST_CODE = 100;

    /**Request that is mandatory*/
    int MANDATORY_REQUEST_CODE = 200;

    /**Request actions send as click_action to perform on click in the notification*/
    /**Mandatory request action*/
    String MANDATORY_UPDATE_ACTION = "LOAD_DATA";
    String RECOMMENDED_UPDATE_ACTION = "OPEN_ACTIVITY";

}
