package com.duckduckgo.mobile.android.events.feedEvents;

import com.duckduckgo.mobile.android.events.Event;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.REQUEST_TYPE;

import java.util.List;


public class FeedRetrieveSuccessEvent extends Event {
    public REQUEST_TYPE requestType;
    public List<FeedObject> feed;

    public FeedRetrieveSuccessEvent(List<FeedObject> feed, REQUEST_TYPE requestType) {
        this.requestType = requestType;
        this.feed = feed;
    }

}