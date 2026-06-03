package com.example.betweenus.activities;

import android.app.AlertDialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;
import com.example.betweenus.adapter.CommentAdapter;
import com.example.betweenus.model.Comment;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MemoriesDetailActivity extends AppCompatActivity {
    ImageView memoryImage;
    TextView tvTitle, tvBody, tvLikes;
    RecyclerView commentsRecycler;
    ImageButton settingsButton, backBtn;

    private List<Comment> commentList = new ArrayList<>();
    private CommentAdapter commentAdapter;

    DatabaseHelper dbHelper;
    int memoryID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_memories_detail);

        memoryImage = findViewById(R.id.memory_image);
        tvTitle = findViewById(R.id.memory_title);
        tvBody = findViewById(R.id.memory_body);
        tvLikes = findViewById(R.id.memory_likes);
        settingsButton = findViewById(R.id.btn_settings);
        commentsRecycler = findViewById(R.id.recycler_comments);
        backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> finish());

        // Initialize RecyclerView with a LayoutManager
        commentsRecycler.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DatabaseHelper(this);
        memoryID = getIntent().getIntExtra("MEMORY_ID", -1);

        Cursor memoryCursor = dbHelper.getMemoryByID(memoryID);

        if (memoryCursor != null && memoryCursor.moveToFirst()) {
            String title = memoryCursor.getString(memoryCursor.getColumnIndexOrThrow(DatabaseHelper.COL_MEMORY_TITLE));
            String body = memoryCursor.getString(memoryCursor.getColumnIndexOrThrow(DatabaseHelper.COL_MEMORY_BODY));
            int likes = memoryCursor.getInt(memoryCursor.getColumnIndexOrThrow(DatabaseHelper.COL_LIKES));
            String imageUrl = memoryCursor.getString(memoryCursor.getColumnIndexOrThrow(DatabaseHelper.COL_MEDIA_URL));

            // --- IMAGE LOADING WITH UPLOAD + ASSETS FALLBACK ---
            if (imageUrl != null && !imageUrl.isEmpty()) {
                File uploadedFile = new File(getFilesDir(), "memories/" + imageUrl);
                if (uploadedFile.exists()) {
                    // Load from internal storage (uploaded by user)
                    Glide.with(this)
                            .load(uploadedFile)
                            .placeholder(R.drawable.memorybtn_filled)
                            .into(memoryImage);
                } else {
                    // Fallback to assets folder
                    String assetPath = "file:///android_asset/memories/" + imageUrl;
                    Glide.with(this)
                            .load(Uri.parse(assetPath))
                            .placeholder(R.drawable.memorybtn_filled)
                            .into(memoryImage);
                }
            } else {
                memoryImage.setImageResource(R.drawable.memorybtn_filled);
            }

            tvTitle.setText(title);
            tvBody.setText(body);
            tvLikes.setText("❤ " + likes);
            memoryCursor.close();
        } else {
            Toast.makeText(this, "Memory not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadComments();

        EditText etComment = findViewById(R.id.et_comment);
        ImageButton btnSend = findViewById(R.id.btn_send_comment);

        btnSend.setOnClickListener(v -> {
            String text = etComment.getText().toString().trim();
            if (!text.isEmpty()) {
                dbHelper.postComment(memoryID, 1, text);
                etComment.setText("");
                loadComments(); // refresh list
            }
        });

        settingsButton.setOnClickListener(v1 -> showSettingsMenu());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void showSettingsMenu() {
        PopupMenu popupMenu = new PopupMenu(this, settingsButton);
        popupMenu.getMenuInflater().inflate(R.menu.memory_settings_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.edit_post) {
                showEditDialog();
                return true;
            } else if (item.getItemId() == R.id.delete_post) {
                dbHelper.deleteMemory(memoryID);
                finish();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void showEditDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_memory, null);
        EditText editTitle = dialogView.findViewById(R.id.edit_title);
        EditText editBody = dialogView.findViewById(R.id.edit_body);

        // Pre-fill existing values
        editTitle.setText(tvTitle.getText().toString());
        editBody.setText(tvBody.getText().toString());

        new AlertDialog.Builder(this)
                .setTitle("Edit Memory")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String newTitle = editTitle.getText().toString();
                    String newBody = editBody.getText().toString();
                    dbHelper.updateMemory(memoryID, newTitle, newBody);
                    tvTitle.setText(newTitle);
                    tvBody.setText(newBody);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadComments() {
        commentList.clear();

        Cursor cursor = dbHelper.getPostComments(memoryID);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMMENT_ID));
                int posterId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMMENTOR_ID));
                int avatar = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_AVATAR));
                String text = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMMENT_BODY));
                String userName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAME));
                String timestampStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMMENT_TIME_STAMP));

                long timestampMillis = parseTimestamp(timestampStr);

                String timeAgo;
                if (timestampMillis > 0) {
                    timeAgo = DateUtils.getRelativeTimeSpanString(timestampMillis, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS).toString();
                } else {
                    timeAgo = "Unknown";
                }

                commentList.add(new Comment(id, posterId, userName, avatar, memoryID, text, timeAgo));
            }
            cursor.close();
        }

        commentAdapter = new CommentAdapter(commentList);
        commentsRecycler.setAdapter(commentAdapter);
    }

    public static long parseTimestamp(String timestampStr) {
        if (timestampStr == null) return 0;

        try {
            return Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(timestampStr);
                return date != null ? date.getTime() : 0;
            } catch (ParseException ex) {
                Log.e("TimestampParse", "Failed to parse date string: " + timestampStr);
                return 0;
            }
        }
    }
}