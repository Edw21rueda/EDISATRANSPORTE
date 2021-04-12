package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.edisatransporte.Index.MenufragActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    //ANIMACION
    Animation sideAnim,buttomAnim;
    private static int SPLASH_TIMER = 1000;

    private EditText nEmail, nPassword;
    private Button nLogin, nRegistration, nButtonResetPassword, nBtnDemo;
    ImageView logo;

    private FirebaseAuth nAuth;
    private  FirebaseAuth.AuthStateListener firebaseAuthListener;
    FirebaseDatabase mibase;
    DatabaseReference mireferencia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mibase = FirebaseDatabase.getInstance();
        mireferencia = mibase.getReference();


        logo = (ImageView)findViewById(R.id.logoi);
        nEmail = (EditText) findViewById(R.id.email);
        nPassword = (EditText) findViewById(R.id.password);
        nLogin = (Button) findViewById(R.id.login);
        nButtonResetPassword = (Button) findViewById(R.id.btnChangePassword);
        nAuth =  FirebaseAuth.getInstance();
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user!=null){
                    Intent intent = new Intent(LoginActivity.this, MenufragActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this).toBundle());
                    finish();
                    return;
                }
            }
        };
        //ANIMATIONS
        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_anim);
        buttomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        logo.setAnimation(sideAnim);
        nLogin.setAnimation(buttomAnim);
        nButtonResetPassword.setAnimation(buttomAnim);
        nEmail.setAnimation(buttomAnim);
        nPassword.setAnimation(buttomAnim);

        nLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nEmail.getText().toString().isEmpty() || nPassword.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Ingresa tus datos", Toast.LENGTH_SHORT).show();

                } else {
                    final String email = nEmail.getText().toString();
                    final String password = nPassword.getText().toString();

                    nAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Usuario o password incorrectos", Toast.LENGTH_SHORT).show();
                                nEmail.setError("Usuario o password incorrectos");

                            }      }
                    });
                }
            }
        }); //autenticacion


    }
    @Override
    protected void onStart(){
        super.onStart();
        nAuth.addAuthStateListener(firebaseAuthListener);

    }
    @Override
    protected void onStop() {
        super.onStop();
        nAuth.removeAuthStateListener(firebaseAuthListener);
    }

}
