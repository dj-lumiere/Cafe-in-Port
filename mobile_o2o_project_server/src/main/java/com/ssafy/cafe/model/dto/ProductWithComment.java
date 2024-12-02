package com.ssafy.cafe.model.dto;

import com.ssafy.cafe.enumclasses.ProductType;

import java.util.List;

public class ProductWithComment extends Product {
    private Integer id;
    private String name;
    private ProductType type;
    private String img;

    private Integer commentCount;
    private Integer totalSells;
    private Double averageStars;

    private List<CommentInfo> comments;


    public ProductWithComment(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.type = product.getType();
        this.img = product.getImg();
    }

    public ProductWithComment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<CommentInfo> getComments() {
        return comments;
    }

    public void setComments(List<CommentInfo> comments) {
        this.comments = comments;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getTotalSells() {
        return totalSells;
    }

    public void setTotalSells(Integer totalSells) {
        this.totalSells = totalSells;
    }

    public Double getAverageStars() {
        return averageStars;
    }

    public void setAverageStars(Double averageStars) {
        this.averageStars = averageStars;
    }

    @Override
    public String toString() {
        return "ProductWithComment{" + "id=" + id + ", name='" + name + '\'' + ", type='" + type + '\'' + ", img='" + img + '\'' + ", commentCount=" + commentCount + ", totalSells=" + totalSells + ", averageStars=" + averageStars + ", comments=" + comments + "} " + super.toString();
    }
}
