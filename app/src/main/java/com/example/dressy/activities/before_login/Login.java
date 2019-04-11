package com.example.dressy.activities.before_login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.dressy.R;

public class Login extends AppCompatActivity {

    private EditText txtUsername, txtPassword;
    private Button btnLogin, btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hides title bar
        getSupportActionBar().hide();
    }


}
