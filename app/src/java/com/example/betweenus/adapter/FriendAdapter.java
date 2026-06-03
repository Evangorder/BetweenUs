package com.example.betweenus.adapter;

// PURPOSE:
// Displays list of friends in RecyclerView.
//
// WHAT THIS FILE SHOULD DO:
// - Inflate item_friend.xml
// - Bind friend username + profile image

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.model.Friend;

import java.util.List;


public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {

    private Context context;
    private List<Friend> friendList;
    private DatabaseHelper dbHelper;

    public FriendAdapter(Context context, List<Friend> friendList) {
        this.context = context;
        this.friendList = friendList;
        this.dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Friend friend = friendList.get(position);
        holder.username.setText(friend.getName());

    }

    @Override
    public int getItemCount() {

        return friendList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage;
        TextView username;

        public ViewHolder(View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.friendProfileImage);
            username = itemView.findViewById(R.id.friendUsername);
        }
    }
}

