package com.example.dressy.activities.after_login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.example.dressy.R;

public class Closet extends AppCompatActivity {

    private Button btnHome, btnCloset, btnFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);
    }
}
