package com.example.betweenus.model;

//PURPOSE:
// Represents a user account
//
// WHAT THIS MODEL STORES:
// - Username
// - Email
// - Bio
// - Profile picture Avatar
// - Premium status
// - List of friends
// - Mood history (optional future)
public class User {

    private int userId;
    private String name;
    private String mood;
    private int avatar;

    public User(int userId, String name, String mood, int avatar) {
        this.userId = userId;
        this.name = name;
        this.mood = mood;
        this.avatar = avatar;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getMood() {
        return mood;
    }

    public int getAvatar() {
        return avatar;
    }
}



