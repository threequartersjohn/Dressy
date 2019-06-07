package com.example.dressy.fragments;


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
import android.widget.ImageView;

import com.example.dressy.R;
import com.example.dressy.activities.Home;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static com.example.dressy.activities.Home.listOfCachedFiles;

public class homeFragment extends Fragment {

    private String TAG = "dressyLogs";
    private ArrayList<String> firstSelectedPhotos = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        Button button = getActivity().findViewById(R.id.btnMudar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNewCombination();
            }
        });
        new LoadPhotosIntoImageView().execute();
    }

    public void showNewCombination(){
        for(String photo:firstSelectedPhotos){
            new File(photo).delete();
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
    }

    private void loadBitmapsIntoImageViews(){


        ImageView imgPhoto1 = getActivity().findViewById(R.id.imgPhoto1);
        ImageView imgPhoto2 = getActivity().findViewById(R.id.imgPhoto2);
        ImageView imgPhoto3 = getActivity().findViewById(R.id.imgPhoto3);
        ImageView imgPhoto4 = getActivity().findViewById(R.id.imgPhoto4);

        Log.d(TAG, firstSelectedPhotos.toString());

        Picasso.get().load(new File(firstSelectedPhotos.get(0))).resize(400, 640).centerInside().into(imgPhoto1);
        Picasso.get().load(new File(firstSelectedPhotos.get(1))).resize(400, 640).centerInside().into(imgPhoto2);
        Picasso.get().load(new File(firstSelectedPhotos.get(2))).resize(400, 640).centerInside().into(imgPhoto3);
        Picasso.get().load(new File(firstSelectedPhotos.get(3))).resize(400, 640).centerInside().into(imgPhoto4);

        Log.d(TAG, "Photos should be loaded into view.");
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
        }
    }
}
