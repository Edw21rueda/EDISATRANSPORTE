package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.edisatransporte.FirmaQR.InicioActivity;
import com.example.edisatransporte.viaje.AgregarIncidenteActivity;
import com.example.edisatransporte.viaje.GastoActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ViajemainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = ViajemainActivity.class.getSimpleName();

    Button remove, request, gasto, incidente, firma, llegada;
    DateFormat dateFormat;
    Date date;
    String user_id, sr;
    int hora, minutos, segundos;

    DateFormat hourFormat;
    private FirebaseAuth nAuth;
    private FusedLocationProviderClient fusedLocationClient;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private MyReceiver myReceiver;

    private LocationUpdatesService mService = null;

    private boolean mBound = false;

    private Button mRequestLocationUpdatesButton;
    private Button mRemoveLocationUpdatesButton;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LocationUpdatesService.LocalBinder binder = (LocationUpdatesService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        myReceiver = new MyReceiver();
        setContentView(R.layout.activity_viajemain);
        sr = getIntent().getStringExtra("sr");
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

        date = new Date();
//Caso 1: obtener la hora y salida por pantalla con formato:
        hourFormat = new SimpleDateFormat("HH:mm");
        System.out.println("Hora: " + hourFormat.format(date));
//Caso 2: obtener la fecha y salida por pantalla con formato:
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        incidente = (Button) findViewById(R.id.incidente);
        firma = (Button) findViewById(R.id.firma);
        llegada = (Button) findViewById(R.id.llegada);

        gasto = (Button) findViewById(R.id.gasto);
        mRequestLocationUpdatesButton = (Button) findViewById(R.id.request_location_updates_button);
        mRemoveLocationUpdatesButton = (Button) findViewById(R.id.remove_location_updates_button);

        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(this)) {
            if (!checkPermissions()) {
                requestPermissions();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        mRequestLocationUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkPermissions()) {
                    requestPermissions();
                } else {
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
                    mService.requestLocationUpdates(sr,hora,minutos);
                    gasto.setVisibility(View.VISIBLE);
                    firma.setVisibility(View.VISIBLE);
                    llegada.setVisibility(View.VISIBLE);

                    incidente.setVisibility(View.VISIBLE);
                    mRequestLocationUpdatesButton.setVisibility(View.INVISIBLE);
                    mRemoveLocationUpdatesButton.setVisibility(View.VISIBLE);
                    Query query = FirebaseDatabase.getInstance().getReference("Viajes").child(sr);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int k, i;
                            k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                            System.out.println("el numero es" + k);
                            //   costos = new String[k];
                            if (k != 0) {
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("status").setValue(false);

                            } else {

                                FirebaseDatabase.getInstance().getReference().child("Gastos").child("pagados").child(sr).setValue(false);
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("status").setValue(false);
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("entrega").setValue(false);
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("llegadacliente").setValue(false);

                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("fechasalida").setValue(dateFormat.format(date));
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("auxt").setValue(hora + ":" + minutos);

                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("horasalida").setValue(hourFormat.format(date));
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("operador").setValue(user_id);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        mRemoveLocationUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater myLayout = LayoutInflater.from(ViajemainActivity.this);
                final View dialogView = myLayout.inflate(R.layout.wardes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ViajemainActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt = (TextView) dialogView.findViewById(R.id.username);
                txt.setText("¿Seguro que quieres finalizar el viaje?");
                builder.setCancelable(false);
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        mService.removeLocationUpdates();
                        FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("status").setValue(true);
                        Intent intent2 = new Intent(ViajemainActivity.this, IndexActivity.class);
                        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(ViajemainActivity.this).toBundle());
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

        // Restore the state of the buttons when the activity (re)launches.
        setButtonsState(Utils.requestingLocationUpdates(this));

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exitt foreground mode.
        bindService(new Intent(this, LocationUpdatesService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);
    }

    public void gasto(View v) {

        Intent intent2 = new Intent(ViajemainActivity.this, GastoActivity.class);
        intent2.putExtra("sr", sr);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(ViajemainActivity.this).toBundle());
        return;
    }

    public void menup(View v) {
        Intent intent2 = new Intent(ViajemainActivity.this, IndexActivity.class);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(ViajemainActivity.this).toBundle());
        return;
    }

    public void firmarcarta(View v) {

        Intent intent2 = new Intent(ViajemainActivity.this, InicioActivity.class);
        intent2.putExtra("sr", sr);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(ViajemainActivity.this).toBundle());
        return;
    }

    public void incidente(View v) {

        Intent intent2 = new Intent(ViajemainActivity.this, AgregarIncidenteActivity.class);
        intent2.putExtra("sr", sr);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(ViajemainActivity.this).toBundle());
        return;
    }

    public void llegada(View v) {
        LayoutInflater myLayout = LayoutInflater.from(ViajemainActivity.this);
        final View dialogView = myLayout.inflate(R.layout.wardes, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(ViajemainActivity.this);
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
                if (ActivityCompat.checkSelfPermission(ViajemainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ViajemainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(ViajemainActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("llegadalatitud").setValue(location.getLatitude());
                                    FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("llegadalongitud").setValue(location.getLongitude());

                                }
                            }
                        });
                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("llegadacliente").setValue(true);
                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("llegadahora").setValue(hora+":"+minutos);

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

@Override
protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
        new IntentFilter(LocationUpdatesService.ACTION_BROADCAST));
        }

@Override
protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
        super.onPause();
        }

@Override
protected void onStop() {
        if (mBound) {
        // Unbind from the service. This signals to the service that this activity is no longer
        // in the foreground, and the service can respond by promoting itself to a foreground
        // service.
        unbindService(mServiceConnection);
        mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
        .unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
        }

/**
 * Returns the current state of the permissions needed.
 */
private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION);
        }

private void requestPermissions() {
        boolean shouldProvideRationale =
        ActivityCompat.shouldShowRequestPermissionRationale(this,
        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
        Log.i(TAG, "Displaying permission rationale to provide additional context.");
        Snackbar.make(
        findViewById(R.id.activity_Viajemain),
        R.string.permission_rationale,
        Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.ok, new View.OnClickListener() {
@Override
public void onClick(View view) {
        // Request permission
        ActivityCompat.requestPermissions(ViajemainActivity.this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        REQUEST_PERMISSIONS_REQUEST_CODE);
        }
        })
        .show();
        } else {
        Log.i(TAG, "Requesting permission");
        // Request permission. It's possible this can be auto answered if device policy
        // sets the permission in a given state or the user denied the permission
        // previously and checked "Never ask again".
        ActivityCompat.requestPermissions(ViajemainActivity.this,
        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
        REQUEST_PERMISSIONS_REQUEST_CODE);
        }
        }

/**
 * Callback received when a permissions request has been completed.
 */
@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
@NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
        if (grantResults.length <= 0) {
        // If user interaction was interrupted, the permission request is cancelled and you
        // receive empty arrays.
        Log.i(TAG, "User interaction was cancelled.");
        } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        // Permission was granted.
        mService.requestLocationUpdates(sr,hora,minutos);
        } else {
        // Permission denied.
        setButtonsState(false);
        Snackbar.make(
        findViewById(R.id.activity_Viajemain),
        R.string.permission_denied_explanation,
        Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.settings, new View.OnClickListener() {
@Override
public void onClick(View view) {
        // Build intent that displays the App settings screen.
        Intent intent = new Intent();
        intent.setAction(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
        BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        }
        })
        .show();
        }
        }
        }

/**
 * Receiver for broadcasts sent by {@link LocationUpdatesService}.
 */
private class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final Location location = intent.getParcelableExtra(LocationUpdatesService.EXTRA_LOCATION);
        if (location != null) {

            Query query2 = FirebaseDatabase.getInstance().getReference("Viajes").child(sr);
        //FIREBASE
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

                Date auxt = null;
                String auxtt;
                //k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    try {
                        auxt = parser.parse(""+dataSnapshot.child("auxt").getValue(String.class));

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println("el auxt es"+dataSnapshot.child("auxt").getValue(String.class));
                    //          tomadoresn2[i]=dataSnapshot.child("nombre").getValue(String.class)+" "+dataSnapshot.child("apellidos").getValue(String.class);
                }
                if (auxt!=null){
                    Calendar calendario = Calendar.getInstance();
                int hora, minutos, segundos;
                hora = calendario.get(Calendar.HOUR_OF_DAY);
                minutos = calendario.get(Calendar.MINUTE);
                segundos = calendario.get(Calendar.SECOND);
                auxtt = hora+":"+minutos;
                Date usertime = null;
                try {
                    usertime = parser.parse(auxtt);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                System.out.println("se compara"+usertime+" con "+auxt);
                System.out.println("la comparacion es"+usertime.after(auxt));
                if (usertime !=null) {
                    if (usertime.after(auxt)) {
                        FirebaseDatabase.getInstance().getReference().child("Tiempo").child(sr).child(hora + ":" + (minutos)).child("latitud").setValue(location.getLatitude());
                            FirebaseDatabase.getInstance().getReference().child("Tiempo").child(sr).child(hora + ":" + (minutos)).child("longitud").setValue(location.getLongitude());

                            if (hora == 23) {
                                hora = 00;
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("auxt").setValue("00:" + (minutos));

                            } else {
                                hora = hora + 1;
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("auxt").setValue(hora+":"+ (minutos));

                            }


                        }

                }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

            FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("velocidad").setValue(""+location.getSpeed());
            FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("latitud").setValue(location.getLatitude());

            FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("longitud").setValue(location.getLongitude());

          //  Toast.makeText(ViajemainActivity.this, Utils.getLocationText(location),
                 //   Toast.LENGTH_SHORT).show();
        }
    }
}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        // Update the buttons state depending on whether location updates are being requested.
        if (s.equals(Utils.KEY_REQUESTING_LOCATION_UPDATES)) {
            setButtonsState(sharedPreferences.getBoolean(Utils.KEY_REQUESTING_LOCATION_UPDATES,
                    false));
        }
    }

    private void setButtonsState(boolean requestingLocationUpdates) {
        if (requestingLocationUpdates) {
            mRequestLocationUpdatesButton.setEnabled(false);
            mRemoveLocationUpdatesButton.setEnabled(true);
            mRequestLocationUpdatesButton.setVisibility(View.INVISIBLE);

            mRemoveLocationUpdatesButton.setVisibility(View.VISIBLE);
            gasto.setVisibility(View.VISIBLE);
            llegada.setVisibility(View.VISIBLE);
            firma.setVisibility(View.VISIBLE);
            incidente.setVisibility(View.VISIBLE);

        } else {
            mRequestLocationUpdatesButton.setEnabled(true);
            mRemoveLocationUpdatesButton.setEnabled(false);
        }
    }
}
