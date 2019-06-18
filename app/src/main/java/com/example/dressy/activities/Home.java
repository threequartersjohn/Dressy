package com.example.dressy.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dressy.R;
import com.example.dressy.classes.Photo;
import com.example.dressy.fragments.closetFragment;
import com.example.dressy.fragments.favoritesFragment;
import com.example.dressy.fragments.homeFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    private TextView mTextMessage;
    private Button btnLogout;
    private String TAG = "dressyLogs";
    private DatabaseReference databaseReference;
    public static List<Photo> photos = new ArrayList<>();
    private List<String[]> categories = new ArrayList<>();
    public static String user_id = "admin";


    //public void logout(){
    //}

    private void populateCategories(){

        categories.add(new String[] {"Jeans", "pants"});
        categories.add(new String[] {"Trousers", "pants"});

        categories.add(new String[] {"Footwear", "shoes"});
        categories.add(new String[] {"Shoe", "shoe"});

        categories.add(new String[] {"Sweater", "sweater"});
        categories.add(new String[] {"Long-sleeved t-shirt", "sweater"});
        categories.add(new String[] {"Blouse", "sweater"});

        categories.add(new String[] {"Jacket", "jacket"});

    }

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

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new homeFragment()).commit();

        populateCategories();


        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)  {
                Log.d(TAG, FirebaseAuth.getInstance().getCurrentUser().toString());
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Home.this, Login.class));
            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();

        //load photo references from database
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "~Reached onDataChange event.");
                for(DataSnapshot  ds: dataSnapshot.getChildren()){
                    Photo photo = new Photo();
                    photo.setPhoto_url(ds.child("photo_url").getValue().toString());
                    photo.setType(ds.child("type").getValue().toString());
                    photos.add(photo);
                }

                Log.d(TAG, String.valueOf(photos.size()) );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "[NETWORK.Database] Unexpected error occurred while fetching database content: " + databaseError.getMessage());
            }
        });

    }

    private void switchIcons(Integer ID){
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
