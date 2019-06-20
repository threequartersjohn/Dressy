package com.example.dressy.activities;

import android.content.Intent;
import android.net.sip.SipSession;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dressy.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private SignInButton signInButton;
    public static int SIGN_IN_CODE = 777;
    private EditText txtEmail, txtPassword;
    private TextView txtNovaPasswordLabel;
    private FirebaseAuth auth;
    private Button btnLogin, btnRegister;
    final int minimumPasswordLength = 6;
    //comentario


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //hides title bar (?)
        getSupportActionBar().hide();

        setContentView(R.layout.activity_login);
        //connect variables with layout
        txtEmail = findViewById(R.id.txtName);
        txtPassword = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        txtNovaPasswordLabel = findViewById(R.id.txtNovaPasswordLabel);

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
                final String email = txtEmail.getText().toString();
                final String password = txtPassword.getText().toString();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Insire o teu email.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Insire a tua password..", Toast.LENGTH_LONG).show();
                    return;
                }

                //Autenticar Utilizador
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                if(password.length() < minimumPasswordLength){
                                    txtPassword.setError(getString(R.string.minimun_password));
                                }
                                else{
                                    Toast.makeText(Login.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                }
                            }
                            else{
                                Intent intent = new Intent(Login.this, Home.class);
                                intent.putExtra("user", email);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
        });
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        final GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_CODE){
            Task<GoogleSignInAccount> account = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(account);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> result) {

        try{
            GoogleSignInAccount account = result.getResult(ApiException.class);
            Intent intent = new Intent(Login.this, Home.class);
            intent.putExtra("user", account.getEmail());
            startActivity(intent);
        } catch (ApiException error) {
            Log.d("dressyLogs", "[Google.SignIn] error logging in: " + error.getStatusCode());
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("dressyLogs", connectionResult.getErrorMessage());
    }

    public void changeToPasswordRequest(View v){
        Intent intent = new Intent(Login.this, PasswordChange.class);
        startActivity(intent);
    }
}


