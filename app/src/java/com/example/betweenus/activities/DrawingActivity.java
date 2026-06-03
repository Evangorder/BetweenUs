package com.example.betweenus.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.betweenus.DrawingView;
import com.example.betweenus.R;

import java.io.File;
import java.io.FileOutputStream;

public class DrawingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_drawing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        DrawingView drawingView = findViewById(R.id.drawingView);
        Button btnSave = findViewById(R.id.btn_save);

        ImageButton backBtn = findViewById(R.id.btn_back);


        btnSave.setOnClickListener(v -> {
            Bitmap bitmap = drawingView.getBitmap();

            try {
                File file = new File(getFilesDir(), "dashboard_drawing.png");
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                finish(); // go back

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        if (backBtn == null) {
            Toast.makeText(this, "Error: Back button not found in XML!", Toast.LENGTH_LONG).show();
        } else {
            backBtn.setOnClickListener(v -> {
                // Use the modern dispatcher instead of just finish()
                getOnBackPressedDispatcher().onBackPressed();
            });
        }
//
    }
}