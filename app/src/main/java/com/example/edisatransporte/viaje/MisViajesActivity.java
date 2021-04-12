package com.example.edisatransporte.viaje;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.R;
import com.example.edisatransporte.RecyclerD.UserModel;
import com.example.edisatransporte.RecyclerD.UsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
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

public class MisViajesActivity extends AppCompatActivity implements UsersAdapter.SelectedUser{
        int k,i,ii,p;
        RequestQueue requestQueue;
        String []  bar;
private Button buttonMapa,buttonServicios;
        //SERVICIOS VAR
        String user_id,pos,sr,fechasalida;
        String[] tomadoresn,tomadoresn2,costos;
        DatabaseReference databaseReference;
private FirebaseAuth nAuth;
        Toolbar toolbar;
        RecyclerView recyclerView;
        List<UserModel> userModelList = new ArrayList<>();
        String[] names ;
        UsersAdapter usersAdapter;


@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_viajes);
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        this.getSupportActionBar().setTitle("MIS VIAJES");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

        usersAdapter = new UsersAdapter(userModelList,MisViajesActivity.this);
        recyclerView.setAdapter(usersAdapter);


        databaseReference = FirebaseDatabase.getInstance().getReference("Viajes");

        //FIREBASE
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        userModelList.clear();
        k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
        tomadoresn = new String[k];
        tomadoresn2 = new String[k];
        names = new String[k];
        i=0;ii=0;p=0;


        for(DataSnapshot snap:dataSnapshot.getChildren()){
                if (snap.child("operador").getValue(String.class).compareTo(user_id)==0 && snap.child("status").getValue(Boolean.class)==true) {
                System.out.println("el operador es"+snap.child("operador").getValue(String.class));
                sr=snap.getRef().getKey();
                        tomadoresn[p] = snap.getRef().getKey();
                        System.out.println("LA CONSUL" + tomadoresn[p] + "pos" + p);
                        fechasalida = snap.child("fechasalida").getValue(String.class);

                        consulta("https://sistemavaltons.com.mx/Main_app/consultaviaje.php?id=" + tomadoresn[p]);
                        System.out.println("la respuesta es" + tomadoresn2[p]);
                        p++;
                }
                }

        }
@Override
public void onCancelled(@NonNull DatabaseError databaseError) {
        }
        });

        }

        private void consulta(String URL){
                System.out.println("ENTRA A LA CONSULTA");
                JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                                System.out.println("RESPONDE");
                                JSONObject jsonObject = null;
                                for (int Ñ=0; Ñ < response.length(); Ñ++) {

                                        try {
                                                jsonObject = response.getJSONObject(Ñ);
                                                System.out.println("el objeto es "+jsonObject.getString("letfol"));
                                                        tomadoresn2[ii]=jsonObject.getString("estado");
                                                System.out.println("el dato es"+tomadoresn2[ii]+"en la posicion"+ii);
                                                names[ii]=tomadoresn2[ii]+"-"+tomadoresn[ii];
                                                UserModel userModel = new UserModel(names[ii],"Fecha: "+fechasalida,""+jsonObject.getString("tipo_ope"));
                                                userModelList.add(userModel);
                                                usersAdapter.notifyDataSetChanged();
                                                ii++;


                                        } catch (JSONException e) {
                                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                }
                        }
                }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                LayoutInflater myLayout = LayoutInflater.from(MisViajesActivity.this);
                                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                                final AlertDialog.Builder builder = new AlertDialog.Builder(MisViajesActivity.this);
                                //dialog.setTitle("Sin servicio en tu zona");
                                builder.setView(dialogView);
                                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                                txt.setText("Error al cargar viaje: Revisa tu conexión a internet y reintenta.");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogo1, int id) {
                                                Intent intent = new Intent(MisViajesActivity.this, IndexActivity.class);
                                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MisViajesActivity.this).toBundle());
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


@Override
public void selectedUser(UserModel userModel) {
        System.out.println("LA ELECCION ES"+userModel.getUsername());
        bar = userModel.getUsername().split("\\-");
        Intent intent = new Intent(MisViajesActivity.this, ViajesFinActivity.class);
        intent.putExtra("producto",bar[1]);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MisViajesActivity.this).toBundle());
        return;

        }
@Override
public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menurecy,menu);
        MenuItem menuItem = menu.findItem(R.id.search_view);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
@Override
public boolean onQueryTextSubmit(String query) {
        return false;
        }

@Override
public boolean onQueryTextChange(String newText) {
        usersAdapter.getFilter().filter(newText);
        return true;
        }
        });
        return true;
        }
@Override
public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id=item.getItemId();
        if (id == R.id.search_view){
        return true;
        }
        return super.onOptionsItemSelected(item);
        }

        }
