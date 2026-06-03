package com.example.betweenus.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.adapter.MessageAdapter;
import com.example.betweenus.model.Message;
import com.example.betweenus.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagingFragment extends Fragment {

    private RecyclerView messagesRecyclerView;
    private MessageAdapter messageAdapter;
    private EditText messageInput;
    private ImageButton sendMessageButton;
    private ImageButton backButton;
    private TextView groupName;
    private ImageView groupImage;
    private DatabaseHelper dbHelper;
    
    // For demo purposes, we'll use a fixed current user ID and receiver ID
    // In a real app, these would be passed in or retrieved from a session
//    private int currentUserId = 1;
    private int receiverId = 2; 

    public MessagingFragment() {
        // Required empty public constructor
    }

    public static MessagingFragment newInstance() {
        return new MessagingFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DatabaseHelper(getContext());
        
        // Get receiverId from arguments if available
        if (getArguments() != null) {
            receiverId = getArguments().getInt("receiverId", 2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_messaging, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messagesRecyclerView = view.findViewById(R.id.messagesRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendMessageButton = view.findViewById(R.id.sendMessageButton);
        backButton = view.findViewById(R.id.backButton);
        groupName = view.findViewById(R.id.groupName);
        groupImage = view.findViewById(R.id.groupImage);

        setupHeader();
        setupRecyclerView();
        loadMessages();

        sendMessageButton.setOnClickListener(v -> {
            String text = messageInput.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
            }
        });

        backButton.setOnClickListener(v -> {
            Navigation.findNavController(view).navigateUp();
        });
    }

    private void setupHeader() {
        Cursor cursor = dbHelper.getUserByID(receiverId);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
            int avatarId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AVATAR)); // 👈 IMPORTANT

            groupName.setText(name);

            // 🐰 set avatar using utility
            groupImage.setImageResource(ChatListFragment.getAvatarImage(avatarId));

            cursor.close();
        }
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        messagesRecyclerView.setLayoutManager(layoutManager);
        
        // Initialize with empty data
        messageAdapter = new MessageAdapter(new java.util.ArrayList<>(), new HashMap<>(), dbHelper.currentUserId);
        messagesRecyclerView.setAdapter(messageAdapter);
    }

    private void loadMessages() {
        List<Message> messages = dbHelper.getMessagesBetween(dbHelper.currentUserId, receiverId);
        List<User> allUsers = dbHelper.getUsersList();

        Map<Integer, User> userMap = new HashMap<>();
        for (User user : allUsers) {
            userMap.put(user.getUserId(), user);
        }

        messageAdapter.updateMessages(messages, userMap);
        if (messageAdapter.getItemCount() > 0) {
            messagesRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }

    private void sendMessage(String text) {
        long result = dbHelper.sendMessage(dbHelper.currentUserId, receiverId, text);
        if (result != -1) {
            messageInput.setText("");
            loadMessages(); // Refresh list
        } else {
            Toast.makeText(getContext(), "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }
}
