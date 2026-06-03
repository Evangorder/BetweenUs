package com.example.betweenus.model;


// PURPOSE:
// Represents a single friend in a user's friend list.
//
// WHAT THIS MODEL SHOULD STORE:
// - Friend user ID (for database reference)
// - Friend username
// - Friend profile picture

public class Friend {

    private String username;
    private int profileImageResId;

    private int id;
    private String name;
    private String email;

    // constructor + getters

    public Friend(int id, String name, String email){
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId(){
        return  id;
    }

    public  String getName() {
        return  name;
    }

    public  String getEmail() {
        return email;
    }

}
