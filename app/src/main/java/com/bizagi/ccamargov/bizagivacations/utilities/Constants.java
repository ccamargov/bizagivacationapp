package com.bizagi.ccamargov.bizagivacations.utilities;

public class Constants {

    public static final String ACCOUNT_TYPE = "com.bizagi.ccamargov.bizagivacations";

    public static final String AUTHTOKEN_TYPE =
            "com.bizagi.ccamargov.bizagivacations";

    public static final String SYNC_IN_ORDER_FINISHED
            = "com.longport.timeandattendance.SYNC_IN_ORDER_FINISHED";

    public static final String SYNC_IN_ORDER_FINISHED_WITH_ERRORS
            = "com.longport.timeandattendance.SYNC_IN_ORDER_FINISHED_WITH_ERRORS";

    public static final String DEFAULT_LANG_PREF = "TimeAndAttendancePrefs";
    public static final String DEFAULT_LANG_KEY = "Lang";

    public static final String TYPE_SYNC = "type_sync";
    public static final int DOWNLOAD_AND_UPLOAD_ALL_RECORDS_SYNC = 0;

    public static final String PARAM_USERNAME = "email";
    static final String PARAM_PASSWORD = "password";
    public static final String PARAM_AUTHTOKEN_TYPE = "authTokenType";
    public static final String PARAM_USER_REMOTE_ID = "remote_id";
    public static final String PARAM_USER_API_KEY = "api_key";
    public static final String PARAM_LAST_SYNC = "last_sync";
    public static final String PARAM_FULL_NAME = "user_full_name";
    private static final String IP_HOST_SERVER = "http://204.232.187.235:9082";
    public static final String LOGIN_SERVER_URL
            = IP_HOST_SERVER + "/api/v1/users/login_user.json";
    public static final String GET_REQUEST_VACATIONS_URL
            = IP_HOST_SERVER + "/api/v1/request_vacations/get_all_requests.json";
    public static final String ERRORS = "errors";
    static final int REGISTRATION_TIMEOUT = 30 * 1000;
    public static final int POLL_FREQUENCY = 3600;

    public static final int VALUE_HTTP_OK = 200;
    public static final int VALUE_WRONG_API_KEY = 3;

    public static final String PARAM_FROM_TO = "go_to_from";
    public static final String VALUE_FROM_LOGIN_TO_HOME = "login_to_activity";

    public static final String ALERT_DIALOG_TITLE_KEY = "alert_dialog_title";
    public static final String ALERT_DIALOG_COLOR_KEY = "alert_dialog_color";
    public static final String ALERT_DIALOG_MESSAGE_KEY = "alert_message_color";

    public static final String ALERT_SUCCESS = "alert_success";
    public static final String ALERT_WARNING = "alert_warning";
    public static final String ALERT_DANGER = "alert_danger";
    public static final String ALERT_INFO = "alert_info";

}
