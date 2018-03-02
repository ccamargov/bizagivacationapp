package com.bizagi.ccamargov.bizagivacations.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bizagi.ccamargov.bizagivacations.model.AuthenticateServerState;
import com.bizagi.ccamargov.bizagivacations.model.NetworkServiceError;
import com.bizagi.ccamargov.bizagivacations.model.RequestVacation;
import com.bizagi.ccamargov.bizagivacations.provider.ContractModel;
import com.bizagi.ccamargov.bizagivacations.utilities.Constants;
import com.bizagi.ccamargov.bizagivacations.utilities.NetworkUtilities;
import com.bizagi.ccamargov.bizagivacations.utilities.Utilities;
import com.bizagi.ccamargov.bizagivacations.utilities.VolleySingleton;
import com.google.gson.Gson;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bizagi.ccamargov.bizagivacations.R;

/**
 * An abstract implementation of SyncAdapter that generates a thread to invoke the synchronization
 * operations that will be executed for the application.
 * If a synchronization operation is already in progress when a synchronization request is received,
 * an error will be returned to the new request and the existing request will be allowed to continue.
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 * @Abbreviations:
 *      SRT => Send Request To...
 *      SDM => Sync Download Mode
 *      MCSE => Manage Custom Sync Errors
 */

public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = SyncAdapter.class.getSimpleName();
    private ContentResolver oResolver;
    // User account
    private static Account oUserAccount;
    private Gson oGson = new Gson();
    // Counter that stores the number of processes that have been executed
    private int iCountRequests;
    // Check if there was an error by the server during synchronization
    private boolean bAnyServerError;
    // Check if there was an error in the api_key during synchronization
    private boolean bTokenErrors;
    // Store the total sync requests
    private int iTotalRequests;
    // Columns of the RequestVacation model that will work during synchronization
    private static final String[] PROJECTION_REQUEST_VACATION = new String[] {
            BaseColumns._ID,
            ContractModel.RequestVacation.REMOTE_ID,
            ContractModel.RequestVacation.PROCESS,
            ContractModel.RequestVacation.ACTIVITY,
            ContractModel.RequestVacation.REQUEST_DATE,
            ContractModel.RequestVacation.EMPLOYEE,
            ContractModel.RequestVacation.BEGIN_DATE,
            ContractModel.RequestVacation.END_DATE,
            ContractModel.RequestVacation.LAST_VACATION_ON,
            ContractModel.RequestVacation.REQUEST_STATUS,
            ContractModel.RequestVacation.STATE,
            ContractModel.RequestVacation.UPDATE_STATE
    };
    // Index of the columns that were previously defined to RequestVacation model
    private static final int REQUEST_VACATION_REMOTE_ID = 1;
    private static final int REQUEST_VACATION_PROCESS = 2;
    private static final int REQUEST_VACATION_ACTIVITY = 3;
    private static final int REQUEST_VACATION_REQUEST_DATE = 4;
    private static final int REQUEST_VACATION_EMPLOYEE = 5;
    private static final int REQUEST_VACATION_BEGIN_DATE = 6;
    private static final int REQUEST_VACATION_END_DATE = 7;
    private static final int REQUEST_VACATION_LAST_VACATION_ON = 8;
    private static final int REQUEST_VACATION_IS_APPROVED = 9;
    private static final int REQUEST_VACATION_STATE = 10;
    private static final int REQUEST_VACATION_UPDATE_STATE = 11;

    /**
     * Constructor class
     * @param context Application context
     * @param autoInitialize Auto-calculated value
     */
    SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        oResolver = context.getContentResolver();
    }

    /**
     * Perform a sync for Bizagi account.
     * @param account Bizagi Account
     * @param extras Extra data to consider in the sync process
     * @param authority Key value to check provider access
     * @param provider Content provider that allows database transactions
     * @param syncResult Synchronization result object
     */
    @Override
    public void onPerformSync(Account account,
                              Bundle extras,
                              String authority,
                              ContentProviderClient provider,
                              final SyncResult syncResult) {
        if (oUserAccount == null) {
            oUserAccount = account;
        }
        if (checkServerUserStatus()) {
            Log.i(TAG, "Bizagi: Sync(onPerformSync) starting...");
            startSync(extras.getInt(Constants.TYPE_SYNC), syncResult);
        } else {
            Log.i(TAG, "Bizagi: Sync, Unauthorized user, removing" +
                    " account and canceling synchronization");
        }
    }

    /**
     * Static method that starts a manual sync
     * @param context Application context
     * @param typeSync Type of the Synchronization requested by the applicacion
     */
    public static void syncNow(Context context, int typeSync) {
        Log.i(TAG, "Bizagi: Sync, Performing manual synchronization request");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putInt(Constants.TYPE_SYNC, typeSync);
        ContentResolver.requestSync(oUserAccount,
                context.getString(R.string.provider_authority), bundle);
    }

    /**
     * Check if the user can authenticate on the server
     * @return True if user can authenticate on the server
     */
    private boolean checkServerUserStatus() {
        AccountManager oAccountManager = AccountManager.get(getContext());
        AuthenticateServerState oAuthServerState =
                NetworkUtilities.authenticateOnServer(oUserAccount.name,
                        oAccountManager.getPassword(oUserAccount),
                        null, null);
        if (oAuthServerState.isServerResponseReceived() && !oAuthServerState.isUserAuthorized()) {
            oAccountManager.removeAccount(oUserAccount, null, null);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Execute all synchronization operations depending on the type of synchronization
     * indicated by the application/user.
     * @param type_sync Type of the Synchronization requested by the applicacion
     * @param syncResult Syncronization result object
     */
    private void startSync(int type_sync, final SyncResult syncResult) {
        Log.i(TAG, "Bizagi: Starting Sync type => " + type_sync);
        iCountRequests = 0;
        bAnyServerError = false;
        bTokenErrors = false;
        AccountManager oAccountManager = AccountManager.get(getContext());
        String sApiKey = oAccountManager.getUserData(oUserAccount,
                Constants.PARAM_USER_API_KEY);
        switch (type_sync) {
            case Constants.DOWNLOAD_AND_UPLOAD_ALL_RECORDS_SYNC:
                iTotalRequests = 1;
                SRTRequestVacationsSDM(sApiKey, syncResult);
                break;
            case Constants.UPLOAD_REQUEST_RECORDS:
                iTotalRequests = 1;
                SRTRequestVacationSUM(sApiKey);
                break;
        }
    }

    /**
     * Prepares the RequestVacation model records that have not been loaded/synchronized to the server.
     */
    private void initRequestVacationUpdate() {
        Uri uri = ContractModel.RequestVacation.CONTENT_URI;
        String sQuerySelection = ContractModel.RequestVacation.UPDATE_STATE + " = ? AND "
                + ContractModel.RequestVacation.STATE + " = ?";
        String[] oSelectionArgs = new String[] {String.valueOf(Constants.RECORD_STATE_PENDING_SYNC), String.valueOf(ContractModel.OK_STATE)};
        ContentValues oValues = new ContentValues();
        oValues.put(ContractModel.RequestVacation.STATE, String.valueOf(ContractModel.SYNC_STATE));
        int iResults = oResolver.update(uri, oValues, sQuerySelection, oSelectionArgs);
        Log.i(TAG, "Bizagi: Sync, Records queued to update: " + iResults);
    }
    /**
     * Returns all records that have not been uploaded to the system
     */
    private Cursor getRequestVacationDirtyRecords() {
        Uri uri = ContractModel.RequestVacation.CONTENT_URI;
        String sQuerySelection = ContractModel.RequestVacation.UPDATE_STATE + " = ? AND "
                + ContractModel.RequestVacation.STATE + " = ?";
        String[] oSelectionArgs = new String[] {String.valueOf(Constants.RECORD_STATE_PENDING_SYNC),  String.valueOf(ContractModel.SYNC_STATE)};
        return oResolver.query(uri, PROJECTION_REQUEST_VACATION, sQuerySelection, oSelectionArgs, null);
    }
    /**
     *Update the status of the records that were successfully uploaded to the server.
     * @param idLocal Local record that was successfully loaded
     */
    private void processRemoteRequestVacationUpdate(int idLocal) {
        Uri uri = ContractModel.RequestVacation.CONTENT_URI;
        String sQuerySelection = ContractModel.RequestVacation.REMOTE_ID + " = ?";
        String[] oSelectionArgs = new String[] {String.valueOf(idLocal)};
        ContentValues oValues = new ContentValues();
        oValues.put(ContractModel.RequestVacation.UPDATE_STATE, String.valueOf(Constants.RECORD_STATE_SYNCED));
        oValues.put(ContractModel.RequestVacation.STATE, String.valueOf(ContractModel.OK_STATE));
        oResolver.update(uri, oValues, sQuerySelection, oSelectionArgs);
    }

    /**
     * Starts synchronization process remote -> local, to update the local RequestVacation model.
     * Make a server request using the respective API, to return the information.
     * @param api_key Api_key assigned to the user who is working on the application.
     *                It is used to approve access to APIs registered in the server.
     * @param syncResult Syncronization result object
     */
    private void SRTRequestVacationsSDM(String api_key, final SyncResult syncResult) {
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        Constants.GET_REQUEST_VACATIONS_URL + "?" +
                                Constants.PARAM_USER_API_KEY + "=" + api_key,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                iCountRequests++;
                                try {
                                    JSONArray request_vacations = response.getJSONArray(ContractModel.ROUT_REQUEST_VACATIONS);
                                    updateLocalDataRequestVacations(request_vacations, syncResult);
                                } catch (JSONException e) {
                                    try {
                                        JSONArray errors = response.getJSONArray(Constants.ERRORS);
                                        manageCustomSyncErrors(errors);
                                    } catch (JSONException ignored) {
                                        bAnyServerError = true;
                                    }
                                }
                                notifyFullSyncResultToUser();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                iCountRequests++;
                                bAnyServerError = true;
                                notifyFullSyncResultToUser();
                                Log.d(TAG, "Bizagi: Sync (makeLocalSync - Request Vacations), Exception => " +
                                        error.getLocalizedMessage());
                            }
                        }
                )
        );
    }
    /**
     * Starts synchronization process local -> remote, to upload the remote RequestVacation model.
     * Make a server request using the respective API, to return the information.
     * @param api_key Api_key assigned to the user who is working on the application.
     *                It is used to approve access to APIs registered in the server.
     */
    private void SRTRequestVacationSUM(String api_key) {
        initRequestVacationUpdate();
        final Cursor oCursor = getRequestVacationDirtyRecords();
        Log.i(TAG, "Bizagi: Sync, Found " + oCursor.getCount() + " dirty records");
        iCountRequests++;
        if (oCursor.getCount() > 0) {
            int iItemsWorking = oCursor.getCount();
            while (oCursor.moveToNext()) {
                final int iRequestId = oCursor.getInt(REQUEST_VACATION_REMOTE_ID);
                final int finalIItemsWorking = iItemsWorking;
                VolleySingleton.getInstance(getContext()).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.POST,
                                Constants.POST_REQUEST_VACATIONS_UPDATE_URL,
                                Utilities.CursorToJsonObject(ContractModel.ROUT_REQUEST_VACATIONS
                                        , oCursor, api_key),
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            response.getJSONArray(Constants.REQUEST_VACATION_UPDATED);
                                            processRemoteRequestVacationUpdate(iRequestId);
                                        } catch (JSONException e) {
                                            try {
                                                JSONArray errors = response.getJSONArray(Constants.ERRORS);
                                                manageCustomSyncErrors(errors);
                                            } catch (JSONException ignored) {
                                                bAnyServerError = true;
                                            }
                                        }
                                        if (finalIItemsWorking == oCursor.getPosition()) {
                                            notifyFullSyncResultToUser();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        bAnyServerError = true;
                                        if (finalIItemsWorking == oCursor.getPosition()) {
                                            notifyFullSyncResultToUser();
                                        }
                                        Log.d(TAG, "Bizagi: Sync (makeRemoteSync), Exception => " +
                                                error.getLocalizedMessage());
                                    }
                                }
                        ) {
                            @Override
                            public Map<String, String> getHeaders() {
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                headers.put("Accept", "application/json");
                                return headers;
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8" + getParamsEncoding();
                            }
                        }
                );
                iItemsWorking++;
            }

        } else {
            notifyFullSyncResultToUser();
            Log.i(TAG, "Bizagi: Sync, Updating no required");
        }
        oCursor.close();
    }

    /**
     * Update the local model according to the changes in the remote model.
     * @param request_vacations JSON Array that contains all records returned from the api for the RequestVacation model.
     * @param syncResult Syncronization result object
     */
    private void updateLocalDataRequestVacations(JSONArray request_vacations, SyncResult syncResult) {
        RequestVacation[] res = oGson.fromJson(request_vacations != null ? request_vacations.toString() : null,
                RequestVacation[].class);
        List<RequestVacation> data = Arrays.asList(res);
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        HashMap<String, RequestVacation> expenseMap = new HashMap<>();
        for (RequestVacation e : data) {
            expenseMap.put(String.valueOf(e.getId()), e);
        }
        Uri uri = ContractModel.RequestVacation.CONTENT_URI;
        String sConditionQuery = ContractModel.RequestVacation.REMOTE_ID + " IS NOT NULL";
        Cursor oCursor = oResolver.query(uri, PROJECTION_REQUEST_VACATION, sConditionQuery,
                null, null);
        if (oCursor != null) {
            Log.i(TAG, "Bizagi: Sync REQUEST VACATION, Found " + oCursor.getCount() + " local records");
            String iRemoteEmpId;
            String sProcess;
            String sActivity;
            String sRequestDate;
            String sEmployee;
            String sBeginDate;
            String sEndDate;
            String sLastVacationOn;
            int isApproved;
            int iRecordState;
            int iUpdateState;
            while (oCursor.moveToNext()) {
                syncResult.stats.numEntries++;
                iRecordState = oCursor.getInt(REQUEST_VACATION_STATE);
                iUpdateState = oCursor.getInt(REQUEST_VACATION_UPDATE_STATE);
                if (iUpdateState == Constants.RECORD_STATE_SYNCED && iRecordState == ContractModel.OK_STATE) {
                    iRemoteEmpId = oCursor.getString(REQUEST_VACATION_REMOTE_ID);
                    sProcess = oCursor.getString(REQUEST_VACATION_PROCESS);
                    sActivity = oCursor.getString(REQUEST_VACATION_ACTIVITY);
                    sRequestDate = oCursor.getString(REQUEST_VACATION_REQUEST_DATE);
                    sEmployee = oCursor.getString(REQUEST_VACATION_EMPLOYEE);
                    sBeginDate = oCursor.getString(REQUEST_VACATION_BEGIN_DATE);
                    sEndDate = oCursor.getString(REQUEST_VACATION_END_DATE);
                    sLastVacationOn = oCursor.getString(REQUEST_VACATION_LAST_VACATION_ON);
                    isApproved = oCursor.getInt(REQUEST_VACATION_IS_APPROVED);
                    RequestVacation match = expenseMap.get(iRemoteEmpId);
                    if (match != null) {
                        expenseMap.remove(iRemoteEmpId);
                        Uri existingUri = ContractModel.RequestVacation.CONTENT_URI.buildUpon()
                                .appendPath(iRemoteEmpId).build();
                        boolean bCheckPR = match.getProcess() != null &&
                                !match.getProcess().equals(sProcess);
                        boolean bCheckAC = match.getActivity() != null &&
                                !match.getActivity().equals(sActivity);
                        boolean bCheckRD = match.getRequestDate() != null &&
                                !match.getRequestDate().equals(sRequestDate);
                        boolean bCheckEM = match.getEmployee() != null &&
                                !match.getEmployee().equals(sEmployee);
                        boolean bCheckBD = match.getBeginDate() != null &&
                                !match.getBeginDate().equals(sBeginDate);
                        boolean bCheckED = match.getEndDate() != null &&
                                !match.getEndDate().equals(sEndDate);
                        boolean bCheckLV = match.getlastVacationOn() != null &&
                                !match.getlastVacationOn().equals(sLastVacationOn);
                        boolean bCheckIA = match.getStatusRequest() != isApproved;
                        if (bCheckPR || bCheckAC || bCheckRD || bCheckEM || bCheckBD
                                || bCheckED || bCheckLV || bCheckIA) {
                            Log.i(TAG, "Bizagi: Sync REQUEST VACATIONS, Scheduling update for => " + existingUri);
                            ops.add(ContentProviderOperation.newUpdate(existingUri)
                                    .withValue(ContractModel.RequestVacation.PROCESS,
                                            match.getProcess())
                                    .withValue(ContractModel.RequestVacation.ACTIVITY,
                                            match.getActivity())
                                    .withValue(ContractModel.RequestVacation.REQUEST_DATE,
                                            match.getRequestDate())
                                    .withValue(ContractModel.RequestVacation.EMPLOYEE,
                                            match.getEmployee())
                                    .withValue(ContractModel.RequestVacation.BEGIN_DATE,
                                            match.getBeginDate())
                                    .withValue(ContractModel.RequestVacation.END_DATE,
                                            match.getEndDate())
                                    .withValue(ContractModel.RequestVacation.LAST_VACATION_ON,
                                            match.getlastVacationOn())
                                    .withValue(ContractModel.RequestVacation.REQUEST_STATUS,
                                            match.getStatusRequest())
                                    .build());
                            syncResult.stats.numUpdates++;
                        } else {
                            Log.i(TAG, "Bizagi: Sync REQUEST VACATIONS, No changes to record => " + existingUri);
                        }
                    } else {
                        Uri deleteUri = ContractModel.RequestVacation.CONTENT_URI.buildUpon()
                                .appendPath(iRemoteEmpId).build();
                        Log.i(TAG, "Bizagi: Sync REQUEST VACATIONS, Scheduling removal of => " + deleteUri);
                        ops.add(ContentProviderOperation.newDelete(deleteUri).build());
                        syncResult.stats.numDeletes++;
                    }
                }
            }
            oCursor.close();
        }
        for (RequestVacation e : expenseMap.values()) {
            Log.i(TAG, "Bizagi: Sync REQUEST VACATIONS, Scheduling insertion of => " + e.getId());
            ops.add(ContentProviderOperation.newInsert(ContractModel.RequestVacation.CONTENT_URI)
                    .withValue(ContractModel.RequestVacation.REMOTE_ID,
                            e.getId())
                    .withValue(ContractModel.RequestVacation.PROCESS,
                            e.getProcess())
                    .withValue(ContractModel.RequestVacation.ACTIVITY,
                            e.getActivity())
                    .withValue(ContractModel.RequestVacation.REQUEST_DATE,
                            e.getRequestDate())
                    .withValue(ContractModel.RequestVacation.EMPLOYEE,
                            e.getEmployee())
                    .withValue(ContractModel.RequestVacation.BEGIN_DATE,
                            e.getBeginDate())
                    .withValue(ContractModel.RequestVacation.END_DATE,
                            e.getEndDate())
                    .withValue(ContractModel.RequestVacation.LAST_VACATION_ON,
                            e.getlastVacationOn())
                    .withValue(ContractModel.RequestVacation.REQUEST_STATUS,
                            e.getStatusRequest())
                    .build());
            syncResult.stats.numInserts++;
        }
        if (syncResult.stats.numInserts > 0 ||
                syncResult.stats.numUpdates > 0 ||
                syncResult.stats.numDeletes > 0) {
            Log.i(TAG, "Bizagi: Sync REQUEST VACATIONS, Applying operations...");
            try {
                oResolver.applyBatch(ContractModel.AUTHORITY, ops);
            } catch (RemoteException | OperationApplicationException e) {
                e.printStackTrace();
            }
            oResolver.notifyChange(
                    ContractModel.RequestVacation.CONTENT_URI,
                    null,
                    false);
            Log.i(TAG, "Bizagi: Sync REQUEST VACATIONS finished");
        }
    }

    /**
     * Notifies the user about the result of the synchronization, using broadcasts.
     */
    private void notifyFullSyncResultToUser() {
        if (iTotalRequests == iCountRequests) {
            AccountManager oAccountManager = AccountManager.get(getContext());
            if (!bAnyServerError && !bTokenErrors) {
                DateTimeFormatter oFormatDate = DateTimeFormat.forPattern("dd MMM, hh:mm a");
                LocalDateTime oCurrentTimestamp = new LocalDateTime();
                oAccountManager.setUserData(oUserAccount, Constants.PARAM_LAST_SYNC,
                        oFormatDate.print(oCurrentTimestamp));
                getContext().sendBroadcast(new Intent(Constants
                        .SYNC_IN_ORDER_FINISHED));
            } else {
                getContext().sendBroadcast(new Intent(Constants
                        .SYNC_IN_ORDER_FINISHED_WITH_ERRORS));
            }
        }
    }

    /**
     * Manage errors returned by the web service (Not by the server request - HTTP Errors).
     * @param errors List of errors returned by the service, as JSONArray object.
     */
    private void manageCustomSyncErrors(JSONArray errors) {
        if (!bTokenErrors) {
            NetworkServiceError[] oErrors = new Gson().fromJson(errors.toString(),
                    NetworkServiceError[].class);
            for (NetworkServiceError oError : oErrors) {
                if (oError.getHttpCode() == Constants.VALUE_HTTP_OK) {
                    switch (oError.getWebCode()) {
                        case Constants.VALUE_WRONG_API_KEY:
                            bTokenErrors = true;
                            break;
                    }
                }
            }
        }
    }

}