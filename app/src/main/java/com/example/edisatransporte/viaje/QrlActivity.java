package com.example.edisatransporte.viaje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.edisatransporte.R;
import com.example.edisatransporte.Servicelocation.MainlocationActivity;
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

public class QrlActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    String[] descrpcion = new String[200];
    FirebaseDatabase mibase;
    DateFormat dateFormat;
    Date date;
    String user_id,sr;
    String srs;

    DateFormat hourFormat;
    Button btnqr;

    private FirebaseAuth nAuth;
    DatabaseReference mireferencia;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrl);
        btnqr = findViewById(R.id.scanner);
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
                            new IntentIntegrator(QrlActivity.this).initiateScan();
                        }

                        for(final DataSnapshot snap:dataSnapshot.getChildren()){
                            System.out.println("el sta es"+snap.child("status").getValue(boolean.class));
                            if (snap.child("status").getValue(boolean.class)==false) {
                                auxp=1;
                                srs = snap.getKey();
                            }
                        }
                        if (auxp==0){
                            new IntentIntegrator(QrlActivity.this).initiateScan();
                        }
                        else{
                            LayoutInflater myLayout = LayoutInflater.from(QrlActivity.this);
                            final View dialogView = myLayout.inflate(R.layout.wardes, null);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(QrlActivity.this);
                            //dialog.setTitle("Sin servicio en tu zona");
                            builder.setView(dialogView);
                            TextView txt =(TextView)dialogView.findViewById(R.id.username);
                            txt.setText("Tienes un viaje iniciado. ¡Finalizalo!");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    Intent intent = new Intent(QrlActivity.this, MainlocationActivity.class);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null)
            if (result.getContents() != null){
                sr = result.getContents();
                date = new Date();
//Caso 1: obtener la hora y salida por pantalla con formato:
                hourFormat = new SimpleDateFormat("HH:mm:ss");
                System.out.println("Hora: "+hourFormat.format(date));
//Caso 2: obtener la fecha y salida por pantalla con formato:
                dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                System.out.println("Fecha: "+dateFormat.format(date));
                mibase = FirebaseDatabase.getInstance();
                mireferencia = mibase.getReference();
                Query query = FirebaseDatabase.getInstance().getReference("Viajes").child(sr);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int k,i;
                        k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                        System.out.println("el numero es"+k);
                        //   costos = new String[k];
                        if (k!=0){
                            Intent intent = new Intent(QrlActivity.this, MainlocationActivity.class);
//                            Intent intent = new Intent(QrlActivity.this, ViajemainActivity.class);
                            intent.putExtra("sr",sr);
                            startActivity(intent);
                            return;
                        }
                        else {

                            Intent intent = new Intent(QrlActivity.this, AgregarEquipoActivity.class);
                           // Intent intent = new Intent(QrlActivity.this, MainlocationActivity.class);
                            intent.putExtra("sr",sr);
                            startActivity(intent);
                            return;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }else{
                System.out.println("Erro");
            }
    }
}