package com.bizagi.ccamargov.bizagivacations.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AuthenticationService extends Service {

    private static final String TAG = AuthenticationService.class.getSimpleName();
    private AppAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Bizagi: Authentication Service started.");
        }
        mAuthenticator = new AppAuthenticator(this);
    }

    @Override
    public void onDestroy() {
        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "Bizagi: Authentication Service stopped.");
        }
    }

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