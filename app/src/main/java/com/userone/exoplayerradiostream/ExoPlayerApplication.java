package com.userone.exoplayerradiostream;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

/**
 * Created by userone on 5/22/2018.
 */

public class ExoPlayerApplication extends Application {

    private static ExoPlayerApplication mInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        getApplicationContext().startService(new Intent(getApplicationContext(), ExoPlayerServices.class));
    }

    public static synchronized ExoPlayerApplication getInstance() {
        return mInstance;
    }


    public void setConnectivityListener(PlayerActionBroadcast.ConnectivityReceiverListener listener) {
        PlayerActionBroadcast.connectivityReceiverListener = listener;
    }

}
