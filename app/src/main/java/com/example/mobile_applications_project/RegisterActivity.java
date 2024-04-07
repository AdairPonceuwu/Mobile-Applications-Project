package com.example.mobile_applications_project;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

    //Google
    SignInButton signInButton;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    GoogleSignInClient mGoogleSignInClient;

    private int sesion = 0;
    //Google

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);
        builder.requestIdToken(getString(R.string.default_web_client_id));
        builder.requestEmail();
        GoogleSignInOptions gso = builder
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // [END config_signin]


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Boton de Google
        signInButton = findViewById(R.id.sign_in_google);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sesion=1;
                signIn();
            }
        });
        //Boton de Google



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

    //Validar password
    public static boolean validarPassword(String password) {
        // Verificar si la contraseña tiene al menos 6 caracteres
        if (password.length() < 6) {
            return false;
        }

        // Verificar si la contraseña contiene al menos 3 letras y 3 números
        int countLetters = 0;
        int countNumbers = 0;

        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                countLetters++;
            } else if (Character.isDigit(c)) {
                countNumbers++;
            }
        }

        return countLetters >= 3 && countNumbers >= 3;
    }
    //Validar password

    private void ClickRegister() {
        final String name = mTextName.getText().toString();
        final String email = mTextEmail.getText().toString();
        final String password = mTextPassword.getText().toString();

        if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            if(validarPassword(password)){
                RegistrarJugador(name,email, password);
            }else{
                Toast.makeText(RegisterActivity.this, "La contraseña debe contener 3 numero y 3 letras",Toast.LENGTH_SHORT).show();
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
                        if(task.isSuccessful() && sesion==0){
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

    //ACCESO CON GOOGLE

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        mProgress.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mProgress.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            assert user != null;
                            String uidString = user.getUid();
                            String nombre = mAuth.getCurrentUser().getDisplayName();
                            String correo = mAuth.getCurrentUser().getEmail();
                            String fechaRegistro = mTextFecha.getText().toString();



                            HashMap<Object, Object> DatosJugador = new HashMap<>();
                            DatosJugador.put("Uid", uidString);
                            DatosJugador.put("Nombre", nombre);
                            DatosJugador.put("Email", correo);
                            DatosJugador.put("Fecha Registro", fechaRegistro);

                            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference mReference = mDatabase.getReference("Datos de mi juego");
                            mReference.child(uidString).setValue(DatosJugador);
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            Toast.makeText(RegisterActivity.this, "Success Register", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            mProgress.dismiss();
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signin]

    private void updateUI(FirebaseUser user) {

    }

    //ACCESO CON GOOGLE

}

