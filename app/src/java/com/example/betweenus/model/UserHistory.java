package com.example.betweenus.model;
public class UserHistory {

    private int recordID;
    private int userID;
    private String recordCategory;
    private int recordValue;
    private String timeRecorded;

    public UserHistory(int recordID,
                       int userID,
                       String recordCategory,
                       int recordValue,
                       String timeRecorded) {

        this.recordID = recordID;
        this.userID = userID;
        this.recordCategory = recordCategory;
        this.recordValue = recordValue;
        this.timeRecorded = timeRecorded;
    }

    // Optional constructor for insert (no recordID)
    public UserHistory(int userID,
                       String recordCategory,
                       int recordValue,
                       String timeRecorded) {

        this.userID = userID;
        this.recordCategory = recordCategory;
        this.recordValue = recordValue;
        this.timeRecorded = timeRecorded;
    }

    public int getUserID() {
        return userID;
    }

    public int getRecordID() {
        return recordID;
    }

    public int getRecordValue() {
        return recordValue;
    }

    public String getRecordCategory() {
        return recordCategory;
    }

    public String getTimeRecorded() {
        return timeRecorded;
    }

    // Getters and setters here
}