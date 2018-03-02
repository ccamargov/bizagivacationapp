package com.bizagi.ccamargov.bizagivacations.model;

import java.io.Serializable;

/**
 * Object that represents the structure of a user.
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class User implements Serializable {

    private int remote_id;
    private String api_key;
    private String first_name;
    private String last_name;

    /**
     *  Constructor class
     *  @param remote_id Record id from server (Original id)
     *  @param api_key Token that determines whether or not the user has access to the
     *                 web services hosted on the central server.
     *  @param first_name Employee's first name
     *  @param last_name Employee's last name
     */
    public User(int remote_id, String api_key, String first_name, String last_name) {
        this.remote_id = remote_id;
        this.api_key = api_key;
        this.first_name = first_name;
        this.last_name = last_name;
    }
    /**
     *  Access method
     */
    public int getRemoteId() {
        return remote_id;
    }
    /**
     *  Access method
     */
    public void setRemoteId(int remote_id) {
        this.remote_id = remote_id;
    }
    /**
     *  Access method
     */
    public String getApiKey() {
        return api_key;
    }
    /**
     *  Access method
     */
    public void setApiKey(String api_key) {
        this.api_key = api_key;
    }
    /**
     *  Access method
     */
    public String getFirstName() {
        return first_name;
    }
    /**
     *  Access method
     */
    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }
    /**
     *  Access method
     */
    public String getLastName() {
        return last_name;
    }
    /**
     *  Access method
     */
    public void setLastName(String last_name) {
        this.last_name = last_name;
    }
    /**
     *  Access method
     */
    public String getFullName() {
        return this.first_name + " " + this.last_name;
    }
}
