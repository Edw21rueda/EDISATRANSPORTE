package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class DetallesActivity extends AppCompatActivity {
    String producto,user_id;
    TextView titulo,desc,precio;
    Button agregar;
    FirebaseDatabase mibase;
    DatabaseReference mireferencia;
    private FirebaseAuth nAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

        mibase = FirebaseDatabase.getInstance();
        mireferencia = mibase.getReference();
        producto = getIntent().getStringExtra("producto");
        titulo = (TextView) findViewById(R.id.titulo);
        desc = (TextView) findViewById(R.id.desc);
        precio = (TextView) findViewById(R.id.precio);
        agregar = (Button) findViewById(R.id.button);
        System.out.println("el intent product es"+producto);
        Query query = FirebaseDatabase.getInstance().getReference("Productos").orderByKey().equalTo(producto);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k,i;
                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                //   costos = new String[k];
                System.out.println("entra la consulta");
                i=0;
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    precio.setText(snap.child("precio").getValue().toString());
                    titulo.setText(snap.child("nombre").getValue().toString());
                    desc.setText(snap.child("descripcion").getValue().toString());
                    setTitle("Detalle: "+snap.child("nombre").getValue().toString());

                }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mireferencia.child("Users").child(user_id).child("carrito").child(producto).setValue(true);
                Intent intent = new Intent(DetallesActivity.this, ProductosActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(DetallesActivity.this).toBundle());
                return;

            }
        });


    }
}
