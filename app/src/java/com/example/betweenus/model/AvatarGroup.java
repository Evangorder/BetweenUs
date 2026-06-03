package com.example.betweenus.model;

public class AvatarGroup {
    public Integer leftAvatar;
    public String leftName;

    public Integer centerAvatar;
    public String centerName;

    public Integer rightAvatar;
    public String rightName;

    public AvatarGroup(Integer leftAvatar, String leftName,
                       Integer centerAvatar, String centerName,
                       Integer rightAvatar, String rightName) {

        this.leftAvatar = leftAvatar;
        this.leftName = leftName;
        this.centerAvatar = centerAvatar;
        this.centerName = centerName;
        this.rightAvatar = rightAvatar;
        this.rightName = rightName;
    }
}