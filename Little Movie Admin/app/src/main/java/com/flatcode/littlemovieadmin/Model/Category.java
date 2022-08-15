package com.flatcode.littlemovieadmin.Model;

public class Category {

    String id, name, image, publisher;
    int interestedCount, moviesCount;
    long timestamp;

    public Category() {

    }

    public Category(String id, String name, String image, String publisher, long timestamp,
                    int interestedCount, int moviesCount) {
        this.id = id;
        this.name = name;
        this.publisher = publisher;
        this.image = image;
        this.timestamp = timestamp;
        this.interestedCount = interestedCount;
        this.moviesCount = moviesCount;
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getInterestedCount() {
        return interestedCount;
    }

    public void setInterestedCount(int interestedCount) {
        this.interestedCount = interestedCount;
    }

    public int getMoviesCount() {
        return moviesCount;
    }

    public void setMoviesCount(int moviesCount) {
        this.moviesCount = moviesCount;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
