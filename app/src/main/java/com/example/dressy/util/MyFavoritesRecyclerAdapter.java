package com.example.dressy.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dressy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MyFavoritesRecyclerAdapter extends RecyclerView.Adapter<MyFavoritesRecyclerAdapter.ViewHolder> {

    private List<ArrayList<String>> favorites = new ArrayList<>();
    private LayoutInflater layoutInflater;
    private ItemClickListener mClickListener;

    public MyFavoritesRecyclerAdapter(Context context, ArrayList<ArrayList<String>> data, ItemClickListener mClickListener){
        this.layoutInflater = LayoutInflater.from(context);
        this.favorites = data;
        this.mClickListener = mClickListener;
    }

    @NonNull
    @Override
    public MyFavoritesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = layoutInflater.inflate(R.layout.favorite_recycler_item, viewGroup, false);
        return new ViewHolder(view, mClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Picasso.get().load(favorites.get(position).get(0)).resize(400, 640).centerInside().into((holder.imageView1));
            Picasso.get().load(favorites.get(position).get(1)).resize(400, 640).centerInside().into((holder.imageView2));
            Picasso.get().load(favorites.get(position).get(2)).resize(400, 640).centerInside().into((holder.imageView3));
            Picasso.get().load(favorites.get(position).get(3)).resize(400, 640).centerInside().into((holder.imageView4));
            Log.d("dressyLogs", "Attempted to fill with " + favorites);
        } catch (Exception error) {
            Log.d("dressyLogs", "error: " + error.getMessage());
            Log.d("dressyLogs", "Failed to fill position " + position);
        }
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView imageView1,imageView2, imageView3, imageView4;
        MyFavoritesRecyclerAdapter.ItemClickListener itemClickListener;

        public ViewHolder(View itemView, MyFavoritesRecyclerAdapter.ItemClickListener itemClickListener) {
            super(itemView);
            imageView1 = itemView.findViewById(R.id.favorite_1);
            imageView2 = itemView.findViewById(R.id.favorite_2);
            imageView3 = itemView.findViewById(R.id.favorite_3);
            imageView4 = itemView.findViewById(R.id.favorite_4);
            this.itemClickListener = itemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemClickListener.onItemClick(getAdapterPosition());
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }
}
