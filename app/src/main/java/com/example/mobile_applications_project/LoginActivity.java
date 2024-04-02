package com.example.mobile_applications_project;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
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

    EditText mTextName;
    EditText mTextEmail;
    EditText mTextPassword;

    Button mButtonRegister;

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

        mTextName = findViewById(R.id.textName);
        mTextEmail = findViewById(R.id.textEmail);
        mTextPassword = findViewById(R.id.textPassword);

        mButtonRegister = findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        Date mDate = new Date();
        SimpleDateFormat mFecha = new SimpleDateFormat("d ' de 'mmmm' del ' yyyy");
        final String StringFecha = mFecha.format(mDate);


        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mTextName.getText().toString();
                String email = mTextEmail.getText().toString();
                String pass = mTextPassword.getText().toString();
                //Validar el correo electronico
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mTextEmail.setError("Correo Invalido");
                    mTextEmail.setFocusable(true);
                    //Validar la contraseña
                }else if(mTextPassword.length()>6){
                    mTextPassword.setError("Su contraseña debe de tener al menos 6 caracteres");
                    mTextEmail.setFocusable(true);
                } else{
                    RegistrarJugador(email,pass);
                }


            }
        });
        mProgress = new ProgressDialog(LoginActivity.this);
        mProgress.setMessage("Registrando, porfavor espere un momento");
        mProgress.setCancelable(false);
    }

    private void RegistrarJugador(String email, String pass) {
        mProgress.show();
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    mProgress.dismiss();
                    FirebaseUser mUser = mAuth.getCurrentUser();
                    int contador = 0;
                    assert mUser != null;
                    String uidString = mUser.getUid();
                    String nombre = mTextName.getText().toString();
                    String correo = mTextEmail.getText().toString();
                    String contrasena = mTextPassword.getText().toString();

                    HashMap<Object, Object> DatosJugador = new HashMap<>();
                    DatosJugador.put("Uid", uidString);
                    DatosJugador.put("Nombre", nombre);
                    DatosJugador.put("Email", correo);
                    DatosJugador.put("Contraseña", contrasena);

                    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                    DatabaseReference mReference = mDatabase.getReference("Datos de mi juego");
                    mReference.child(uidString).setValue(DatosJugador);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    mProgress.dismiss();
                    Toast.makeText(LoginActivity.this, "Deny", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}