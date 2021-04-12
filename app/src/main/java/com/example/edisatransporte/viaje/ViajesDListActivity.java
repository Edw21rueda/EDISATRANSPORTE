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
import com.example.edisatransporte.Class.LoadingDialog;
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

public class    ViajesDListActivity extends AppCompatActivity implements UsersAdapter.SelectedUser {
    int k,i,pp;
    String []  bar;
    RequestQueue requestQueue;
    int aux =0;
    //Estudio Array
//    private ArrayList<Estudio> listEstudios;
    private Button buttonMapa,buttonServicios;
    //SERVICIOS VAR
    LoadingDialog loadingDialog;
    String user_id,pos,nombre,aps,app,apm;
    String[] tomadoresn,tomadoresn2,costos;
    DatabaseReference databaseReference;
    private FirebaseAuth nAuth;
    Toolbar toolbar;
    String auxedo;
    RecyclerView recyclerView;
    List<UserModel> userModelList = new ArrayList<>();
    String[] names ;
    UsersAdapter usersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viajes_d_list);
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);

        this.getSupportActionBar().setTitle("VIAJES DISPONIBLES");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

        usersAdapter = new UsersAdapter(userModelList, ViajesDListActivity.this);
        recyclerView.setAdapter(usersAdapter);

        Query query = FirebaseDatabase.getInstance().getReference("Viajes").orderByChild("status").equalTo(true);
        //FIREBASE
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int j;
                int k;
                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                i=0;
                    names = new String[k];
                    for (DataSnapshot snap2 : dataSnapshot.getChildren()) {
                        System.out.println("el valor es" + snap2.child("status").getValue(Boolean.class));
                                  names[aux] = snap2.getRef().getKey();
                            aux++;

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        System.out.println("sigue");
        Query query2 = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        //FIREBASE
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                nombre=dataSnapshot.child("nombre").getValue(String.class);
                System.out.println("EL nombre es"+nombre);
                aps=dataSnapshot.child("apellidos").getValue(String.class);
                aps = aps.replace(" ", "|");
                System.out.println("los apellidos son"+aps);
                 bar = aps.split("\\|");
                System.out.println("el bar es"+bar);
                app=bar[0];
                apm=bar[1];
                System.out.println("el app"+app+" "+apm);
                consulta("https://sistemavaltons.com.mx/Main_app/consultaviajeoperador.php?app="+app+"&apm="+apm+"&nombre="+nombre);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
        System.out.println("el noombre es"+nombre+" "+app+" "+apm);

//BUSCADOR
    }

    @Override
    public void selectedUser(UserModel userModel) {

        int aux,posa;
        posa=0;
        System.out.println("LA ELECCION ES"+userModel.getUsername()+"con tamaño"+tomadoresn.length);

        for (int l=0;l<tomadoresn.length;l++){
           // System.out.println("La comparacion es "+tomadoresn[l].compareTo(userModel.getUsername()));
            if (tomadoresn[l]!=null) {
                if (tomadoresn[l].compareTo(userModel.getUsername()) == 0) {
                    posa = l;
                    l = tomadoresn.length;
                }
            }
        }
        System.out.println("LA ELECCION ES"+userModel.getUsername()+"con posicion"+posa);
        pos = tomadoresn2[posa];
        System.out.println("El producto es"+pos);
        Intent intent = new Intent(ViajesDListActivity.this, ViajesDisActivity.class);
        intent.putExtra("producto",pos);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ViajesDListActivity.this).toBundle());
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

    private void consulta(String URL){
        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                int ii=0;
                tomadoresn = new String[response.length()];
                tomadoresn2 = new String[response.length()];
                int jj = response.length();
                int st=0;
                System.out.println("el tamaño es"+jj);
                for (int i=ii; i < response.length(); i++) {

                    try {
                        jsonObject = response.getJSONObject(i);
                        if (jsonObject.getString("estado").isEmpty()) {
                        } else{
                            int auxc=0;
                           // System.out.println("el tamaño name"+names.length);
                            if (names.length!=0){
                            for (int z=0;z<names.length;z++){
                                    if (names[z].compareTo(jsonObject.getString("id")) == 0) {
                                    } else {
                                        auxc++;
                                    }
                            }
                            if (auxc==names.length) {
                                System.out.println("el estado es" + jsonObject.getString("estado") + "en la pos" + i);
                                tomadoresn[i] = jsonObject.getString("estado") + jsonObject.getString("id");
                                tomadoresn2[i] = jsonObject.getString("id");
                                UserModel userModel = new UserModel(jsonObject.getString("estado") + jsonObject.getString("id"),jsonObject.getString("tipo_carga"),jsonObject.getString("tipo_ope"));
                                userModelList.add(userModel);
                                usersAdapter.notifyDataSetChanged();
                            }
                            }
                            else{
                                tomadoresn[i] = jsonObject.getString("estado") + jsonObject.getString("id");
                                tomadoresn2[i] = jsonObject.getString("id");
                                UserModel userModel = new UserModel(jsonObject.getString("estado") + jsonObject.getString("id"),jsonObject.getString("tipo_carga"),jsonObject.getString("tipo_ope"));
                                userModelList.add(userModel);
                                usersAdapter.notifyDataSetChanged();

                            }

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
                LayoutInflater myLayout = LayoutInflater.from(ViajesDListActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(ViajesDListActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error al cargar viaje: Revisa tu conexión a internet y reintenta.");
                builder.setCancelable(false);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        Intent intent = new Intent(ViajesDListActivity.this, IndexActivity.class);
                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ViajesDListActivity.this).toBundle());
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

}
