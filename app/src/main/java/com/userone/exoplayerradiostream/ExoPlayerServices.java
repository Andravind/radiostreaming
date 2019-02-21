package com.userone.exoplayerradiostream;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by userone on 5/22/2018.
 */

public class ExoPlayerServices extends Service implements PlayerActionBroadcast.ConnectivityReceiverListener {

    private final IBinder mBinder = new LocalBinder();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ExoPlayerApplication.getInstance().setConnectivityListener(this);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /**
     * this method are used to SignalR bind service to activity/fragments
     */
    public class LocalBinder extends Binder {
        public ExoPlayerServices getService() {
            return ExoPlayerServices.this;
        }
    }

    @Override
    public void onNetworkConnectionChanged(int stateType) {
        try {
            Intent intent = new Intent().setAction("PLAY_STATUS_CHANGED");
            intent.putExtra("PLAY_STATUS", stateType);
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
