package com.bizagi.ccamargov.bizagivacations.model;

public class AuthenticateServerState {

    private boolean serverResponseReceived;
    private boolean userAuthorized;

    public AuthenticateServerState() {
    }

    public boolean isServerResponseReceived() {
        return serverResponseReceived;
    }

    public void setServerResponseReceived(boolean serverResponseReceived) {
        this.serverResponseReceived = serverResponseReceived;
    }

    public boolean isUserAuthorized() {
        return userAuthorized;
    }

    public void setUserAuthorized(boolean userAuthorized) {
        this.userAuthorized = userAuthorized;
    }

}
