package com.example.edisatransporte.Mainviaje;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.edisatransporte.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LocationService extends Service {
    String sr;
    int horaaux,minutosaux;
    DateFormat hourFormat;
    DateFormat dateFormat;
    Date date;
    Date auxt = null;
    String auxtt;
    Date usertime = null;
    final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
    SharedPreferences preferences;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {

            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();
                try {
                    auxt = parser.parse(""+preferences.getInt("hora",0)+":"+preferences.getInt("minutos",0));
                } catch (ParseException e) {
                }
                if (auxt!=null){
                    Calendar calendario = Calendar.getInstance();
                    int hora, minutos, segundos;
                    hora = calendario.get(Calendar.HOUR_OF_DAY);
                    minutos = calendario.get(Calendar.MINUTE);
                    segundos = calendario.get(Calendar.SECOND);

                    auxtt = hora + ":" + minutos;
                    try {
                        usertime = parser.parse(auxtt);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (usertime != null) {
                        System.out.println("se compara" + usertime + " con " + auxt);
                        System.out.println("la comparacion es" + usertime.after(auxt));
                        if (usertime.after(auxt)) {
                            FirebaseDatabase.getInstance().getReference().child("Tiempo").child(sr).child(hora + ":" + (minutos)).child("latitud").setValue(locationResult.getLastLocation().getLatitude());
                            FirebaseDatabase.getInstance().getReference().child("Tiempo").child(sr).child(hora + ":" + (minutos)).child("longitud").setValue(locationResult.getLastLocation().getLongitude());
                            if (hora == 23) {
                                hora = 00;
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("auxt").setValue("00:" + (minutos));
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("hora",hora);
                                editor.putInt("minutos",minutos);
                                editor.apply();

//                                horaaux = hora;
                                //                              minutosaux = minutos;
                            } else {
                                hora = hora + 1;
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("auxt").setValue(hora + ":" + (minutos));
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putInt("hora",hora);
                                editor.putInt("minutos",minutos);
                                editor.apply();

                                // horaaux = hora;
                                //      minutosaux = minutos;
                            }
                        }
                    }
                }
                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("velocidad").setValue(""+locationResult.getLastLocation().getSpeed());

                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("latitud").setValue(locationResult.getLastLocation().getLatitude());

                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("longitud").setValue(locationResult.getLastLocation().getLongitude());
                //Toast.makeText(getApplicationContext(), "sigue con latitude" + latitude + " longitude" + longitude+" con sr"+sr, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void startLocationService() {
        preferences = getSharedPreferences("tiempo",Context.MODE_PRIVATE);


        date = new Date();
//Caso 1: obtener la hora y salida por pantalla con formato:
        hourFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.println("Hora: "+hourFormat.format(date));
//Caso 2: obtener la fecha y salida por pantalla con formato:
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendario = Calendar.getInstance();
        SharedPreferences.Editor editor = preferences.edit();
        if (calendario.get(Calendar.HOUR_OF_DAY) == 23) {
            editor.putInt("hora",00);

        } else {
            editor.putInt("hora",(calendario.get(Calendar.HOUR_OF_DAY)+1));
        }
        editor.putInt("minutos",calendario.get(Calendar.MINUTE));
        editor.apply();


        String channelId = "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultintent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0,
                resultintent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                channelId);
        builder.setContentText("¡Que tengas un buen viaje!")
                .setContentTitle("VIAJE EN CURSO")
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.drawable.logoe);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelId,
                        "Location Service",
                        notificationManager.IMPORTANCE_NONE
                );
                notificationChannel.setDescription("Canal para el location service");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        startForeground(com.example.edisatransporte.Mainviaje.Constants.LOCATION_SERVICE_ID,builder.build());
    }

    private void stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null){
            String action = intent.getAction();
            sr = intent.getStringExtra("sr");

            if (action!= null){
                if (action.equals(com.example.edisatransporte.Mainviaje.Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService();
                }else if (action.equals(com.example.edisatransporte.Mainviaje.Constants.ACTION_STOP_LOCATION_SERVICE)){
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}