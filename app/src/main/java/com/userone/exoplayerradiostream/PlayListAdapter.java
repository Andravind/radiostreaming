package com.userone.exoplayerradiostream;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by userone on 5/16/2018.
 */

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.ViewHolder> {
    private Context context;
    private List<CurrentPlayModel> playModelsList;

    PlayListAdapter(Context context, List<CurrentPlayModel> upComingPlayList) {
        this.context = context;
        this.playModelsList = upComingPlayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CurrentPlayModel model = playModelsList.get(position);
        if (position == 0)
            holder.iv_Radio_Station_Playing.setVisibility(View.VISIBLE);

        holder.tv_Radio_Station_Name.setText(model.getName());
        if (model.getDescription() != null && model.getDescription().length() > 0)
            holder.tv_Radio_Station_desc.setText(model.getDescription());

        if (model.getImage_path() != null && model.getImage_path().length() > 0) {
            Picasso.get().load(model.getImage_path()).into(holder.radio_stream_img, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {


                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return playModelsList != null ? playModelsList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_Radio_Station_Name;
        TextView tv_Radio_Station_desc;
        ImageView iv_Radio_Station_Playing;
        ImageView radio_stream_img;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_Radio_Station_Name = (TextView) itemView.findViewById(R.id.radio_name);
            tv_Radio_Station_desc = (TextView) itemView.findViewById(R.id.radio_desc);
            iv_Radio_Station_Playing = (ImageView) itemView.findViewById(R.id.radio_playing_img);
            radio_stream_img = (ImageView) itemView.findViewById(R.id.radio_stream_img);
        }
    }
}
