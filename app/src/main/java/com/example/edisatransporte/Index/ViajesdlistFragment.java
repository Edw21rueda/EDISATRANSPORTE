package com.example.edisatransporte.Index;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.edisatransporte.viaje.ViajesDListActivity;
import com.example.edisatransporte.viaje.ViajesDisActivity;
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


public class ViajesdlistFragment extends Fragment implements UsersAdapter.SelectedUser {
    private View PrincipalView;
    RequestQueue requestQueue;
    int i,aux =0;
    String user_id,pos,nombre,aps,app,apm;
    String[] names,bar,tomadoresn,tomadoresn2;
    private FirebaseAuth nAuth;
    Toolbar toolbar;
    RecyclerView recyclerView;
    List<UserModel> userModelList = new ArrayList<>();
    UsersAdapter usersAdapter;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ViajesdlistFragment() {
        // Required empty public constructor
    }
    public static ViajesdlistFragment newInstance(String param1, String param2) {
        ViajesdlistFragment fragment = new ViajesdlistFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        PrincipalView = inflater.inflate(R.layout.fragment_viajesdlist,container,false);
        recyclerView = PrincipalView.findViewById(R.id.recyclerview);
        toolbar = PrincipalView.findViewById(R.id.toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();
        usersAdapter = new UsersAdapter(userModelList, this);
        recyclerView.setAdapter(usersAdapter);
        Query query = FirebaseDatabase.getInstance().getReference("Viajes").orderByChild("status").equalTo(true);
        //FIREBASE
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k;
                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                i=0;
                names = new String[k];
                for (DataSnapshot snap2 : dataSnapshot.getChildren()) {
                    names[aux] = snap2.getRef().getKey();
                    aux++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Query query2 = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        //FIREBASE
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nombre=dataSnapshot.child("nombre").getValue(String.class);
                aps=dataSnapshot.child("apellidos").getValue(String.class);
                aps = aps.replace(" ", "|");
                bar = aps.split("\\|");
                app=bar[0];
                apm=bar[1];
                consulta("https://sistemavaltons.com.mx/Main_app/consultaviajeoperador.php?app="+app+"&apm="+apm+"&nombre="+nombre);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    return PrincipalView;
    }

    @Override
    public void selectedUser(UserModel userModel) {

        int posa;
        posa=0;

        for (int l=0;l<tomadoresn.length;l++){
            if (tomadoresn[l]!=null) {
                if (tomadoresn[l].compareTo(userModel.getUsername()) == 0) {
                    posa = l;
                    l = tomadoresn.length;
                }
            }
        }
        pos = tomadoresn2[posa];
        Intent intent = new Intent(getActivity(), ViajesDisActivity.class);
        intent.putExtra("producto",pos);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        return;
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
                for (int i=ii; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        if (jsonObject.getString("estado").isEmpty()) {
                        } else{
                            int auxc=0;
                            if (names.length!=0){
                                for (int z=0;z<names.length;z++){
                                    if (names[z].compareTo(jsonObject.getString("id")) == 0) {
                                    } else {
                                        auxc++;
                                    }
                                }
                                if (auxc==names.length) {
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
                        //Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                LayoutInflater myLayout = LayoutInflater.from(getActivity());
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error al cargar viaje: Revisa tu conexión a internet y reintenta.");
                builder.setCancelable(false);
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.cancel();
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
        requestQueue= Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(jsonArrayRequest);


    }

}