package com.bizagi.ccamargov.bizagivacations.model;

import java.io.Serializable;

/**
 * Object that represents the structure of an error message
 * that can be returned from the server (mainly during synchronization - API Request).
 * This object will be serializable
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class NetworkServiceError implements Serializable {

    private int http_code;
    private int web_code;

    /**
     *  Constructor class
     *  @param http_code HTTP error code
     *  @param web_code Custom type code of error presented (See more in utilities/Constants)
     */
    public NetworkServiceError(int http_code, int web_code) {
        this.http_code = http_code;
        this.web_code = web_code;
    }
    /**
     *  Access method
     */
    public int getHttpCode() {
        return http_code;
    }
    /**
     *  Access method
     */
    public int getWebCode() {
        return web_code;
    }

}