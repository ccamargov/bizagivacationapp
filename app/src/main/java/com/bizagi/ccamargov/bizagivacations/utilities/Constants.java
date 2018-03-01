package com.bizagi.ccamargov.bizagivacations.utilities;

public class Constants {

    public static final String ACCOUNT_TYPE = "com.bizagi.ccamargov.bizagivacations";

    public static final String AUTHTOKEN_TYPE =
            "com.bizagi.ccamargov.bizagivacations";

    public static final String PARAM_USERNAME = "email";
    static final String PARAM_PASSWORD = "password";
    public static final String PARAM_AUTHTOKEN_TYPE = "authTokenType";
    public static final String PARAM_USER_REMOTE_ID = "remote_id";
    public static final String PARAM_USER_API_KEY = "api_key";
    public static final String PARAM_LAST_SYNC = "last_sync";
    private static final String IP_HOST_SERVER = "http://204.232.187.235:9082";
    static final String LOGIN_SERVER_URL
            = IP_HOST_SERVER + "/api/v1/users/login_user.json";
    public static final String ERRORS = "errors";
    static final int REGISTRATION_TIMEOUT = 30 * 1000;
    public static final int POLL_FREQUENCY = 3600;

}
