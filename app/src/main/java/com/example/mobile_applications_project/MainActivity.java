package com.example.mobile_applications_project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    FirebaseUser mUser;
    FirebaseAuth mAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    // Firebase Final
    TextView mTextName;
    TextView mTextEmail;
    TextView mTextFecha;
    TextView mTextIdentificador;

    Button mButtonOut;
    Button mButtonJugar;
    Button mButtonUpdate;


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
        mTextEmail=findViewById(R.id.textViewCorreo);
        mTextFecha=findViewById(R.id.textViewFecha);
        mTextIdentificador=findViewById(R.id.textViewUid);
        // TEXTVIEW final
        mButtonUpdate=findViewById(R.id.btnEditar);
        mButtonJugar =findViewById(R.id.btnJugar);
        mButtonJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Boton Jugar",Toast.LENGTH_SHORT).show();
            }
        });



        mButtonOut = findViewById(R.id.btnCerrarSesion);

        mButtonOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CerrarSesion();
            }
        });

        UserInfo();
    }

    // Vemos los datos del usuario Logueado inicio
    private void UserInfo(){
        if (mUser != null){
            Query query = databaseReference.orderByChild("Email").equalTo(mUser.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String uidUsuario = "" + ds.child("Uid").getValue();
                        String nameUsuario = "" + ds.child("Nombre").getValue();
                        String emailUsuario = "" + ds.child("Email").getValue();
                        String fechaUsuario = "" + ds.child("Fecha Registro").getValue();

                        mTextIdentificador.setText(uidUsuario);
                        mTextName.setText(nameUsuario);
                        mTextEmail.setText(emailUsuario);
                        mTextFecha.setText(fechaUsuario);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Toast.makeText(MainActivity.this,"Bienvenid@ a nuestro juego",Toast.LENGTH_SHORT).show();
        }

    }



    // Vemos los datos del usuario Logueado final



    //Cerramos la sesion del usuario inicio
    private void CerrarSesion() {
        mAuth.signOut();
        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        Toast.makeText(MainActivity.this, "Sign Out",Toast.LENGTH_SHORT).show();
    }
    //Cerramos la sesion del usuario final


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

