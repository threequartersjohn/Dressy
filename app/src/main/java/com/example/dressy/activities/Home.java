package com.example.dressy.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.dressy.R;
import com.example.dressy.fragments.closetFragment;
import com.example.dressy.fragments.favoritesFragment;
import com.example.dressy.fragments.homeFragment;

public class Home extends AppCompatActivity {

    private TextView mTextMessage;
    private static final String TAG = "Home";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navCloset:
                    selectedFragment = new homeFragment();
                    switchIcons(R.id.navCloset);
                    break;
                case R.id.navHome:
                    selectedFragment = new closetFragment();
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

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
