package com.example.mobile_applications_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    //Login
    EditText mLoginEmail;
    EditText mLoginPassword;

    Button mButtonLoginGame;
    //Login

    //Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Login
        mLoginEmail = findViewById(R.id.loginEmail);
        mLoginPassword = findViewById(R.id.loginPassword);

        mButtonLoginGame = findViewById(R.id.btnLoginGame);
        //Login

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        mProgress = new ProgressDialog(LoginActivity.this);
        mProgress.setMessage("Realizando tarea...");
        mProgress.setCancelable(false);

        mButtonLoginGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getText().toString();
                String pass = mLoginPassword.getText().toString();
                LoginUsuario(email,pass);
            }
        });

    }

    private void LoginUsuario(String email, String pass) {
        mProgress.show();
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mProgress.dismiss();
                    FirebaseUser mUser = mAuth.getCurrentUser();
                    assert mUser != null;
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    Toast.makeText(LoginActivity.this, "Success Login", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    mProgress.dismiss();
                    Toast.makeText(LoginActivity.this, "Deny Login", Toast.LENGTH_SHORT).show();
                }

            }
        }) //Falla el registro de usuario
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, " "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}