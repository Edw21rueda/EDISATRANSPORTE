package com.example.edisatransporte.Servicelocation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.edisatransporte.FirmaQR.InicioActivity;
import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.R;
import com.example.edisatransporte.viaje.AgregarIncidenteActivity;
import com.example.edisatransporte.viaje.GastoActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainlocationActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    String user_id, sr;
    private FusedLocationProviderClient fusedLocationClient;
    int hora, minutos, segundos;
    SharedPreferences preferences;

    DateFormat hourFormat;
    private FirebaseAuth nAuth;
    DateFormat dateFormat;
    Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        setContentView(R.layout.activity_mainlocation);
        preferences = getSharedPreferences("idcarta", Context.MODE_PRIVATE);
        sr = ""+preferences.getInt("idnum",0);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

        date = new Date();
//Caso 1: obtener la hora y salida por pantalla con formato:
        hourFormat = new SimpleDateFormat("HH:mm");
        System.out.println("Hora: " + hourFormat.format(date));
//Caso 2: obtener la fecha y salida por pantalla con formato:
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        isLocationServiceRunning();


        findViewById(R.id.request_location_updates_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainlocationActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    startLocationService();

                    findViewById(R.id.request_location_updates_button).setVisibility(View.INVISIBLE);
                    findViewById(R.id.firma).setVisibility(View.VISIBLE);
                    findViewById(R.id.gasto).setVisibility(View.VISIBLE);
                    findViewById(R.id.incidente).setVisibility(View.VISIBLE);

                    findViewById(R.id.remove_location_updates_button).setVisibility(View.VISIBLE);
                    findViewById(R.id.llegada).setVisibility(View.VISIBLE);
                    Calendar calendario = Calendar.getInstance();
                    hora = calendario.get(Calendar.HOUR_OF_DAY);
                    minutos = calendario.get(Calendar.MINUTE);
                    segundos = calendario.get(Calendar.SECOND);
                    System.out.println("la fecha es" + hora + ":" + minutos + ":" + segundos);
                    if (hora == 23) {
                        hora = 00;
                    } else {
                        hora = hora + 1;
                    }
                    Query query = FirebaseDatabase.getInstance().getReference("Viajes").child(""+preferences.getInt("idnum",0));
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int k, i;
                            k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                            System.out.println("el numero es" + k);
                            //   costos = new String[k];
                            if (k != 0) {
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("status").setValue(false);

                            } else {

                                FirebaseDatabase.getInstance().getReference().child("Gastos").child("pagados").child(""+preferences.getInt("idnum",0)).setValue(false);
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("status").setValue(false);
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("entrega").setValue(false);
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("llegadacliente").setValue(false);

                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("fechasalida").setValue(dateFormat.format(date));
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("auxt").setValue(hora + ":" + minutos);

                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("horasalida").setValue(hourFormat.format(date));
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("operador").setValue(user_id);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

        });

        findViewById(R.id.remove_location_updates_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater myLayout = LayoutInflater.from(MainlocationActivity.this);
                final View dialogView = myLayout.inflate(R.layout.wardes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainlocationActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt = (TextView) dialogView.findViewById(R.id.username);
                txt.setText("¿Seguro que quieres finalizar el viaje?");
                builder.setCancelable(false);
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        stopLocationService();
                        FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("status").setValue(true);
                        Intent intent2 = new Intent(MainlocationActivity.this, IndexActivity.class);
                        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(MainlocationActivity.this).toBundle());
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
                return;
            }
        });
    }

    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null){
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                System.out.println("la compa"+LocationService.class.getName()+" con"+service.service.getClassName());

                if (LocationService.class.getName().equals(service.service.getClassName())){
                    if (service.foreground){
                        findViewById(R.id.request_location_updates_button).setVisibility(View.INVISIBLE);
                        findViewById(R.id.firma).setVisibility(View.VISIBLE);
                        findViewById(R.id.remove_location_updates_button).setVisibility(View.VISIBLE);
                        findViewById(R.id.incidente).setVisibility(View.VISIBLE);

                        findViewById(R.id.gasto).setVisibility(View.VISIBLE);
                        findViewById(R.id.llegada).setVisibility(View.VISIBLE);
                        return true;
                    }
                }
            }
            return false;

        }
        return false;
    }
    private void startLocationService(){
        // if (isLocationServiceRunning()){
        Intent intent = new Intent(getApplicationContext(),LocationService.class);
        intent.putExtra("sr",sr);
        intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
        startService(intent);
      //  Toast.makeText(getApplicationContext(), "INICIA SERVICIO", Toast.LENGTH_SHORT).show();
       /* }
        else{
            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();

        }*/
    }
    private void stopLocationService(){
        // if (isLocationServiceRunning()){
        Intent intent = new Intent(getApplicationContext(),LocationService.class);
        intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
        startService(intent);
       // Toast.makeText(getApplicationContext(), "SERVICIO DETENIDO", Toast.LENGTH_SHORT).show();
        // }
    }


    public void gasto(View v) {

        Intent intent2 = new Intent(MainlocationActivity.this, GastoActivity.class);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(MainlocationActivity.this).toBundle());
        return;
    }

    public void menup(View v) {
        Intent intent2 = new Intent(MainlocationActivity.this, IndexActivity.class);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(MainlocationActivity.this).toBundle());
        return;
    }

    public void firmarcarta(View v) {

        Intent intent2 = new Intent(MainlocationActivity.this, InicioActivity.class);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(MainlocationActivity.this).toBundle());
        return;
    }

    public void incidente(View v) {

        Intent intent2 = new Intent(MainlocationActivity.this, AgregarIncidenteActivity.class);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(MainlocationActivity.this).toBundle());
        return;
    }

    public void llegada(View v) {
        LayoutInflater myLayout = LayoutInflater.from(MainlocationActivity.this);
        final View dialogView = myLayout.inflate(R.layout.wardes, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainlocationActivity.this);
        //dialog.setTitle("Sin servicio en tu zona");
        builder.setView(dialogView);
        TextView txt = (TextView) dialogView.findViewById(R.id.username);
        txt.setText("¿Seguro que ya llegaste con el cliente?");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                Calendar calendario = Calendar.getInstance();
                int hora, minutos, segundos;
                hora = calendario.get(Calendar.HOUR_OF_DAY);
                minutos = calendario.get(Calendar.MINUTE);
                segundos = calendario.get(Calendar.SECOND);
                System.out.println("la fecha es" + hora + ":" + minutos + ":" + segundos);
                if (ActivityCompat.checkSelfPermission(MainlocationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainlocationActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(MainlocationActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("llegadalatitud").setValue(location.getLatitude());
                                    FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("llegadalongitud").setValue(location.getLongitude());

                                }
                            }
                        });
                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("llegadacliente").setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("llegadahora").setValue(hora+":"+minutos);

            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.cancel();
            }
        });
        AlertDialog titulo= builder.create();
        titulo.show();
    }

}