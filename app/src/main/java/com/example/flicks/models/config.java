package com.example.flicks.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class config {
    // the base url for loading images
    String imageBaseUrl;
    // the poster size to use when fetching images, part of the url
    String posterSize;
    // the backdrop size to use when fetching image
    String backdropSize;

    public config (JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");
        //get the image base url
        imageBaseUrl = images.getString("secure_base_url");
        // get the poster size
        JSONArray posterSizeOptions = images.getJSONArray("poster_sizes");
        // use the option at index 3 or w342 as a fallback
        posterSize = posterSizeOptions.optString(3, "w342");
        // parse the backdrop size and use the option at index 1 or w780 as a fallback
        JSONArray backdropSizeOptions = images.getJSONArray("backdropSize");
        backdropSize = backdropSizeOptions.optString(1,"w780");

    }

    // helper method for creating urls
    public String getImageUrl(String size, String path) {
        return String.format("%s%s%s", imageBaseUrl, size, path); // concatenate all three
    }
    public String getImageBaseUrl () {
        return imageBaseUrl;
    }

    public String getPosterSize () {
        return posterSize;
    }

    public String getBackdropSize () {
        return backdropSize;
    }
}
