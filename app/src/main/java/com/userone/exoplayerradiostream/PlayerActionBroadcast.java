package com.userone.exoplayerradiostream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by userone on 5/21/2018.
 */

public class PlayerActionBroadcast extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;
    public static int TYPE_CANCELED = 0;
    public static int TYPE_PLAY_PAUSE = 1;


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(NotificationContextWrapper.NOTIFICATION_INTENT_PLAY_PAUSE))
            connectivityReceiverListener.onNetworkConnectionChanged(TYPE_PLAY_PAUSE);
        else if (action.equals(NotificationContextWrapper.NOTIFICATION_INTENT_CANCEL))
            connectivityReceiverListener.onNetworkConnectionChanged(TYPE_CANCELED);
    }

    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(int stateType);
    }
}
