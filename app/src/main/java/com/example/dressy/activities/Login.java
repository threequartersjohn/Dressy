package com.example.dressy.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dressy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private EditText txtEmail, txtPassword;
    private TextView txtNovaPasswordLabel;
    private FirebaseAuth auth;
    private Button btnLogin, btnRegister;
    final String dressyLogTag = "dressylogs";
    final int minimumPasswordLength = 6;
    //comentario


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, Home.class));
            finish();
        }

        //hides title bar (?)
        getSupportActionBar().hide();


        setContentView(R.layout.activity_login);
        //connect variables with layout
        txtEmail = (EditText) findViewById(R.id.txtName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        txtNovaPasswordLabel = (TextView) findViewById(R.id.txtNovaPasswordLabel);

        auth = FirebaseAuth.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
            }
        });

        txtNovaPasswordLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, PasswordChange.class));
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Insire o teu email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Insire a tua password..", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Autenticar Utilizador
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                if(password.length() < 6){
                                    txtPassword.setError(getString(R.string.minimun_password));
                                }
                                else{
                                    Toast.makeText(Login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Intent intent = new Intent(Login.this, Home.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
        });
    }
}

    /* public void login(View view){
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
*/
