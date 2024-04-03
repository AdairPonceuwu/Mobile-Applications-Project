package com.example.mobile_applications_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseUser mUser;
    FirebaseAuth mAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // Firebase Final
    TextView mTextName;
    TextView mTextEmail;
    TextView mTextFecha;

    Button mButtonOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Firebase Inicio
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Datos de mi juego");
        // Firebase Final

        // TEXTVIEW inicio
        mTextName=findViewById(R.id.textViewNombre);
        mTextName=findViewById(R.id.textViewCorreo);
        mTextName=findViewById(R.id.textViewFecha);
        // TEXTVIEW final


        mButtonOut = findViewById(R.id.btnCerrarSesion);

        mButtonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CerrarSesion();
            }
        });
    }

    //Cerramos la sesion del usuario
    private void CerrarSesion() {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        Toast.makeText(MainActivity.this, "Sign Out",Toast.LENGTH_SHORT).show();
    }
    //Cerramos la sesion del usuario


    //Metodo que se ejecuta cuando el juego se abre
    @Override
    protected void onStart(){
        UsuarioLogeado();
        super.onStart();
    }

    //Metodo que se ejecuta cuando el juego se abre


    //Metodo que comprueba si un usuario a iniciado sesion
    private void UsuarioLogeado() {
        if(mUser != null){
            Toast.makeText(MainActivity.this, "Online", Toast.LENGTH_SHORT).show();
        }else{
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));

        }
    }
    //Metodo que comprueba si un usuario a iniciado sesion

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }


}

