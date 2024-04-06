package com.example.mobile_applications_project;

import android.annotation.SuppressLint;
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

public class RegisterActivity extends AppCompatActivity {
    //Registro
    EditText mTextName;
    EditText mTextEmail;
    EditText mTextPassword;

    TextView mTextFecha;

    Button mButtonLogin;

    Button mButtonRegister;
    //Registro

    //Firebase
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Registro
        mTextName = findViewById(R.id.textName);
        mTextEmail = findViewById(R.id.textEmail);
        mTextPassword = findViewById(R.id.textPassword);

        mTextFecha = findViewById(R.id.textFecha);

        mButtonLogin = findViewById(R.id.btnLogin);
        mButtonRegister = findViewById(R.id.btnRegister);

        //Registro

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        Date mDate = new Date();
        SimpleDateFormat mFecha = new SimpleDateFormat("d ' de 'MMMM' del ' yyyy");
        final String StringFecha = mFecha.format(mDate);
        mTextFecha.setText(StringFecha);

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();

            }
        });



        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mTextName.getText().toString();
                String email = mTextEmail.getText().toString();
                String pass = mTextPassword.getText().toString();
                String fecha = mTextFecha.getText().toString();
                //Validar el correo electronico
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    mTextEmail.setError("Correo Invalido");
                    mTextEmail.setFocusable(true);
                    //Validar la contraseña
                }else if(mTextPassword.length()<6){
                    mTextPassword.setError("Su contraseña debe de tener al menos 6 caracteres");
                    mTextEmail.setFocusable(true);
                } else{
                    ClickRegister();
                }


            }
        });
        mProgress = new ProgressDialog(RegisterActivity.this);
        mProgress.setMessage("Registrando, porfavor espere un momento");
        mProgress.setCancelable(false);
    }

    private void ClickRegister() {
        final String name = mTextName.getText().toString();
        final String email = mTextEmail.getText().toString();
        final String password = mTextPassword.getText().toString();

        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            if(password.length() >= 6){
                RegistrarJugador(name,email, password);
            }else{
                Toast.makeText(RegisterActivity.this, "La contraseña debe ser mayor o igual a 6 caracteres",Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(RegisterActivity.this, "Complete todos los campos",Toast.LENGTH_SHORT).show();
        }
    }

    private void RegistrarJugador(String name,String email, String pass) {
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
                            String fechaRegistro = mTextFecha.getText().toString();
                            String contrasena = mTextPassword.getText().toString();


                            HashMap<Object, Object> DatosJugador = new HashMap<>();
                            DatosJugador.put("Uid", uidString);
                            DatosJugador.put("Nombre", nombre);
                            DatosJugador.put("Email", correo);
                            DatosJugador.put("Fecha Registro", fechaRegistro);
                            DatosJugador.put("Contraseña", contrasena);

                            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference mReference = mDatabase.getReference("Datos de mi juego");
                            mReference.child(uidString).setValue(DatosJugador);
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            Toast.makeText(RegisterActivity.this, "Success Register", Toast.LENGTH_SHORT).show();
                            finish();
                        }else{
                            mProgress.dismiss();
                            Toast.makeText(RegisterActivity.this, "Deny Register", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                //Falla el registro de usuario
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, " "+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }


}