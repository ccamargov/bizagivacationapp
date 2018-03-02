package com.bizagi.ccamargov.bizagivacations.dbsql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.bizagi.ccamargov.bizagivacations.provider.ContractModel;
import com.bizagi.ccamargov.bizagivacations.utilities.Constants;

/**
 * Allows executing transactions between the application and the local database (SQLite3).
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    /** Defines the database version and name.
     * This version must be modified for each update
     */
    private static final int CURRENT_VERSION = 1;
    private static final String DATABASE_NAME = "bizagi.db";

    /**
     *  Constructor class
     * @param  context  Activity context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
    }

    /**
     *  Called when the database has been open.
     *  Enables the handling of foreign keys in the database.
     * @param  db  Local database instance
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    /**
     *  Called when the database has been created.
     * @param  database  Local database instance
     */
    @Override
    public void onCreate(SQLiteDatabase database) {
        createTables(database);
    }

    /**
     *  Create the tables of the persistent models that will be needed within the application.
     * @param  database  Local database instance
     */
    private void createTables(SQLiteDatabase database) {
        // Stores the string with the query that will be executed in the database.
        String sQuery;
        sQuery = "CREATE TABLE " + ContractModel.ROUT_REQUEST_VACATIONS + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ContractModel.RequestVacation.PROCESS + " TEXT, " +
                ContractModel.RequestVacation.ACTIVITY + " TEXT, " +
                ContractModel.RequestVacation.REQUEST_DATE + " TEXT, " +
                ContractModel.RequestVacation.EMPLOYEE + " TEXT, " +
                ContractModel.RequestVacation.BEGIN_DATE + " TEXT, " +
                ContractModel.RequestVacation.END_DATE + " TEXT, " +
                ContractModel.RequestVacation.LAST_VACATION_ON + " TEXT, " +
                ContractModel.RequestVacation.REQUEST_STATUS + " INTEGER, " +
                ContractModel.RequestVacation.REMOTE_ID + " INTEGER UNIQUE," +
                ContractModel.RequestVacation.STATE + " INTEGER NOT NULL DEFAULT " + ContractModel.OK_STATE + "," +
                ContractModel.RequestVacation.UPDATE_STATE + " INTEGER NOT NULL DEFAULT " + Constants.RECORD_STATE_SYNCED + ")";
        database.execSQL(sQuery);
    }

    /**
     *  Called when the database has been updated (Version released).
     * @param  db  Local database instance
     * @param  oldVersion  Previous database version
     * @param  newVersion  New database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE " + ContractModel.ROUT_REQUEST_VACATIONS);
        } catch (SQLiteException ignored) {
        }
        onCreate(db);
    }


}