package com.example.betweenus.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.betweenus.R;
import com.example.betweenus.model.ShopItem;

import java.io.File;
import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private ArrayList<ShopItem> items;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onBuyClick(ShopItem item);
    }

    public ShopAdapter(ArrayList<ShopItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, desc, price;
        ImageView image;
        Button btnBuy;

        public ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.tvName);
            desc = view.findViewById(R.id.tvDescription);
            price = view.findViewById(R.id.tvPrice);
            image = view.findViewById(R.id.imgPreview);
            btnBuy = view.findViewById(R.id.btnBuy);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shop, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ShopItem item = items.get(position);

        holder.name.setText(item.getItemName());
        holder.desc.setText(item.getItemDescription());
        holder.price.setText(String.valueOf(item.getItemPrice()));

        String imagePath = item.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            Context context = holder.itemView.getContext();
            File uploadedFile = new File(context.getFilesDir(), "backgrounds/" + imagePath);
            if (uploadedFile.exists()) {

                Glide.with(context)
                        .load(uploadedFile)
                        .placeholder(R.drawable.memorybtn_filled)
                        .into(holder.image);
            } else {

                String assetPath = "file:///android_asset/" + imagePath;
                Glide.with(context)
                        .load(Uri.parse(assetPath))
                        .placeholder(R.drawable.memorybtn_filled)
                        .into(holder.image);
            }
        } else {
            holder.image.setImageResource(R.drawable.study_coin);
        }

        holder.btnBuy.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBuyClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}