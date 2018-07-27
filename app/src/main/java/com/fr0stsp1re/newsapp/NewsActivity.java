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

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = NewsActivity.class.getName();

    // request url
    private static final String NEWS_REQUEST_URL = "https://content.guardianapis.com/search?q=android&api-key=37df9c67-44dc-43a5-a7da-d4d812e60f4e";

    // no news TextView
    private TextView mNoNewsTextView;

    private static final int NEWS_LOADER_ID = 1;

    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);

        ListView newsListView = (ListView) findViewById(R.id.list);

        mNoNewsTextView = (TextView) findViewById(R.id.empty_view);
        newsListView.setEmptyView(mNoNewsTextView);

        // set up adapter and populate
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        newsListView.setAdapter(mAdapter);

        // Set an item click listener for news items to open in web browser
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                News currentNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object. Create intent and pass URI.
                Uri newsUri = Uri.parse(currentNews.getUrl());
                Intent webIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                startActivity(webIntent);
            }
        });

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // Grab news if there is a network connection
        if (networkInfo != null && networkInfo.isConnected()) {

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        } else {

            //hide loading indicator and display error if no connection
            View loadingIndicator = findViewById(R.id.progress_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mNoNewsTextView.setText(R.string.no_internet);
            mNoNewsTextView.setPadding(8, 8, 8, 8);

        }
    }

    // Loader callbacks
    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        Log.v(LOG_TAG, "The loader is being created");
        return new NewsLoader(this, NEWS_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        View loadingIndicator = findViewById(R.id.progress_indicator);
        loadingIndicator.setVisibility(View.GONE);

        Log.v(LOG_TAG, "The loader is finished");

        mNoNewsTextView.setText(R.string.no_news);

        mAdapter.clear();

        if (news != null && !news.isEmpty()) {
            mAdapter.addAll(news);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<com.fr0stsp1re.newsapp.News>> loader) {
        mAdapter.clear();
    }
}