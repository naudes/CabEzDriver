package com.overthere.express.model;


public class Post {

    private int postId, OwnerId, postTimes,postTotal, postBalance;
    private String title, Text, position, color, imageName;

    public int getPostID() {
        return postId;
    }

    public void setPostID(int postId) {
        this.postId = postId;
    }

    public int getOwnerID() {
        return OwnerId;
    }

    public void setOwnerID(int OwnerId) {
        this.OwnerId = OwnerId;
    }

    public int getPostTimes() {
        return postTimes;
    }

    public void setPostTimes(int postTimes) {
        this.postTimes = postTimes;
    }

    public int getPostTotal() {
        return postTotal;
    }

    public void setPostTotal(int postTotal) {
        this.postTotal = postTotal;
    }

    public int getPostBalance() {
        return postBalance;
    }

    public void setPostBalance(int postBalance) {
        this.postBalance = postBalance;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() { return Text; }

    public void setText(String Text) { this.Text = Text;}

    public String getPosition() { return position; }

    public void setPosition(String position) { this.position = position;}

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color;}

    public String getImageName() { return imageName; }

    public void setImageName(String imageName) { this.imageName = imageName;}

}
