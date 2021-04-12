package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FinalizarActivity extends AppCompatActivity {

    TextView n,ap,t,d,f,h,l1,l2,tot;
    FirebaseDatabase mibase;
    DatabaseReference mireferencia;
    private FirebaseAuth nAuth;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizar);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

        mibase = FirebaseDatabase.getInstance();
        mireferencia = mibase.getReference();

        n = (TextView)findViewById(R.id.edtnombre);
        ap = (TextView)findViewById(R.id.txtapt);
        t = (TextView)findViewById(R.id.txttel);
        d = (TextView)findViewById(R.id.txtdir);
        f = (TextView)findViewById(R.id.txtfecha);
        h = (TextView)findViewById(R.id.txthora);
        tot = (TextView)findViewById(R.id.txttotal);
        Query query = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k,i;
                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                //   costos = new String[k];
                if (k!=0){
                    n.setText(dataSnapshot.child("nombre").getValue(String.class));
                    ap.setText(dataSnapshot.child("apellidos").getValue(String.class));
                    t.setText(dataSnapshot.child("telefono").getValue(String.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
