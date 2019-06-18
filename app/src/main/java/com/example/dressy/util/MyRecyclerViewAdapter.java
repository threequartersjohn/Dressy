package com.example.dressy.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.example.dressy.R;
import com.example.dressy.classes.Photo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> implements Filterable {

    private Integer iterationCounter = 0;
    private final List<Photo> photos;
    public final static ArrayList<Photo> photosFiltered = new ArrayList<>();
    private final LayoutInflater mInflater;
    private final ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<Photo> data, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.photos = data;
        photosFiltered.addAll(photos);
        this.mClickListener = mClickListener;
    }

    // inflates the cell layout from xml when needed
    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.closet_recycler_item , parent, false);
        return new ViewHolder(view, mClickListener);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return photos.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imageView;
        final ItemClickListener itemClickListener;

        private ViewHolder(View itemView, ItemClickListener itemClickListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.closet_photo);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }

    // binds the data to the TextView in each cell
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        iterationCounter++;
        Log.d("dressylogs", "RecyclerView on iteration: " + iterationCounter);
        try {
            Picasso.get().load(photosFiltered.get(position).getPhoto_url()).resize(400, 640).centerInside().into((holder.imageView));
        } catch (Exception error) {
            Log.d("dressyLogs", "Failed to fill position " + position);
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return photosFiltered.get(id).toString();
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private final Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Photo> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0){
                filteredList.addAll(photos);
            } else {
                String filterType = constraint.toString();
                Log.d("dressyLogs", "type to filer: " + constraint);

                for(Photo photo: photos){
                    String type = photo.getType();
                    if (type.equals(filterType)) {
                        filteredList.add(photo);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            Log.d("dressyLogs", "publishing results to recyclerview");

            photosFiltered.clear();
            photosFiltered.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
