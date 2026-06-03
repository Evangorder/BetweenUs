package com.example.betweenus.model;

public class Comment {

    private int commentId;
    private int posterId;
    private String commenterName;
    private int commenterAvatar;
    private int memoryId;
    private String commentBody;
    private String commentTimestamp;

    // Constructor
    public Comment(int commentId,
                   int posterId,
                   String commenterName,
                   int commenterAvatar,
                   int memoryId,
                   String commentBody,
                   String commentTimestamp) {

        this.commentId = commentId;
        this.posterId = posterId;
        this.commenterName = commenterName;
        this.commenterAvatar = commenterAvatar;
        this.memoryId = memoryId;
        this.commentBody = commentBody;
        this.commentTimestamp = commentTimestamp;
    }

    // Getters
    public int getCommentId() {
        return commentId;
    }

    public int getPosterId() {
        return posterId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public int getCommenterAvatar() {
        return commenterAvatar;
    }

    public int getMemoryId() {
        return memoryId;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public String getCommentTimestamp() {
        return commentTimestamp;
    }
}