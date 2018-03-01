package com.bizagi.ccamargov.bizagivacations.dbsql;

import android.content.Context;

public class DataBaseOperations {

    private static DatabaseHelper oDbHelper;
    private static DataBaseOperations oInstance = new DataBaseOperations();

    private DataBaseOperations() {

    }

    public static DataBaseOperations getInstance(Context context) {
        if (oDbHelper == null) {
            oDbHelper = new DatabaseHelper(context);
        }
        return oInstance;
    }

}
