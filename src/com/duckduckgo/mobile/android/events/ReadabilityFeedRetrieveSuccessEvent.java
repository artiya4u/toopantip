package com.duckduckgo.mobile.android.events;

import com.duckduckgo.mobile.android.objects.FeedObject;

import java.util.List;

public class ReadabilityFeedRetrieveSuccessEvent extends Event {
    public List<FeedObject> feed;

    public ReadabilityFeedRetrieveSuccessEvent(List<FeedObject> feed) {
        this.feed = feed;
    }

}