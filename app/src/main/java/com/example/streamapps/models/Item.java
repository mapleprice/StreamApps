package com.example.streamapps.models;

public abstract class Item {
    protected String title;
    protected String image;
    protected String streamURL;

    public abstract String getImage();

    public abstract String getTitle();

    public abstract String getStreamURL();
}
