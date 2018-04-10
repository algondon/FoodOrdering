package com.example.foodordering.bean;

/**
 * Created by xh on 2017/5/29.
 */

public class Comment {


    private String id;
    private String name;
    private String imageUrl;//图片
    private int rating;//星级
    private  String commentContent;
    private  String CommentDateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getCommentDateTime() {
        return CommentDateTime;
    }

    public void setCommentDateTime(String commentDateTime) {
        CommentDateTime = commentDateTime;
    }
}
