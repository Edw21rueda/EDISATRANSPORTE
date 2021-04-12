package com.example.edisatransporte.FirmaQR;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.edisatransporte.Mainviaje.MainviajeActivity;
import com.example.edisatransporte.R;
import com.example.edisatransporte.Servicelocation.MainlocationActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class InicioActivity extends AppCompatActivity {
    SharedPreferences preferences;
    Button btns;
    TextView txtfolio;
    RequestQueue requestQueue;
    String descrpcion;
    MyCanvas myCanvas;
    Bitmap bitmap;
    View vcanva;
    String sr;
    EditText nom,puesto,apellidos;
    StringRequest stringRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        preferences = getSharedPreferences("idcarta", Context.MODE_PRIVATE);
        sr = ""+preferences.getInt("idnum",0);
        btns = (Button) findViewById(R.id.btns);
        nom = (EditText)findViewById(R.id.edtnombre);
        apellidos = findViewById(R.id.edtapellidos);
        puesto = (EditText)findViewById(R.id.edtpuesto);
        vcanva = (View)findViewById(R.id.view);


        myCanvas = new MyCanvas(this,null);
         //  String url ="https://sistemavaltons.com.mx/Main_app/consultacartasfirmadas.php?id="+sr;
        String url ="http://esdmproyectos.com/EsdmConte/Main_app/consultacartasfirmadas.php?id="+sr;
        consulta(url);





        btns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nom.getText().toString().isEmpty()){
                    nom.setError("Ingresa tu nombre");
                    LayoutInflater myLayout = LayoutInflater.from(InicioActivity.this);
                    final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this);
                    //dialog.setTitle("Sin servicio en tu zona");
                    builder.setView(dialogView);

                    TextView txt =(TextView)dialogView.findViewById(R.id.username);
                    txt.setText("Ingresa tu nombre");

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                else if (apellidos.getText().toString().isEmpty()){
                    apellidos.setError("Ingresa tus apellidos");
                    LayoutInflater myLayout = LayoutInflater.from(InicioActivity.this);
                    final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this);
                    //dialog.setTitle("Sin servicio en tu zona");
                    builder.setView(dialogView);

                    TextView txt =(TextView)dialogView.findViewById(R.id.username);
                    txt.setText("Ingresa tus apellidos");

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                else if (puesto.getText().toString().isEmpty()){
                    puesto.setError("Ingresa tu puesto");
                    LayoutInflater myLayout = LayoutInflater.from(InicioActivity.this);
                    final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this);
                    //dialog.setTitle("Sin servicio en tu zona");
                    builder.setView(dialogView);
                    TextView txt =(TextView)dialogView.findViewById(R.id.username);
                    txt.setText("Ingresa tu puesto");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            System.out.println("El nomnre"+nom.getText().toString()+" "+apellidos.getText().toString());

                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }
                else {
                    //String url = "https://sistemavaltons.com.mx/Main_app/test.php";
                    String url = "http://esdmproyectos.com/EsdmConte/Main_app/test.php";
                    ejecutarservicio(url);
                    System.out.println("El nomnre"+nom.getText().toString()+" "+apellidos.getText().toString());
                }
            }
        });

        String sr1="798";


        // tomadores("http://nube-nyssen.ddns.net:8080/vlatonsapp/consulta_carta.php?id="+sr);


    }


    private void cargarWebServies(){

        String url="http://nube-nyssen.ddns.net:8080/vlatonsapp/consulta_carta.php?";
        stringRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String sr1= sr;
                String imag = convertirImgString(bitmap);
                Map<String,String> parametros=new HashMap<>();
                parametros.put("srl",sr);
                parametros.put("imagen",imag);
                return parametros;

            }
        };
    }

    private String convertirImgString(Bitmap bitmap) {
        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imageByte=array.toByteArray();
        String imageString= Base64.encodeToString(imageByte,Base64.DEFAULT);

        return imageString;
    }
    //CONSULTA DE SERVICIOS
    private void crear(String URL){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override

            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;
                Toast.makeText(getApplicationContext(), "" + response.length(), Toast.LENGTH_SHORT).show();
                int ii=0;
                int jj = response.length();

                //  Toast.makeText(getApplicationContext(), ""+ii, Toast.LENGTH_SHORT).show();

                for (int i=ii; i < response.length(); i++) {

                    try {
                        jsonObject = response.getJSONObject(i);
                        //ttomadores.setText(tomadoresn[i]);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);


    }
    //FIN DE CONSULTA
    //CONSULTA DE SERVICIOS
    private void tomadores(String URL){

        JsonArrayRequest jsonArrayRequest=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {

            @Override

            public void onResponse(JSONArray response) {

                JSONObject jsonObject = null;
                Toast.makeText(getApplicationContext(), "" + response.length(), Toast.LENGTH_SHORT).show();
                int ii=0;
                int jj = response.length();

                //  Toast.makeText(getApplicationContext(), ""+ii, Toast.LENGTH_SHORT).show();

                for (int i=ii; i < response.length(); i++) {

                    try {
                        jsonObject = response.getJSONObject(i);
                        descrpcion= jsonObject.getString("Nombre");
                        //ttomadores.setText(tomadoresn[i]);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);


    }
    //FIN DE CONSULTA
    private void ejecutarservicio(String URL){
        vcanva.setDrawingCacheEnabled(true);
        vcanva.buildDrawingCache();
        bitmap = Bitmap.createBitmap(vcanva.getDrawingCache());


        vcanva.setDrawingCacheEnabled(false);
        vcanva.destroyDrawingCache();



        StringRequest stringRequest=new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), "FIRMA GUARDADA"+response, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(InicioActivity.this, TestActivity.class);
                intent.putExtra("sr",sr);
                startActivity(intent);
                finish();
                return;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                LayoutInflater myLayout = LayoutInflater.from(InicioActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error: Favor de reintentar le proceso");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(InicioActivity.this, MainviajeActivity.class);
                        intent.putExtra("sr",sr);
                        startActivity(intent);
                        finish();
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String imag = convertirImgString(bitmap);
                Map<String,String> parametros= new HashMap<String, String>();
                parametros.put("nombre",nom.getText().toString()+" "+apellidos.getText().toString());
                parametros.put("id_carta",sr);
                parametros.put("imag",imag);
                parametros.put("puesto",puesto.getText().toString());
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

    //CONSULTA CARTAS FIRMADAS
    private void consulta(String URL){

        StringRequest stringRequest=new StringRequest(URL, new Response.Listener<String>() {

            @Override

            public void onResponse(String response) {
                if (Integer.parseInt(response)==1){
                    LayoutInflater myLayout = LayoutInflater.from(InicioActivity.this);
                    final View dialogView = myLayout.inflate(R.layout.exitodes, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this);
                    //dialog.setTitle("Sin servicio en tu zona");
                    builder.setView(dialogView);

                    TextView txt =(TextView)dialogView.findViewById(R.id.username);
                    txt.setText("Carta firmada, no se puede volver a firmar");

                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(InicioActivity.this, MainviajeActivity.class);
                            intent.putExtra("sr",sr);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //    Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                LayoutInflater myLayout = LayoutInflater.from(InicioActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(InicioActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Error: Favor de reintentar le proceso");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(InicioActivity.this, MainviajeActivity.class);
                        intent.putExtra("sr",sr);
                        startActivity(intent);
                        finish();
                        return;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        requestQueue= Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                15000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);


    }


}
