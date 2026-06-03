package com.example.betweenus.fragments;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.betweenus.activities.CreateMemoryActivity;
import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.activities.MemoriesDetailActivity;
import com.example.betweenus.adapter.MemoriesAdapter;
import com.example.betweenus.model.MemoriesItem;

import java.util.ArrayList;
import java.util.List;

public class MemoriesFragment extends Fragment {
    private RecyclerView recyclerView;
    private MemoriesAdapter memoriesAdapter;
    private List<MemoriesItem> memoriesItems;
    private DatabaseHelper dbHelper;
    private TextView tvEmptyState;

    private ImageButton btnAddMemory;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_memories, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_posts);
        tvEmptyState = view.findViewById(R.id.tv_empty_memories);
        
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        btnAddMemory = view.findViewById(R.id.btn_add_memory);

        btnAddMemory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateMemoryActivity.class);
            startActivity(intent);
        });

        dbHelper = new DatabaseHelper(requireContext());
        
        // Ensure currentUserId is set (handle fragment args if passed from MainActivity)
        if (getArguments() != null) {
            dbHelper.currentUserId = getArguments().getInt("USER_ID", dbHelper.currentUserId);
        }
        
        memoriesItems = new ArrayList<>();
        memoriesAdapter = new MemoriesAdapter(requireContext(), memoriesItems);
        recyclerView.setAdapter(memoriesAdapter);
        
        loadMemories();
        
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMemories();
    }

    private void loadMemories() {
        memoriesItems.clear();
        
        // BUG FIX: Only load memories from friends (and yourself)
        Cursor cursor = dbHelper.getFriendsMemories(dbHelper.currentUserId);
        
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int memoryID = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MEMORY_ID));
                    int posterID = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_POSTER_ID));
                    int posterAvatar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AVATAR));
                    String posterName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                    String memoryTitle = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MEMORY_TITLE));
                    String memoryBody = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MEMORY_BODY));
                    String mediaURL = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MEDIA_URL));
                    int likes = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_LIKES));
                    String timestampStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TIME_STAMP));

                    long timestampMillis = MemoriesDetailActivity.parseTimestamp(timestampStr);

                    String timeAgo;
                    if (timestampMillis > 0) {
                        timeAgo = DateUtils.getRelativeTimeSpanString(timestampMillis, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
                    } else {
                        timeAgo = "Unknown";
                    }
                    memoriesItems.add(new MemoriesItem(memoryID, posterID, posterAvatar, posterName, memoryTitle, memoryBody, mediaURL, timeAgo, likes));

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        
        if (memoriesItems.isEmpty()) {
            if (tvEmptyState != null) tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            if (tvEmptyState != null) tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        
        memoriesAdapter.notifyDataSetChanged();
    }
}
