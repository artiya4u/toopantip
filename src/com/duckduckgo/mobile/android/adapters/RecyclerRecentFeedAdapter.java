package com.duckduckgo.mobile.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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
import com.duckduckgo.mobile.android.events.feedEvents.MainFeedItemSelectedEvent;
import com.duckduckgo.mobile.android.objects.FeedObject;
import com.duckduckgo.mobile.android.util.DDGControlVar;
import com.duckduckgo.mobile.android.views.DDGOverflowMenu;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

public class RecyclerRecentFeedAdapter extends RecyclerView.Adapter<RecyclerRecentFeedAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;

    private DDGOverflowMenu feedMenu = null;
    private Menu menu = null;

    private Cursor cursor;

    public ArrayList<FeedObject> data;

    private HashMap<Integer, FeedObject> filterData;

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

    public RecyclerRecentFeedAdapter(Context context, ArrayList<FeedObject> data) {
        this.context = context;
        this.data = data;
        filterData = new HashMap<Integer, FeedObject>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        menu = new MenuBuilder(context);
        ((Activity) context).getMenuInflater().inflate(R.menu.feed, menu);
        feedMenu = new DDGOverflowMenu(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View v = inflater.inflate(R.layout.item_main_feed, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final FeedObject feed = data.get(position);

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

        //Set the Category
        holder.textViewCategory.setText(feed.getCategory().toUpperCase());
        holder.frameCategoryContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DDGControlVar.targetCategory != null) {
                    DDGControlVar.targetCategory = null;
                    //resetFilterCategory();
                    cancelCategoryFilter();
                    //BusProvider.getInstance().post(new FeedCancelCategoryFilterEvent());
                } else {
                    DDGControlVar.targetCategory = feed.getCategory();
                    filterCategory(feed.getCategory());
                }
            }
        });

        if (DDGControlVar.readArticles.contains(feed.getId())) {
            holder.textViewTitle.setTextColor(Color.GRAY);
        }

        holder.frameMenuContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(holder.imageViewMenu, feed);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusProvider.getInstance().post(new MainFeedItemSelectedEvent(feed));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showMenu(holder.imageViewMenu, feed);
                return true;
            }
        });


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
            menu.findItem(R.id.action_remove_recent).setVisible(true);
            feedMenu.setFeed(feed);
            feedMenu.setMenu(menu);
            feedMenu.showFeedMenu(anchor);
        }

    }

    public void resetFilterCategory() {
        //Log.e("aaa", "inside reset filter");
        //for(HashMap.Entry<Integer, FeedObject> entry : filteredCategory.entrySet()) {
        //Log.e("aaa", "key: "+entry.getKey()+" - value: "+entry.getValue().getTitle());
        //filteredData.add(entry.getKey(), entry.getValue());
        //notifyItemInserted(entry.getKey());
        //}
//        SortedSet<Integer> keys = new TreeSet<Integer>(filteredCategory.keySet());
//        for(Integer key : keys) {
//            Log.e("aaa", "key: "+key+" - value: "+filteredCategory.get(key).getTitle());
//            if(DDGControlVar.targetSource==null || DDGControlVar.targetSource.equals(filteredCategory.get(key).getType())) {
//                int insertPosition = key < filteredData.size() ? key : filteredData.size();
//                filteredData.add(insertPosition, filteredCategory.get(key));
//                notifyItemInserted(insertPosition);
//            }
        //       }
//        filteredCategory.clear();
/*
        Log.e("aaa", "reset filter category, filtered data: "+filteredData.size()+" - data: "+data.size());
        filteredData.clear();
        filteredData.addAll(data);// = data;
        notifyDataSetChanged();*/
    }

    public void resetFilterSource() {
//        Log.e("aaa", "inside reset filter");
//        Log.e("aaa", "hash map, total size: "+filteredCategory.size());/*
//        for(HashMap.Entry<Integer, FeedObject> entry : filteredSource.entrySet()) {
//            Log.e("aaa", "should add key: "+entry.getKey()+" - value: "+entry.getValue().getTitle());
        //filteredData.add(entry.getKey(), entry.getValue());
        //notifyItemInserted(entry.getKey());
//        }*/
//        SortedSet<Integer> keys = new TreeSet<Integer>(filteredSource.keySet());
//        for(Integer key : keys) {
//            Log.e("aaa", "key: "+key+" - value: "+filteredSource.get(key).getTitle());
//            if(DDGControlVar.targetCategory==null || DDGControlVar.targetCategory.equals(filteredSource.get(key).getCategory())) {
//                int insertPosition = key < filteredData.size() ? key : filteredData.size();
//                filteredData.add(insertPosition, filteredSource.get(key));
//                notifyItemInserted(insertPosition);
//                filtered
//            }
//        }
        //filteredSource.clear();
        /*
        filteredData.clear();
        filteredData.addAll(data);// = data;
        notifyDataSetChanged();*/
    }
/*
    private void filterCategory(String category) {
        Log.e("aaa", "filter category, total size: "+data.size());
        Log.e("aaa", "hash map, total size: "+filteredCategory.size());
        int i = filteredData.size() - 1;
        for(; i>=0; i--) {
            if(!filteredData.get(i).getCategory().equals(category)) {
                filteredCategory.put(i, filteredData.get(i));
                Log.e("aaa", "should remove: "+i+" - value: "+filteredData.get(i).getTitle());
                filteredData.remove(i);
                notifyItemRemoved(i);
            }
        }
        Log.e("aaa", "data removed, new hash mp size: "+filteredCategory.size());
    }

    private void filterSource(String source) {
        int i = filteredData.size() - 1;
        for(; i>=0; i--) {
            String out = filteredData.get(i).getTitle();
            if(!filteredData.get(i).getType().equals(source)) {
                filteredSource.put(i, filteredData.get(i));
                filteredData.remove(i);
                notifyItemRemoved(i);
                out += " to delete";
            } else {
                out += " to keep";
            }
            Log.e("aaa", out);
        }
    }
*/


    private void cancelSourceFilter() {
        SortedSet<Integer> keys = new TreeSet<Integer>(filterData.keySet());
        for (Integer key : keys) {
            FeedObject feed = filterData.get(key);
            boolean removeItem = false;
            if (!data.contains(feed) && (DDGControlVar.targetCategory == null || DDGControlVar.targetCategory.equals(feed.getType()))) {
                int position = key < data.size() ? key : data.size();
                data.add(position, feed);
                notifyItemInserted(position);
                removeItem = true;
                filterData.remove(key);
            }


            //Log.e("aaa", "entry value: "+key+" removed: "+removeItem);
            //filteredSource.clear();
        }
    }

    private void filterSource(String source) {
        int i = data.size() - 1;
        int size = i;
        for (; i >= 0; i--) {
            int key = i;
            if (filterData.containsKey(i)) {
                key = size;
            }
            if (!data.get(i).getType().equals(source)) {
                if (!filterData.containsValue(data.get(i))) {
                    filterData.put(key, data.get(i));
                }
                data.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    private void cancelCategoryFilter() {
        for (HashMap.Entry<Integer, FeedObject> entry : filterData.entrySet()) {
            Log.e("aaa", "entry key: " + entry.getKey() + " - value: " + entry.getValue().getTitle().substring(0, 5));
        }
        Log.e("aaa", "---");
        SortedSet<Integer> keys = new TreeSet<Integer>(filterData.keySet());
        for (Integer key : keys) {
            FeedObject feed = filterData.get(key);
            boolean removeItem = false;
            if (!data.contains(feed) && (DDGControlVar.targetSource == null || DDGControlVar.targetSource.equals(feed.getType()))) {
                int position = key < data.size() ? key : data.size();
                data.add(position, feed);
                notifyItemInserted(position);
                removeItem = true;
                filterData.remove(key);
            }

            //Log.e("aaa", "entry value: " + key + " removed: " + removeItem);
            //filteredCategory.clear();
        }
    }

    private void filterCategory(String category) {
        int i = data.size() - 1;
        int size = i;
        for (; i >= 0; i--) {
            int key = i;
            if (!data.get(i).getCategory().equals(category)) {
                if (filterData.containsKey(i)) {
                    key = size;
                }
                if (!filterData.containsValue(data.get(i))) {
                    filterData.put(key, data.get(i));
                }
                data.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void changeData(ArrayList<FeedObject> newData) {
        this.data = newData;
//        filteredData = newData;
        notifyDataSetChanged();
        if (DDGControlVar.targetCategory != null) {
//            filterCategory(DDGControlVar.targetCategory);
        }
    }

    /*
        public void clear() {
            this.data.clear();
            notifyDataSetChanged();
        }
    */
    public void removeData() {
        //this.data.remo
    }
}
