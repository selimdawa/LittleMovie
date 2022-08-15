package com.flatcode.littlemovie.Model;

import com.flatcode.littlemovie.Unit.DATA;

public class Cast {

    public String id, publisher, name, image, aboutMy;
    int interestedCount, moviesCount;
    long timestamp;

    public Cast(String id, String publisher, String name, String image, String aboutMy,
                long timestamp, int interestedCount, int moviesCount) {

        if (name.trim().equals(DATA.EMPTY)) {
            name = "No Name";
        }

        this.id = id;
        this.publisher = publisher;
        this.name = name;
        this.image = image;
        this.aboutMy = aboutMy;
        this.timestamp = timestamp;
        this.interestedCount = interestedCount;
        this.moviesCount = moviesCount;
    }

    public Cast() {

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

    public String getAboutMy() {
        return aboutMy;
    }

    public void setAboutMy(String aboutMy) {
        this.aboutMy = aboutMy;
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
