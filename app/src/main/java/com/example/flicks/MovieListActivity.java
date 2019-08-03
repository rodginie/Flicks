package com.example.flicks;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flicks.models.Movie;
import com.example.flicks.models.config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MovieListActivity extends AppCompatActivity {

    // constants
    //the bas eURL fot the API
    public final static String API_BASE_URL = "https://api.themoviedb.org/3";
    // the parameter name fot the API key
    public final static String API_KEY_PARAM = "api_key";
    // tag fot logging from this Activity
    public final static String TAG = "MovieListActivity";

    // instance fields
    AsyncHttpClient client;

    // the list of currently playing movies
    ArrayList<Movie> movies;
    // the recycler view
    RecyclerView rvMovies;
    // the adapter wired to the recycler view
    MovieAdapter adapter;
    // image config
    config config;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);
        // initialize the client
        client = new AsyncHttpClient();
        // initialize the list of movies
        movies = new ArrayList<>();
        // initialize the adapter -- movies array cannot be reinitialized after this point
        adapter = new MovieAdapter(movies);

        // resolve the recycler view and connect a layout manager and the adapter
        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        // get the configuration on app creation
        getConfiguration();
        getNowPlaying();

    }

    // get the list of currently playing movies from the API
    private void getNowPlaying(){
        // create the url
        String url = API_BASE_URL + "/movie/now_playing";
        //set the request parameters
        RequestParams params= new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API key, always required
        //execute a GET request expecting a JSON object response
        client.get(url,params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess (int statusCode, Header[] headers, JSONObject response) {
                // load the results into movies list
                try {
                    JSONArray results = response.getJSONArray("results");
                    //iterate through result et and create Movie objects
                    for (int i = 0; i < results.length(); i++){
                        Movie movie = new Movie(results.getJSONObject(i));
                        movies.add(movie);
                        // notify adapter that a row was added
                        adapter.notifyItemInserted(movies.size() - 1);
                    }
                    Log.i(TAG, String.format("Loaded %s movies", results.length()));
                } catch (JSONException e) {
                    LogError("Failed to parse now playing movies", e, true);
                }
            }

            @Override
            public void onFailure (int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogError("Failed to get data from now_playing endpoint", throwable, true);
            }
        });
    }

    // get the configuration from API
    private void getConfiguration(){
        // create the url
        String url = API_BASE_URL + "/configuration";
        //set the request parameters
        RequestParams params= new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key)); // API key, always required
        //execute a GET request expecting a JSON object response
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess (int statusCode, Header[] headers, JSONObject response) {
                // get the image base url
                try {
                    config = new config(response);
                    Log.i(TAG, String.format("Loaded configuration with imageBaseUrl %s and posterSizes %s",
                            config.getImageBaseUrl(),
                            config.getPosterSize()));
                    // pass config to adapter
                    adapter.setConfig(config);
                    // get now playing movie list
                    getNowPlaying();
                } catch (JSONException e) {
                    LogError("Failed parsing configuration", e, true);
                }
            }

            @Override
            public void onFailure (int statusCode, Header[] headers, String responseString, Throwable throwable) {
                LogError("Failed getting configuration", throwable, true);
            }
        });
    }

    // handle errors, Log and alert user
    private void LogError(String message, Throwable error, boolean alertUser){
        // always log the error
        Log.e(TAG, message, error);
        // alert the user to avoid silent errors
        if (alertUser) {
            // show a long toast with the error message
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}