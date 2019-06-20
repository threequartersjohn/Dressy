package com.example.dressy.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.dressy.R;
import com.example.dressy.activities.Login;
import com.example.dressy.util.MyFavoritesRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;

import static com.example.dressy.activities.Home.favorites;

public class favoritesFragment extends Fragment implements MyFavoritesRecyclerAdapter.ItemClickListener{

    private RecyclerView recyclerView;
    private MyFavoritesRecyclerAdapter adapter;
    private View rootView;
    private final String TAG = "dressyLogs";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        ImageView logout = rootView.findViewById(R.id.logout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), Login.class));
            }
        });
        return rootView;
    }

    private void populateWithFavorites(){
        recyclerView = getActivity().findViewById(R.id.rvFavorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new MyFavoritesRecyclerAdapter(getActivity(), favorites, this);
        recyclerView.setAdapter(adapter);
    }

    public void onStart() {
        super.onStart();
        populateWithFavorites();
    }

    @Override
    public void onItemClick(int position) {
        Log.d(TAG, "clicou");
    }
}
