package com.example.mobile_applications_project;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

public class Nivel5_Multiplicaciones extends AppCompatActivity {
    // Declaramos las variantes de las funcionalidades del juego inicio
    int score, numAleatorio_uno,numAleatorio_dos,resultado,vidas = 3;
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
        setContentView(R.layout.activity_nivel5_multiplicaciones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toast.makeText(this, "Ahora vamos con multiplicaciones", Toast.LENGTH_SHORT).show();

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
        // pasar los strings extra de las vidas y el puntaje inicio/////////////////////
        // puntaje inicio
        string_score = getIntent().getStringExtra("score");
        score = Integer.parseInt(string_score);
        puntajeJugador.setText(" " + score);
        // puntaje final

        // vidas inicio
        string_vidas = getIntent().getStringExtra("vidas");
        vidas = Integer.parseInt(string_vidas);
        if(vidas == 3){
            imagenVidas.setImageResource(R.drawable.vida3);
        }if(vidas == 2){
            imagenVidas.setImageResource(R.drawable.vida2);
        }if(vidas == 1){
            imagenVidas.setImageResource(R.drawable.vida1);
        }
        //vidas final


        // pasar los strings extra de las vidas y el puntaje final//////////////////////


        informacionDelJugador();
        NumAleatorio();


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
            Toast.makeText(Nivel5_Multiplicaciones.this,"",Toast.LENGTH_SHORT).show();
        }
    }
    // Obteniendo nombre del jugador final


    // Logistica del juego inicio
    public void Comparar(View view){
        String respuesta = textoDeRespuesta.getText().toString();
        if (!respuesta.equals("")){
            int respuesta_jugador = Integer.parseInt(respuesta);
            if(resultado == respuesta_jugador){
                score++;
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
                        Toast.makeText(Nivel5_Multiplicaciones.this, "Te quedan dos vidas", Toast.LENGTH_SHORT).show();
                        imagenVidas.setImageResource(R.drawable.vida2);
                        break;

                    case 1:
                        Toast.makeText(Nivel5_Multiplicaciones.this, "Te quedan una vida", Toast.LENGTH_SHORT).show();
                        imagenVidas.setImageResource(R.drawable.vida1);
                        break;
                    case 0:
                        Toast.makeText(Nivel5_Multiplicaciones.this, "Has perdido todas tus vidas", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                }
                textoDeRespuesta.setText("");
            }
            NumAleatorio();
        }else{
            Toast.makeText(Nivel5_Multiplicaciones.this, "Debes escribir una respuesta", Toast.LENGTH_SHORT).show();
        }
    }

    private void NumAleatorio() {
        if(score <= 49){
            numAleatorio_uno = (int) (Math.random() * 10);
            numAleatorio_dos =  (int) (Math.random() * 10);
            resultado = numAleatorio_uno * numAleatorio_dos;

            for(int i =0;i< numero.length;i++){
                int id = getResources().getIdentifier(numero[i],"drawable",getPackageName());
                if(numAleatorio_uno == i){
                    imagenIzquierda.setImageResource(id);
                }if(numAleatorio_dos == i){
                    imagenDerecha.setImageResource(id);
                }
            }


        }
        else{
            Intent intent = new Intent(this, Nivel5_Multiplicaciones.class);
            string_score = String.valueOf(score);
            string_vidas = String.valueOf(vidas);
            intent.putExtra("score",string_score);
            intent.putExtra("vidas",string_vidas);

            startActivity(intent);
            finish();
        }
    }

    private void BaseDeDatos() {
        AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this,"BD", null,1);
        SQLiteDatabase BD = adminSQLiteOpenHelper.getWritableDatabase();
        Cursor consulta = BD.rawQuery("select * from puntaje where score = (select max(score) from puntaje)",null);
        if(consulta.moveToFirst()){
            String temp_nombre = consulta.getString(0);
            String temp_score = consulta.getString(1);

            int bestScore = Integer.parseInt(temp_score);

            if(score> bestScore){
                ContentValues modification = new ContentValues();
                modification.put("nombre",nombre_jugador);
                modification.put("score", score);
                BD.update("puntaje",modification, "score=" + bestScore,null);
            }

            BD.close();

        }else{
            ContentValues insertar = new ContentValues();

            insertar.put("nombre",nombre_jugador);
            insertar.put("score",score);
            BD.insert("puntaje",null,insertar);
            BD.close();
        }
    }
    // Logistica del juego final


    @Override
    public void onBackPressed() {

    }
}