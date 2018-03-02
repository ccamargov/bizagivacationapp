package com.bizagi.ccamargov.bizagivacations.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Authentication service that will be executed after the application is deployed.
 * It is used for AppAuthenticator to work.
 * This class is necessary to work with the android synchronization process.
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

public class AuthenticationService extends Service {

    private static final String TAG = AuthenticationService.class.getSimpleName();
    private AppAuthenticator mAuthenticator;
    // Auto-generated service methods
    @Override
    public void onCreate() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Bizagi: Authentication Service started.");
        }
        mAuthenticator = new AppAuthenticator(this);
    }
    // Auto-generated service methods
    @Override
    public void onDestroy() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Bizagi: Authentication Service stopped.");
        }
    }
    // Auto-generated service methods
    @Override
    public IBinder onBind(Intent intent) {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG,
                    "Bizagi: Returning the AccountAuthenticator binder for intent."
                            + intent);
        }
        return mAuthenticator.getIBinder();
    }

}