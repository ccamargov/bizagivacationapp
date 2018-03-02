package com.bizagi.ccamargov.bizagivacations.provider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.bizagi.ccamargov.bizagivacations.dbsql.DatabaseHelper;

public class ProviderModel extends ContentProvider {

    private ContentResolver oResolver;
    private DatabaseHelper oDatabaseHelper;

    private static final UriMatcher uriMatcher;
    private static final int ID_URI_REQUEST_VACATIONS = 10;
    private static final int ID_URI_REQUEST_VACATION_ID = 15;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ContractModel.AUTHORITY, ContractModel.ROUT_REQUEST_VACATIONS,
                ID_URI_REQUEST_VACATIONS);
        uriMatcher.addURI(ContractModel.AUTHORITY, ContractModel.ROUT_REQUEST_VACATIONS + "/#",
                ID_URI_REQUEST_VACATION_ID);
    }

    @Override
    public boolean onCreate() {
        oDatabaseHelper = new DatabaseHelper(getContext());
        oResolver = getContext().getContentResolver();
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (uriMatcher.match(uri)) {
            case ID_URI_REQUEST_VACATIONS:
                return ContractModel.createMIME(ContractModel.ROUT_REQUEST_VACATIONS);
            case ID_URI_REQUEST_VACATION_ID:
                return ContractModel.createMimeItem(ContractModel.ROUT_REQUEST_VACATIONS);
            default:
                throw new UnsupportedOperationException("Bizagi: Unknown URI =>" + uri);
        }
    }

    @Override
    public Cursor query(
            @NonNull Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
        SQLiteDatabase oSQLiteDB = oDatabaseHelper.getWritableDatabase();
        int iMatch = uriMatcher.match(uri);
        Cursor oCursor;
        switch (iMatch) {
            case ID_URI_REQUEST_VACATIONS:
                oCursor = oSQLiteDB.query(ContractModel.ROUT_REQUEST_VACATIONS, projection,
                        selection, selectionArgs,
                        null, null, sortOrder);
                oCursor.setNotificationUri(
                        oResolver,
                        ContractModel.RequestVacation.CONTENT_URI);
                break;
            case ID_URI_REQUEST_VACATION_ID:
                long lIdRequestVacation = ContentUris.parseId(uri);
                oCursor = oSQLiteDB.query(ContractModel.ROUT_REQUEST_VACATIONS, projection,
                        ContractModel.RequestVacation.REMOTE_ID + " = " + lIdRequestVacation,
                        selectionArgs, null, null, sortOrder);
                oCursor.setNotificationUri(
                        oResolver,
                        ContractModel.RequestVacation.CONTENT_URI);
                break;
            default:
                throw new IllegalArgumentException("Bizagi: URI not supported => " + uri);
        }
        return oCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        SQLiteDatabase oSQLiteDB = oDatabaseHelper.getWritableDatabase();
        ContentValues contentValues;
        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }
        int iMatch = uriMatcher.match(uri);
        switch (iMatch) {
            case ID_URI_REQUEST_VACATIONS:
                long lRecordId = oSQLiteDB.insert(ContractModel.ROUT_REQUEST_VACATIONS,
                        null, contentValues);
                if (lRecordId > 0) {
                    Uri oUriRequestVacation = ContentUris.withAppendedId(
                            ContractModel.RequestVacation.CONTENT_URI, lRecordId);
                    oResolver.notifyChange(oUriRequestVacation, null, false);
                    return oUriRequestVacation;
                }
                throw new SQLException("Bizagi: Error inserting a new row (RequestVacation) => " + uri);
            default:
                throw new IllegalArgumentException("Bizagi: URI not supported => " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase oSQLiteDB = oDatabaseHelper.getWritableDatabase();
        int iMatch = uriMatcher.match(uri);
        int iRowAffected;
        switch (iMatch) {
            case ID_URI_REQUEST_VACATIONS:
                iRowAffected = oSQLiteDB.delete(ContractModel.ROUT_REQUEST_VACATIONS,
                        selection,
                        selectionArgs);
                break;
            case ID_URI_REQUEST_VACATION_ID:
                long lIdRequestVacation = ContentUris.parseId(uri);
                iRowAffected = oSQLiteDB.delete(ContractModel.ROUT_REQUEST_VACATIONS,
                        ContractModel.RequestVacation.REMOTE_ID + " = " + lIdRequestVacation
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Bizagi: Unknown element => " +
                        uri);
        }
        oResolver.
                notifyChange(uri, null, false);
        return iRowAffected;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase oSQLiteDB = oDatabaseHelper.getWritableDatabase();
        int iRowAffected;
        switch (uriMatcher.match(uri)) {
            case ID_URI_REQUEST_VACATIONS:
                iRowAffected = oSQLiteDB.update(ContractModel.ROUT_REQUEST_VACATIONS, values,
                        selection, selectionArgs);
                break;
            case ID_URI_REQUEST_VACATION_ID:
                String lIdRequestVacation = uri.getPathSegments().get(1);
                iRowAffected = oSQLiteDB.update(ContractModel.ROUT_REQUEST_VACATIONS, values,
                        ContractModel.RequestVacation.REMOTE_ID + " = " + lIdRequestVacation
                                + (!TextUtils.isEmpty(selection) ?
                                " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Bizagi: Unknown URI => " + uri);
        }
        oResolver.notifyChange(uri, null, false);
        return iRowAffected;
    }

}