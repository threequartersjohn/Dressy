package com.example.dressy.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dressy.R;

public class Login extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private Button btnLogin, btnRegister;
    final String dressyLogTag = "dressylogs";
    final int minimumPasswordLength  = 6;
    //comentario


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hides title bar
        getSupportActionBar().hide();


        //connect variables with layout
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
    }

    public void login(View view){
        String password = txtPassword.getText().toString();
        String email = txtEmail.getText().toString();

        if(validateLogin(email, password)) {

            //authenticate to firebase


            //move to main screen
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);

        }

        else {
            clearLoginFields();
            badLoginWarning();
        }
    }

    private void badLoginWarning(){
        Toast.makeText(this, "O seu Login é inválido!", Toast.LENGTH_LONG).show();
    }

    private void clearLoginFields(){
        txtPassword.setText("");
        txtEmail.setText("");
    }

    private boolean validateLogin(String email, String password){

        Boolean isValid = true;

        if (email.isEmpty()){
            Log.d(dressyLogTag, "[LOGIN.VALIDATION] Email field is empty.");
            isValid = false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Log.d(dressyLogTag, "[LOGIN.VALIDATION] Email is not a valid email.");
            isValid = false;
        }

        if (password.isEmpty()){
            Log.d(dressyLogTag, "[LOGIN.VALIDATION] Password field is empty.");
            isValid = false;
        }

        if (password.length() < minimumPasswordLength){
            Log.d(dressyLogTag, "[LOGIN.VALIDATION] Password is smaller than minimum length.");
            isValid = false;
        }

        //else
        return isValid;
    }


}
