package com.bizagi.ccamargov.bizagivacations.provider;

import android.net.Uri;

/**
 * Contract class that enables communication between the content provider of the current application
 * with external applications, indicating how the information can be accessed.
 * This class is essential for the synchronization service.
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class ContractModel {
    // States of the local -> remote sync process.
    public static final int OK_STATE = 0;
    public static final int SYNC_STATE = 1;
    // Authorization key that enables communication with other applications
    public static final String AUTHORITY
            = "com.bizagi.ccamargov.bizagivacations";
    // Uri base that will be used to build the communication Uris of the application.
    private static final Uri CONTENT_URI_BASE
            = Uri.parse("content://" + AUTHORITY);
    // Used to return a single row
    private final static String SINGLE_MIME
            = "vnd.android.cursor.item/vnd." + AUTHORITY;
    // Used to return a multiple rows
    private final static String MULTIPLE_MIME
            = "vnd.android.cursor.dir/vnd." + AUTHORITY;
    // Name of the request vacations model in the database (As table). Will be user to build Uris
    public static final String ROUT_REQUEST_VACATIONS = "request_vacations";
    // Interface that stores the name of all columns that will be included in the RequestVacations table.
    interface RequestVacationCols {
        String PROCESS = "process";
        String ACTIVITY = "activity";
        String REQUEST_DATE = "request_date";
        String EMPLOYEE = "employee";
        String BEGIN_DATE = "begin_date";
        String END_DATE = "end_date";
        String LAST_VACATION_ON = "last_vacation_on";
        String REQUEST_STATUS = "approved";
        String STATE = "state";
        String REMOTE_ID = "id_remote";
        String UPDATE_STATE = "is_updated";

    }
    /**
     * Class that stores the structure of the model RequestVacation in the database.
     * Includes the communication uri to obtain information on the table.
     * Includes all RequestVacations table columns.
     * @author Camilo Camargo
     * @author http://ccamargov.byethost18.com/
     * @version 1.0
     * @since 1.0
     */
    public static class RequestVacation implements RequestVacationCols {
        public static final Uri CONTENT_URI =
                CONTENT_URI_BASE.buildUpon().appendPath(ROUT_REQUEST_VACATIONS).build();
    }
    /**
     *  Constructor class
     */
    private ContractModel() {

    }
    /**
     *  Returns MIME to work with multiple rows.
     *  @param id Route used to build the mime
     */
    static String createMIME(String id) {
        if (id != null) {
            return MULTIPLE_MIME + id;
        } else {
            return null;
        }
    }
    /**
     *  Returns MIME to work with a single row.
     *  @param id Route used to build the mime
     */
    static String createMimeItem(String id) {
        if (id != null) {
            return SINGLE_MIME + id;
        } else {
            return null;
        }
    }
}