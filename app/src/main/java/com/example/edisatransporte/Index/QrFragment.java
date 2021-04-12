 package com.example.edisatransporte.Index;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.edisatransporte.Mainviaje.MainviajeActivity;
import com.example.edisatransporte.R;
import com.example.edisatransporte.Servicelocation.MainlocationActivity;
import com.example.edisatransporte.viaje.AgregarEquipoActivity;
import com.example.edisatransporte.viaje.QrlActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QrFragment extends Fragment {
    private View PrincipalView;
    String user_id;
    String srs,sr;
    DateFormat dateFormat;
    Date date;

    DateFormat hourFormat;
    Button btnqr;

    FirebaseDatabase mibase;
    private FirebaseAuth nAuth;
    DatabaseReference mireferencia;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public QrFragment() {
    }
    public static QrFragment newInstance(String param1, String param2) {
        QrFragment fragment = new QrFragment();
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
        PrincipalView = inflater.inflate(R.layout.fragment_qr,container,false);
        btnqr = PrincipalView.findViewById(R.id.scanner);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();
        btnqr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = FirebaseDatabase.getInstance().getReference("Viajes").orderByChild("operador").equalTo(user_id);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        int k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                        int auxp = 0;
                        System.out.println("el num de k es"+k);
                        if (k==0)
                        {
                            new IntentIntegrator(getActivity()).initiateScan();
                        }

                        for(final DataSnapshot snap:dataSnapshot.getChildren()){
                            System.out.println("el sta es"+snap.child("status").getValue(boolean.class));
                            if (snap.child("status").getValue(boolean.class)==false) {
                                auxp=1;
                                srs = snap.getKey();

                            }

                        }
                        if (auxp==0){
                            new IntentIntegrator(getActivity()).initiateScan();
                        }
                        else{
                            LayoutInflater myLayout = LayoutInflater.from(getActivity());
                            final View dialogView = myLayout.inflate(R.layout.wardes, null);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            //dialog.setTitle("Sin servicio en tu zona");
                            builder.setView(dialogView);
                            TextView txt =(TextView)dialogView.findViewById(R.id.username);
                            txt.setText("Tienes un viaje iniciado. ¡Finalizalo!");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    Intent intent = new Intent(getActivity(), MainviajeActivity.class);
                                    intent.putExtra("sr",srs);
                                    startActivity(intent);
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

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        return PrincipalView;

    }


}