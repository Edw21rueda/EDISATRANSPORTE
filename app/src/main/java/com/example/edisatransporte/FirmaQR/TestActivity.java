package com.example.edisatransporte.FirmaQR;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.edisatransporte.Mainviaje.MainviajeActivity;
import com.example.edisatransporte.R;
import com.example.edisatransporte.Servicelocation.MainlocationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    RequestQueue requestQueue;
    RadioGroup rg1,rg2,rg3,rg4;
    RadioButton rb1,rbb,rbb2,rbb3,rbb4,rb2,rb3,rb4,rb5,rb6,rb7,rb8;
    Button btnfin;
    String sr,val11,val22,val33,val44;
    String val1,val2,val3,val4;
    EditText com;
    int aux=0;
    Date date;
    String user_id;
    DateFormat hourFormat;

    private FirebaseAuth nAuth;
    DatabaseReference mireferencia;
    FirebaseDatabase mibase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        btnfin = (Button) findViewById(R.id.btnfin);
        com =(EditText) findViewById(R.id.editText2);
        mibase = FirebaseDatabase.getInstance();
        mireferencia = mibase.getReference();
       /* rb1 = (RadioButton) findViewById(R.id.rbtn1);
        rb2 = (RadioButton) findViewById(R.id.rbtn2);
        rb3 = (RadioButton) findViewById(R.id.rbtn3);
        rb4 = (RadioButton) findViewById(R.id.rbtn6);
        rb5 = (RadioButton) findViewById(R.id.rbtn);
        rb6 = (RadioButton) findViewById(R.id.rbtn5);
        rb7 = (RadioButton) findViewById(R.id.rbtn4);
        rb8 = (RadioButton) findViewById(R.id.rbtn7);*/
        rg1 = (RadioGroup) findViewById(R.id.rg1);
        rg2 = (RadioGroup) findViewById(R.id.rg2);
        rg3 = (RadioGroup) findViewById(R.id.rg3);
        rg4 = (RadioGroup) findViewById(R.id.rg4);

        sr = getIntent().getStringExtra("sr");

        int b=0;
//        crear("http://nube-nyssen.ddns.net:8080/esdm_v4p/Main_app/Utilerias/reportes/crea_pdf_firmado.php?id_carta="+sr);
        Toast.makeText(getApplicationContext(), "CREANDO CARTA PORTE FIRMADA......", Toast.LENGTH_SHORT).show();

        //crearc("https://sistemavaltons.com.mx/Main_app/Utilerias/reportes/crea_pdf_firmado2.php");
        crearc("http://esdmproyectos.com/EsdmConte/Main_app/Utilerias/reportes/crea_pdf_firmado2.php");
        Toast.makeText(getApplicationContext(), "CREANDO CARTA PORTE FIRMADA......", Toast.LENGTH_SHORT).show();
        //crearc("https://sistemavaltons.com.mx/Main_app/Utilerias/reportes/crea_pdf_firmado2.php");
        crearc("http://esdmproyectos.com/EsdmConte/Main_app/Utilerias/reportes/crea_pdf_firmado2.php");
        Toast.makeText(getApplicationContext(), "CREANDO CARTA PORTE FIRMADA......", Toast.LENGTH_SHORT).show();
        //crearc("https://sistemavaltons.com.mx/Main_app/Utilerias/reportes/crea_pdf_firmado2.php");
        crearc("http://esdmproyectos.com/EsdmConte/Main_app/Utilerias/reportes/crea_pdf_firmado2.php");
        Toast.makeText(getApplicationContext(), "CREANDO CARTA PORTE FIRMADA......", Toast.LENGTH_SHORT).show();

        btnfin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url2 ="https://sistemavaltons.com.mx/guarda_encu.php";
                String url ="http://esdmproyectos.com/EsdmConte/guarda_encu.php";
                ejecutarservicio(url);
                //ejecutarservicio(url2);

               // envioc("https://sistemavaltons.com.mx/Main_app/Utilerias/reportes/envar_carta_portep.php");
                envioc("http://esdmproyectos.com/EsdmConte/Main_app/Utilerias/reportes/envar_carta_porteprueba.php");
            }
        });
    }

    //check
    public void checkButton(View v){


    }

    private void ejecutarservicio(String URL){
        int radioId = rg1.getCheckedRadioButtonId();
        int radioId2 = rg2.getCheckedRadioButtonId();
        int radioId3 = rg3.getCheckedRadioButtonId();
        int radioId4 = rg4.getCheckedRadioButtonId();
        rbb=findViewById(radioId);
        rbb2=findViewById(radioId2);
        rbb3=findViewById(radioId3);
        rbb4=findViewById(radioId4);
        val11 = ""+rbb.getText();
        val22 = "" + rbb2.getText();
        val33 = "" + rbb3.getText();
        val44 = "" + rbb4.getText();
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "ENCUESTA REGISTRADA", Toast.LENGTH_SHORT).show();

//            Toast.makeText(getApplicationContext(),sr,Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //   Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "ENVÍO REALIZADO", Toast.LENGTH_SHORT).show();
                LayoutInflater myLayout = LayoutInflater.from(TestActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error: Favor de reintentar le proceso");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(TestActivity.this, MainviajeActivity.class);
                        intent.putExtra("sr",sr);
                        startActivity(intent);
                        finish();
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                       try {
                                        dialog.show();
                                }
                                catch (WindowManager.BadTokenException e) {
                                        //use a log message
                                };

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros= new HashMap<String, String>();
                parametros.put("id_carta",sr);
                parametros.put("pregun1_res",val11);
                parametros.put("pregun2_res",val22);
                parametros.put("pregun3_res",val33);
                parametros.put("pregun4_res",val44);
                parametros.put("comentario",com.getText().toString());

                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    private void envioc(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                date = new Date();
//Caso 1: obtener la hora y salida por pantalla con formato:
                hourFormat = new SimpleDateFormat("HH:mm:ss");

                //Toast.makeText(getApplicationContext(), "ENVÍO REALIZADO", Toast.LENGTH_SHORT).show();
                LayoutInflater myLayout = LayoutInflater.from(TestActivity.this);
                final View dialogView = myLayout.inflate(R.layout.exitodes, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("ENVÍO REALIZADO Y ENVIADO A NUESTRA BASE DE DATOS, ¡GRACIAS POR SU PREFERENCIA!");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mireferencia.child("Viajes").child(sr).child("entrega").setValue(true);
                        mireferencia.child("Viajes").child(sr).child("horaentrega").setValue(hourFormat.format(date));

                        Intent intent = new Intent(TestActivity.this, MainviajeActivity.class);
                        intent.putExtra("sr",sr);
                        startActivity(intent);
                        finish();
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                       try {
                                        dialog.show();
                                }
                                catch (WindowManager.BadTokenException e) {
                                        //use a log message
                                };

//            Toast.makeText(getApplicationContext(),sr,Toast.LENGTH_SHORT).show();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //     Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                //  Toast.makeText(getApplicationContext(), "ENVÍO REALIZADO ", Toast.LENGTH_SHORT).show();
                LayoutInflater myLayout = LayoutInflater.from(TestActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error: Favor de reintentar le proceso");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(TestActivity.this, MainviajeActivity.class);
                        intent.putExtra("sr",sr);
                        startActivity(intent);
                        finish();
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                       try {
                                        dialog.show();
                                }
                                catch (WindowManager.BadTokenException e) {
                                        //use a log message
                                };

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros= new HashMap<String, String>();
                parametros.put("id_carta",sr);
                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }
    private int crearc(String URL){
        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                aux++;
                if (aux==2) {
                    Toast.makeText(getApplicationContext(), "CARTA PORTE CREADA", Toast.LENGTH_SHORT).show();
//            Toast.makeText(getApplicationContext(),sr,Toast.LENGTH_SHORT).show();
                    btnfin.setVisibility(View.VISIBLE);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //  Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), "ENVÍO REALIZADO Y ENVIADO A NUESTRA BASE DE DATOS, ¡GRACIAS POR SU PREFERENCIA!", Toast.LENGTH_SHORT).show();
                LayoutInflater myLayout = LayoutInflater.from(TestActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error: Favor de reintentar le proceso");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(TestActivity.this, MainviajeActivity.class);
                        intent.putExtra("sr",sr);
                        startActivity(intent);
                        finish();
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                       try {
                                        dialog.show();
                                }
                                catch (WindowManager.BadTokenException e) {
                                        //use a log message
                                };

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> parametros= new HashMap<String, String>();
                parametros.put("id_carta",sr);

                return parametros;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
        return 1;
    }

}
