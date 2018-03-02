package com.bizagi.ccamargov.bizagivacations.provider;

import android.net.Uri;

public class ContractModel {

    public static final int OK_STATE = 0;
    public static final int SYNC_STATE = 1;
    public static final String AUTHORITY
            = "com.bizagi.ccamargov.bizagivacations";
    private static final Uri CONTENT_URI_BASE
            = Uri.parse("content://" + AUTHORITY);
    private final static String SINGLE_MIME
            = "vnd.android.cursor.item/vnd." + AUTHORITY;
    private final static String MULTIPLE_MIME
            = "vnd.android.cursor.dir/vnd." + AUTHORITY;
    public static final String ROUT_REQUEST_VACATIONS = "request_vacations";

    interface RequestVacationCols {
        String PROCESS = "process";
        String ACTIVITY = "activity";
        String REQUEST_DATE = "request_date";
        String EMPLOYEE = "employee";
        String BEGIN_DATE = "begin_date";
        String END_DATE = "end_date";
        String LAST_VACATION_ON = "last_vacation_on";
        String REQUEST_STATUS = "approved";
        String REMOTE_ID = "id_remote";
    }

    public static class RequestVacation implements RequestVacationCols {
        public static final Uri CONTENT_URI =
                CONTENT_URI_BASE.buildUpon().appendPath(ROUT_REQUEST_VACATIONS).build();
    }

    private ContractModel() {

    }

    static String createMIME(String id) {
        if (id != null) {
            return MULTIPLE_MIME + id;
        } else {
            return null;
        }
    }

    static String createMimeItem(String id) {
        if (id != null) {
            return SINGLE_MIME + id;
        } else {
            return null;
        }
    }
}