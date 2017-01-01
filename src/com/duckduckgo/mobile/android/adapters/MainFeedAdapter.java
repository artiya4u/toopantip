package com.duckduckgo.mobile.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.download.Holder;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainFeedAdapter extends ArrayAdapter<FeedObject>/* implements Filterable */ {
    private static final String TAG = "MainFeedAdapter";

    private Context context;
    private final LayoutInflater inflater;

    private ArrayList<FeedObject> feedObjects;

    public OnClickListener sourceClickListener;
    public OnClickListener categoryClickListener;

    private SimpleDateFormat dateFormat;
    private Date lastFeedDate = null;

    private String markedItem = null;
    private String markedSource = null;
    private String markedCategory = null;

    private AlphaAnimation blinkanimation = null;

    //TODO: Should share this image downloader with the autocompleteresults adapter instead of creating a second one...

    public MainFeedAdapter(Context context, OnClickListener sourceClickListener, OnClickListener categoryClickListener) {
        super(context, 0);
        this.context = context;
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        feedObjects = new ArrayList<FeedObject>();
        this.sourceClickListener = sourceClickListener;
        this.categoryClickListener = categoryClickListener;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        // animation to use for blinking cue
        blinkanimation = new AlphaAnimation(1, 0.3f);
        blinkanimation.setDuration(300);
        blinkanimation.setInterpolator(new LinearInterpolator());
        blinkanimation.setRepeatCount(2);
        blinkanimation.setRepeatMode(Animation.REVERSE);
    }

    @Override
    public View getView(int position, View cv, ViewGroup parent) {
        View view = cv;
        if (view == null) {
            view = inflater.inflate(R.layout.item_main_feed, null);
            Holder holder = new Holder(/*(Toolbar) cv.findViewById(R.id.feedWrapper),*/
                    (TextView) view.findViewById(R.id.feedTitleTextView),
                    (TextView) view.findViewById(R.id.feedCategoryTextView),
                    (AsyncImageView) view.findViewById(R.id.feedItemBackground),
                    (TextView) view.findViewById(R.id.feedSummaryTextView));
            //holder.toolbar.inflateMenu(R.menu.feed);
            view.setTag(holder);
        }

        final FeedObject feed = getItem(position);

        final Holder holder = (Holder) view.getTag();

        if (feed != null) {

            final String feedId = feed.getId();

            //Download the background image
            if (feed.getImageUrl() != null && !feed.getImageUrl().equals("null")) {
                if (feed.getImageUrl().equals("http://ptcdn.info/pantip/pantip_logo_02.png")) {
                    holder.imageViewBackground.setVisibility(View.GONE);
                } else {
                    holder.imageViewBackground.setVisibility(View.VISIBLE);
                    Picasso.with(context)
                            .load(feed.getImageUrl())
                            .placeholder(android.R.color.transparent)
                            .into(holder.imageViewBackground);
                }
            }

            holder.textViewTitle.setText(feed.getTitle());
            holder.textViewSummary.setText(feed.getDescription());

            //Set the Title
            holder.textViewTitle.setText(feed.getTitle());

            // FIXME : it'd be good to reset color to default color for textview in layout XML
            holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.feed_title));
            if (DDGControlVar.readArticles.contains(feedId)) {
                holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.feed_title_viewed));
            }

            //set the category
            //todo insert size
            final String category = feed.getCategory();
            holder.textViewCategory.setText(category.toUpperCase());
            holder.textViewCategory.setOnClickListener(categoryClickListener);
        }

        if ((markedItem != null && markedItem.equals(feed.getId())) || (markedSource != null && markedSource.equals(feed.getId()))) {
            blinkanimation.reset();
            cv.startAnimation(blinkanimation);
        } else {
            view.setAnimation(null);
        }

        return view;
    }

    public void setList(List<FeedObject> feed) {
        this.clear();
        //feedObjects.clear();
        //getFilter().filter("");
        for (FeedObject next : feed) {
            this.add(next);
            //feedObjects.add(next);
        }
    }

    @Override
    public void clear() {
        super.clear();
        //feedObjects.clear();
    }

    @Override
    public void add(FeedObject feed) {
        super.add(feed);
        //feedObjects.add(feed);
    }

    public void addData(List<FeedObject> feed) {
        if (this.lastFeedDate == null) {
            setList(feed);
            return;
        }

        Date tmpFeedDate = null;
        for (FeedObject next : feed) {
            try {
                tmpFeedDate = this.dateFormat.parse(next.getTimestamp());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (tmpFeedDate == null || !tmpFeedDate.after(lastFeedDate)) {
                return;
            }

            this.insert(next, 0);
            this.lastFeedDate = tmpFeedDate;
        }
    }

    /**
     * Mark a list item position to be blinked
     */
    public void mark(String itemId) {
        markedItem = itemId;
    }

    public void unmark() {
        markedItem = null;
    }

    public void markSource(String itemId) {
        markedSource = itemId;
    }

    public void unmarkSource() {
        markedSource = null;
    }

    public void markCategory(String itemId) {
        markedCategory = itemId;
    }

    public void unmarkCategory() {
        markedCategory = null;
    }

}
