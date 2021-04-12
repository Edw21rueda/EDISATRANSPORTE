package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class CarritoActivity extends AppCompatActivity {
    int cost, k,i;
    ListView listaResultado;
    String servf="",servf2="",pos,pos2,nombre,apt,tel,dir,fecha,hora;
    private ArrayList<String> lista;
    private ArrayAdapter<String> adaptador1;
    private ArrayList<Integer> preciosAsig;
    private Button buttonMapa,buttonServicios;
    ListView listaServ;
    private ArrayList<String> listaSerTot,listaSerTot2;
    private ArrayAdapter<String> adaptadorPrin;
    String cadena,press1,feho,fec,user_id;
    String[] tomadoresn,tomadoresn2,costos;
    TextView sub,tot;
    int total=0;
    DatabaseReference databaseReference;
    private FirebaseAuth nAuth;
    FirebaseDatabase mibase;
    DatabaseReference mireferencia;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

        listaSerTot = new ArrayList<>();
        mibase = FirebaseDatabase.getInstance();
        mireferencia = mibase.getReference();

        listaSerTot2 = new ArrayList<>();
        buttonMapa = (Button) findViewById(R.id.btnubi);
        sub = (TextView)findViewById(R.id.txtsubtotal);
        tot = (TextView)findViewById(R.id.txttot);


        listaServ =(ListView)findViewById(R.id.simple_list);
        // listaResultado =(ListView)findViewById(R.id.listServ);//Se agregan seleccionados
        //listEstudios = new ArrayList<Estudio>();
        lista = new ArrayList<>();
        adaptadorPrin = new ArrayAdapter<String>(CarritoActivity.this, android.R.layout.simple_list_item_1,lista);
        listaServ.setAdapter(adaptadorPrin);

        preciosAsig = new ArrayList<>();//ARREGLO TEMPORAL

        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("carrito");

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
                    Query query = FirebaseDatabase.getInstance().getReference("Productos").child(tomadoresn[i]);
                        query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Long auxtot;
                            int auxt;
                            tomadoresn2[i]=dataSnapshot.child("nombre").getValue(String.class);
                            lista.add(tomadoresn2[i]);
                            auxtot = dataSnapshot.child("precio").getValue(Long.class);
                            preciosAsig.add(auxtot.intValue());
                            auxt = auxtot.intValue();
                            System.out.println("El long es"+auxt);
                            total = total + auxt;
                            System.out.println("EL dato esss"+total);
                            adaptadorPrin.notifyDataSetChanged();
                            sub.setText("$"+total);

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });

                    System.out.println("dddd"+costos[i]+"pos"+i);
                    i++;
                }

//              System.out.println("costoooo"+total);
                adaptadorPrin.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        listaServ.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = adaptadorPrin.getItem(position);
                System.out.println("El producto es"+pos);
                Intent intent = new Intent(CarritoActivity.this, DetallesActivity.class);
                intent.putExtra("producto",pos);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CarritoActivity.this).toBundle());
                return;
            }
        });
        listaServ.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                LayoutInflater myLayout = LayoutInflater.from(CarritoActivity.this);
                final View dialogView = myLayout.inflate(R.layout.wardes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(CarritoActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("¿Seguro que quieres eliminar este producto?");
                builder.setCancelable(false);
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                pos2 = tomadoresn[position];
                mireferencia.child("Users").child(user_id).child("carrito").child(pos2).setValue(null);
                total = total - preciosAsig.get(position);
                sub.setText("$"+total);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                    }
                });
                AlertDialog titulo= builder.create();
                       try {
                                        titulo.show();
                                }
                                catch (WindowManager.BadTokenException e) {
                                        //use a log message
                                }

                return true;
            }
        });

        buttonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarritoActivity.this, FinalizarActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CarritoActivity.this).toBundle());
                return;

            }
        });


    }

}
