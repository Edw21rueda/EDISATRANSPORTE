package com.example.edisatransporte.Mainviaje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.edisatransporte.FirmaQR.InicioActivity;
import com.example.edisatransporte.Index.InicioFragment;
import com.example.edisatransporte.Index.MenufragActivity;
import com.example.edisatransporte.Index.MiperfilFragment;
import com.example.edisatransporte.Index.QrFragment;
import com.example.edisatransporte.Index.RedessocialesFragment;
import com.example.edisatransporte.Index.ViajesdlistFragment;
import com.example.edisatransporte.Index.ViajesfinlistFragment;
import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.R;
import com.example.edisatransporte.Servicelocation.LocationService;
import com.example.edisatransporte.Servicelocation.MainlocationActivity;
import com.example.edisatransporte.viaje.AgregarEquipoActivity;
import com.google.android.material.navigation.NavigationView;
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

public class MainviajeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;
    static final float END_SCALE = 0.7f;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainviaje);
        preferences = getSharedPreferences("idcarta", Context.MODE_PRIVATE);
        contentView = findViewById(R.id.content);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);

        showSelectedItem(new InicioviajeFragment());
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
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                showSelectedItem(new InicioviajeFragment());
                break;
            case R.id.nav_agregagasto:
                drawerLayout.closeDrawer(GravityCompat.START);
                showSelectedItem(new AgregargastoBlankFragment());
                break;
            case R.id.nav_menup:
                Intent intent = new Intent(MainviajeActivity.this, MenufragActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_agregaincidente:
                drawerLayout.closeDrawer(GravityCompat.START);
                showSelectedItem(new AgregarincidenteFragment());
                break;
            case R.id.nav_viajesp:
                drawerLayout.closeDrawer(GravityCompat.START);
                showSelectedItem(new ViajesdlistFragment());
                break;
            case R.id.nav_firmacarta:
                Intent intentfirma = new Intent(MainviajeActivity.this, InicioActivity.class);
                startActivity(intentfirma);
                break;
            case R.id.nav_finalizaviaje:
                LayoutInflater myLayout = LayoutInflater.from(MainviajeActivity.this);
                final View dialogView = myLayout.inflate(R.layout.wardes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainviajeActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt = (TextView) dialogView.findViewById(R.id.username);
                txt.setText("¿Seguro que quieres finalizar el viaje?");
                builder.setCancelable(false);
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        stopLocationService();
                        FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("status").setValue(true);
                        Intent intent2 = new Intent(MainviajeActivity.this, MenufragActivity.class);
                        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(MainviajeActivity.this).toBundle());
                        return;

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.cancel();
                    }
                });
                AlertDialog titulo = builder.create();
                titulo.show();
                break;
        }
        return true;
    }
    private void stopLocationService(){
        // if (isLocationServiceRunning()){
        Intent intent = new Intent(getApplicationContext(), LocationService.class);
        intent.setAction(com.example.edisatransporte.Mainviaje.Constants.ACTION_STOP_LOCATION_SERVICE);
        startService(intent);
        // Toast.makeText(getApplicationContext(), "SERVICIO DETENIDO", Toast.LENGTH_SHORT).show();
        // }
    }
}