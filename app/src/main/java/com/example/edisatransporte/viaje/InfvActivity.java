package com.example.edisatransporte.viaje;

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
import android.widget.Toast;

import com.example.edisatransporte.FirmaQR.InicioActivity;
import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;

public class InfvActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener  {

    private static final String TAG="InfvActivity";
    FirebaseDatabase mibase;
    DatabaseReference mireferencia;
    MyBackgroundServices mService=null;
    Boolean mBound = false;
    String sr,user_id;
    Button request,remove;
    private FirebaseAuth nAuth;
    DatabaseReference databaseReference;

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
        setContentView(R.layout.activity_infv);
        sr = getIntent().getStringExtra("sr");
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();
        mibase = FirebaseDatabase.getInstance();
        mireferencia = mibase.getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("ViajesF");


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

                request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mService.requestLocationUpdates(sr);
                    }
                });
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mService.removeLocationUpdates();
                    }
                });
                setButtonState(Common.requestingLocationUpdates(InfvActivity.this));
                bindService(new Intent(InfvActivity.this,
                                MyBackgroundServices.class),
                        mServiceConection, Context.BIND_AUTO_CREATE);

            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken toekn){

            }
        }).check();

    }


    public void finalizar(View v){
        LayoutInflater myLayout = LayoutInflater.from(InfvActivity.this);
        final View dialogView = myLayout.inflate(R.layout.wardes, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(InfvActivity.this);
        //dialog.setTitle("Sin servicio en tu zona");
        builder.setView(dialogView);
        TextView txt =(TextView)dialogView.findViewById(R.id.username);
        txt.setText("¿Seguro que quieres finalizar el viaje?");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                mService.removeLocationUpdates();
                mireferencia.child("Viajes").child(sr).child("status").setValue(true);
                Intent intent2 = new Intent(InfvActivity.this, IndexActivity.class);
                startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(InfvActivity.this).toBundle());
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
    public void gasto(View v){

        Intent intent2 = new Intent(InfvActivity.this, GastoActivity.class);
        intent2.putExtra("sr",sr);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(InfvActivity.this).toBundle());
        return;
    }
    public void menup(View v){
        Intent intent2 = new Intent(InfvActivity.this, IndexActivity.class);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(InfvActivity.this).toBundle());
        return;
    }
    public void firmarcarta(View v){

        Intent intent2 = new Intent(InfvActivity.this, InicioActivity.class);
        intent2.putExtra("sr",sr);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(InfvActivity.this).toBundle());
        return;
    }
    public void incidente(View v){

        Intent intent2 = new Intent(InfvActivity.this, AgregarIncidenteActivity.class);
        intent2.putExtra("sr",sr);
        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(InfvActivity.this).toBundle());
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
            request.setEnabled(true);
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
            mireferencia.child("Viajes").child(sr).child("latitud").setValue(event.getLocation().getLatitude());
            mireferencia.child("Viajes").child(sr).child("longitud").setValue(event.getLocation().getLongitude());
            mireferencia.child("Viajes").child(sr).child("velocidad").setValue(event.getLocation().getSpeed());

            String data = new StringBuilder()
                    .append(event.getLocation().getLatitude())
                    .append("/")
                    .append(event.getLocation().getLongitude())
                    .toString();
            Toast.makeText(mService,"entro"+data,Toast.LENGTH_LONG).show();
        }

    }

}

