package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class BienvenidoActivity extends AppCompatActivity {

    ImageView logo,splashimg;
    LottieAnimationView lottieAnimationView;
    SharedPreferences onBoardingScreen;
    TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido);
        logo = findViewById(R.id.logo);
        texto = findViewById(R.id.texto);
        splashimg = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.lottie);


        splashimg.animate().translationY(-2800).setDuration(1000).setStartDelay(5000);
        logo.animate().translationY(1800).setDuration(1000).setStartDelay(5000);
        texto.animate().translationY(1800).setDuration(1000).setStartDelay(5000);
        lottieAnimationView.animate().translationY(1800).setDuration(1000).setStartDelay(5000);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("User");

        Query query2 = FirebaseDatabase.getInstance().getReference("Gastos").child("pagados");
        query2.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                System.out.println("el cambio es"+dataSnapshot.getValue());
                System.out.println("DE la referencia de cambio"+dataSnapshot.getRef().getKey());
                if (dataSnapshot.getValue(boolean.class)==true){
                    notificacion(dataSnapshot.getRef().getKey());
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(BienvenidoActivity.this,"lembuitA")
                            .setSmallIcon(R.drawable.logoe)
                            .setContentTitle("GASTOS PAGADOS")
                            .setContentText("Gasto pagado del viaje "+dataSnapshot.getRef().getKey())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(BienvenidoActivity.this);
                    notificationManagerCompat.notify(100,builder.build());

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBoardingScreen = getSharedPreferences("WELCOME",MODE_PRIVATE);
                boolean isFirstTime = onBoardingScreen.getBoolean("firstTime",true);

                if (isFirstTime){
                    SharedPreferences.Editor editor = onBoardingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();
                    Intent intent = new Intent(BienvenidoActivity.this,LoginActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(BienvenidoActivity.this).toBundle());
                    finish();
                }
                else {
                    Intent intent = new Intent(BienvenidoActivity.this, LoginActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(BienvenidoActivity.this).toBundle());
                    finish();
                }
            }
        },5000);

    }
    private void notificacion(String dato){


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "PAGO DE GASTO REALIZACO";
            String description = "pago realizado del viaje "+dato+" ";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("lembuitA", name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            NotificationManager notificationChannel = getSystemService(NotificationManager.class);
            notificationChannel.createNotificationChannel(channel);
        }
    }

}
