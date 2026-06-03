package com.example.betweenus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.R;
import com.example.betweenus.model.AvatarGroup;

import java.util.List;

public class AvatarPagerAdapter extends RecyclerView.Adapter<AvatarPagerAdapter.ViewHolder> {

    private List<AvatarGroup> avatarGroups;

    public AvatarPagerAdapter(List<AvatarGroup> avatarGroups) {
        this.avatarGroups = avatarGroups;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView leftImg, centerImg, rightImg;
        TextView leftName, centerName, rightName;
        View leftContainer, centerContainer, rightContainer;

        public ViewHolder(View view) {
            super(view);

            leftContainer = view.findViewById(R.id.avatarLeft);
            centerContainer = view.findViewById(R.id.avatarCenter);
            rightContainer = view.findViewById(R.id.avatarRight);

            leftImg = leftContainer.findViewById(R.id.imgAvatar);
            centerImg = centerContainer.findViewById(R.id.imgAvatar);
            rightImg = rightContainer.findViewById(R.id.imgAvatar);

            leftName = leftContainer.findViewById(R.id.txt_status);
            centerName = centerContainer.findViewById(R.id.txt_status);
            rightName = rightContainer.findViewById(R.id.txt_status);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_avatar_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AvatarGroup group = avatarGroups.get(position);

        // CENTER
        holder.centerImg.setImageResource(group.centerAvatar);
        holder.centerName.setText(group.centerName);

        // LEFT
        if (group.leftAvatar != null) {
            holder.leftContainer.setVisibility(View.VISIBLE);
            holder.leftImg.setImageResource(group.leftAvatar);
            holder.leftName.setText(group.leftName);
        } else {
            holder.leftContainer.setVisibility(View.INVISIBLE);
        }

        // RIGHT
        if (group.rightAvatar != null) {
            holder.rightContainer.setVisibility(View.VISIBLE);
            holder.rightImg.setImageResource(group.rightAvatar);
            holder.rightName.setText(group.rightName);
        } else {
            holder.rightContainer.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return avatarGroups.size();
    }
}
