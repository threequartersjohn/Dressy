package com.example.dressy.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dressy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private EditText txtName, txtEmail, txtPassword, txtRepeatPassword;
    private Button btnRegister;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        //ainda n existe layout para isto
        //variaveis
        //btnReturn = (Button) findViewById(R.id.btn);
        btnRegister = findViewById(R.id.btnRegister);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtName);
        txtPassword = findViewById(R.id.txtPassword);
        txtRepeatPassword = findViewById(R.id.txtRepeatPassword);


        //onclick btnRegister
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //variaveis
                final String name = txtName.getText().toString().trim();
                final String email = txtEmail.getText().toString().trim();
                final String password = txtPassword.getText().toString().trim();
                final String repeatPassword = txtRepeatPassword.getText().toString().trim();

                //Informaçoes para user
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getApplicationContext(), "Insere o teu nome.", Toast.LENGTH_LONG).show();
                }

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Insere o teu email.", Toast.LENGTH_LONG).show();
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Insere a tua password.", Toast.LENGTH_LONG).show();
                }
                if(TextUtils.isEmpty(repeatPassword)){
                    Toast.makeText(getApplicationContext(), "Repete a tua password", Toast.LENGTH_LONG).show();
                }

                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Minimo 6 caracteres.", Toast.LENGTH_LONG).show();
                }

                if(!password.equals(repeatPassword)){
                    Toast.makeText(getApplicationContext(), "As passwords não correspondem. Insere novamente.", Toast.LENGTH_LONG).show();
                    return;
                }


                //criar o utilizador
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(Register.this, "UtilizadorCriadoComSucesso:onComplete" + task.isSuccessful(), Toast.LENGTH_LONG).show();

                        //Se falhar manda toast
                        if(!task.isSuccessful()){
                            Toast.makeText(Register.this, "A autenticação falhou!" + task.getException(), Toast.LENGTH_LONG).show();
                        }
                        else{
                            Intent intent = new Intent(Register.this, Home.class);
                            intent.putExtra("user", email);
                            startActivity(intent);
                    }
                    }
                });

            }
        });
        //hides title bar
        getSupportActionBar().hide();
    }
}
