package com.example.flicks;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.flicks.models.Movie;
import com.example.flicks.models.config;

import java.util.ArrayList;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    // list of movies
    ArrayList<Movie> movies;
    // config needed for image urls
    config config;
    // context for rendering
    Context context;

    //initialize with list
    public MovieAdapter (ArrayList<Movie> movies) {
        this.movies = movies;
    }

    public com.example.flicks.models.config getConfig () {
        return config;
    }

    public void setConfig (com.example.flicks.models.config config) {
        this.config = config;
    }

    // creates and inflates a new view
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        // get the context and create the inflater
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // create the view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        // return a new ViewHolder
        return new ViewHolder(movieView);
    }
    // binds and inflated view to a new item
    @Override
    public void onBindViewHolder (@NonNull ViewHolder holder, int position) {
        // get the movie data at the specified position
        Movie movie = movies.get(position);
        // populate the view ith the movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverView.setText(movie.getOverview());

        // determine the current orientation
        boolean isPortarait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        // build url for poster image
        String imageUrl = null;

        // if in portrait mode, load the poster image
        if(isPortarait){
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        } else {
            // load the backdrop image
            imageUrl = config.getImageUrl(config.getBackdropSize(),movie.getBackdropPath());
        }

        // get  the correct placeholder and image viwe for the current orientation
        int placeholderId = isPortarait ? R.drawable.flicks_backdrop_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortarait ? holder.ivPosterImage : holder.ivBackdropImage;

        // load image using glide
        Glide.with(context)
                .load(imageUrl)
                . placeholder (placeholderId)
                .error(placeholderId)
                .into(imageView);

    }
    // returns the total number of items and value on the list
    @Override
    public int getItemCount () {
        return movies.size();
    }

    // create the viewholder as a static inner class
    public static class ViewHolder extends RecyclerView.ViewHolder {

        // track new objects
        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverView;


        public ViewHolder (@NonNull View itemView) {
            super(itemView);
            // lookup view objects by id
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            tvOverView = (TextView) itemView.findViewById(R.id.tvOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }


}
