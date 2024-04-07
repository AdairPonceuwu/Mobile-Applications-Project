package com.example.mobile_applications_project;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
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

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class JuegoAleatorio extends AppCompatActivity {


    // Declaramos las variantes de las funcionalidades del juego inicio
    int score, numAleatorio_uno,numAleatorio_dos,resultado,vidas = 3;
    String nombre_jugador;
    String string_score;
    String string_vidas;
    String numero[] = {"cero","uno","dos","tres","cuatro","cinco","seis","siete","ocho","nueve"};

    // Declaramos las variantes de las funcionalidades del juego final

    // Musica del Juego
    MediaPlayer mediaMusica;
    MediaPlayer mediaAcierto;
    MediaPlayer mediaFallo;
    // Musica del Juego


    // Declaro xml inicio

    TextView nombreJugador;
    TextView puntajeJugador;
    EditText textoDeRespuesta;
    Button mButtonRespuesta;
    // Declaro xml final

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    //View
    LottieAnimationView imagenVidas;
    LottieAnimationView imagenDerecha;
    LottieAnimationView imagenIzquierda;
    LottieAnimationView imagenSigno;
    //View


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_juego_aleatorio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toast.makeText(this, "Ahora vamos con operaciones aleatorias", Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Datos de mi juego");

        // Musica del Juego
        mediaMusica = MediaPlayer.create(this, R.raw.musica_menu);
        mediaMusica.start();
        mediaMusica.setLooping(true);
        mediaAcierto = MediaPlayer.create(this, R.raw.aciertos_inicio);
        mediaFallo = MediaPlayer.create(this, R.raw.resorte);
        // Musica del Juego


        // instancio el xml inicio
        imagenVidas = findViewById(R.id.imageVidas);
        imagenIzquierda = findViewById(R.id.imageViewNumeroUno);
        imagenDerecha = findViewById(R.id.imageViewNumeroDos);
        imagenSigno = findViewById(R.id.imageViewSigno);

        nombreJugador = findViewById(R.id.textViewUsuario);
        puntajeJugador = findViewById(R.id.textViewPuntos);
        textoDeRespuesta = findViewById(R.id.editTextResponder);
        mButtonRespuesta = findViewById(R.id.btnRespuesta);
        // instancio xml final

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
            Toast.makeText(JuegoAleatorio.this,"",Toast.LENGTH_SHORT).show();
        }
    }
    // Obteniendo nombre del jugador final


    // Logistica del juego inicio
    public void Comparar(View view){
        String respuesta = textoDeRespuesta.getText().toString();
        if (!respuesta.equals("")){
            int respuesta_jugador = Integer.parseInt(respuesta);
            if(resultado == respuesta_jugador){
                mediaAcierto.start();
                score++;
                puntajeJugador.setText(""+score);
                textoDeRespuesta.setText("");
                BaseDeDatos();
            }else{
                mediaFallo.start();
                vidas--;
                BaseDeDatos();
                switch (vidas){
                    case 3:
                        imagenVidas.setAnimation(R.raw.tres);
                        imagenVidas.playAnimation();
                        break;

                    case 2:
                        Toast.makeText(JuegoAleatorio.this, "Te quedan dos vidas", Toast.LENGTH_SHORT).show();
                        imagenVidas.setAnimation(R.raw.dos);
                        imagenVidas.playAnimation();
                        break;

                    case 1:
                        Toast.makeText(JuegoAleatorio.this, "Te quedan una vida", Toast.LENGTH_SHORT).show();
                        imagenVidas.setAnimation(R.raw.uno);
                        imagenVidas.playAnimation();
                        break;
                    case 0:
                        Toast.makeText(JuegoAleatorio.this, "Has perdido todas tus vidas", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        mediaMusica.stop();
                        mediaMusica.release();
                        break;
                }
                textoDeRespuesta.setText("");
            }
            NumAleatorio();
        }else{
            Toast.makeText(JuegoAleatorio.this, "Debes escribir una respuesta", Toast.LENGTH_SHORT).show();
        }
    }

    public void NumAleatorio(){
        if(score <= 1000000){

            numAleatorio_uno = (int) (Math.random() * 10);
            numAleatorio_dos = (int) (Math.random() * 10);

            if(numAleatorio_uno >= 0 && numAleatorio_uno <= 3){
                resultado = numAleatorio_uno + numAleatorio_dos;
                imagenSigno.setAnimation(R.raw.suma);
                imagenSigno.playAnimation();
            } else if(numAleatorio_uno >= 4 && numAleatorio_uno <= 7) {
                resultado = numAleatorio_uno - numAleatorio_dos;
                imagenSigno.setAnimation(R.raw.resta);
                imagenSigno.playAnimation();
            } else {
                resultado = numAleatorio_uno * numAleatorio_dos;
                imagenSigno.setAnimation(R.raw.multiplicacion);
                imagenSigno.playAnimation();
            }

            if(resultado >= 0){

                for (int i = 0; i < numero.length; i++){
                    int id = getResources().getIdentifier(numero[i], "raw", getPackageName());
                    if(numAleatorio_uno == i){
                        imagenIzquierda.setAnimation(id);
                        imagenIzquierda.playAnimation();
                    }if(numAleatorio_dos == i){
                        imagenDerecha.setAnimation(id);
                        imagenDerecha.playAnimation();
                    }
                }

            } else {
                NumAleatorio();
            }

        }else {
            Intent intent = new Intent(this, MainActivity.class);

            Toast.makeText(this, "Eres un matemático maravilloso", Toast.LENGTH_SHORT).show();
            startActivity(intent);
            finish();
            mediaMusica.stop();
            mediaMusica.release();
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


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

    }


}