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