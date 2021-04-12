package com.example.edisatransporte.viaje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.edisatransporte.Class.LoadingDialog;
import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.Mainviaje.MainviajeActivity;
import com.example.edisatransporte.Servicelocation.MainlocationActivity;
import com.example.edisatransporte.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class AgregarEquipoActivity extends AppCompatActivity {
    DateFormat dateFormat;
    Date date;
    String user_id;
    Button request,remove;
    DateFormat hourFormat;
    int auxc=0;
    private FirebaseAuth nAuth;
    RequestQueue requestQueue;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_equipo);
        //sr = getIntent().getStringExtra("sr");
        preferences = getSharedPreferences("idcarta", Context.MODE_PRIVATE);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();
        consulta("https://sistemavaltons.com.mx/Main_app/consultaviaje.php?id="+preferences.getInt("idnum",0));

        Dexter.withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )).withListener(new MultiplePermissionsListener(){
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report){
                request = (Button) findViewById(R.id.inicia);
                remove = (Button) findViewById(R.id.remove_location_updates_button);

                request.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        date = new Date();
//Caso 1: obtener la hora y salida por pantalla con formato:
                        hourFormat = new SimpleDateFormat("HH:mm:ss");
                        System.out.println("Hora: "+hourFormat.format(date));
//Caso 2: obtener la fecha y salida por pantalla con formato:
                        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        System.out.println("Fecha: "+dateFormat.format(date));
                        Query query = FirebaseDatabase.getInstance().getReference("Equipo").child(""+preferences.getInt("idnum",0));
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                int k,i;
                                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                                int x = auxc - k;
                                System.out.println("el numero es"+k);
                                //   costos = new String[k];
                                if (k>=auxc){
                                    Intent intent = new Intent(AgregarEquipoActivity.this, MainviajeActivity.class);
                                    startActivity(intent);
                                   // finish();
                                    return;
                                }
                                else {
                                    LayoutInflater myLayout = LayoutInflater.from(AgregarEquipoActivity.this);
                                    final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                                    final AlertDialog.Builder builder = new AlertDialog.Builder(AgregarEquipoActivity.this);
                                    //dialog.setTitle("Sin servicio en tu zona");
                                    builder.setView(dialogView);
                                    TextView txt =(TextView)dialogView.findViewById(R.id.username);
                                    txt.setText("Te falta agregar "+x+ " equipo(s)");
                                    builder.setCancelable(false);
                                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {
                                            dialogo1.cancel();
                                        }
                                    });
                                    AlertDialog titulo= builder.create();
                                    titulo.show();
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
                        //    mService.removeLocationUpdates();
                        Intent intent2 = new Intent(AgregarEquipoActivity.this, ImageEquipoActivity.class);
                        startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(AgregarEquipoActivity.this).toBundle());
                        return;
                    }
                });


            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken toekn){

            }
        }).check();


    }
    private void consulta(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override

            public void onResponse(JSONArray response) {
                final LoadingDialog loadingDialog = new LoadingDialog(AgregarEquipoActivity.this);
                loadingDialog.startLoadingDialog();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                },15000);
                JSONObject jsonObject = null;
                int ii=0;
                int jj = response.length();
                int st;
                for (int i=ii; i < response.length(); i++) {

                    try {
                        jsonObject = response.getJSONObject(i);
                        if (jsonObject.getString("tipo_envio").compareTo("F")==0) {
                            auxc = 3 ;
                        }
                        if (jsonObject.getString("tipo_envio").compareTo("S")==0) {
                            auxc = 1 ;

                        }

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                loadingDialog.dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                LayoutInflater myLayout = LayoutInflater.from(AgregarEquipoActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(AgregarEquipoActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error al cargar viaje: Revisa tu conexión a internet y reintenta.");
                builder.setCancelable(false);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Intent intent = new Intent(AgregarEquipoActivity.this, IndexActivity.class);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(AgregarEquipoActivity.this).toBundle());
                        return;
                    }
                });
                AlertDialog titulo= builder.create();
                titulo.show();

            }
        });

        requestQueue= Volley.newRequestQueue(this);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);


    }


}
