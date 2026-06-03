package com.example.betweenus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.model.User;

import java.util.ArrayList;
import java.util.List;

public class AvatarAdapter extends RecyclerView.Adapter<AvatarAdapter.ViewHolder> {

    private List<User> users;
    DatabaseHelper dbHelper = new DatabaseHelper(null);
//    private int currentUserId;

    public AvatarAdapter(List<User> users, int currentUserId) {
        dbHelper.currentUserId = currentUserId;
        this.users = sortUsers(users);
    }

    private List<User> sortUsers(List<User> list) {
        List<User> sorted = new ArrayList<>();

        User current = null;

        for (User u : list) {
            if (u.getUserId() == dbHelper.currentUserId)
                current = u;
        }

        if (current != null) {
            sorted.add(current);
            list.remove(current);
        }

        sorted.addAll(list);
        return sorted;
    }

    public void updateUsers(List<User> newUsers) {
        users = sortUsers(newUsers);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_avatar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        User user = users.get(position);
//
//        int avatarResId = getAvatarDrawable(user);
//        holder.avatar.setImageResource(avatarResId);

        User user = users.get(position);
        int avatarResId;

        switch (user.getAvatar()) {
            case 1:
                avatarResId = R.drawable.bunny_happy;
                break;
            case 2:
                avatarResId = R.drawable.giraffe_happy;
                break;
            case 3:
                avatarResId = R.drawable.kitty_happy;
                break;
            case 4:
                avatarResId = R.drawable.puppy_happy;
                break;
            default:
                avatarResId = R.drawable.bunny_happy;
        }
        holder.avatar.setImageResource(avatarResId);
    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
