package com.example.betweenus.model;

public class MemoriesItem {
    private int memoryID;
    private int posterID;

    private int posterAvatar;
    private String posterName;
    private String memoryTitle;
    private String memoryBody;
    private String mediaType;
    private String mediaURL;
    private String timeStamp;
    private int likes;

    public MemoriesItem(int memoryID,
                        int posterID,
                        int posterAvatar,
                        String posterName,
                        String memoryTitle,
                        String memoryBody,
                        String mediaURL,
                        String timeStamp,
                        int likes) {

        this.memoryID = memoryID;
        this.posterID = posterID;
        this.posterAvatar = posterAvatar;
        this.posterName = posterName;
        this.memoryTitle = memoryTitle;
        this.memoryBody = memoryBody;
        this.mediaURL = mediaURL;
        this.timeStamp = timeStamp;
        this.likes = likes;
    }

    public int getMemoryID() {
        return memoryID;
    }

    public int getPosterAvatar() {return posterAvatar;}

    public void setMemoryID(int memoryID) {
        this.memoryID = memoryID;
    }

    public int getPosterID() {
        return posterID;
    }

    public void setPosterID(int posterID) {
        this.posterID = posterID;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }


    public String getMemoryTitle() {
        return memoryTitle;
    }

    public void setMemoryTitle(String memoryTitle) {
        this.memoryTitle = memoryTitle;
    }

    public String getMemoryBody() {
        return memoryBody;
    }

    public String getMediaURL() {
        return mediaURL;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

}
