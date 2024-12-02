package com.ssafy.cafe.model.dto;

public class Comment {
    private Integer commentId;
    private Integer userNo;
    private Integer productId;
    private Double rating;
    private String comment;

    public Comment(Integer commentId, Integer userNo, Integer productId, Double rating, String comment) {
        super();
        this.commentId = commentId;
        this.userNo = userNo;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }

    public Comment(Integer userNo, Integer productId, Double rating, String comment) {
        this.userNo = userNo;
        this.productId = productId;
        this.rating = rating;
        this.comment = comment;
    }

    public Comment() {

    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public Integer getUserNo() {
        return userNo;
    }

    public void setUserNo(Integer userNo) {
        this.userNo = userNo;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Comment{" + "id=" + commentId + ", userNo=" + userNo + ", productId=" + productId + ", rating=" + rating + ", comment='" + comment + '\'' + '}';
    }
}