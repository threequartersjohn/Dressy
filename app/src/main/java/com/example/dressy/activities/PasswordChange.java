package com.example.dressy.activities;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordChange extends AppCompatActivity {

    private EditText txtName;
    private Button btnSubmit;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        txtName = findViewById(R.id.txtName);
        btnSubmit = findViewById(R.id.btnSubmit);
        //btnBack = (Button) findViewById(R.id.btnBack);

        auth = FirebaseAuth.getInstance();

        //btnBack.setOnClickListener(new View.OnClickListener() {
            //@Override
            //public void onClick(View v) {
                //finish();
            //}
        //});

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtName.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(getApplication(), "Insira o seu email registado", Toast.LENGTH_LONG).show();
                    return;
                }

                auth.sendPasswordResetEmail(email) .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(PasswordChange.this, "Enviamos email para redefinir a sua nova password", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(PasswordChange.this, "Email não registado", Toast.LENGTH_LONG).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PasswordChange.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        ///hides title bar
        getSupportActionBar().hide();
    }
}
