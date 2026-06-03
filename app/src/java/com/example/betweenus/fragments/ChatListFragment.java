package com.example.betweenus.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.adapter.ChatListAdapter;
import com.example.betweenus.model.User;

import java.util.List;

public class ChatListFragment extends Fragment {

    private RecyclerView chatListRecyclerView;
    private ChatListAdapter chatListAdapter;
    private DatabaseHelper dbHelper;
//    private int currentUserId = 1; // For demo, should be retrieved from session

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dbHelper = new DatabaseHelper(requireContext());
        chatListRecyclerView = view.findViewById(R.id.chatListRecyclerView);
        chatListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadChats();
    }

    private void loadChats() {
        // For simplicity, we'll list all friends as potential chats.
        // In a real app, you'd show users with existing message history.
        List<User> users = dbHelper.getUserAndFriends(dbHelper.currentUserId);
        // Remove current user from the list
        users.removeIf(user -> user.getUserId() == dbHelper.currentUserId);

        chatListAdapter = new ChatListAdapter(users, user -> {
            Bundle bundle = new Bundle();
            bundle.putInt("receiverId", user.getUserId());
            bundle.putString("receiverName", user.getName());
            Navigation.findNavController(requireView()).navigate(R.id.action_navigation_chat_list_to_messagingFragment, bundle);
        });
        chatListRecyclerView.setAdapter(chatListAdapter);
    }


    public static int getAvatarImage(int avatarId) {
        switch (avatarId) {
            case 1:
                return R.drawable.bunny_profile;
            case 2:
                return R.drawable.giraffe_profile;
            case 3:
                return R.drawable.kitty_profile;
            case 4:
                return R.drawable.puppy_profile;
            default:
                return R.drawable.profilebtn_filled;
        }
    }

    public static int getMessageColor(int avatarId, boolean isSender) {
        switch (avatarId) {
            case 1: // bunny → pink
                return isSender ? 0xFFF8A5C2 : 0xFFFADADD;

            case 2: // giraffe → yellow
                return isSender ? 0xFFFDCB6E : 0xFFFFF3D6;

            case 3: // kitty → blue
                return isSender ? 0xFF74B9FF : 0xFFD6E9FF;

            case 4: // puppy → green
                return isSender ? 0xFF55EFC4 : 0xFFDFFFEF;

            default:
                return 0xFFE0E0E0;
        }
    }
}