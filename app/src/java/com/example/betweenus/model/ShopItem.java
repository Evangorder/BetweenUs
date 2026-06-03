package com.example.betweenus.model;

import android.content.ClipData;

public class ShopItem {
    private int ItemID;
    private String ItemName;
    private String ItemDescription;
    private int ItemPrice;

    private String ImagePath;

    public ShopItem(int ItemID, String ItemName, String ItemDescription, int ItemPrice, String ImagePath) {
        this.ItemID = ItemID;
        this.ItemName = ItemName;
        this.ItemDescription = ItemDescription;
        this.ItemPrice = ItemPrice;
        this.ImagePath = ImagePath;
    }


    public int getItemID() {
        return ItemID;
    }

    public String getItemName() {
        return ItemName;
    }

    public String getItemDescription() {
        return ItemDescription;
    }

    public int getItemPrice() {
        return ItemPrice;
    }

    public String getImagePath() {
        return ImagePath;
    }
}