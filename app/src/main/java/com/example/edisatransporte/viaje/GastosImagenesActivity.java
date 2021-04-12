package com.example.edisatransporte.viaje;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.example.edisatransporte.R;

public class GastosImagenesActivity extends AppCompatActivity {

    String id,gasto;
    RequestQueue requestQueue;
    ImageView imagen;
    String []  bar;
    TextView concepto,monto;
    Button regreso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos_imagenes);
        imagen=findViewById(R.id.imggasto);
        id = getIntent().getStringExtra("id");
        regreso= findViewById(R.id.btnup);
        concepto = findViewById(R.id.txtconcepto);
        monto = findViewById(R.id.txtmonto);
        concepto.setText("PAGO DEL VIAJE"+id);
        setTitle("PAGO DEL VIAJE");
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        cargaimagen(id);
        regreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GastosImagenesActivity.this, ViajesFinActivity.class);
                intent.putExtra("producto",id);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(GastosImagenesActivity.this).toBundle());
                return;
            }
        });
    }

    private void cargaimagen(String id){
        String url= "https://sistemavaltons.com.mx/gastos/images/"+id+"/pagogasto.jpeg";
        url=url.replace(" ","%20");
        ImageRequest imageRequest=new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imagen.setImageBitmap(response);
            }
        },0,0, ImageView.ScaleType.CENTER,null,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(getApplication(),"Error al cargar)",Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(imageRequest);
    }
}
