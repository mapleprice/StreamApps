package com.example.streamapps.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.streamapps.MovieItemClickListener;
import com.example.streamapps.R;
import com.example.streamapps.models.Movie;

import java.util.List;

public class SearchedItemAdapter extends RecyclerView.Adapter<SearchedItemAdapter.MyViewHolder> {
    Context context;
    List<Movie> mData;
    MovieItemClickListener movieClickListener;

    public SearchedItemAdapter(Context context, List<Movie> mData, MovieItemClickListener movieClickListener) {
        this.context = context;
        this.mData = mData;
        this.movieClickListener = movieClickListener;
    }

    @NonNull
    @Override
    public SearchedItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.searched_item,parent,false);
        return new SearchedItemAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchedItemAdapter.MyViewHolder holder, int position) {
        //Load Picture
        Glide.with(this.context).load(mData.get(position).getImage()).into(holder.Cover);
        //Load Texts
        holder.titleText.setText(String.valueOf(mData.get(position).getTitle()));
        holder.studio.setText(String.valueOf(mData.get(position).getStudio()));
        holder.year.setText("");
        long minutes = mData.get(position).getRuntime();
        String runtime = (minutes / 60) + " hrs" + (minutes / 60 != 0 ? " " + ((minutes % 60) + " mins"):"") + ".";
        holder.runtime.setText(runtime);

    }

    @Override
    public int getItemCount() {
         return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView titleText;
        private ImageView Cover;
        private TextView year;
        private TextView studio;
        private TextView runtime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.searched_title);
            Cover = itemView.findViewById(R.id.searched_cover);
            year = itemView.findViewById(R.id.searched_year);
            studio = itemView.findViewById(R.id.searched_studio);
            runtime = itemView.findViewById(R.id.searched_runtime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    movieClickListener.onMovieClick(mData.get(getAdapterPosition()),Cover);
                }
            });

        }
    }
}
