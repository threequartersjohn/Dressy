package com.example.dressy.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.dressy.R;
import com.example.dressy.classes.Photo;
import com.example.dressy.fragments.closetFragment;
import com.example.dressy.fragments.favoritesFragment;
import com.example.dressy.fragments.homeFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Home extends AppCompatActivity {

    public static List<Photo> photos = new ArrayList<>();
    public static String user_id = "admin";

    private ArrayList<ArrayList<String>> filesByCategory = new ArrayList<>();
    private ArrayList<String> pants = new ArrayList<>();
    private ArrayList<String> jacket = new ArrayList<>();
    private ArrayList<String> shoes = new ArrayList<>();
    private ArrayList<String> sweater = new ArrayList<>();

    private ArrayList<String[]> listOfCachedFiles;

    private String TAG = "dressyLogs";
    private DatabaseReference databaseReference;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navCloset:
                    selectedFragment = new closetFragment();
                    switchIcons(R.id.navCloset);
                    break;
                case R.id.navHome:
                    selectedFragment = new homeFragment();
                    switchIcons(R.id.navHome);
                    break;
                case R.id.navFavorites:
                    selectedFragment = new favoritesFragment();
                    switchIcons(R.id.navFavorites);
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //hides title bar
        getSupportActionBar().hide();

        databaseReference = FirebaseDatabase.getInstance().getReference().child(user_id);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new homeFragment()).commit();


    }


    private ArrayList<String> selectRandomPhotos(){

        ArrayList<String> tempSelectedPhotos = new ArrayList();
        Random random = new Random();

        for(ArrayList<String> list: filesByCategory){
            Integer rnd = random.nextInt(list.size()-1);
            tempSelectedPhotos.add(list.get(rnd));
        }

        return tempSelectedPhotos;

    }

    private void loadFilesIntoCache(ArrayList<String> selection) {

        while(listOfCachedFiles.size()<=3){

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        loadDataFromDatabase();

    }

    public void loadDataFromDatabase(){
        //load photo references from database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Photo> tempPhotoHolder = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Photo photo = new Photo();

                    photo.setPhoto_url(ds.child("photo_url").getValue().toString());
                    photo.setType(ds.child("type").getValue().toString());

                    tempPhotoHolder.add(photo);
                }

                photos = new ArrayList<>(tempPhotoHolder);
                populateCategoryList();
                loadFilesIntoCache(selectRandomPhotos());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "[NETWORK.Database] Unexpected error occurred while fetching database content: " + databaseError.getMessage());
            }
        });
    }

    private void populateCategoryList(){

        String type;

        for(Photo photo:photos){
            type = photo.getType();

            switch (type){
                case "shoes":
                    shoes.add(photo.getPhoto_url());
                    break;
                case "pants":
                    pants.add(photo.getPhoto_url());
                    break;
                case "jacket":
                    jacket.add(photo.getPhoto_url());
                    break;
                case "sweater":
                    sweater.add(photo.getPhoto_url());
                    break;
            }
        }

        filesByCategory.add(jacket);
        filesByCategory.add(sweater);
        filesByCategory.add(pants);
        filesByCategory.add(shoes);

    }

    private void switchIcons(Integer ID) {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        Menu menu = navigation.getMenu();
        MenuItem home = menu.findItem(R.id.navHome);
        MenuItem favorites = menu.findItem(R.id.navFavorites);
        MenuItem closet = menu.findItem(R.id.navCloset);

        switch (ID) {
            case R.id.navHome:
                home.setIcon(R.drawable.baseline_home_black_18dp);
                favorites.setIcon(R.drawable.outline_favorite_border_black_18dp);
                closet.setIcon(R.drawable.outline_photo_library_black_18dp);
                break;
            case R.id.navFavorites:
                home.setIcon(R.drawable.outline_home_black_18dp);
                favorites.setIcon(R.drawable.baseline_favorite_black_18dp);
                closet.setIcon(R.drawable.outline_photo_library_black_18dp);
                break;
            case R.id.navCloset:
                home.setIcon(R.drawable.outline_home_black_18dp);
                favorites.setIcon(R.drawable.outline_favorite_border_black_18dp);
                closet.setIcon(R.drawable.baseline_photo_library_black_18dp);
                break;

        }
    }

}

