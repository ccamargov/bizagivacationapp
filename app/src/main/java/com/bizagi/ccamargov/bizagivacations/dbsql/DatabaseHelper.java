package com.bizagi.ccamargov.bizagivacations.dbsql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.bizagi.ccamargov.bizagivacations.provider.ContractModel;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bizagi.db";
    private static final int CURRENT_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, CURRENT_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createTables(database);
    }

    private void createTables(SQLiteDatabase database) {
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
                ContractModel.RequestVacation.REMOTE_ID + " INTEGER UNIQUE)";
        database.execSQL(sQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE " + ContractModel.ROUT_REQUEST_VACATIONS);
        } catch (SQLiteException ignored) {
        }
        onCreate(db);
    }


}