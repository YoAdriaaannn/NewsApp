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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    // Used to split timestamp and date
    private static final String LOCATION_SEPARATOR = "T";

    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.news_list_item, parent, false);
        }

        News currentNews = getItem(position);

        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(currentNews.getTitle());

        TextView sectionView = (TextView) listItemView.findViewById(R.id.section);
        sectionView.setText(currentNews.getSection());

        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        authorView.setText(currentNews.getAuthor());

        // Date comes back from JSON with time. Strip time from date and display date only.
        String originalDate = currentNews.getDate();
        String separatedDate;

        if (originalDate.contains(LOCATION_SEPARATOR)) {
            String[] parts = originalDate.split(LOCATION_SEPARATOR);
            separatedDate = parts[0];
        } else {
            separatedDate = getContext().getString(R.string.no_date_available);
        }

        TextView dateView = (TextView) listItemView.findViewById(R.id.date);
        dateView.setText(separatedDate);

        ImageView newsImageView = listItemView.findViewById(R.id.news_image);
        String imageUrl = currentNews.getImageUrl();

        // check if there is an image if not set a default placeholder
        if (imageUrl != null) {
            Picasso.get().load(imageUrl).into(newsImageView);
        } else {
            newsImageView.setImageResource(R.drawable.no_image_available);
        }
        return listItemView;
    }
}