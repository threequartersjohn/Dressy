package com.example.dressy.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.dressy.R;

public class Register extends AppCompatActivity {

    private EditText txtName, txtUsername, txtPassword, txtRepeatPassword;
    private Button btnRegister, btnReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
