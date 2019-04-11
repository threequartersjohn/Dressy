package com.example.dressy.activities.before_login.password;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.dressy.R;

public class PasswordChange extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change2);

        ///hides title bar
        getSupportActionBar().hide();
    }
}
