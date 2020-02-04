package com.example.streamapps.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamapps.MovieItemClickListener;
import com.example.streamapps.R;
import com.example.streamapps.adapters.SearchedItemAdapter;
import com.example.streamapps.models.Movie;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements MovieItemClickListener {

    private RecyclerView resultView;
    private List<Movie> resultList;
    private FirebaseFirestore database;
    private TextView resultText;

    SearchedItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_srearch);

        resultView = findViewById(R.id.searchedView);
        resultList = new ArrayList<>();
        String query = getIntent().getStringExtra("query");
        resultText = findViewById(R.id.tv_result);
        String t = "Results for " + "\"" +query+"\"";
        resultText.setText(t);
        Toast.makeText(this,"Searching", Toast.LENGTH_SHORT);
        adapter = new SearchedItemAdapter(this,resultList,this);
        resultView.setAdapter(adapter);
        resultView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        //LOADING DATABASE
        database = FirebaseFirestore.getInstance();
        database.collection("movieList")
                .whereGreaterThanOrEqualTo("title",query)
                .whereLessThanOrEqualTo("title",query + "z")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot DocumentSnapshots) {
                if(!DocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = DocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d: list){
                        resultList.add(new Movie(
                                d.getString("title"),
                                d.getString("description"),
                                d.getString("thumbnailURL"),
                                d.getString("CoverURL"),
                                d.getString("studio"),
                                d.getString("streamURL"),
                                d.getLong("runtime")));
                    }

                    adapter.notifyDataSetChanged();
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SearchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onMovieClick(Movie movie, ImageView movieImageView) {

        Intent intent = new Intent(this,MovieDetailActivity.class);
        intent.putExtra("title",movie.getTitle());
        intent.putExtra("imageURL",movie.getImage());
        intent.putExtra("cover",movie.getThumbnailURL());
        intent.putExtra("description", movie.getDescription());
        intent.putExtra("movieURL",movie.getStreamURL());

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SearchActivity.this, movieImageView, "SharedName");

        startActivity(intent,options.toBundle());
    }
}
