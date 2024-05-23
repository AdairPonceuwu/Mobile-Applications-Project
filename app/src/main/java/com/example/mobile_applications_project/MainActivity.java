package com.example.mobile_applications_project;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.window.OnBackInvokedDispatcher;
import androidx.activity.OnBackPressedCallback;

import androidx.activity.EdgeToEdge;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity {

    // Firebase
    FirebaseUser mUser;
    FirebaseAuth mAuth;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference mDataBase;
    // Firebase

    // Musica del Juego
    MediaPlayer mediaMusica;
    MediaPlayer mediaAcierto;
    MediaPlayer mediaFallo;
    // Musica del Juego

    // XML
    TextView mTextName;
    TextView mTextEmail;
    TextView mTextFecha;
    TextView mTextIdentificador;
    TextView mTextViewPuntaje;

    //Botones
    Button mButtonOut;
    Button mButtonDelete;
    Button mButtonJugar;
    Button mButtonJugarCarrera;
    Button mButtonUpdate;

    LottieAnimationView animationView;
    // XML

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
        mDataBase = firebaseDatabase.getReference("Temporada");
        // Firebase Final

        // Musica del Juego
        mediaMusica = MediaPlayer.create(this, R.raw.musica_niveles);
        // Musica del Juego

        // TEXTVIEW inicio
        mTextName=findViewById(R.id.textViewNombre);
        mTextEmail=findViewById(R.id.textViewCorreo);
        mTextFecha=findViewById(R.id.textViewFecha);
        mTextIdentificador=findViewById(R.id.textViewUid);
        mTextViewPuntaje = findViewById(R.id.textViewPuntaje);
        // TEXTVIEW final

        //View
        animationView = findViewById(R.id.animationViewTemp);
        //View

        //FirebaseAuth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Verificar si el usuario está autenticado
        if (user != null) {
            String uidString = user.getUid();

            //puntaje máximo del jugador de SQLite
            AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this, "BD", null, 1);
            SQLiteDatabase BD = adminSQLiteOpenHelper.getWritableDatabase();

            Cursor consulta = BD.rawQuery("select * from puntaje", null);

            if (consulta.moveToFirst()) {
                String temp_nombre = consulta.getString(0);
                String temp_score = consulta.getString(1);
                mTextViewPuntaje.setText("Puntaje Maximo: " + temp_score);

                // Almacenar el puntaje en Firebase
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mReference = mDatabase.getReference("Datos de mi juego");

                // Actualiza el nuevo puntaje
                mReference.child(uidString).child("Puntaje").setValue(temp_score)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Datos actualizados con cada juego
                                Toast.makeText(MainActivity.this, "Puntaje actualizado", Toast.LENGTH_SHORT).show();
                            }
                        });


                BD.close();
            } else {
                BD.close();
            }
        } else {
            // No hay usuario autenticado
            Toast.makeText(MainActivity.this, "No se encontró ningún usuario autenticado", Toast.LENGTH_SHORT).show();
        }






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
                Toast.makeText(MainActivity.this,"Sumas faciles",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, Nivel1_Sumas_Faciles.class);
                startActivity(intent);
                finish();
                mediaMusica.stop();
                mediaMusica.release();
            }
        });

        mButtonJugarCarrera =findViewById(R.id.btnJugarCarrera);
        mButtonJugarCarrera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"Juego con operaciones aleatorias",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, JuegoAleatorio.class);
                startActivity(intent);
                finish();
                mediaMusica.stop();
                mediaMusica.release();
            }
        });

        //Instanciamos el click del boton para eliminar el usuario
        mButtonDelete = findViewById(R.id.btnEliminacion);

        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EliminarUser();
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
        Temporada();
    }

    private void Temporada() {
        mDataBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("primavera")){
                    animationView.setAnimation(R.raw.animation_spring);
                    animationView.playAnimation();
                } else if (snapshot.hasChild("verano")) {
                    animationView.setAnimation(R.raw.animation_summer);
                    animationView.playAnimation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Aqui actualizamos los datos del usuario inicio
    private void updateInfo() {
        String[] opciones = {"Nombre", "Contraseña"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    UpdateName("Nombre");
                }  else if (i == 1) {
                    updatePassword("Contraseña");
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

    private void updatePassword(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Editar la " + key);
        LinearLayoutCompat linearLayoutCompat = new LinearLayoutCompat(MainActivity.this);
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setPadding(10,10,10,10);
        final EditText editText = new EditText(MainActivity.this);
        editText.setHint("Ingrese su nueva " + key);
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
                        Toast.makeText(MainActivity.this, "La contraseña se actualizó correctamente", Toast.LENGTH_SHORT).show();
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



    //Eliminamos el usuario de la BD de Firebase
    private void EliminarUser() {
        // Detener la musica
        mediaMusica.stop();
        mediaMusica.release();

        // Obtener una referencia al usuario actualmente autenticado
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // Verificar si el usuario está autenticado
        if (user != null) {
            // Eliminar el usuario de Firebase Authentication
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Eliminar los datos del usuario de la base de datos de Firebase
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Datos de mi juego").child(user.getUid());
                                userRef.removeValue();

                                // Sign out del usuario
                                FirebaseAuth.getInstance().signOut();

                                // Redirigir a la pantalla del registro, ya que fue eliminado el usuario
                                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                                Toast.makeText(MainActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                            } else {
                                // Si no se pudo eliminar el usuario, mostrar un mensaje de error
                                Toast.makeText(MainActivity.this, "Falla al eliminar", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            // Si el usuario no está autenticado, simplemente redirige a la pantalla de registro
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
        }
    }


    //Cerramos la sesion del usuario inicio
    private void CerrarSesion() {
        mediaMusica.stop();
        mediaMusica.release();
        AdminSQLiteOpenHelper adminSQLiteOpenHelper = new AdminSQLiteOpenHelper(this,"BD", null,1);
        SQLiteDatabase BD = adminSQLiteOpenHelper.getWritableDatabase();
        BD.delete("puntaje", null, null);
        BD.close();
        mAuth.signOut();
        FirebaseAuth.getInstance().signOut();
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
            mediaMusica.start();
            mediaMusica.setLooping(true);
        }else{
            startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            finish();

        }
    }
    //Metodo que comprueba si un usuario a iniciado sesion

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        mediaMusica.stop();
        mediaMusica.release();
        super.onBackPressed();
    }


}



