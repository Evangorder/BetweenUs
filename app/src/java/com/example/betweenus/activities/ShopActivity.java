package com.example.betweenus.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.betweenus.R;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.*;

import com.example.betweenus.DatabaseHelper;
import com.example.betweenus.adapter.ShopAdapter;
import com.example.betweenus.model.ShopItem;

import java.util.ArrayList;

public class ShopActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseHelper db;
    private TextView points;

    private int currentUserID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        recyclerView = findViewById(R.id.recyclerShop);
        db = new DatabaseHelper(this);

        points = findViewById(R.id.tvCoins);
        points.setText(String.valueOf(db.getUserPoints(currentUserID)));

        ImageButton backBtn = findViewById(R.id.btn_back);

        backBtn.setOnClickListener(v -> finish());


        loadShop();
    }

    private void loadShop() {

        ArrayList<ShopItem> items = db.getAllShopItems();

        ShopAdapter adapter = new ShopAdapter(items, item -> {

            boolean success = db.buyItem(currentUserID, item);

            if (success) {
                Toast.makeText(this, "Purchased!", Toast.LENGTH_SHORT).show();
                points.setText(String.valueOf(db.getUserPoints(currentUserID)));
            } else {
                Toast.makeText(this, "Not enough points or already owned", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.setAdapter(adapter);
    }
}