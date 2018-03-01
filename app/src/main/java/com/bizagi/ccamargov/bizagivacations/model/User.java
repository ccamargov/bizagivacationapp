package com.bizagi.ccamargov.bizagivacations.model;

import java.io.Serializable;

public class User implements Serializable {

    private int remote_id;
    private String api_key;
    private String first_name;
    private String last_name;

    public User(int remote_id, String api_key, String first_name, String last_name) {
        this.remote_id = remote_id;
        this.api_key = api_key;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public int getRemoteId() {
        return remote_id;
    }

    public void setRemoteId(int remote_id) {
        this.remote_id = remote_id;
    }

    public String getApiKey() {
        return api_key;
    }

    public void setApiKey(String api_key) {
        this.api_key = api_key;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getFullName() {
        return this.first_name + " " + this.last_name;
    }
}
