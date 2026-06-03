package com.example.betweenus.model;

// PURPOSE:
// Represents a single chat message in a group conversation.
//
// WHAT THIS MODEL STORES:
// - Username of sender
// - Message text
// - Profile picture reference
// - Timestamp (optional)
//  - Group ID (optional)
public class Message {

    private int messageID;
    private int senderID;
    private int recieverId;
    private String text;

    private String timestamp;

    public Message(int messageID, int senderID, int receiverId, String text, String timestamp) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.recieverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;
    }

    public int getMessageID() {
        return messageID;
    }

    public int getSenderID() {
        return senderID;
    }

    public int getReceiverId() {
        return recieverId;
    }

    public String getMessageText() {
        return text;
    }

    public String getTimestamp() {return timestamp;}
}
