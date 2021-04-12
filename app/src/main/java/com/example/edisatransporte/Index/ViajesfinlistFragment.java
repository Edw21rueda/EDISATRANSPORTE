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
import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.R;
import com.example.edisatransporte.RecyclerD.UserModel;
import com.example.edisatransporte.RecyclerD.UsersAdapter;
import com.example.edisatransporte.viaje.MisViajesActivity;
import com.example.edisatransporte.viaje.ViajesFinActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

public class ViajesfinlistFragment extends Fragment implements UsersAdapter.SelectedUser {

    private View PrincipalView;
    int k,i,ii,p;
    RequestQueue requestQueue;
    String []  bar;
    private Button buttonMapa,buttonServicios;
    String user_id,pos,sr,fechasalida;
    String[] tomadoresn,tomadoresn2,costos;
    DatabaseReference databaseReference;
    private FirebaseAuth nAuth;
    Toolbar toolbar;
    RecyclerView recyclerView;
    List<UserModel> userModelList = new ArrayList<>();
    String[] names ;
    UsersAdapter usersAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public ViajesfinlistFragment() {
        // Required empty public constructor
    }

    public static ViajesfinlistFragment newInstance(String param1, String param2) {
        ViajesfinlistFragment fragment = new ViajesfinlistFragment();
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
        PrincipalView = inflater.inflate(R.layout.fragment_viajesfinlist,container,false);
        recyclerView = PrincipalView.findViewById(R.id.recyclerview);
        toolbar = PrincipalView.findViewById(R.id.toolbar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();
        usersAdapter = new UsersAdapter(userModelList, this);
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
                        sr=snap.getRef().getKey();
                        tomadoresn[p] = snap.getRef().getKey();
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

        return PrincipalView;
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
                      //  Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void selectedUser(UserModel userModel) {
        System.out.println("LA ELECCION ES"+userModel.getUsername());
        bar = userModel.getUsername().split("\\-");
        Intent intent = new Intent(getActivity(), ViajesFinActivity.class);
        intent.putExtra("producto",bar[1]);
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

}