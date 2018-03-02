package com.bizagi.ccamargov.bizagivacations.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {

    private static SyncAdapter oSyncAdapter = null;

    public SyncService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (oSyncAdapter == null) {
            oSyncAdapter = new SyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return oSyncAdapter.getSyncAdapterBinder();
    }
}
