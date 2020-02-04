package com.example.streamapps.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.streamapps.R;
import com.example.streamapps.activities.MoviePlayerActivity;
import com.example.streamapps.models.Item;
import com.example.streamapps.models.Slide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class
SlidePagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Item> mList;

    public SlidePagerAdapter(Context mContext, List<Item> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View slideLayout = inflater.inflate(R.layout.slide_item,null);

        ImageView slideImg = slideLayout.findViewById(R.id.mImage);
        TextView slideText = slideLayout.findViewById(R.id.mTitle);
        FloatingActionButton playButton = slideLayout.findViewById(R.id.sliderPlayButton);

        Glide.with(mContext).load(mList.get(position).getImage()).into(slideImg);
        slideText.setText(mList.get(position).getTitle());

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MoviePlayerActivity.class);
                intent.putExtra("videoURL", mList.get(position).getStreamURL());

                mContext.startActivity(intent);
            }
        });

        container.addView(slideLayout);
        return slideLayout;



    }


    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
