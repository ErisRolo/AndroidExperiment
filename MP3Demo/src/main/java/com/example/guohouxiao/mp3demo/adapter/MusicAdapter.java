package com.example.guohouxiao.mp3demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.guohouxiao.mp3demo.bean.Music;
import com.example.guohouxiao.mp3demo.R;
import com.example.guohouxiao.mp3demo.ui.PlayActivity;

import java.util.List;

/**
 * Created by guohouxiao on 2017/11/25.
 * Music适配器
 */

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private static final String MUSIC_DATA = "music_data";
    private static final String MUSIC_POSITION = "listPosition";

    private Context mContext;
    private List<Music> mMusicList;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View musicView;
        LinearLayout ll_item;
        TextView tv_title;
        TextView tv_artist;

        public ViewHolder(View itemView) {
            super(itemView);

            musicView = itemView;
            ll_item = itemView.findViewById(R.id.ll_item);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_artist = itemView.findViewById(R.id.tv_artist);
        }
    }

    public MusicAdapter(Context context, List<Music> musicList) {
        mContext = context;
        mMusicList = musicList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Music music = mMusicList.get(position);
        final int listPosition = position;
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PlayActivity.class);
                intent.putExtra(MUSIC_POSITION,listPosition);
                intent.putExtra(MUSIC_DATA, music);
                mContext.startActivity(intent);
            }
        });
        holder.tv_title.setText(music.getTitle());
        holder.tv_artist.setText(music.getArtist());
    }

    @Override
    public int getItemCount() {
        return mMusicList.size();
    }
}
