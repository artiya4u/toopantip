package com.duckduckgo.mobile.android.download;

import android.widget.TextView;

public class Holder {/*
    public final Toolbar toolbar;*/

    public final TextView textViewTitle;
    public final TextView textViewCategory;
    public final AsyncImageView imageViewBackground;
    public final TextView textViewSummary;

    public Holder(/*final Toolbar toolbar, */final TextView textViewTitle,
                  final TextView textViewCategory, final AsyncImageView imageViewBackground,
                  final TextView textViewSummary) {/*
        this.toolbar = toolbar;*/
        this.textViewTitle = textViewTitle;
        this.textViewCategory = textViewCategory;
        this.imageViewBackground = imageViewBackground;
        this.textViewSummary = textViewSummary;
    }
}