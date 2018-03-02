package com.bizagi.ccamargov.bizagivacations.model;

/**
 * Object that represents the response obtained by the application,
 * after trying to authenticate the user on the server (WebService Login)
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class AuthenticateServerState {

    private boolean serverResponseReceived;
    private boolean userAuthorized;

    /**
     *  Constructor class
     */
    public AuthenticateServerState() {
    }
    /**
     *  Access method
     *  True return if a response has been received from the server.
     */
    public boolean isServerResponseReceived() {
        return serverResponseReceived;
    }
    /**
     *  Access method
     */
    public void setServerResponseReceived(boolean serverResponseReceived) {
        this.serverResponseReceived = serverResponseReceived;
    }
    /**
     *  Access method
     *  True return if a response has been received from the server.
     */
    public boolean isUserAuthorized() {
        return userAuthorized;
    }
    /**
     *  Access method
     */
    public void setUserAuthorized(boolean userAuthorized) {
        this.userAuthorized = userAuthorized;
    }

}
