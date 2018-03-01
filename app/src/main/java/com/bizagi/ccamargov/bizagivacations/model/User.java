package com.bizagi.ccamargov.bizagivacations.model;

import java.io.Serializable;

public class User implements Serializable {

    private int remote_id;
    private String api_key;

    public User(int remote_id, String api_key) {
        this.remote_id = remote_id;
        this.api_key = api_key;
    }

    public int getRemoteId() {
        return remote_id;
    }

    public String getApiKey() {
        return api_key;
    }

}
