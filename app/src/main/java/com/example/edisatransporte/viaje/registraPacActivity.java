package com.example.edisatransporte.viaje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.edisatransporte.FirmaQR.InicioActivity;
import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class registraPacActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener  {

    Button remove,request,gasto,incidente,firma;
    DateFormat dateFormat;
    Date date;
    String user_id,sr;
    DateFormat hourFormat;
    private FirebaseAuth nAuth;

    MyBackgroundServices mService=null;
    Boolean mBound = false;

    private final ServiceConnection mServiceConection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyBackgroundServices.LocalBinder binder = (MyBackgroundServices.LocalBinder)iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registra_pac);
        sr = getIntent().getStringExtra("sr");
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

        date = new Date();
//Caso 1: obtener la hora y salida por pantalla con formato:
        hourFormat = new SimpleDateFormat("HH:mm:ss");
        System.out.println("Hora: "+hourFormat.format(date));
//Caso 2: obtener la fecha y salida por pantalla con formato:
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        System.out.println("Fecha: "+dateFormat.format(date));
        Dexter.withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )).withListener(new MultiplePermissionsListener(){
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report){
                request = (Button) findViewById(R.id.request_location_updates_button);
                remove = (Button) findViewById(R.id.remove_location_updates_button);
                incidente = (Button) findViewById(R.id.incidente);
                firma = (Button) findViewById(R.id.firma);
                gasto = (Button) findViewById(R.id.gasto);

                request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mService.requestLocationUpdates(sr);
                        request.setVisibility(View.INVISIBLE);
                        remove.setVisibility(View.VISIBLE);
                        gasto.setVisibility(View.VISIBLE);
                        firma.setVisibility(View.VISIBLE);
                        incidente.setVisibility(View.VISIBLE);
                        Query query = FirebaseDatabase.getInstance().getReference("Viajes").child(sr);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int k,i;
                                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                                System.out.println("el numero es"+k);
                                //   costos = new String[k];
                                if (k!=0){
                                }
                                else {
                                    FirebaseDatabase.getInstance().getReference().child("Gastos").child("pagados").child(sr).setValue(false);
                                    FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("status").setValue(false);
                                    FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("entrega").setValue(false);
                                    FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("fechasalida").setValue(dateFormat.format(date));
                                    FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("horasalida").setValue(hourFormat.format(date));
                                    FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("operador").setValue(user_id);

                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }
                });
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LayoutInflater myLayout = LayoutInflater.from(registraPacActivity.this);
                        final View dialogView = myLayout.inflate(R.layout.wardes, null);
                        final AlertDialog.Builder builder = new AlertDialog.Builder(registraPacActivity.this);
                        //dialog.setTitle("Sin servicio en tu zona");
                        builder.setView(dialogView);
                        TextView txt =(TextView)dialogView.findViewById(R.id.username);
                        txt.setText("¿Seguro que quieres finalizar el viaje?");
                        builder.setCancelable(false);
                        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                mService.removeLocationUpdates();
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("status").setValue(true);
                                Intent intent2 = new Intent(registraPacActivity.this, IndexActivity.class);
                                startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(registraPacActivity.this).toBundle());
                                return;

                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo1, int id) {
                                dialogo1.cancel();
                            }
                        });
                        AlertDialog titulo= builder.create();
                        titulo.show();
                        return ;
                    }
                });

                setButtonState(Common.requestingLocationUpdates(registraPacActivity.this));
                bindService(new Intent(registraPacActivity.this,
                                MyBackgroundServices.class),
                        mServiceConection, Context.BIND_AUTO_CREATE);

            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken toekn){

            }
        }).check();
    }


    public void gasto(View v){

        Intent intent2 = new Intent(registraPacActivity.this, GastoActivity.class);
        intent2.putExtra("sr",sr);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(registraPacActivity.this).toBundle());
        return;
    }
    public void menup(View v){
        Intent intent2 = new Intent(registraPacActivity.this, IndexActivity.class);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(registraPacActivity.this).toBundle());
        return;
    }
    public void firmarcarta(View v){

        Intent intent2 = new Intent(registraPacActivity.this, InicioActivity.class);
        intent2.putExtra("sr",sr);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(registraPacActivity.this).toBundle());
        return;
    }
    public void incidente(View v){

        Intent intent2 = new Intent(registraPacActivity.this, AgregarIncidenteActivity.class);
        intent2.putExtra("sr",sr);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(registraPacActivity.this).toBundle());
        return;
    }


    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if (mBound){
            unbindService(mServiceConection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);
        super.onStop();

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals(Common.KEY_REQUESTING_LOCATION_UPDATES))
        {
            setButtonState(sharedPreferences.getBoolean(Common.KEY_REQUESTING_LOCATION_UPDATES,false));
        }
    }

    private void setButtonState(boolean isRequestEnable) {
        if (isRequestEnable){
            request.setEnabled(false);
            remove.setVisibility(View.VISIBLE);
            gasto.setVisibility(View.VISIBLE);
            firma.setVisibility(View.VISIBLE);
            incidente.setVisibility(View.VISIBLE);

            remove.setEnabled(true);
        }
        else {
            request.setEnabled(true);
            remove.setEnabled(false);
        }

    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenLocation(SendLocationToActivity event){
        if (event!=null){
            String data = new StringBuilder()
                    .append(event.getLocation().getLatitude())
                    .append("/")
                    .append(event.getLocation().getLongitude())
                    .toString();
            FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("latitud").setValue(event.getLocation().getLatitude());

            FirebaseDatabase.getInstance().getReference().child("Viajes").child(sr).child("longitud").setValue(event.getLocation().getLongitude());

        //    Toast.makeText(mService,data,Toast.LENGTH_LONG).show();
        }

    }

}
