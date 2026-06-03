package com.example.betweenus.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CreateMemoryActivity extends AppCompatActivity {

    EditText etTitle, etBody;
    Button btnPost, btnSelectImage;
    ImageView imagePreview;

    DatabaseHelper dbHelper;

    Uri selectedImageUri = null;

    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_memory);

        etTitle = findViewById(R.id.et_title);
        etBody = findViewById(R.id.et_body);
        btnPost = findViewById(R.id.btn_post);

        btnSelectImage = findViewById(R.id.btn_select_image);
        imagePreview = findViewById(R.id.image_preview);

        ImageButton backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(v -> finish());


        dbHelper = new DatabaseHelper(this);

        // Select image from gallery
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Create post
        btnPost.setOnClickListener(v1 -> {

            String title = etTitle.getText().toString();
            String body = etBody.getText().toString();

            String imageName;

            if (selectedImageUri != null) {
                imageName = saveImageToInternalStorage(selectedImageUri, title);
            } else {
                imageName = "default_memory.jpg";
            }

            String timestamp = new java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    java.util.Locale.getDefault()
            ).format(new java.util.Date());

            dbHelper.createMemory(
                    1,
                    title,
                    body,
                    "image",
                    imageName, // ONLY filename stored
                    timestamp
            );

            Toast.makeText(this, "Post created!", Toast.LENGTH_SHORT).show();
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
        }
    }

    // Save image and return ONLY filename
    private String saveImageToInternalStorage(Uri uri, String title) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            // Clean filename + ensure uniqueness
            String fileName = title.replaceAll("[^a-zA-Z0-9]", "_")
                    + "_" + System.currentTimeMillis() + ".webp";

            File directory = new File(getFilesDir(), "memories");
            if (!directory.exists()) directory.mkdirs();

            File file = new File(directory, fileName);

            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return fileName; // ✅ ONLY NAME returned

        } catch (Exception e) {
            e.printStackTrace();
            return "default_memory.jpg";
        }
    }
}