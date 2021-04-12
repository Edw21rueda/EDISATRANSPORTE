package com.example.edisatransporte.Mainviaje;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.edisatransporte.viaje.GastoActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AgregargastoBlankFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private View PrincipalView;

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


    public AgregargastoBlankFragment() {
        // Required empty public constructor
    }
    public static AgregargastoBlankFragment newInstance(String param1, String param2) {
        AgregargastoBlankFragment fragment = new AgregargastoBlankFragment();
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
        PrincipalView = inflater.inflate(R.layout.fragment_agregargasto_blank,container,false);
        preferences = getActivity().getSharedPreferences("idcarta", Context.MODE_PRIVATE);
        sr = ""+preferences.getInt("idnum",0);
        btnChoose = PrincipalView.findViewById(R.id.btnchoose);
        monto = PrincipalView.findViewById(R.id.monto);
        concepto = PrincipalView.findViewById(R.id.concepto);
        btnUpload = PrincipalView.findViewById(R.id.btnup);
        imageUpload = PrincipalView.findViewById(R.id.image);
        mibase = FirebaseDatabase.getInstance();
        mireferencia = mibase.getReference();

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

                final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
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
                                        LayoutInflater myLayout = LayoutInflater.from(getActivity());
                                        final View dialogView = myLayout.inflate(R.layout.registrades, null);
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        //dialog.setTitle("Sin servicio en tu zona");
                                        builder.setView(dialogView);
                                        TextView txt = (TextView) dialogView.findViewById(R.id.username);
                                        txt.setText("Gasto registrado con éxito");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogo1, int id) {

                                                loadingDialog.dismissDialog();
                                                showSelectedItem(new InicioviajeFragment());

                                            }
                                        });
                                        AlertDialog titulo = builder.create();
                                        titulo.show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_LONG).show();
                                        LayoutInflater myLayout = LayoutInflater.from(getActivity());
                                        final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                        //dialog.setTitle("Sin servicio en tu zona");
                                        builder.setView(dialogView);
                                        TextView txt =(TextView)dialogView.findViewById(R.id.username);
                                        txt.setText("Error al cargar viaje: Revisa tu conexión a internet y reintenta.");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialogo1, int id) {
                                                showSelectedItem(new InicioviajeFragment());

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
                                RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                                stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                                        15000,
                                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                                requestQueue.add(stringRequest);
                            }
                            else {
                                LayoutInflater myLayout = LayoutInflater.from(getActivity());
                                final View dialogView = myLayout.inflate(R.layout.wardes, null);
                                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                            LayoutInflater myLayout = LayoutInflater.from(getActivity());
                            final View dialogView = myLayout.inflate(R.layout.wardes, null);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
        return PrincipalView;
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
                Toast.makeText(getActivity().getApplicationContext(),"Debes aceptar los permisos",Toast.LENGTH_LONG).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private String imagetostring(Bitmap bitmap){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imagebytes = outputStream.toByteArray();
        String encodedimage = Base64.encodeToString(imagebytes,Base64.DEFAULT);
        return encodedimage;
    }
    private void showSelectedItem(Fragment fragment){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.conatiner,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

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
}