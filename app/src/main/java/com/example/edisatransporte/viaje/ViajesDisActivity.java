package com.example.edisatransporte.viaje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
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

public class ViajesDisActivity extends AppCompatActivity implements UsersAdapter.SelectedUser {
    StringRequest stringRequest;
    RequestQueue requestQueue;
    TextView tipoc,origen,gastos,totalgastos,statuspago,monto;
    TextView destino,status;
    String id,dgastos;
    ImageView imagen;
    RecyclerView recyclerView;
    List<UserModel> userModelList = new ArrayList<>();
    String[] names ;
    UsersAdapter usersAdapter;
    DatabaseReference databaseReference,databaseReference2;
    Toolbar toolbar;
    int total,tgastos=0;

    private static final String CHANNEL_ID ="NOTIFICACION";
    private static final int NOTIFICATION_ID = 0;

    NotificationCompat.Builder mBuilder;
    int mNotificationId = 1;
    String channelId = "my_channel_01";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes_dis);
        setTitle("Informaciòn del viaje");
        dgastos="";
        destino = findViewById(R.id.txtdestino);
        imagen = findViewById(R.id.imggasto);
        totalgastos = findViewById(R.id.txttotalgastos);
        tipoc = findViewById(R.id.tipoc);
        status = findViewById(R.id.txtstatusp);
        gastos = findViewById(R.id.txtgastos);
        monto = findViewById(R.id.txtmonto);
        origen = findViewById(R.id.txtorigen);
        id = getIntent().getStringExtra("producto");
        System.out.println("la clave es"+id);
        consulta("https://sistemavaltons.com.mx/Main_app/consultaviaje.php?id="+id);
        consultagastos("https://sistemavaltons.com.mx/Main_app/consultagastos.php?id="+id);

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
        Query query = FirebaseDatabase.getInstance().getReference("GastosIniciales").child(id);

        //FIREBASE
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k;
                String sna="";
                System.out.println("entra"+String.valueOf(dataSnapshot.getChildrenCount()));
                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                if (k !=0){
                    for(DataSnapshot snap:dataSnapshot.getChildren()){
                        status.setText("Pago De Gastos Realizado");
                        sna = snap.getValue(String.class);
                        System.out.println("el sna es"+sna);
                        monto.setText("Monto: $ "+sna);
                        cargaimagen(id);
                    }


                }
                else{
                    status.setText("Pago De Gastos Pendiente");

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
                LayoutInflater myLayout = LayoutInflater.from(ViajesDisActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ViajesDisActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error al cargar viaje: Revisa tu conexión a internet y reintenta.");
                builder.setCancelable(false);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Intent intent = new Intent(ViajesDisActivity.this, IndexActivity.class);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ViajesDisActivity.this).toBundle());
                        return;
                    }
                });
                AlertDialog titulo= builder.create();
                try {
                    titulo.show();
                }
                catch (WindowManager.BadTokenException e) {
                    //use a log message
                }
            }
        });

        requestQueue= Volley.newRequestQueue(this);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);


    }
    private void consultagastos(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override

            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;
                int ii=0;
                int jj = response.length();
                int st;
                System.out.println("el tam es"+jj);
                for (int i=ii; i < response.length(); i++) {

                    try {

                        jsonObject = response.getJSONObject(i);
                        dgastos = dgastos + jsonObject.getString("descripcion")+": $ "+jsonObject.getString("importe")+"\n";
                        tgastos = tgastos +  jsonObject.getInt("importe");
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                System.out.println("los gastos son"+gastos);
                totalgastos.setText("$ "+tgastos);
                gastos.setText(dgastos);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

              /*  Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                LayoutInflater myLayout = LayoutInflater.from(ViajesDisActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ViajesDisActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error al cargar viaje: Revisa tu conexión a internet y reintenta.");
                builder.setCancelable(false);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Intent intent = new Intent(ViajesDisActivity.this, IndexActivity.class);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ViajesDisActivity.this).toBundle());
                        return;
                    }
                });
                AlertDialog titulo= builder.create();
                titulo.show();
*/
              gastos.setText("Sin gastos registrados");
            }
        });

        requestQueue= Volley.newRequestQueue(this);
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);


    }
    private void cargaimagen(String id){
        String url= "https://sistemavaltons.com.mx/gastosiniciales/images/"+id+"/pagogastoini.jpeg";
        url=url.replace(" ","%20");
        ImageRequest imageRequest=new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imagen.setImageBitmap(response);
            }
        },0,0, ImageView.ScaleType.CENTER,null,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplication(),"Error al cargar)",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(imageRequest);
    }
    @Override
    public void selectedUser(UserModel userModel) {

    }
}
