package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class MiPerfilActivity extends AppCompatActivity {
    DatabaseReference databaseReference;
    String user_id;
    private FirebaseAuth nAuth;
    TextInputEditText nombre,ap,tel,correo;
    TextView nomp,viajes,status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_perfil);
        nombre = findViewById(R.id.txtnombre);
        nomp = findViewById(R.id.fulname);
        ap = findViewById(R.id.ap);
        tel = findViewById(R.id.tel);
        correo = findViewById(R.id.cor);
        status = findViewById(R.id.payment_label2);
        viajes = findViewById(R.id.payment_desc);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();
        setTitle("MI PERFIL");


        Query query = FirebaseDatabase.getInstance().getReference("Viajes").orderByChild("operador").equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                int kk = 0;
                System.out.println("el num de k es"+k);

                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    System.out.println("el sta es"+snap.child("status").getValue(boolean.class));
                   if (snap.child("status").getValue(boolean.class)==true) {
                       kk++;
                   }
                }

                viajes.setText(""+kk);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Query query2 = FirebaseDatabase.getInstance().getReference("Viajes").orderByChild("status").equalTo(false);
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                if (k==1){
                status.setText("En viaje");
                }
                else{
                    status.setText("Sin viaje");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user_id);

        //FIREBASE
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    System.out.println("Mi nombre"+dataSnapshot.child("nombre").getValue(String.class));
                    nombre.setText(""+dataSnapshot.child("nombre").getValue(String.class));
                    nomp.setText(""+dataSnapshot.child("nombre").getValue(String.class));
                    ap.setText(""+dataSnapshot.child("apellidos").getValue(String.class));
                    tel.setText(""+dataSnapshot.child("telefono").getValue(String.class));
                    correo.setText(""+dataSnapshot.child("correo").getValue(String.class));
                    //          tomadoresn2[i]=dataSnapshot.child("nombre").getValue(String.class)+" "+dataSnapshot.child("apellidos").getValue(String.class);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
}
