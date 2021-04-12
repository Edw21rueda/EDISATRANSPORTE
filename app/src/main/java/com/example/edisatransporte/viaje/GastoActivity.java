package com.example.edisatransporte.viaje;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.edisatransporte.Class.LoadingDialog;
import com.example.edisatransporte.IndexActivity;
import com.example.edisatransporte.R;
import com.example.edisatransporte.Servicelocation.MainlocationActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class GastoActivity extends AppCompatActivity {
    Button btnChoose,btnUpload,btnhome;
    ImageView imageUpload;
    final int CODE_GALLERY_REQUEST = 999;
    Bitmap bitmap;
    EditText monto,concepto;
    String urlUpload="https://sistemavaltons.com.mx/imageup.php";
    FirebaseDatabase mibase;
    DatabaseReference mireferencia;
    String sr,c;
    SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gasto);
        preferences = getSharedPreferences("idcarta", Context.MODE_PRIVATE);
        sr = ""+preferences.getInt("idnum",0);
        btnChoose = findViewById(R.id.btnchoose);
        monto = findViewById(R.id.monto);
        concepto = findViewById(R.id.concepto);
        btnUpload = findViewById(R.id.btnup);
        btnhome = findViewById(R.id.btnhome);
        imageUpload = findViewById(R.id.image);
        mibase = FirebaseDatabase.getInstance();
        mireferencia = mibase.getReference();

        btnhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GastoActivity.this, MainlocationActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(GastoActivity.this).toBundle());
                finish();
                return;
            }
        });

        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  ActivityCompat.requestPermissions(
                        GastoActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        CODE_GALLERY_REQUEST
                );*/
              Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
              startActivityForResult(intent,0);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = concepto.getText().toString();

                final LoadingDialog loadingDialog = new LoadingDialog(GastoActivity.this);
                loadingDialog.startLoadingDialog();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                },15000);
                Query query = FirebaseDatabase.getInstance().getReference("Gastos").child(sr);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                        int kk =0;
                        System.out.println("el num de k es en gasto"+k);
                        for(DataSnapshot snap:dataSnapshot.getChildren()){
                            if (snap.getRef().getKey().compareTo(c)==0){
                             kk=1;
                            }
                        }
                        if (kk ==0){
                            if (bitmap!=null) {
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        String g = monto.getText().toString();
                                        int gas;
                                        gas = Integer.parseInt(g);
                                        mireferencia.child("Gastos").child(sr).child(c).setValue(gas);
                                        //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                                        LayoutInflater myLayout = LayoutInflater.from(GastoActivity.this);
                                        final View dialogView = myLayout.inflate(R.layout.registrades, null);
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(GastoActivity.this);
                                        //dialog.setTitle("Sin servicio en tu zona");
                                        builder.setView(dialogView);
                                        TextView txt = (TextView) dialogView.findViewById(R.id.username);
                                        txt.setText("Gasto registrado con éxito");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogo1, int id) {

                                                loadingDialog.dismissDialog();
                                                Intent intent = new Intent(GastoActivity.this, MainlocationActivity.class);
                                                intent.putExtra("sr", sr);
                                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(GastoActivity.this).toBundle());
                                                finish();
                                                return;

                                            }
                                        });
                                        AlertDialog titulo = builder.create();
                                        titulo.show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                                        LayoutInflater myLayout = LayoutInflater.from(GastoActivity.this);
                                        final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(GastoActivity.this);
                                        //dialog.setTitle("Sin servicio en tu zona");
                                        builder.setView(dialogView);
                                        TextView txt =(TextView)dialogView.findViewById(R.id.username);
                                        txt.setText("Error al cargar viaje: Revisa tu conexión a internet y reintenta.");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogo1, int id) {
                                                Intent intent = new Intent(GastoActivity.this, IndexActivity.class);
                                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(GastoActivity.this).toBundle());
                                                return;
                                            }
                                        });
                                        AlertDialog titulo= builder.create();
                                        titulo.show();
                                    }
                                }){
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map<String,String> params =  new HashMap<>();
                                        String imageData = imagetostring(bitmap);
                                        params.put("image",imageData);
                                        System.out.println("el nombre es"+c);
                                        params.put("nombre",c);
                                        params.put("viaje",sr);
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(GastoActivity.this);
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        15000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                requestQueue.add(stringRequest);
                            }
                            else {
                                LayoutInflater myLayout = LayoutInflater.from(GastoActivity.this);
                                final View dialogView = myLayout.inflate(R.layout.wardes, null);
                                final AlertDialog.Builder builder = new AlertDialog.Builder(GastoActivity.this);
                                //dialog.setTitle("Sin servicio en tu zona");
                                builder.setView(dialogView);
                                TextView txt = (TextView) dialogView.findViewById(R.id.username);
                                txt.setText("Debes agregar una imagen");
                                builder.setCancelable(false);
                                builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogo1, int id) {
                                        loadingDialog.dismissDialog();

                                        dialogo1.cancel();
                                    }
                                });
                                AlertDialog titulo = builder.create();
                                titulo.show();

                            }
                        }
                        else{
                            LayoutInflater myLayout = LayoutInflater.from(GastoActivity.this);
                            final View dialogView = myLayout.inflate(R.layout.wardes, null);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(GastoActivity.this);
                            //dialog.setTitle("Sin servicio en tu zona");
                            builder.setView(dialogView);
                            TextView txt = (TextView) dialogView.findViewById(R.id.username);
                            txt.setText("Ya tienes un gasto con ese concepto. Debes ingresar otro concepto");
                            builder.setCancelable(false);
                            builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogo1, int id) {
                                    loadingDialog.dismissDialog();

                                    dialogo1.cancel();
                                }
                            });
                            AlertDialog titulo = builder.create();
                            titulo.show();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode==CODE_GALLERY_REQUEST){
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Selecciona la imagen"),CODE_GALLERY_REQUEST);
            }
            else {
                Toast.makeText(getApplicationContext(),"Debes aceptar los permisos",Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        bitmap=(Bitmap)data.getExtras().get("data");
        imageUpload.setImageBitmap(bitmap);
        /*
        if (requestCode==CODE_GALLERY_REQUEST && resultCode==RESULT_OK && data !=null){
            Uri filePath = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(filePath);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageUpload.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);*/
    }
    private String imagetostring(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imagebytes = outputStream.toByteArray();
        String encodedimage = Base64.encodeToString(imagebytes,Base64.DEFAULT);
        return encodedimage;
    }
}
