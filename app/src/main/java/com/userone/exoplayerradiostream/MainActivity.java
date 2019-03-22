package com.userone.exoplayerradiostream;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public static SimpleExoPlayer player;
    ImageView playicon;
    private Context mContext;
    private BandwidthMeter bandwidthMeter;
    private ExtractorsFactory extractorsFactory;
    private TrackSelection.Factory trackSelectionFactory;
    private TrackSelector trackSelector;
    private DefaultBandwidthMeter defaultBandwidthMeter;
    private DataSource.Factory dataSourceFactory;
    private MediaSource mediaSource;
    private CurrentPlayModel playModel;
    private NotificationContextWrapper notificationContextWrapper;
    private Bitmap bitmap;
    private ExoPlayerServices mExoPlayerService;
    private ExoPlayerReceiver mExoPlayerReceiver;
    private boolean mBound;

	//Replace your latest stream url my stream works for only 15 days.
    private String radio_strem_url ="http://testrocks.out.airtime.pro:8000/testrocks_b";
    private String get_latest_stream_shows= "http://testrocks.airtime.pro/api/live-info-v2?shows=10";
    /**
     * SignalR Service to class level receiver connection
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            if (service instanceof ExoPlayerServices.LocalBinder) {
                ExoPlayerServices.LocalBinder binder = (ExoPlayerServices.LocalBinder) service;
                mExoPlayerService = binder.getService();
                mBound = true;
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;
        initializeReceivers();
        final ImageView iv_album_img = (ImageView) findViewById(R.id.albumimg);
        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        playicon = (ImageView) findViewById(R.id.playicon);
        notificationContextWrapper = new NotificationContextWrapper(mContext);


        playicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.getPlayWhenReady()) {
                    player.setPlayWhenReady(false);
                    playicon.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.play_button));
                    notificationContextWrapper.showPlayer(player, playModel.getName(), playModel.getDescription(), R.mipmap.default_art, bitmap);
                } else {
                    player.setPlayWhenReady(true);
                    playicon.setImageDrawable(ContextCompat.getDrawable(mContext, R.mipmap.pause));
                    notificationContextWrapper.showPlayer(player, playModel.getName(), playModel.getDescription(), R.mipmap.default_art, bitmap);
                }
            }
        });

        bandwidthMeter = new DefaultBandwidthMeter();
        extractorsFactory = new DefaultExtractorsFactory();

        trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(trackSelectionFactory);

/*        dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "mediaPlayerSample"),
                (TransferListener<? super DataSource>) bandwidthMeter);*/

        defaultBandwidthMeter = new DefaultBandwidthMeter();
        dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "mediaPlayerSample"), defaultBandwidthMeter);


        mediaSource = new ExtractorMediaSource(Uri.parse(radio_strem_url), dataSourceFactory, extractorsFactory, null, null);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);


        player.prepare(mediaSource);

       

        JsonObjectRequest stringRequest = new JsonObjectRequest(get_latest_stream_shows, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<CurrentPlayModel> upComingPlayList = new ArrayList<>();

                try {
                    JSONObject showsJson = response.getJSONObject("shows");
                    JSONObject currentPlayObject = showsJson.getJSONObject("current");
                    if (currentPlayObject != null) {
                        playModel = new CurrentPlayModel(currentPlayObject.getString("name"),
                                currentPlayObject.getString("description"),
                                currentPlayObject.getInt("instance_id"),
                                currentPlayObject.getInt("id"),
                                currentPlayObject.getString("image_path"));

                        new ImageDownloadAsync().execute();
                        Picasso.get().load(playModel.getImage_path()).into(iv_album_img, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                iv_album_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.image_loader));

                            }
                        });
                        upComingPlayList.add(playModel);
                    }else{
                        playicon.setVisibility(View.GONE);
                    }


                    JSONArray upcomingshowArray = showsJson.getJSONArray("next");
                    if (upcomingshowArray.length() > 0) {
                        for (int i = 0; i < upcomingshowArray.length(); i++) {
                            JSONObject nextObject = upcomingshowArray.getJSONObject(i);
                            upComingPlayList.add(new CurrentPlayModel(nextObject.getString("name"),
                                    nextObject.getString("description"),
                                    nextObject.getInt("instance_id"),
                                    nextObject.getInt("id"),
                                    nextObject.getString("image_path")));
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));


                        recyclerView.setAdapter(new PlayListAdapter(mContext, upComingPlayList));
                    }

                } catch (JSONException /*| InterruptedException | ExecutionException */je) {
                    je.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(stringRequest);

    }

    private void initializeReceivers() {
        mExoPlayerReceiver = new ExoPlayerReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("PLAY_STATUS_CHANGED");
        registerReceiver(mExoPlayerReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound)
            unregisterReceiver(mExoPlayerReceiver);

        player.setPlayWhenReady(false);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onBackPressed() {
    }

    class ImageDownloadAsync extends AsyncTask<String, Void, Void> {


        protected Void doInBackground(String... urls) {
            try {
                URL url = new URL(playModel.getImage_path());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
        }
    }

    /**
     * SignalR Service to class level receiver connection
     */
    private class ExoPlayerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case "PLAY_STATUS_CHANGED":
                        int play_status = intent.getIntExtra("PLAY_STATUS", 0);
                        if (play_status == 1) {
                            if (player.getPlayWhenReady())
                                player.setPlayWhenReady(false);
                            else
                                player.setPlayWhenReady(true);
                            playicon.setImageDrawable(ContextCompat.getDrawable(mContext, player.getPlayWhenReady() ? R.mipmap.pause : R.mipmap.play_button));
                            notificationContextWrapper.showPlayer(player, playModel.getName(), playModel.getDescription(), R.mipmap.default_art, bitmap);

                        } else if (play_status == 0) {
                            player.setPlayWhenReady(false);
                            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancelAll();
                            playicon.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.play_button
                            ));
                        }
                        break;
                }
            }
        }
    }
}