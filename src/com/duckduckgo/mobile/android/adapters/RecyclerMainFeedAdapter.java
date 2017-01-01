package com.duckduckgo.mobile.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.duckduckgo.mobile.android.DDGApplication;
import com.duckduckgo.mobile.android.R;
import com.duckduckgo.mobile.android.bus.BusProvider;
import com.duckduckgo.mobile.android.download.AsyncImageView;
import com.duckduckgo.mobile.android.events.feedEvents.FeedCancelCategoryFilterEvent;
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemSelectedEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.views.DDGOverflowMenu;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecyclerMainFeedAdapter extends RecyclerView.Adapter<RecyclerMainFeedAdapter.ViewHolder> {
    private static final String TAG = "MainFeedAdapter";

    private Context context;
    private final LayoutInflater inflater;

    //public View.OnClickListener sourceClickListener;
    //public View.OnClickListener categoryClickListener;

    private SimpleDateFormat dateFormat;
    private Date lastFeedDate = null;

    //private String markedItem = null;
    //private String markedSource = null;
    //private String markedCategory = null;

    //private AlphaAnimation blinkanimation = null;

    private DDGOverflowMenu feedMenu = null;
    private Menu menu = null;

    public ArrayList<FeedObject> data;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView textViewTitle;
        public final FrameLayout frameCategoryContainer;
        public final TextView textViewCategory;
        public final FrameLayout frameMenuContainer;
        public final ImageView imageViewMenu;
        public final AsyncImageView imageViewBackground;
        public final TextView textViewSummary;

        public ViewHolder(View v) {
            super(v);
            this.textViewTitle = (TextView) v.findViewById(R.id.feedTitleTextView);
            this.frameCategoryContainer = (FrameLayout) v.findViewById(R.id.feedCategoryContainer);
            this.textViewCategory = (TextView) v.findViewById(R.id.feedCategoryTextView);
            this.frameMenuContainer = (FrameLayout) v.findViewById(R.id.feedMenuContainer);
            this.imageViewMenu = (ImageView) v.findViewById(R.id.feedMenuImage);
            this.imageViewBackground = (AsyncImageView) v.findViewById(R.id.feedItemBackground);
            this.textViewSummary = (TextView) v.findViewById(R.id.feedSummaryTextView);
        }
    }

    public RecyclerMainFeedAdapter(Context context/*, View.OnClickListener sourceClickListener, View.OnClickListener categoryClickListener*/) {
        this.context = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        data = new ArrayList<FeedObject>();

        //this.sourceClickListener = sourceClickListener;
        //this.categoryClickListener = categoryClickListener;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        // animation to use for blinking cue
        /*blinkanimation = new AlphaAnimation(1, 0.3f);
        blinkanimation.setDuration(300);
        blinkanimation.setInterpolator(new LinearInterpolator());
        blinkanimation.setRepeatCount(2);
        blinkanimation.setRepeatMode(Animation.REVERSE);*/

        menu = new MenuBuilder(context);
        ((Activity) context).getMenuInflater().inflate(R.menu.feed, menu);
        feedMenu = new DDGOverflowMenu(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflater.inflate(R.layout.item_main_feed, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final FeedObject feed = data.get(position);

        if (feed != null) {
            final String feedId = feed.getId();

            //Download the background image
            if (feed.getImageUrl() != null && !feed.getImageUrl().equals("null")) {
                if (feed.getImageUrl().endsWith("pantip_logo_02.png")) {
                    holder.imageViewBackground.setVisibility(View.GONE);
                } else {
                    holder.imageViewBackground.setVisibility(View.VISIBLE);
                    Picasso.with(context)
                            .load(feed.getImageUrl())
                            .placeholder(android.R.color.transparent)
                            .into(holder.imageViewBackground);
                }
            }

            //Set the Title
            holder.textViewTitle.setText(feed.getTitle());
            holder.textViewSummary.setText(feed.getDescription());

            // FIXME : it'd be good to reset color to default color for textview in layout XML
            holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.feed_title));
            if (DDGControlVar.readArticles.contains(feedId)) {
                holder.textViewTitle.setTextColor(context.getResources().getColor(R.color.feed_title_viewed));
            }

            //set the category
            //todo insert size
            final String category = feed.getCategory();
            holder.textViewCategory.setText(category.toUpperCase());

            holder.frameCategoryContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (DDGControlVar.targetCategory != null) {
                        DDGControlVar.targetCategory = null;
                        BusProvider.getInstance().post(new FeedCancelCategoryFilterEvent());
                    } else {
                        DDGControlVar.targetCategory = category;
                        filterCategory(category);
                    }
                }
            });

            holder.frameMenuContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu(holder.imageViewMenu, feed);
                }
            });

            if (feed.getFeed() != null && !feed.getFeed().equals("null")) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BusProvider.getInstance().post(new MainFeedItemSelectedEvent(feed));
                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //BusProvider.getInstance().post(new MainFeedItemLongClickEvent(feed));
                        showMenu(holder.imageViewMenu, feed);
                        return true;
                    }
                });
            }
        }

/*
        if(false && holder.itemView != null) {
            if((markedItem != null && markedItem.equals(feed.getId())) || (markedSource!=null && markedSource.equals(feed.getId()))) {
                blinkanimation.reset();
                holder.itemView.startAnimation(blinkanimation);
            }
            else {
                holder.itemView.setAnimation(null);
            }
        }*/
    }

    public FeedObject getItem(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void showMenu(View anchor, FeedObject feed) {
        if (feedMenu == null) {
            feedMenu = new DDGOverflowMenu(context);
        }
        if (!feedMenu.isShowing()) {
            if (DDGApplication.getDB().isSaved(feed.getId())) {
                menu.findItem(R.id.action_add_favorite).setVisible(false);
                menu.findItem(R.id.action_remove_favorite).setVisible(true);
            } else {
                menu.findItem(R.id.action_add_favorite).setVisible(true);
                menu.findItem(R.id.action_remove_favorite).setVisible(false);
            }
            feedMenu.setFeed(feed);
            feedMenu.setMenu(menu);
            feedMenu.showFeedMenu(anchor);
        }

    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void addData(List<FeedObject> newData) {
        this.data = (ArrayList) newData;
        notifyDataSetChanged();
    }

    public void addSourceData(List<FeedObject> newSources) {
        Log.e("aaa", "add source data, data size: " + data.size());
        for (FeedObject feed : data) {
            Log.e("aaa", "old feed: " + feed.getTitle());
        }
        for (FeedObject newFeed : newSources) {
            boolean isPresent = false;
            for (FeedObject feed : data) {
                if (feed.getId().equals(newFeed.getId())) {
                    isPresent = true;
                }
            }
            //boolean isPresent = data.contains(feed);
            Log.e("aaa", "feed: " + newFeed.getTitle() + " - is present: " + isPresent);

            if (!isPresent) {
                data.add(newFeed);
                notifyItemInserted(data.size());
            }
        }
    }

    public void filterCategory(String category) {
        int i = data.size() - 1;
        for (; i >= 0; i--) {
            if (!data.get(i).getCategory().equals(category)) {
                data.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    private void filterSource(String source) {
        int i = data.size() - 1;
        for (; i >= 0; i--) {
            if (!data.get(i).getType().equals(source)) {
                data.remove(i);
                notifyItemRemoved(i);
            }
        }
    }
/*
    public void markSource(String itemId) {
        markedSource = itemId;
    }

    public void unmarkSource() {
        markedSource = null;
    }*/
}
