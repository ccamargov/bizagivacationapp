package com.bizagi.ccamargov.bizagivacations.utilities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public final class VolleySingleton {

    private static VolleySingleton oSingleton;
    private RequestQueue oRQ;
    private static Context oContext;

    private VolleySingleton(Context context) {
        VolleySingleton.oContext = context;
        oRQ = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (oSingleton == null) {
            oSingleton = new VolleySingleton(context.getApplicationContext());
        }
        return oSingleton;
    }

    private RequestQueue getRequestQueue() {
        if (oRQ == null) {
            oRQ = Volley.newRequestQueue(oContext.getApplicationContext());
        }
        return oRQ;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
