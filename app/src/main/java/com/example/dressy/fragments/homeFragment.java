package com.example.dressy.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.dressy.R;
import com.example.dressy.activities.Home;
import com.example.dressy.activities.Login;
import com.github.nisrulz.sensey.Sensey;
import com.github.nisrulz.sensey.ShakeDetector;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.dressy.activities.Home.listOfCachedFiles;
import static com.example.dressy.activities.Home.tasks;
import static com.example.dressy.activities.Home.user_id;

public class homeFragment extends Fragment {

    private final String TAG = "dressyLogs";
    private ArrayList<ArrayList<String>> firstSelectedPhotos = new ArrayList<>();
    private ShakeDetector.ShakeListener shakeListener;
    private View rootView;
    private Boolean imagesLoaded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Sensey.getInstance().init(getActivity());


        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStop() {
        super.onStop();
        Sensey.getInstance().stopShakeDetection(shakeListener);
    }



    @Override
    public void onStart() {
        super.onStart();

        shakeListener = new ShakeDetector.ShakeListener() {
            @Override
            public void onShakeDetected() {
                Log.d(TAG, String.valueOf(imagesLoaded));
                Log.d(TAG, "shake detected!!!!!!!!");
                Log.d(TAG, "size: " + listOfCachedFiles.size());

                if (listOfCachedFiles.size()>1){
                    showNewCombination();
                };
            }

            @Override
            public void onShakeStopped() { }
        };
        Sensey.getInstance().startShakeDetection(shakeListener);
        tasks.add(new LoadPhotosIntoImageView().execute()) ;
    }

    private void showNewCombination(){
        for(ArrayList<String>photo:firstSelectedPhotos){
            new File(photo.get(0)).delete();
        }

        listOfCachedFiles.remove(0);
        try{
            ((Home)getActivity()).loadFilesIntoCache();
        } catch(IOException error){
            Log.d(TAG, "[Storage] Unexpected error attempting loading photos to local files: " + error.getMessage());
        }

        if (!listOfCachedFiles.isEmpty()) {
            firstSelectedPhotos = listOfCachedFiles.get(0);
        }
        loadBitmapsIntoImageViews();


        ImageView favoriteButton = getActivity().findViewById(R.id.favoriteButton);
        favoriteButton.setImageResource(R.drawable.favorite_icon_empty);

    }

    private void loadBitmapsIntoImageViews(){

        int size = listOfCachedFiles.size();

        Log.d(TAG, "size: " + listOfCachedFiles.size());

        if(size >1) {
            imagesLoaded = true;
        }

        else {
            imagesLoaded = false;
        }

        ImageView imgPhoto1 = getActivity().findViewById(R.id.imgPhoto1);
        ImageView imgPhoto2 = getActivity().findViewById(R.id.imgPhoto2);
        ImageView imgPhoto3 = getActivity().findViewById(R.id.imgPhoto3);
        ImageView imgPhoto4 = getActivity().findViewById(R.id.imgPhoto4);

        Log.d(TAG, firstSelectedPhotos.toString());

        Picasso.get().load(new File(firstSelectedPhotos.get(0).get(0))).resize(400, 640).centerInside().into(imgPhoto1);
        Picasso.get().load(new File(firstSelectedPhotos.get(1).get(0))).resize(400, 640).centerInside().into(imgPhoto2);
        Picasso.get().load(new File(firstSelectedPhotos.get(2).get(0))).resize(400, 640).centerInside().into(imgPhoto3);
        Picasso.get().load(new File(firstSelectedPhotos.get(3).get(0))).resize(400, 640).centerInside().into(imgPhoto4);

        Log.d(TAG, "Photos should be loaded into view.");
    }

    private void saveFavoriteCombination(){
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        ArrayList<String> collection = new ArrayList<>();

        for (int i = 0; i<4; i++){
            collection.add(firstSelectedPhotos.get(i).get(1));
        }

        database.child(user_id).child("favorites").push().setValue(collection,  new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                    Toast.makeText(getActivity(), "Combinação guardada como favorita!", Toast.LENGTH_LONG ).show();
                }

            }
        });

        ImageView favoriteButton = getActivity().findViewById(R.id.favoriteButton);
        favoriteButton.setImageResource(R.drawable.favorite_icon_filled);

    }

    private class LoadPhotosIntoImageView extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... urls) {
            while(listOfCachedFiles.size()== 0){
                Log.d(TAG, listOfCachedFiles.toString());
                Log.d(TAG, "Files not loaded, waiting 1 second");
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch(InterruptedException error) {
                    Log.d(TAG, "[Waiting] Unexpected interruption while waiting: " + error.getMessage());
                }
            }
            firstSelectedPhotos = listOfCachedFiles.get(0);
            return null;
        }

        @Override
        protected void onPostExecute(String aLong) {
            loadBitmapsIntoImageViews();
            ImageView favoriteButton = getActivity().findViewById(R.id.favoriteButton);
            favoriteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    saveFavoriteCombination();
                }
            });
        }
    }
}
