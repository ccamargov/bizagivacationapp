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

/**
 * Class used to handle all transactions to the local database,
 * requested by external applications or services.
 * This class is essential for the synchronization service.
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class ProviderModel extends ContentProvider {

    private ContentResolver oResolver;
    private DatabaseHelper oDatabaseHelper;
    // uriMatcher is used to decode URIS
    private static final UriMatcher uriMatcher;
    // Uri to get all request vacations
    private static final int ID_URI_REQUEST_VACATIONS = 10;
    // Uri to get a single request vacation
    private static final int ID_URI_REQUEST_VACATION_ID = 15;
    // Related Uris to RequestVacation are added to be decoded in the provider's methods.
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ContractModel.AUTHORITY, ContractModel.ROUT_REQUEST_VACATIONS,
                ID_URI_REQUEST_VACATIONS);
        uriMatcher.addURI(ContractModel.AUTHORITY, ContractModel.ROUT_REQUEST_VACATIONS + "/#",
                ID_URI_REQUEST_VACATION_ID);
    }
    // Get database manager instance and provides applications access to the content model.
    @Override
    public boolean onCreate() {
        oDatabaseHelper = new DatabaseHelper(getContext());
        oResolver = getContext().getContentResolver();
        return true;
    }
    // Handle requests for the MIME type of the data at the given URI.
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

    /**
     * Handle query requests from clients
     * Use the uriMatcher to determine which model you are going to work on.
     * @param uri Model Uri
     * @param projection Columns that will be returned in the query
     * @param selection Query conditions
     * @param selectionArgs Conditions params
     * @param sortOrder Order query by
     * @return Cursor with the data result
     */
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
    /**
     * Inserts a row into a table at the given URL.
     * Use the uriMatcher to determine which model you are going to work on.
     * @param uri Model Uri
     * @param values Values of the new record that will be created
     * @return Uri created to the new record
     */
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
    /**
     * Deletes a row from a table at the given URL.
     * Use the uriMatcher to determine which model you are going to work on.
     * @param uri Model Uri
     * @param selection Query conditions
     * @param selectionArgs Conditions params
     * @return Affected row id
     */
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
    /**
     * Updates a row from a table at the given URL.
     * Use the uriMatcher to determine which model you are going to work on.
     * @param uri Model Uri
     * @param selection Query conditions
     * @param selectionArgs Conditions params
     * @return Affected row id
     */
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