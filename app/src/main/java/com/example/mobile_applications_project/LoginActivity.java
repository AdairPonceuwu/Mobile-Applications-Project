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
                // LoginUsuario(email,pass);
                login();
            }
        });

    }

    private void login() {
        String email = mLoginEmail.getText().toString();
        String password = mLoginPassword.getText().toString();

        if(!email.isEmpty() && !password.isEmpty()){
            if(password.length() >= 6){
                mProgress.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    mProgress.dismiss();
                                    FirebaseUser mUser = mAuth.getCurrentUser();
                                    assert mUser != null;
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    Toast.makeText(LoginActivity.this, "Has ingresado correctamente", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    mProgress.dismiss();
                                    Toast.makeText(LoginActivity.this, "No se pudo validar al usuario", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }) //Falla el registro de usuario
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(LoginActivity.this, " "+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
            }else{
                Toast.makeText(LoginActivity.this, " La contraseña y el email son obligatorios",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(LoginActivity.this, " La contraseña y el email son obligatorios",Toast.LENGTH_SHORT).show();
        }
    }

    private void LoginUsuario(String email, String pass) {
        mProgress.show();
        if (email.isEmpty() && pass.isEmpty()){
            Toast.makeText(LoginActivity.this, "Llena los datos para iniciar sesion", Toast.LENGTH_SHORT).show();
            mProgress.dismiss();
        }else{
            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mProgress.dismiss();
                                FirebaseUser mUser = mAuth.getCurrentUser();
                                assert mUser != null;
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                Toast.makeText(LoginActivity.this, "Has ingresado correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            }else{
                                mProgress.dismiss();
                                Toast.makeText(LoginActivity.this, "No se pudo validar al usuario", Toast.LENGTH_SHORT).show();
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
}