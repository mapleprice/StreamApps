package com.example.streamapps.models;

public class Slide extends Item {

    private String streamURL;

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getStreamURL() {
        return streamURL;
    }

    public Slide(String title, String image, String streamURL){
        this.image = image;
        this.title = title;
        this.streamURL = streamURL;
    }
}
