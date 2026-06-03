package com.example.betweenus.activities;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.MainActivity;
import com.example.betweenus.R;
import com.example.betweenus.adapter.FriendAdapter;
import com.example.betweenus.adapter.TaskAdapter;
import com.example.betweenus.model.Friend;
import com.example.betweenus.model.Task;

import java.util.ArrayList;
import java.util.List;

public class ProfileDetailActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FriendAdapter adapter;
    private List<Friend> friendList;
    private DatabaseHelper dbHelper;
//    private int currentUserID = 1; // replace later with logged-in user


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_detail);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        recyclerView = findViewById(R.id.friendsRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        friendList = new ArrayList<>();
        adapter = new FriendAdapter(this, friendList);
        recyclerView.setAdapter(adapter);

        EditText searchInput = findViewById(R.id.searchFriends);
        Button addFriendBtn = findViewById(R.id.addFriendBtn);
        ImageButton backBtn = findViewById(R.id.btn_back);





        backBtn.setOnClickListener(v -> finish());



        addFriendBtn.setOnClickListener(v -> {
            String input = searchInput.getText().toString().trim();

            if (input.isEmpty()) {
                Toast.makeText(this, "Enter username or ID", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor userCursor;

            if (input.matches("\\d+")) {
                userCursor = dbHelper.getUserByID(Integer.parseInt(input));
            } else {
                userCursor = dbHelper.getUserByName(input);
            }

            if (userCursor == null || !userCursor.moveToFirst()) {
                Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
                return;
            }

            int friendID = userCursor.getInt(
                    userCursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)
            );
            userCursor.close();

            if (friendID == dbHelper.currentUserId) {
                Toast.makeText(this, "You can't add yourself", Toast.LENGTH_SHORT).show();
                return;
            }

            long result = dbHelper.newFriend(dbHelper.currentUserId, friendID);

            if (result != -1) {
                Toast.makeText(this, "Friend added!", Toast.LENGTH_SHORT).show();
                searchInput.setText("");
                loadFriends();
            } else {
                Toast.makeText(this, "Already friends", Toast.LENGTH_SHORT).show();
            }
        });

        loadFriends();
    }

    private void loadFriends() {
        Cursor cursor = dbHelper.getUserFriends(dbHelper.currentUserId);
        friendList.clear();

        if (cursor.moveToFirst()) {
            do {
                int f1 = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_FIRST_FRIEND_ID));
                int f2 = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SECOND_FRIEND_ID));

                int friendID = (f1 == dbHelper.currentUserId) ? f2 : f1;

                Cursor friendCursor = dbHelper.getUserByID(friendID);

                if (friendCursor.moveToFirst()) {
                    String name = friendCursor.getString(
                            friendCursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME)
                    );
                    String email = friendCursor.getString(
                            friendCursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)
                    );

                    friendList.add(new Friend(friendID, name, email));
                }

                friendCursor.close();

            } while (cursor.moveToNext());
        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }
}