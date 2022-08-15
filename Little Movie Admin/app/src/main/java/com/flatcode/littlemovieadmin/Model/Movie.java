package com.flatcode.littlemovieadmin.Model;

import com.flatcode.littlemovieadmin.Unit.DATA;

public class Movie {

    public String id, publisher, image, categoryId, name, description, movieLink, duration;
    int viewsCount, lovesCount, castCount, editorsChoice, year;
    long timestamp;

    public Movie(String id, String publisher, long timestamp, String categoryId, String name,
                 String description, String duration, String image, String movieLink, int viewsCount,
                 int lovesCount, int castCount, int editorsChoice, int year) {

        if (name.trim().equals(DATA.EMPTY)) {
            name = "No Name";
        }

        this.id = id;
        this.publisher = publisher;
        this.timestamp = timestamp;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.image = image;
        this.duration = duration;
        this.movieLink = movieLink;
        this.viewsCount = viewsCount;
        this.lovesCount = lovesCount;
        this.castCount = castCount;
        this.editorsChoice = editorsChoice;
        this.year = year;
    }

    public Movie() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMovieLink() {
        return movieLink;
    }

    public void setMovieLink(String movieLink) {
        this.movieLink = movieLink;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public int getLovesCount() {
        return lovesCount;
    }

    public void setLovesCount(int lovesCount) {
        this.lovesCount = lovesCount;
    }

    public int getCastCount() {
        return castCount;
    }

    public void setCastCount(int castCount) {
        this.castCount = castCount;
    }

    public int getEditorsChoice() {
        return editorsChoice;
    }

    public void setEditorsChoice(int editorsChoice) {
        this.editorsChoice = editorsChoice;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}