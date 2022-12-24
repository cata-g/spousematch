package com.example.spousematch;

public class Notification {

    String imageLink;
    String userId;
    String notificationType;
    String userName;

    public Notification(String imageLink, String userId, String notificationType, String userName) {
        this.imageLink = imageLink;
        this.userId = userId;
        this.notificationType = notificationType;
        this.userName = userName;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
