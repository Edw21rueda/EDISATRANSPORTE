package com.example.edisatransporte.Index;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.edisatransporte.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.Principal;

public class MiperfilFragment extends Fragment {
    DatabaseReference databaseReference;
    String user_id;
    private FirebaseAuth nAuth;
    TextInputEditText nombre,ap,tel,correo;
    TextView nomp,viajes,status;
    private View PrincipalView;



    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public MiperfilFragment() {
        // Required empty public constructor
    }
    public static MiperfilFragment newInstance(String param1, String param2) {
        MiperfilFragment fragment = new MiperfilFragment();
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
        // Inflate the layout for this fragment
        PrincipalView = inflater.inflate(R.layout.fragment_miperfil,container,false);
        nombre = PrincipalView.findViewById(R.id.txtnombre);
        nomp = PrincipalView.findViewById(R.id.fulname);
        ap = PrincipalView.findViewById(R.id.ap);
        tel = PrincipalView.findViewById(R.id.tel);
        correo = PrincipalView.findViewById(R.id.cor);
        status = PrincipalView.findViewById(R.id.payment_label2);
        viajes = PrincipalView.findViewById(R.id.payment_desc);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();
        Query query = FirebaseDatabase.getInstance().getReference("Viajes").orderByChild("operador").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
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
        query2.addListenerForSingleValueEvent(new ValueEventListener() {
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
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
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

        return PrincipalView;

    }
}