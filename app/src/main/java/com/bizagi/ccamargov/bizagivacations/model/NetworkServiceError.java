package com.bizagi.ccamargov.bizagivacations.model;

import java.io.Serializable;

public class NetworkServiceError implements Serializable {

    private int http_code;
    private int web_code;

    public NetworkServiceError(int http_code, int web_code) {
        this.http_code = http_code;
        this.web_code = web_code;
    }

    public int getHttpCode() {
        return http_code;
    }

    public int getWebCode() {
        return web_code;
    }

}