package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;

public class RecentFeedCursorAdapter extends CursorAdapter {

    public RecentFeedCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.item_main_feed, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final String id = cursor.getString(cursor.getColumnIndex("_id"));
        final String type = cursor.getString(cursor.getColumnIndex("type"));
        final String data = cursor.getString(cursor.getColumnIndex("data"));
        final String url = cursor.getString(cursor.getColumnIndex("url"));
        final String extraType = cursor.getString(cursor.getColumnIndex("extraType"));
        final String feedId = cursor.getString(cursor.getColumnIndex("feedId"));

//        final String feedId = cursor.getString(cursor.getColumnIndex("_id"));
        final String title = cursor.getString(cursor.getColumnIndex("title"));
        final String feedType = cursor.getString(cursor.getColumnIndex("type"));
        final String imageUrl = cursor.getString(cursor.getColumnIndex("imageurl"));
        final String feedContent = cursor.getString(cursor.getColumnIndex("feed"));
        final String category = cursor.getString(cursor.getColumnIndex("category"));
        final String favUrl = cursor.getString(cursor.getColumnIndex("favicon"));
        final String summary = cursor.getString(cursor.getColumnIndex("description"));

        final TextView textViewTitle = (TextView) view.findViewById(R.id.feedTitleTextView);
        final TextView textViewCategory = (TextView) view.findViewById(R.id.feedCategoryTextView);
        final AsyncImageView imageViewBackground = (AsyncImageView) view.findViewById(R.id.feedItemBackground);
        final TextView textViewSummary = (TextView) view.findViewById(R.id.feedSummaryTextView);

        URL feedUrl = null;

        //Download the background image
        if (imageUrl != null && !imageUrl.equals("null")) {
            if (imageUrl.equals("http://ptcdn.info/pantip/pantip_logo_02.png")) {
                imageViewBackground.setVisibility(View.GONE);
            } else {
                imageViewBackground.setVisibility(View.VISIBLE);
                Picasso.with(context)
                        .load(imageUrl)
                        .placeholder(android.R.color.transparent)
                        .into(imageViewBackground);
            }
        }

        final View iconParent = (View) imageViewBackground.getParent();
        iconParent.post(new Runnable() {
            public void run() {
                // Post in the parent's message queue to make sure the parent
                // lays out its children before we call getHitRect()
                Rect delegateArea = new Rect();
                delegateArea.top = 0;
                delegateArea.bottom = iconParent.getBottom();
                delegateArea.left = 0;
                // right side limit also considers the space that is available from TextView, without text displayed
                // in TextView padding area on the left
                delegateArea.right = textViewTitle.getLeft() + textViewTitle.getPaddingLeft();
            }

            ;
        });

        //Set the Title
        textViewTitle.setText(title);

        textViewSummary.setText(summary);

        //Set the Category
        textViewCategory.setText(category.toUpperCase());

        if (DDGControlVar.readArticles.contains(feedId)) {
            textViewTitle.setTextColor(Color.GRAY);
        }

        if (feedContent != null && !feedContent.equals("null")) {
            try {
                feedUrl = new URL(feedContent);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            if (feedUrl != null) {
                String host = feedUrl.getHost();
                if (host.indexOf(".") != host.lastIndexOf(".")) {
                    //Cut off the beginning, because we don't want/need it
                    host = host.substring(host.indexOf(".") + 1);
                }
            }
        }

    }
}