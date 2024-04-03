package com.example.mobile_applications_project;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

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
        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });
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

    // Aqui actualizamos los datos del usuario inicio
    private void updateInfo() {
        String [] opciones = {"Nombre"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    UpdateName("Nombre");
                }
            }
        });
        builder.create().show();
    }
    // Aqui actualizamos los datos del usuario final
    private void UpdateName(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Editar el " + key);
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(MainActivity.this);
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setPadding(10,10,10,10);
        final EditText editText = new EditText(MainActivity.this);
        editText.setHint("Ingrese su nuevo " + key);
        linearLayoutCompat.addView(editText);
        builder.setView(linearLayoutCompat);
        // Esto saldra si el usuario hace click en actualizar info
        builder.setPositiveButton("Actualizar informacion", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String value = editText.getText().toString().trim();
                HashMap<String,Object> result = new HashMap<>();
                result.put(key,value);
                databaseReference.child(mAuth.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(MainActivity.this, "El nombre se actualizó correctamente", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Ha cancelado la edicion del perfil", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
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

