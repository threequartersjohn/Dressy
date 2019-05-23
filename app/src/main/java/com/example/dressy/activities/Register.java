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

    private EditText txtName, txtUsername, txtEmail, txtPassword, txtRepeatPassword;
    private Button btnRegister, btnReturn;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        //ainda n existe layout para isto
        //variaveis
        btnReturn = (Button) findViewById(R.id.btnReturn);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        txtName = (EditText) findViewById(R.id.txtName);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtEmail = (EditText) findViewById(R.id.txtName);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtRepeatPassword = (EditText) findViewById(R.id.txtRepeatPassword);


        //onclick btnReturn
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Register.this, Login.class));
            }
        });

        //onclick btnRegister
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //variaveis
                String name = txtName.getText().toString().trim();
                String username = txtUsername.getText().toString().trim();
                String email = txtEmail.getText().toString().trim();
                String password = txtPassword.getText().toString().trim();
                String repeatPassword = txtRepeatPassword.getText().toString().trim();

                //Informaçoes para user
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(getApplicationContext(), "Insere o teu nome.", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(username)){
                    Toast.makeText(getApplicationContext(), "Insere o teu username.", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplicationContext(), "Insere o teu email.", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(password)){
                    Toast.makeText(getApplicationContext(), "Insere a tua password.", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(repeatPassword)){
                    Toast.makeText(getApplicationContext(), "Repete a tua password", Toast.LENGTH_SHORT).show();
                }

                if(password.length() < 6){
                    Toast.makeText(getApplicationContext(), "Minimo 6 caracteres.", Toast.LENGTH_SHORT).show();
                }

                if(password != repeatPassword){
                    Toast.makeText(getApplicationContext(), "As passwords não correspondem. Insere novamente.", Toast.LENGTH_SHORT).show();
                }


                //criar o utilizador
                auth.createUserWithEmailAndPassword(username, password) .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(Register.this, "UtilizadorCriadoComSucesso:onComplete" + task.isSuccessful().show();
                        //Se falhar manda toast
                        if(!task.isSuccessful()){
                            Toast.makeText(Register.this, "A autenticação falhou!" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        else{
                            startActivity(new Intent(Register.this, Home.class));
                        }
                    }
                });

            }
        });
        //hides title bar
        getSupportActionBar().hide();
    }

    @Override
    protected void onResume(){
    super.onResume();
    }
}
