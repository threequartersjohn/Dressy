package com.example.dressy.util;

import android.content.ClipData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dressy.R;
import com.example.dressy.classes.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

    private Integer iterationCounter = 0;
    private List<Photo> photos;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapter(Context context, List<Photo> data, ItemClickListener mClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.photos = data;
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
        public ImageView imageView;
        ItemClickListener itemClickListener;

        public ViewHolder(View itemView, ItemClickListener itemClickListener) {
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
        Picasso.get().load(photos.get(position).getPhoto_url()).resize(400, 640).centerInside().into((holder.imageView));
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return photos.get(id).toString();
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);
    }
}
