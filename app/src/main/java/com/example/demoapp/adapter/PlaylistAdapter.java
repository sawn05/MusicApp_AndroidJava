package com.example.demoapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoapp.R;
import com.example.demoapp.model.Playlist;
import com.example.demoapp.storage_data.DBHelper;
import com.example.demoapp.ui.PlaylistSongs;

import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> {

    private List<Playlist> playlistList;
    private static int layoutId;

    public PlaylistAdapter(List<Playlist> playlistList, int layoutId) {
        this.playlistList = playlistList;
        this.layoutId = layoutId;
    }

    @NonNull
    @Override
    public PlaylistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaylistViewHolder holder, int position) {
        Playlist playlist = playlistList.get(position);
        holder.tvName.setText(playlist.getName());
        holder.img.setImageResource(playlist.getImageResId());

        // Nếu layout là item_playlist_vertical, hiển thị số bài hát + xử lý nút 3 chấm
        if (layoutId == R.layout.item_playlist_vertical) {
            Context context = holder.itemView.getContext();
            DBHelper dbHelper = new DBHelper(context);
            holder.tvNumSong.setText(dbHelper.getNumSongByPlaylist(playlist.getName()) + " bài hát");
            dbHelper.close();

            // ===== NÚT 3 CHẤM =====
            if (holder.btnMore != null) {
                holder.btnMore.setOnClickListener(v -> {
                    PopupMenu popup = new PopupMenu(v.getContext(), v);
                    popup.getMenuInflater().inflate(R.menu.menu_album, popup.getMenu());

                    popup.setOnMenuItemClickListener(item -> {
                        int id = item.getItemId();
                        if (id == R.id.action_rename) {
                            showRenameDialog(v.getContext(), playlist, holder.getAdapterPosition());
                            return true;
                        }
                        return false;
                    });

                    popup.show();
                });
            }
        }

        // Xử lý sự kiện khi người dùng nhấn vào item -> mở màn hình AlbumSongs
        holder.itemView.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent playIntent = new Intent(context, PlaylistSongs.class);
            playIntent.putExtra("playlistSong", playlist.getName());
            playIntent.putExtra("imgPlaylist", playlist.getImageResId());
            context.startActivity(playIntent);
        });
    }

    @Override
    public int getItemCount() {
        return playlistList.size();
    }

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName, tvNumSong;
        ImageButton btnMore; // nút 3 chấm

        public PlaylistViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgPlaylist);
            tvName = itemView.findViewById(R.id.tvNamePlaylist);
            if (layoutId == R.layout.item_playlist_vertical) {
                tvNumSong = itemView.findViewById(R.id.tvNumSong);
                btnMore = itemView.findViewById(R.id.btnMore);
            }
        }
    }

    // ===== Dialog đổi tên album =====
    private void showRenameDialog(Context context, Playlist playlist, int position) {
        final EditText input = new EditText(context);
        input.setText(playlist.getName());

        new AlertDialog.Builder(context)
                .setTitle("Đổi tên Playlist")
                .setView(input)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (newName.isEmpty()) {
                        Toast.makeText(context, "Tên không được để trống", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DBHelper dbHelper = new DBHelper(context);
                    int rows = dbHelper.renamePlaylist(playlist.getName(), newName);

                    if (rows > 0) {
                        playlist.setName(newName);      // cập nhật model
                        notifyItemChanged(position);    // cập nhật UI
                        Toast.makeText(context, "Đã đổi tên album", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Đổi tên thất bại", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }


}
