package com.bizagi.ccamargov.bizagivacations.utilities;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Utility class. Class to handle and optimize the sending of Http requests from Android applications to external servers.
 * User in the synchronization process
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public final class VolleySingleton {

    private static VolleySingleton oSingleton;
    private RequestQueue oRQ;
    private static Context oContext;
    /**
     *  Constructor class
     *  @param context Application context
     */
    private VolleySingleton(Context context) {
        VolleySingleton.oContext = context;
        oRQ = getRequestQueue();
    }

    /**
     * Get VolleySingleton instance
     * @param context Application context
     */
    public static synchronized VolleySingleton getInstance(Context context) {
        if (oSingleton == null) {
            oSingleton = new VolleySingleton(context.getApplicationContext());
        }
        return oSingleton;
    }
    // Get http request Queued
    private RequestQueue getRequestQueue() {
        if (oRQ == null) {
            oRQ = Volley.newRequestQueue(oContext.getApplicationContext());
        }
        return oRQ;
    }
    // Add new http request to Queue
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
