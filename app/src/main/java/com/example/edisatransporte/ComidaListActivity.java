package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.edisatransporte.Cliente.Estudios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ComidaListActivity extends AppCompatActivity {
    int cost;
    ListView listaResultado;
    String servf="",servf2="",pos,nombre,apt,tel,dir,fecha,hora;
    private ArrayList<String> lista;
    private ArrayAdapter<String> adaptador1;
    private ArrayList<Integer> preciosAsig;
    int subt,tott,j;
    String pro ="";
    Spinner genero;
    int k,i;


    //Estudio Array
//    private ArrayList<Estudio> listEstudios;
    private Button buttonMapa,buttonServicios;
    RequestQueue requestQueue;
    //SERVICIOS VAR
    ListView listaServ;
    private ArrayList<String> listaSerTot,listaSerTot2;
    private ArrayAdapter<String> adaptadorPrin;
    SearchView sv;
    private EditText et_buscar;
    String guardar = "";
    String costs="";
    String cadena,press1,feho,fec,user_id;
    String[] tomadoresn,tomadoresn2,costos;
    RequestQueue requestQueueServ;
    TextView sub,tot;
    int         a, m, d, h, n;
    int contador = 0;
    DatabaseReference databaseReference;
    private FirebaseAuth nAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comida_list);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();
        setTitle("COMIDA");

        listaSerTot = new ArrayList<>();
        listaSerTot2 = new ArrayList<>();
        buttonMapa = (Button) findViewById(R.id.btnubi);

        listaServ =(ListView)findViewById(R.id.simple_list);
        sv = (SearchView)findViewById(R.id.sv);
        // listaResultado =(ListView)findViewById(R.id.listServ);//Se agregan seleccionados
        //listEstudios = new ArrayList<Estudio>();
        lista = new ArrayList<>();
        adaptadorPrin = new ArrayAdapter<String>(ComidaListActivity.this, android.R.layout.simple_list_item_1,lista);
        listaServ.setAdapter(adaptadorPrin);

        preciosAsig = new ArrayList<>();//ARREGLO TEMPORAL
        Query query = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("carrito");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k,i;
                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                //   costos = new String[k];
                if (k!=0){
                    buttonMapa.setText("Ver carrito ("+k+")");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Query databaseReference = FirebaseDatabase.getInstance().getReference("Productos").orderByChild("categoria").equalTo("comida");

        //FIREBASE
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista.clear();
                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                tomadoresn = new String[k];
                tomadoresn2 = new String[k+1];
                costos = new String[k];
                i=0;
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    tomadoresn[i]=snap.getRef().getKey();
                    Estudios es= snap.getValue(Estudios.class);
                    // String bb=(String) snap.child("precio").getValue(String.class);
                    costos[i]= String.valueOf(es);
                    Query query = FirebaseDatabase.getInstance().getReference("Productos").child(tomadoresn[i]);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Long auxtot;
                            int auxt;
                            tomadoresn2[i]=dataSnapshot.child("nombre").getValue(String.class);
                            lista.add(tomadoresn2[i]);
                            auxtot = dataSnapshot.child("precio").getValue(Long.class);
                            adaptadorPrin.notifyDataSetChanged();
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                    i++;
                }
                System.out.println("costoooo"+costos);
                adaptadorPrin.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//BUSCADOR
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adaptadorPrin.getFilter().filter(newText);
                return false;
            }
        });



        listaServ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = tomadoresn[position];
                System.out.println("El producto es"+pos);
                Intent intent = new Intent(ComidaListActivity.this, DetallesActivity.class);
                intent.putExtra("producto",pos);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ComidaListActivity.this).toBundle());
                return;
            }
        });

        buttonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ComidaListActivity.this, CarritoActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ComidaListActivity.this).toBundle());
                return;

            }
        });


    }

}
