/*
 * Copyright (c) 2018.  Adrian Raff AKA Fr0stsp1re
 * ************PROJECT LICENSE*************
 *
 * This project was submitted by Adrian Raff as part of the  Android Basics Nanodegree At Udacity.
 *
 * The Udacity Honor code requires your submissions must be your own work.
 * Submitting this project as yours will cause you to break the Udacity Honor Code
 * and may result in disiplinary action.
 *
 * The author of this project allows you to check the code as a reference only. You may not submit this project or any part
 * of the code as your own.
 *
 * Besides the above notice, the following license applies and this license notice
 * must be included in all works derived from this project.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.fr0stsp1re.newsapp;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtilities {

    // log tag for message logging
    private static final String LOG_TAG = QueryUtilities.class.getSimpleName();

    private QueryUtilities() {

    }

    public static List<News> GrabNewsData(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<News> news = extractJSON(jsonResponse);
        return news;
    }

    // Returns new URL object
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    // makes http request
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Got a 200 response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }

            // response other than 200
            else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {

            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);

        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    // read input stream
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    // extract JSON response
    private static List<News> extractJSON(String newsJSON) {

        // empty JSON
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // create empty array list
        List<News> news = new ArrayList<>();

        try {
            // Create a JSONObject and extract results
            JSONObject rootJsonResponse = new JSONObject(newsJSON);
            JSONObject newsArray = rootJsonResponse.optJSONObject("response");
            JSONArray jsonArray = newsArray.optJSONArray("results");

            // loop through jsonArray and set variables
            for (int i = 0; i < jsonArray.length(); i++) {

                // grab news starting at array named results
                JSONObject article = jsonArray.optJSONObject(i);

                // get the title
                String title = article.getString("webTitle");

                // Extract the value for the key called "place"
                String date = article.getString("webPublicationDate");

                // Extract the value for the key called "sectionName"
                String section = article.getString("sectionName");

                // Extract the value for the key called "url"
                String url = article.getString("webUrl");

                // grab image data
                JSONObject fields = article.optJSONObject("fields");
                String imageUrl = fields.getString("thumbnail");

                // Start down the tags array and extract author
                JSONArray tags = article.getJSONArray("tags");

                String author;

                if (tags.length() > 0) {
                    JSONObject tagsField = tags.getJSONObject(0);
                    author = tagsField.getString("webTitle");
                }
                else{
                    author = "No author information avaialbe";
                }
                News newNews = new News(title, date, section, author, imageUrl, url);
                news.add(newNews);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }
        return news;
    }
}