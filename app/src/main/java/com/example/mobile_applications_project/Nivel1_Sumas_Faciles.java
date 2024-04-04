package com.example.mobile_applications_project;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Nivel1_Sumas_Faciles extends AppCompatActivity {

    ImageView imagenVidas;
    TextView nombreJugador;
    TextView puntajeJugador;
    ImageView imagenIzquierda;
    ImageView imagenSigno;
    ImageView imagenDerecha;


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


        imagenVidas = findViewById(R.id.imageVidas);
        nombreJugador = findViewById(R.id.textViewUsuario);
        puntajeJugador = findViewById(R.id.textViewPuntos);
        imagenIzquierda = findViewById(R.id.imageViewNumeroUno);
        imagenDerecha = findViewById(R.id.imageViewNumeroDos);
        imagenSigno = findViewById(R.id.imageViewSigno);

    }
}