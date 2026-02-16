package org.example.unishpere;

public class Post {
    private int userId;
    private String caption;
    private String photoUrl;

    public Post(int userId, String caption, String photoUrl) {
        this.userId = userId;
        this.caption = caption;
        this.photoUrl = photoUrl;
    }

    public int getUserId() {
        return userId;
    }

    public String getCaption() {
        return caption;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
}
