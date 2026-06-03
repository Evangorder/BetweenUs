package com.example.betweenus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.R;
import com.example.betweenus.fragments.ChatListFragment;
import com.example.betweenus.model.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvComment.setText(comments.get(position).getCommentBody());
        holder.tvUsername.setText(comments.get(position).getCommenterName());
        holder.tvTime.setText(comments.get(position).getCommentTimestamp());
        holder.ivAvatar.setImageResource(ChatListFragment.getAvatarImage(comments.get(position).getCommenterAvatar()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvComment, tvUsername, tvTime;
        ImageView ivAvatar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_comment_avatar);
            tvUsername = itemView.findViewById(R.id.tv_comment_user);
            tvTime = itemView.findViewById(R.id.tv_comment_time);
            tvComment = itemView.findViewById(R.id.tv_comment);
        }
    }
}
