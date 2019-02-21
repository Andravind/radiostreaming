package com.userone.exoplayerradiostream;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * Created by userone on 5/21/2018.
 */

public class NotificationContextWrapper extends ContextWrapper {
    /**
     * Notification action intent strings
     */
    public static final String NOTIFICATION_INTENT_PLAY_PAUSE = "INTENT_PLAYPAUSE";
    public static final String NOTIFICATION_INTENT_CANCEL = "INTENT_CANCEL";
    public static final String NOTIFICATION_INTENT_OPEN_PLAYER = "INTENT_OPENPLAYER";
    private final NotificationCompat.InboxStyle inboxStyle;
    private final Context mContext;
    private NotificationManager notificationManager;

    /**
     * constructor to generate the Notification Channels to above oreo devices
     *
     * @param context
     */
    public NotificationContextWrapper(Context context) {
        super(context);
        mContext = context;
        inboxStyle = new NotificationCompat.InboxStyle();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel messageChannel = new NotificationChannel("PLAYER_CHANNEL", "PLAYER_CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
            messageChannel.setLightColor(Color.RED);
            getNotificationManager().createNotificationChannel(messageChannel);
        }
    }


    /**
     * Get the notification manager.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getNotificationManager() {
        if (notificationManager == null)
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        return notificationManager;
    }

    /**
     * applying notification sound to notification builder
     *
     * @return
     */
    private Uri getDefaultSound() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    /**
     * welcome Self Broad Cast Notification.
     */
    public void showPlayer(SimpleExoPlayer player, String singerName, String songName, int smallImage, Bitmap bigArt) {

        /**
         * Intents
         */
        Intent intentPlayPause = new Intent(NOTIFICATION_INTENT_PLAY_PAUSE);
        Intent intentOpenPlayer = new Intent(NOTIFICATION_INTENT_OPEN_PLAYER);
        Intent intentCancel = new Intent(NOTIFICATION_INTENT_CANCEL);

        /**
         * Pending intents
         */
        PendingIntent playPausePending = PendingIntent.getBroadcast(this, 23, intentPlayPause, 0);
        PendingIntent openPending = PendingIntent.getBroadcast(this, 31, intentOpenPlayer, 0);
        PendingIntent cancelPending = PendingIntent.getBroadcast(this, 12, intentCancel, 0);

        /**
         * Remote view for normal view
         */

        RemoteViews mNotificationTemplate = new RemoteViews(this.getPackageName(), R.layout.notification);
        Notification.Builder notificationBuilder = new Notification.Builder(this);


        mNotificationTemplate.setTextViewText(R.id.notification_line_one, singerName);
        mNotificationTemplate.setTextViewText(R.id.notification_line_two, songName);
        mNotificationTemplate.setImageViewResource(R.id.notification_play, player.getPlayWhenReady() ? R.drawable.notification_bar_pause : R.drawable.notification_bar_play);
        mNotificationTemplate.setImageViewBitmap(R.id.notification_image, bigArt);

        /**
         * OnClickPending intent for collapsed notification
         */
        mNotificationTemplate.setOnClickPendingIntent(R.id.notification_collapse, cancelPending);
        mNotificationTemplate.setOnClickPendingIntent(R.id.notification_play, playPausePending);

        /**
         * Create notification instance
         */
        Notification notification = notificationBuilder
                .setSmallIcon(smallImage)
                .setContentIntent(openPending)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContent(mNotificationTemplate)
                .setUsesChronometer(true)
                .build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        /**
         * Expanded notification
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

            RemoteViews mExpandedView = new RemoteViews(this.getPackageName(), R.layout.notification_expanded);

            mExpandedView.setTextViewText(R.id.notification_line_one, singerName);
            mExpandedView.setTextViewText(R.id.notification_line_two, songName);
            mExpandedView.setImageViewResource(R.id.notification_expanded_play, player.getPlayWhenReady() ? R.drawable.notification_bar_pause : R.drawable.notification_bar_play);
            mExpandedView.setImageViewBitmap(R.id.notification_image, bigArt);

            mExpandedView.setOnClickPendingIntent(R.id.notification_collapse, cancelPending);
            mExpandedView.setOnClickPendingIntent(R.id.notification_expanded_play, playPausePending);

            notification.bigContentView = mExpandedView;
            if (getNotificationManager() != null)
                getNotificationManager().notify(9999, notification);
        }

    }
    /**
     * State enum for Radio Player state (IDLE, PLAYING, STOPPED, INTERRUPTED)
     */
    public enum State {
        IDLE,
        PLAYING,
        STOPPED,
    }
}
