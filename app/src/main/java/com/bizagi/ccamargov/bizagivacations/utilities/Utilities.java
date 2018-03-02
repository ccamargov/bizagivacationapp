package com.bizagi.ccamargov.bizagivacations.utilities;

import android.database.Cursor;


import com.bizagi.ccamargov.bizagivacations.provider.ContractModel;

import org.json.JSONException;
import org.json.JSONObject;


public class Utilities {

    public static JSONObject CursorToJsonObject(String route, Cursor c, String api_key) {
        JSONObject jObject = new JSONObject();
        switch (route) {
            case ContractModel.ROUT_REQUEST_VACATIONS:
                int iApproved;
                int iRemoteId;
                iApproved = c.getInt(c.getColumnIndex(ContractModel.RequestVacation.REQUEST_STATUS));
                iRemoteId = c.getInt(c.getColumnIndex(ContractModel.RequestVacation.REMOTE_ID));
                try {
                    jObject.put(ContractModel.RequestVacation.REQUEST_STATUS, iApproved);
                    jObject.put(ContractModel.RequestVacation.REMOTE_ID, iRemoteId);
                    jObject.put(Constants.PARAM_USER_API_KEY, api_key);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return jObject;
    }

}
