package com.example.streamapps.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.streamapps.models.Item;
import com.example.streamapps.models.Movie;
import com.example.streamapps.adapters.MovieAdapter;
import com.example.streamapps.MovieItemClickListener;
import com.example.streamapps.R;
import com.example.streamapps.models.Slide;
import com.example.streamapps.adapters.SlidePagerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity implements MovieItemClickListener {

    private List<Item> lstSlide;
    private ViewPager slidePager;
    private TabLayout indicator;
    private TabLayout genreSelector;
    private List<Movie> newMovies;
    private List<Movie> recentMovies;
    private RecyclerView newMovieView;
    private RecyclerView recentWatchedView;
    private RecyclerView genresViews;
    private FirebaseFirestore database;
    private TextView userGreetings;
    private FirebaseAuth auth;
    private List<Movie> genreList;

    private SlidePagerAdapter pagerAdapter;
    private MovieAdapter genreAdapter;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);

        MenuItem logout = menu.findItem(R.id.logout);
        MenuItem account_settings = menu.findItem(R.id.account_setting);
        MenuItem searchItem = menu.findItem(R.id.search_box);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query",query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.account_setting:
                Toast.makeText(this, "Account Settings", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,AccountSettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //INITIATE VIEWS3
        initiateViews();

        //LOADING DATABASE
        loadingDatabase();

        //ADDING ELEMENTS TO SLIDES
        pagerAdapter = new SlidePagerAdapter(this,this.lstSlide);
        slidePager.setAdapter(pagerAdapter);


        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MainActivity.slideTimer(),4000,6000);

        //SETTING UP INDICATOR
        indicator.setupWithViewPager(slidePager,true);

        //MAKING MOVIE ADAPTER
        MovieAdapter newMovieAdapter = new MovieAdapter(this,newMovies,this);
        newMovieView.setAdapter(newMovieAdapter);
        newMovieView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        MovieAdapter playedMovieAdapter = new MovieAdapter(this,recentMovies,this);
        recentWatchedView.setAdapter(playedMovieAdapter);
        recentWatchedView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        genreAdapter = new MovieAdapter(this, genreList, this);
        genresViews.setAdapter(genreAdapter);
        genresViews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // ADDING GENRE TAB LAYOUT LISTENER
        genreSelector.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Toast.makeText(MainActivity.this,"Genre Changed", Toast.LENGTH_SHORT).show();
                database.collection("movieList")
                        .whereArrayContains("tag",String.valueOf(tab.getText()))
                        .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot DocumentSnapshots) {
                        if(!DocumentSnapshots.isEmpty()){
                            List<DocumentSnapshot> list = DocumentSnapshots.getDocuments();
                            for(DocumentSnapshot d: list){
                                genreList.add(new Movie(
                                        d.getString("title"),
                                        d.getString("description"),
                                        d.getString("thumbnailURL"),
                                        d.getString("CoverURL"),
                                        d.getString("studio"),
                                        d.getString("streamURL"),
                                        d.getLong("runtime")
                                ));
                            }
                            genreAdapter.notifyDataSetChanged();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                genreList.clear();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                genreAdapter.notifyDataSetChanged();
            }
        });
    }
//##################################################################################################
//VIEWS INITIATION
//##################################################################################################
    private void initiateViews() {
        newMovieView = (RecyclerView) findViewById(R.id.Rv_newMovies);
        slidePager = (ViewPager) findViewById(R.id.featuringPager);
        indicator = findViewById(R.id.indicator);
        recentWatchedView = findViewById(R.id.Rv_recentlyWatched);
        genreSelector = findViewById(R.id.genreSelector);
        lstSlide = new ArrayList<>();
        recentMovies = new ArrayList<>();
        newMovies = new ArrayList<>();
        genreList = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        userGreetings = findViewById(R.id.userGreetings);
        String greetingText = "Hello, " + auth.getCurrentUser().getEmail();
        userGreetings.setText(greetingText);
        genresViews = findViewById(R.id.Rv_genresView);
    }

//##################################################################################################
//DATABASE INITIATION
//##################################################################################################

    private void loadingDatabase() {
        database = FirebaseFirestore.getInstance();
        database.collection("movieList").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot DocumentSnapshots) {
                if(!DocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = DocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d: list){
                        newMovies.add(new Movie(
                                d.getString("title"),
                                d.getString("description"),
                                d.getString("thumbnailURL"),
                                d.getString("CoverURL"),
                                d.getString("studio"),
                                d.getString("streamURL"),
                                d.getLong("runtime")
                        ));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        database.collection("movieList")
                .whereEqualTo("studio","Sony Pictures")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot DocumentSnapshots) {
                if(!DocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = DocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d: list){
                        recentMovies.add(new Movie(
                                d.getString("title"),
                                d.getString("description"),
                                d.getString("thumbnailURL"),
                                d.getString("CoverURL"),
                                d.getString("studio"),
                                d.getString("streamURL"),
                                d.getLong("runtime")
                        ));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        database.collection("movieList")
                .whereArrayContains("tag",String.valueOf(genreSelector.getTabAt(genreSelector.getSelectedTabPosition()).getText()))
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot DocumentSnapshots) {
                if(!DocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = DocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d: list){
                        genreList.add(new Movie(
                                d.getString("title"),
                                d.getString("description"),
                                d.getString("thumbnailURL"),
                                d.getString("CoverURL"),
                                d.getString("studio"),
                                d.getString("streamURL"),
                                d.getLong("runtime")
                        ));
                    }
                    genreAdapter.notifyDataSetChanged();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        database.collection("movieList")
                .orderBy("thumbnailURL").limit(4)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot DocumentSnapshots) {
                if(!DocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> list = DocumentSnapshots.getDocuments();
                    for(DocumentSnapshot d: list){
                        lstSlide.add(new Slide(
                                d.getString("title"),
                                d.getString("thumbnailURL"),
                                d.getString("streamURL")
                        ));
                        pagerAdapter.notifyDataSetChanged();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

//##################################################################################################
//MOVIE CLICK FUNCTION
//##################################################################################################

    @Override
    public void onMovieClick(Movie movie, ImageView movieImageView) {

        Intent intent = new Intent(this,MovieDetailActivity.class);
        intent.putExtra("title",movie.getTitle());
        intent.putExtra("imageURL",movie.getImage());
        intent.putExtra("cover",movie.getThumbnailURL());
        intent.putExtra("description", movie.getDescription());
        intent.putExtra("movieURL",movie.getStreamURL());
        intent.putExtra("studio",movie.getStudio());
        intent.putExtra("runtime",movie.getRuntime());

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, movieImageView, "SharedName");

        startActivity(intent,options.toBundle());
    }
//##################################################################################################
//TIMER
//##################################################################################################
    class slideTimer extends TimerTask {
        public void run(){
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (slidePager.getCurrentItem()<lstSlide.size()-1){
                        slidePager.setCurrentItem(slidePager.getCurrentItem()+1);
                    }
                    else
                        slidePager.setCurrentItem(0);
                }
            });
        }
    }
}

 