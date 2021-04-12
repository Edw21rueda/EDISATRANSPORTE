package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class MainActivity extends AppCompatActivity {

    //ANIMACION
    Animation sideAnim,buttomAnim;
    private static int SPLASH_TIMER = 3000;
    final private int ASK_PERMISSION=111;

    ImageView logo;
    TextView textView;

    SharedPreferences onBoardingScreen;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
      //  solicitarPermisos();

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
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this,"lembuitA")
                            .setSmallIcon(R.drawable.logoe)
                            .setContentTitle("GASTOS PAGADOS")
                            .setContentText("Gasto pagado del viaje "+dataSnapshot.getRef().getKey())
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
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
        logo = (ImageView)findViewById(R.id.logoi);
        textView =(TextView)findViewById(R.id.txtv);
        //ANIMATIONS
        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_anim);
        buttomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        logo.setAnimation(sideAnim);
        textView.setAnimation(buttomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                onBoardingScreen = getSharedPreferences("WELCOME",MODE_PRIVATE);
                boolean isFirstTime = onBoardingScreen.getBoolean("firstTime",true);

                if (isFirstTime){
                    SharedPreferences.Editor editor = onBoardingScreen.edit();
                    editor.putBoolean("firstTime",false);
                    editor.commit();
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    finish();
                }
                else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    finish();
                }
            }
        },SPLASH_TIMER);

    }
    private void solicitarPermisos()
    {
        int permisogps = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permisogps!= PackageManager.PERMISSION_GRANTED)
        {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M)
            {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},ASK_PERMISSION);
            }
        }
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

