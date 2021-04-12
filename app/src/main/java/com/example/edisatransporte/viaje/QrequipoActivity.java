package com.example.edisatransporte.viaje;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

public class QrequipoActivity extends AppCompatActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrequipo);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

    }
    public void scanBarra(View v) {
        try { //Inicia escaneo desde: com.google.zxing.client.android.SCAN
            Intent i1 = new Intent(ACTION_SCAN);
            i1.putExtra("SCAN_MODE", "PRODUCT_MODE");
            startActivityForResult(i1, 0);
        } catch (ActivityNotFoundException anfe) { //Al obtenerlo, muestra diálogo de descarga
            showDialog(QrequipoActivity.this, "No hay scanner", "Descargar scanner?", "Si", "No").show();
        }
    }
    public void scanQR(View v) { //Modo QR del producto
        try { //Inicia escaneo desde: com.google.zxing.client.android.SCAN
            Intent i2 = new Intent(ACTION_SCAN);
            i2.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(i2, 0);
        } catch (ActivityNotFoundException anfe) { //Al obtenerlo, muestra diálogo de descarga
            showDialog(QrequipoActivity.this, "No hay scanner", "Descargar scanner?", "Si", "No").show();
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
        super.onActivityResult(requestCode, resultCode, i);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) { //Obtiene extras regresados por el intento
                sr = i.getStringExtra("SCAN_RESULT");
                LayoutInflater myLayout = LayoutInflater.from(QrequipoActivity.this);
                final View dialogView = myLayout.inflate(R.layout.exitodes, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(QrequipoActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("EQUIPO "+sr+" AGREGADO");
                builder.setCancelable(false);
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        System.out.println("EL RESULTADO ES"+sr);
                        Intent intent = new Intent(QrequipoActivity.this, IndexActivity.class);
                        startActivity(intent);
                        return;
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.cancel();
                    }
                });
                AlertDialog titulo= builder.create();
                titulo.show();
            }
        }
    }

}



