package com.example.edisatransporte.viaje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.R;
import com.example.edisatransporte.RecyclerD.UserModel;
import com.example.edisatransporte.RecyclerD.UsersAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViajesFinActivity extends AppCompatActivity implements UsersAdapter.SelectedUser {
    StringRequest stringRequest;
    RequestQueue requestQueue;
    TextView tipoc,origen,gastos,totalgastos,statuspago;
    TextView destino;
    String id;
    Button btnpago;
    RecyclerView recyclerView;
    List<UserModel> userModelList = new ArrayList<>();
    String[] names ;
    UsersAdapter usersAdapter;
    DatabaseReference databaseReference,databaseReference2;
    Toolbar toolbar;
    int total;

    private static final String CHANNEL_ID ="NOTIFICACION";
    private static final int NOTIFICATION_ID = 0;

    NotificationCompat.Builder mBuilder;
    int mNotificationId = 1;
    String channelId = "my_channel_01";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes_fin);
        setTitle("Resumen de liquidación");
        btnpago = findViewById(R.id.btnpago);
        statuspago = findViewById(R.id.txtstatuspago);
        destino = findViewById(R.id.txtdestino);
        totalgastos = findViewById(R.id.txttotalgastos);
        tipoc = findViewById(R.id.tipoc);
        gastos = findViewById(R.id.txtgastos);
        origen = findViewById(R.id.txtorigen);
        id = getIntent().getStringExtra("producto");
        System.out.println("la clave es"+id);
        consulta("https://sistemavaltons.com.mx/Main_app/consultaviaje.php?id="+id);

        databaseReference = FirebaseDatabase.getInstance().getReference("Gastos").child(id);

        //FIREBASE
        databaseReference.addValueEventListener(new ValueEventListener() {
            String dato="";
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModelList.clear();
                dato="";
                total =0;
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    int aux = (int) snap.getValue(Integer.class);
                    System.out.println("TIPO DE DATO"+snap.getValue(long.class));
                    dato = dato + snap.getRef().getKey()+" : $"+snap.getValue()+"\n";
                    total = total + aux;
                }
                gastos.setText(""+dato);
                totalgastos.setText("$"+total);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        btnpago.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViajesFinActivity.this, GastosImagenesActivity.class);
                intent.putExtra("id",id);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ViajesFinActivity.this).toBundle());
                return;

            }
        });
        Query query = FirebaseDatabase.getInstance().getReference("Gastos").child("pagados");

        //FIREBASE
        query.addValueEventListener(new ValueEventListener() {
            String dato="";
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snap:dataSnapshot.getChildren()) {
                    if (snap.getRef().getKey().compareTo(id) == 0 ){

                    if(snap.getValue(Boolean.class) == true) {
                        System.out.println("entra pos");
                        btnpago.setVisibility(View.VISIBLE);
                        statuspago.setText("Tus Gastos Estan Pagados");
                    } else {
                        statuspago.setText("Pago Pendiente");
                        System.out.println("entra else");

                    }
                }
                    System.out.println("la comparacion es"+snap.getRef().getKey().compareTo(id));
                    System.out.println("TIPO GASTO"+snap.getValue(Boolean.class)+snap.getRef().getKey());
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });




    }
    private void consulta(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override

            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;
                int ii=0;
                int jj = response.length();
                int st;
                for (int i=ii; i < response.length(); i++) {

                    try {
                        jsonObject = response.getJSONObject(i);
                        destino.setText(jsonObject.getString("estado"));
                        if (jsonObject.getString("tipo_envio").compareTo("F")==0) {
                            tipoc.setText("Full");
                        }
                        if (jsonObject.getString("tipo_envio").compareTo("S")==0) {
                            tipoc.setText("Sencillo");
                        }

                        if (jsonObject.getString("letfol").compareTo("A")==0) {
                            origen.setText("ALTAMIRA");
                        }
                        System.out.println("LA COMPA"+jsonObject.getString("tipo_carga").compareTo("M"));
                        if (jsonObject.getString("letfol").compareTo("M")==0) {
                            origen.setText("MANZANILLO");
                            System.out.println("ENTRA M");
                        } if (jsonObject.getString("letfol").compareTo("V")==0) {
                            origen.setText("VERACRUZ");
                        } if (jsonObject.getString("letfol").compareTo("C")==0) {
                            origen.setText("CDMX");
                        } if (jsonObject.getString("letfol").compareTo("L")==0) {
                            origen.setText("LAZARO CARDENAS");
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                LayoutInflater myLayout = LayoutInflater.from(ViajesFinActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ViajesFinActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error al cargar viaje: Revisa tu conexión a internet y reintenta.");
                builder.setCancelable(false);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Intent intent = new Intent(ViajesFinActivity.this, IndexActivity.class);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ViajesFinActivity.this).toBundle());
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

    @Override
    public void selectedUser(UserModel userModel) {

    }

    private void notificacion(){

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this,null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CITA GENERADA";
            String description = "¡GRACIAS POR TU CONFIANZA!";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            mBuilder = new NotificationCompat.Builder(this,channelId);
        }
        mBuilder.setSmallIcon(R.drawable.logoe)
                .setContentTitle("CITA GENERADA")
                .setContentText("¡GRACIAS POR LA CONFIANZA!");
        mBuilder.setChannelId(channelId);
        mNotificationManager.notify(mNotificationId,mBuilder.build());

    }
}
