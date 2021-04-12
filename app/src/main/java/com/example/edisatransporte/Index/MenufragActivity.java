package com.example.edisatransporte.Index;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.LoginActivity;
import com.example.edisatransporte.Mainviaje.MainviajeActivity;
import com.example.edisatransporte.MiPerfilActivity;
import com.example.edisatransporte.R;
import com.example.edisatransporte.Servicelocation.MainlocationActivity;
import com.example.edisatransporte.viaje.AgregarEquipoActivity;
import com.example.edisatransporte.viaje.MisViajesActivity;
import com.example.edisatransporte.viaje.QrlActivity;
import com.example.edisatransporte.viaje.ViajesDListActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenufragActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;
    static final float END_SCALE = 0.7f;
    String sr;
    DateFormat dateFormat;
    Date date;
    DateFormat hourFormat;
    FirebaseDatabase mibase;
    DatabaseReference mireferencia;
    SharedPreferences preferences;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menufrag);
        preferences = getSharedPreferences("idcarta", Context.MODE_PRIVATE);
        contentView = findViewById(R.id.content);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);

        showSelectedItem(new InicioFragment());
        naviagtionDrawer();


    }
    private void naviagtionDrawer() {
        //Naviagtion Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        animateNavigationDrawer();
    }


    private void showSelectedItem(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.conatiner,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    private void animateNavigationDrawer() {
      drawerLayout.setScrimColor(getResources().getColor(R.color.darkblue));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);
                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
          /*  case R.id.nav_citas:
                Intent intent = new Intent(IndexActivity.this,ListviaActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
                break;*/
            case R.id.nav_agendar:
                drawerLayout.closeDrawer(GravityCompat.START);
                showSelectedItem(new QrFragment());
                break;
            case R.id.nav_exit:
                cerrarsesion();
                break;
                 case R.id.nav_redes:
                drawerLayout.closeDrawer(GravityCompat.START);
                showSelectedItem(new RedessocialesFragment());
                break;
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                showSelectedItem(new InicioFragment());
                break;
            case R.id.nav_viajes:
                drawerLayout.closeDrawer(GravityCompat.START);
                showSelectedItem(new ViajesfinlistFragment());
                break;
            case R.id.nav_viajesp:
                drawerLayout.closeDrawer(GravityCompat.START);
                showSelectedItem(new ViajesdlistFragment());
                break;
            case R.id.nav_profile:
                drawerLayout.closeDrawer(GravityCompat.START);
                showSelectedItem(new MiperfilFragment());
                break;
        }
        return true;
    }
    @Override
    public void  onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null)
            if (result.getContents() != null){
                sr = result.getContents();
                date = new Date();
//Caso 1: obtener la hora y salida por pantalla con formato:
                hourFormat = new SimpleDateFormat("HH:mm:ss");
                System.out.println("Hora: "+hourFormat.format(date));
//Caso 2: obtener la fecha y salida por pantalla con formato:
                dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println("Fecha: "+dateFormat.format(date));
                mibase = FirebaseDatabase.getInstance();
                mireferencia = mibase.getReference();
                Query query = FirebaseDatabase.getInstance().getReference("Viajes").child(sr);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int k,i;
                        k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                        System.out.println("el numero es"+k);
                        //   costos = new String[k];
                        if (k!=0){
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("idnum", Integer.parseInt(sr));
                            editor.apply();
                            Intent intent = new Intent(MenufragActivity.this, MainviajeActivity.class);
                            startActivity(intent);
                            return;
                        }
                        else {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putInt("idnum", Integer.parseInt(sr));
                            editor.apply();
                            Intent intent = new Intent(MenufragActivity.this, AgregarEquipoActivity.class);
                            startActivity(intent);
                            return;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }else{
                System.out.println("Erro");
            }
    }
    private void cerrarsesion(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MenufragActivity.this, LoginActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MenufragActivity.this).toBundle());
        finish();
        return;

    }
}