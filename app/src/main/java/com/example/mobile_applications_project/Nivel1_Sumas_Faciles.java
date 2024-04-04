package com.example.mobile_applications_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class Nivel1_Sumas_Faciles extends AppCompatActivity {

    // Declaramos las variantes de las funcionalidades del juego inicio
    int score, numAleatorio_uno,numAleatorio_dos,vidas = 3;
    String nombre_jugador;
    String string_score;
    String string_vidas;
    String numero[] = {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};

    // Declaramos las variantes de las funcionalidades del juego final

    // Declaro xml inicio
    ImageView imagenVidas;
    TextView nombreJugador;
    TextView puntajeJugador;
    ImageView imagenIzquierda;
    ImageView imagenSigno;
    ImageView imagenDerecha;
    EditText textoDeRespuesta;
    Button mButtonRespuesta;
    // Declaro xml final

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nivel1_sumas_faciles);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Datos de mi juego");


        // instancio el xml inicio
        imagenVidas = findViewById(R.id.imageVidas);
        nombreJugador = findViewById(R.id.textViewUsuario);
        puntajeJugador = findViewById(R.id.textViewPuntos);
        imagenIzquierda = findViewById(R.id.imageViewNumeroUno);
        imagenDerecha = findViewById(R.id.imageViewNumeroDos);
        imagenSigno = findViewById(R.id.imageViewSigno);
        textoDeRespuesta = findViewById(R.id.editTextResponder);
        mButtonRespuesta = findViewById(R.id.btnRespuesta);
        // instancio xml final
        mButtonRespuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Nivel1_Sumas_Faciles.this,"Botón funcionando",Toast.LENGTH_SHORT).show();
            }
        });

        informacionDelJugador();

    }

    // Obteniendo nombre del jugador inicio
    private void informacionDelJugador(){
        if (mUser != null){
            Query query = databaseReference.orderByChild("Email").equalTo(mUser.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                        String uidUsuario = "" + ds.child("Uid").getValue();
                        String nameUsuario = "" + ds.child("Nombre").getValue();
                        nombreJugador.setText(nameUsuario);

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else{
            Toast.makeText(Nivel1_Sumas_Faciles.this,"",Toast.LENGTH_SHORT).show();
        }
    }
    // Obteniendo nombre del jugador final


    // Logistica del juego inicio
    public void Comparar(){
        String respuesta = textoDeRespuesta.getText().toString();
        if (!respuesta.equals("")){
            int respuesta_jugador = Integer.parseInt(respuesta);
            if(resultado == respuesta_jugador){
                scrore++;
                puntajeJugador.setText(""+score);
                textoDeRespuesta.setText("");
                BaseDeDatos();
            }else{
                vidas--;
                BaseDeDatos();
                switch (vidas){
                    case 3:
                        imagenVidas.setImageResource(R.drawable.vida3);
                       break;

                    case 2:
                        Toast.makeText(Nivel1_Sumas_Faciles.this, "Te quedan dos vidas", Toast.LENGTH_SHORT).show();
                        imagenVidas.setImageResource(R.drawable.vida2);
                        break;

                    case 1:
                        Toast.makeText(Nivel1_Sumas_Faciles.this, "Te quedan una vida", Toast.LENGTH_SHORT).show();
                        imagenVidas.setImageResource(R.drawable.vida1);
                        break;
                    case 0:
                        Toast.makeText(Nivel1_Sumas_Faciles.this, "Has perdido todas tus vidas", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                textoDeRespuesta.setText("");
            }
            NumAleatorio();
        }else{
            Toast.makeText(Nivel1_Sumas_Faciles.this, "Debes escribir una respuesta", Toast.LENGTH_SHORT).show();
        }
    }

    private void NumAleatorio() {
    }

    private void BaseDeDatos() {
    }
    // Logistica del juego final


}