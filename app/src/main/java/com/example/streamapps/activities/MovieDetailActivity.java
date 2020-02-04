package com.example.streamapps.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.streamapps.MoviePlayButtonClickListener;
import com.example.streamapps.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView movieImg, movieCoverImg;
    private FloatingActionButton startMovieButton;
    private TextView tv_title,tv_description;
    private String streamURL;
    private TextView tv_studios;
    private TextView tv_runtime;
    private boolean isSubscribed;

    private MoviePlayButtonClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        iniViews();
    }

    void iniViews(){
        String movieTitle = getIntent().getExtras().getString("title");
        String imageId = getIntent().getExtras().getString("imageURL");
        String coverId = getIntent().getExtras().getString("cover");
        String description = getIntent().getExtras().getString("description");
        String studio = getIntent().getExtras().getString("studio");
        long runtime = getIntent().getExtras().getLong("runtime");

        getSupportActionBar().setTitle(movieTitle);
        getSupportActionBar().hide();

        long minutes = runtime;
        String time = (minutes / 60) + " hrs" + (minutes / 60 != 0 ? " " + ((minutes % 60) + " mins"):"") + ".";

        movieImg = (ImageView)findViewById(R.id.detailMovieImg);
        Glide.with(this).load(imageId).into(movieImg);

        movieCoverImg = findViewById(R.id.detailMovieCover);
        Glide.with(this).load(coverId).into(movieCoverImg);

        tv_title= findViewById(R.id.detailMovieTitle);
        tv_title.setText(movieTitle);

        tv_runtime = findViewById(R.id.detailMovieRuntime);
        tv_runtime.setText(time);

        tv_studios = findViewById(R.id.detailMovieStudio);
        tv_studios.setText(studio);

        tv_description = findViewById(R.id.detailMovieDesc);
        tv_description.setText(description);

        movieCoverImg.setAnimation(AnimationUtils.loadAnimation(this,R.anim.scale_anim));

        streamURL = getIntent().getExtras().getString("movieURL");

        //--------------------------------------------FIREBASE READING SECTION-----------------------------------------

        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    isSubscribed = documentSnapshot.getBoolean("isSubscribed");
                }
                else{
                    Toast.makeText(MovieDetailActivity.this,"Can't find user data",Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MovieDetailActivity.this,"Can't connect to database.",Toast.LENGTH_SHORT).show();
            }
        });

        //--------------------------------------------FIREBASE READING SECTION-----------------------------------------

        startMovieButton = findViewById(R.id.startMovieButton);
        startMovieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()) {
                            isSubscribed = documentSnapshot.getBoolean("isSubscribed");
                        }
                        else{
                            Toast.makeText(MovieDetailActivity.this,"Can't find user data",Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MovieDetailActivity.this,"Can't connect to database.",Toast.LENGTH_SHORT).show();
                    }
                });
                if(isSubscribed)
                    onPlayClick(streamURL);
                else
                    Toast.makeText(MovieDetailActivity.this,"Please subscribe first.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPlayClick(String streamURL) {
        Intent intent = new Intent(this,MoviePlayerActivity.class);
        intent.putExtra("videoURL", streamURL);
        startActivity(intent);
    }
}
