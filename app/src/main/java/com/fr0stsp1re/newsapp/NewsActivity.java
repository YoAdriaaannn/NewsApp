
package com.fr0stsp1re.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = NewsActivity.class.getName();

   // request url
    private static final String NEWS_REQUEST_URL =
            "https://content.guardianapis.com/search?q=android&api-key=37df9c67-44dc-43a5-a7da-d4d812e60f4e";
    // No news TextView
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
                Uri earthquakeUri = Uri.parse(currentNews.getUrl());
               Intent webIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

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
            //hide loading indicator and display error
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            mNoNewsTextView.setText("Uh oh! No internet connection!");
        }
    }


    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {

        View loadingIndicator = findViewById(R.id.loading_indicator);
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
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        Log.v(LOG_TAG, "The loader is being created");
        return new NewsLoader(this, NEWS_REQUEST_URL);
    }

}