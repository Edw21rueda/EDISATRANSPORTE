package com.example.edisatransporte.Mainviaje;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.os.Looper;
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
import com.example.edisatransporte.viaje.AgregarIncidenteActivity;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AgregarincidenteFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private View PrincipalView;

    Button btnChoose, btnUpload,btnhome;
    ImageView imageUpload;
    final int CODE_GALLERY_REQUEST = 999;
    Location mLastLocation;
    int LOCATION_REQUEST_CODE = 10001;
    SharedPreferences preferences;

    double latitud,longitud;
    Geocoder geocoder;
    Bitmap bitmap;
    EditText monto, concepto;
    String urlUpload = "https://sistemavaltons.com.mx/imageupincidente.php";
    FirebaseDatabase mibase;
    DatabaseReference mireferencia;
    String sr, c;
    FusedLocationProviderClient fusedLocationProviderClient;

    String fulladd;

    List<Address> addressList;
    LocationRequest locationRequest;
    LocationCallback locationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult==null){
                return;
            }
            for (Location location:locationResult.getLocations()){
                System.out.println("la localizacion es"+location.toString());
                System.out.println("Latitud"+location.getLatitude());
                System.out.println("longitud"+location.getTime());
            }
        }
    };


    public AgregarincidenteFragment() {
    }

    public static AgregarincidenteFragment newInstance(String param1, String param2) {
        AgregarincidenteFragment fragment = new AgregarincidenteFragment();
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
        PrincipalView = inflater.inflate(R.layout.fragment_agregarincidente,container,false);
        preferences = getActivity().getSharedPreferences("idcarta", Context.MODE_PRIVATE);
        sr = ""+preferences.getInt("idnum",0);
        btnChoose = PrincipalView.findViewById(R.id.btnchoose);
        concepto = PrincipalView.findViewById(R.id.concepto);
        btnUpload = PrincipalView.findViewById(R.id.btnup);
        imageUpload = PrincipalView.findViewById(R.id.image);
        mibase = FirebaseDatabase.getInstance();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getLastLocation();
        mireferencia = mibase.getReference();
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LoadingDialog loadingDialog = new LoadingDialog(getActivity());
                loadingDialog.startLoadingDialog();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                },15000);
                if (bitmap != null) {
                    c = concepto.getText().toString();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, urlUpload, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Incidentes");
                            geocoder = new Geocoder(getActivity(), Locale.getDefault());
                            try {
                                addressList = geocoder.getFromLocation(latitud, longitud, 1);
                                String address = addressList.get(0).getAddressLine(0);
                                String area = addressList.get(0).getLocality();
                                String city = addressList.get(0).getAdminArea();
                                String country = addressList.get(0).getCountryName();
                                String code = addressList.get(0).getPostalCode();
                                fulladd = address + ", " + area + ", " + city + ", " + country + ", " + code;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            mireferencia.child("Incidentes").child(sr).child("ubi").setValue(fulladd);
                            mireferencia.child("Incidentes").child(sr).child("latitud").setValue(latitud);
                            mireferencia.child("Incidentes").child(sr).child("longitud").setValue(longitud);

                            mireferencia.child("Incidentes").child(sr).child("desc").setValue(c);

                            LayoutInflater myLayout = LayoutInflater.from(getActivity());
                            final View dialogView = myLayout.inflate(R.layout.registrades, null);
                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            //dialog.setTitle("Sin servicio en tu zona");
                            builder.setView(dialogView);
                            TextView txt = (TextView) dialogView.findViewById(R.id.username);
                            txt.setText("Incidente enviado con éxito");
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
                            //Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
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
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            String imageData = imagetostring(bitmap);
                            params.put("image", imageData);
                            System.out.println("el nombre es" + c);
                            params.put("nombre", c);
                            params.put("viaje", sr);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            15000,
                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    requestQueue.add(stringRequest);
                } else {
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
        });

    return PrincipalView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        bitmap = (Bitmap) data.getExtras().get("data");
        imageUpload.setImageBitmap(bitmap);

    }

    private String imagetostring(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] imagebytes = outputStream.toByteArray();
        String encodedimage = Base64.encodeToString(imagebytes, Base64.DEFAULT);
        return encodedimage;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //  getLastLocation();
            checkSettingsAndStartLocationUpdates();
        } else {
            askLocationPermission();
        }
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //  startLocationUpdates();
            }
        });
        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(getActivity(), 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });

    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    System.out.println("On succes"+location.toString());
                    latitud=location.getLatitude();
                    longitud=location.getLongitude();
                    System.out.println("On succes latitud"+location.getLatitude());
                    System.out.println("On succes longitud"+location.getLongitude());
                } else{
                    System.out.println("error");
                }
            }
        });
        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("OnFailure"+e.getLocalizedMessage());
            }
        });
    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }else{
                ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_REQUEST_CODE);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull String[]permissions,@NonNull int[]grantResults){
        if (requestCode == LOCATION_REQUEST_CODE){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //  getLastLocation();
                checkSettingsAndStartLocationUpdates();
            }
            else{

            }
        }
    }
    private void showSelectedItem(Fragment fragment){
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.conatiner,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
}