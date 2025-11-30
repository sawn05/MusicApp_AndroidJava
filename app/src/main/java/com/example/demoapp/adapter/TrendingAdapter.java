package com.example.demoapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.MyService;
import com.example.demoapp.R;
import com.example.demoapp.model.Trending;
import com.example.demoapp.ui.PlayMusic;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.TrendingViewHolder> {
    private List<Trending> trendingList;

    public TrendingAdapter(List<Trending> trendingList) {
        this.trendingList = trendingList;
    }

    @NonNull
    @Override
    public TrendingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trending, parent, false);
        return new TrendingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendingViewHolder holder, int position) {
        Trending trending = trendingList.get(position);
        holder.tvNameSong.setText(trending.getNameSong());
        holder.tvNameAuthor.setText(trending.getNameAuthor());
        holder.imgSong.setImageResource(trending.getImgSong());

        holder.itemView.setOnClickListener(v -> {
            /*Context context = v.getContext();
            Intent intent = new Intent(context, MyService.class);
            intent.putExtra("song_res_id", trending.getSongResId());
            context.startService(intent);

            Intent playIntent = new Intent(context, PlayMusic.class);
            playIntent.putExtra("songName", trending.getNameSong());
            playIntent.putExtra("artist", trending.getNameAuthor());
            playIntent.putExtra("imageSong", trending.getImgSong());
            context.startActivity(playIntent);*/

            Context context = v.getContext();

            // Gửi tới Service để phát nhạc
            Intent intent = new Intent(context, MyService.class);
            intent.putExtra("song_res_id", trending.getSongResId());
            intent.putExtra("songName", trending.getNameSong());
            intent.putExtra("artist", trending.getNameAuthor());
            intent.putExtra("imageSong", trending.getImgSong());
            intent.setAction("ACTION_PLAY_NEW_SONG");
            context.startService(intent);

            // Mở màn hình PlayMusic — PHẢI truyền cùng dữ liệu
            Intent playIntent = new Intent(context, PlayMusic.class);
            playIntent.putExtra("song_res_id", trending.getSongResId());
            playIntent.putExtra("songName", trending.getNameSong());
            playIntent.putExtra("artist", trending.getNameAuthor());
            playIntent.putExtra("imageSong", trending.getImgSong());
            context.startActivity(playIntent);
        });

    }

    @Override
    public int getItemCount() {
        return trendingList.size();
    }


    public static class TrendingViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSong;
        TextView tvNameSong;
        TextView tvNameAuthor;

        public TrendingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSong = itemView.findViewById(R.id.imgPlaylist);
            tvNameSong = itemView.findViewById(R.id.txtNameSong);
            tvNameAuthor = itemView.findViewById(R.id.txtNameAuthor);
        }
    }
}
