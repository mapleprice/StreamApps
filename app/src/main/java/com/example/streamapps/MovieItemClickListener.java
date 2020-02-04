package com.example.streamapps;

import android.widget.ImageView;

import com.example.streamapps.models.Movie;

public interface MovieItemClickListener {

    void onMovieClick(Movie movie, ImageView movieImageView);

}
