package com.example.betweenus.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.activities.MemoriesDetailActivity;
import com.example.betweenus.fragments.ChatListFragment;
import com.example.betweenus.model.MemoriesItem;

import java.io.File;
import java.util.List;

public class MemoriesAdapter extends RecyclerView.Adapter<MemoriesAdapter.ViewHolder> {

    private Context context;
    private List<MemoriesItem> memoriesItems;
    private DatabaseHelper dbHelper;

    public MemoriesAdapter(Context context, List<MemoriesItem> memoriesItems) {
        this.context = context;
        this.memoriesItems = memoriesItems;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_memory_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemoriesItem memoriesItem = memoriesItems.get(position);

        holder.profileImage.setImageResource(ChatListFragment.getAvatarImage(memoriesItem.getPosterAvatar()));
        holder.tvTitle.setText(memoriesItem.getMemoryTitle());
        holder.tvUsername.setText(memoriesItem.getPosterName());
        holder.tvBody.setText(memoriesItem.getMemoryBody());
        holder.tvLikes.setText("❤ " + memoriesItem.getLikes());
        holder.tvDate.setText(memoriesItem.getTimeStamp());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MemoriesDetailActivity.class);
            intent.putExtra("MEMORY_ID", memoriesItem.getMemoryID());
            context.startActivity(intent);
        });

        // --- IMAGE LOADING WITH UPLOAD + ASSETS FALLBACK ---
        String imagePath = memoriesItem.getMediaURL();
        if (imagePath != null && !imagePath.isEmpty()) {
            File uploadedFile = new File(context.getFilesDir(), "memories/" + imagePath);
            if (uploadedFile.exists()) {
                // Load from internal storage
                Glide.with(holder.itemView.getContext())
                        .load(uploadedFile)
                        .placeholder(R.drawable.memorybtn_filled)
                        .into(holder.ivImage);
            } else {
                // Fallback to assets
                String assetPath = "file:///android_asset/memories/" + imagePath;
                Glide.with(holder.itemView.getContext())
                        .load(Uri.parse(assetPath))
                        .placeholder(R.drawable.memorybtn_filled)
                        .into(holder.ivImage);
            }
        } else {
            holder.ivImage.setImageResource(R.drawable.memorybtn_filled);
        }

        holder.btnLike.setOnClickListener(v -> {
            int newLikes = memoriesItem.getLikes() + 1;
            memoriesItem.setLikes(newLikes);
            holder.tvLikes.setText("❤ " + newLikes);

            dbHelper.updateMemoryLikes(memoriesItem.getMemoryID(), newLikes);
        });

        holder.btnComment.setOnClickListener(v -> {
            Intent intent = new Intent(context, MemoriesDetailActivity.class);
            intent.putExtra("MEMORY_ID", memoriesItem.getMemoryID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return memoriesItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvBody, tvLikes, tvDate, tvTitle;
        ImageView ivImage, profileImage;
        ImageButton btnLike, btnComment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvUsername = itemView.findViewById(R.id.tv_username);
            profileImage = itemView.findViewById(R.id.iv_profile_image);
            tvBody = itemView.findViewById(R.id.tv_body);
            tvLikes = itemView.findViewById(R.id.tv_likes);
            tvDate = itemView.findViewById(R.id.mem_date);
            ivImage = itemView.findViewById(R.id.iv_image);
            btnLike = itemView.findViewById(R.id.btn_like);
            btnComment = itemView.findViewById(R.id.btn_comment);
        }
    }
}