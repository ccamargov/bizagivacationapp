package com.bizagi.ccamargov.bizagivacations.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Synchronization service that will be executed in the background after the application is deployed.
 * It is used to execute the synchronization functions contained in the SyncAdapter.
 * This class is necessary to work with the android synchronization process.
 * @author Camilo Camargo
 * @author http://ccamargov.byethost18.com/
 * @version 1.0
 * @since 1.0
 */

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
