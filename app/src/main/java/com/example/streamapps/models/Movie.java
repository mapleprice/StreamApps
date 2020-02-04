package com.example.streamapps.models;

public class Movie extends Item {

    private String title;
    private String description;
    private long runtime;
    private String studio;
    private long year;
    private String image;
    private String thumbnailURL;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public long getRuntime() {
        return runtime;
    }

    public String getStudio() {
        return studio;
    }

    public String getStreamURL(){return  streamURL;}

    public String getImage() {
        return image;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public long getYear() {
        return year;
    }

    public Movie(String title, String description, String thumbnailURL, String image, String studio, String streamURL , long runtime) {
        this.title = title;
        this.description = description;
        this.thumbnailURL = thumbnailURL;
        this.studio = studio;
        this.streamURL = streamURL;
        this.image = image;
        this.runtime = runtime;
    }
}
