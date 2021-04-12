package com.example.edisatransporte.viaje;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.android.volley.RequestQueue;
import com.example.edisatransporte.R;
import com.example.edisatransporte.ViajemainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class QRActivity extends Activity {
    RequestQueue requestQueue;
    String[] descrpcion = new String[200];
    FirebaseDatabase mibase;
    DateFormat dateFormat;
    Date date;
    String user_id,sr;
    DateFormat hourFormat;

    private FirebaseAuth nAuth;
    DatabaseReference mireferencia;
    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    @Override
    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_q_r);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

    }
    public void scanBarra(View v) {
        try { //Inicia escaneo desde: com.google.zxing.client.android.SCAN
            Intent i1 = new Intent(ACTION_SCAN);
            i1.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(i1, 0);
        } catch (ActivityNotFoundException anfe) { //Al obtenerlo, muestra diálogo de descarga
            showDialog(QRActivity.this, "No hay scanner", "Descargar scanner?", "Si", "No").show();
        }
    }

    public void scanQR(View v) { //Modo QR del producto
        try { //Inicia escaneo desde: com.google.zxing.client.android.SCAN
            Intent i2 = new Intent(ACTION_SCAN);
            i2.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(i2, 0);
        } catch (ActivityNotFoundException anfe) { //Al obtenerlo, muestra diálogo de descarga
            showDialog(QRActivity.this, "No hay scanner", "Descargar scanner?", "Si", "No").show();
        }
    }
    private static AlertDialog showDialog(final Activity a, CharSequence st, CharSequence mn, CharSequence bnSi, CharSequence bnNo) {
        AlertDialog.Builder adb = new AlertDialog.Builder(a); //Alert para descarga
        adb.setTitle(st);
        adb.setMessage(mn);
        adb.setPositiveButton(bnSi, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent in = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    a.startActivity(in);
                } catch (ActivityNotFoundException anfe) {
                }
            }
        });
        adb.setNegativeButton(bnNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface di, int i) {
            }
        });
        return adb.show();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onActivityResult(int requestCode, int resultCode, Intent i) { //Resultado
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) { //Obtiene extras regresados por el intento
                 sr = i.getStringExtra("SCAN_RESULT");
                String sf = i.getStringExtra("SCAN_RESULT_FORMAT");
                // Toast t = Toast.makeText(this, "Content:" + sr + " Format:" + sf, Toast.LENGTH_LONG);
                //t.show();

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
                            Intent intent = new Intent(QRActivity.this, ViajemainActivity.class);
                            intent.putExtra("sr",sr);
                            startActivity(intent);
                            return;
                        }
                        else {

                            Intent intent = new Intent(QRActivity.this, AgregarEquipoActivity.class);
                            intent.putExtra("sr",sr);
                            startActivity(intent);
                            return;
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        }
    }

}



