package com.bizagi.ccamargov.bizagivacations.utilities;

/**
 * Utility class, which contains all the constant values that will be used throughout the application.
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class Constants {
    // Account type to recognize that the account is from this application
    public static final String ACCOUNT_TYPE = "com.bizagi.ccamargov.bizagivacations";
    // Token to check that the authentication service is from this application
    public static final String AUTHTOKEN_TYPE =
            "com.bizagi.ccamargov.bizagivacations";
    /**
     * Broadcast key to handle Broadcast's to handle broadcast that will notify the user of the success
     * of the synchronization
     */
    public static final String SYNC_IN_ORDER_FINISHED
            = "com.longport.timeandattendance.SYNC_IN_ORDER_FINISHED";
    /**
     * Broadcast key to handle Broadcast's to handle broadcast that will notify the user of the
     * errors in the synchronization process.
     */
    public static final String SYNC_IN_ORDER_FINISHED_WITH_ERRORS
            = "com.longport.timeandattendance.SYNC_IN_ORDER_FINISHED_WITH_ERRORS";
    // Key to manage handle preferences
    public static final String DEFAULT_LANG_PREF = "TimeAndAttendancePrefs";
    // Key to manage handle lang preference
    public static final String DEFAULT_LANG_KEY = "Lang";
    // Key yo handle synchronization types.
    public static final String TYPE_SYNC = "type_sync";
    public static final int DOWNLOAD_AND_UPLOAD_ALL_RECORDS_SYNC = 0;
    public static final int UPLOAD_REQUEST_RECORDS = 1;
    //Parameters used to assign the properties to the Bizagi account in the Android account manager.
    public static final String PARAM_USERNAME = "email";
    static final String PARAM_PASSWORD = "password";
    public static final String PARAM_AUTHTOKEN_TYPE = "authTokenType";
    public static final String PARAM_USER_REMOTE_ID = "remote_id";
    public static final String PARAM_USER_API_KEY = "api_key";
    public static final String PARAM_LAST_SYNC = "last_sync";
    public static final String PARAM_FULL_NAME = "user_full_name";
    // URL of the server that is being used to consume resources and for the synchronization process.
    private static final String IP_HOST_SERVER = "https://bizagivacations.herokuapp.com";
    // List of web services that will be used in the application.
    public static final String LOGIN_SERVER_URL
            = IP_HOST_SERVER + "/api/v1/users/login_user.json";
    public static final String GET_REQUEST_VACATIONS_URL
            = IP_HOST_SERVER + "/api/v1/request_vacations/get_all_requests.json";
    public static final String POST_REQUEST_VACATIONS_UPDATE_URL
            = IP_HOST_SERVER + "/api/v1/request_vacations/update_request_vacation.json";
    public static final String ERRORS = "errors";
    static final int REGISTRATION_TIMEOUT = 30 * 1000;
    // Frequency with which automatic synchronization will be performed
    public static final int POLL_FREQUENCY = 3600;
    public static final int VALUE_HTTP_OK = 200;
    public static final int VALUE_WRONG_API_KEY = 3;
    public static final String PARAM_FROM_TO = "go_to_from";
    public static final String VALUE_FROM_LOGIN_TO_HOME = "login_to_activity";
    // Custom alert's type properties.
    public static final String ALERT_DIALOG_TITLE_KEY = "alert_dialog_title";
    public static final String ALERT_DIALOG_COLOR_KEY = "alert_dialog_color";
    public static final String ALERT_DIALOG_MESSAGE_KEY = "alert_message_color";
    public static final String ALERT_SUCCESS = "alert_success";
    public static final String ALERT_WARNING = "alert_warning";
    public static final String ALERT_DANGER = "alert_danger";
    public static final String ALERT_INFO = "alert_info";
    public static final int RECORD_STATE_SYNCED = 0;
    public static final int RECORD_STATE_PENDING_SYNC = 1;
    // Response from server when a record has been uploaded succesfully
    public static final String REQUEST_VACATION_UPDATED = "request_vacation_updated";

}
