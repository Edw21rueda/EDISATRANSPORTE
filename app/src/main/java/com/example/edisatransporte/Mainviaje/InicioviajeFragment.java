package com.example.edisatransporte.Mainviaje;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.edisatransporte.R;
import com.example.edisatransporte.Servicelocation.LocationService;
import com.example.edisatransporte.Servicelocation.MainlocationActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class InicioviajeFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private View PrincipalView;
    private String mParam1;
    private String mParam2;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    String user_id, sr;
    private FusedLocationProviderClient fusedLocationClient;
    int hora, minutos, segundos;
    SharedPreferences preferences;

    DateFormat hourFormat;
    private FirebaseAuth nAuth;
    DateFormat dateFormat;
    Date date;
    Button incio;


    public InicioviajeFragment() {
    }
  public static InicioviajeFragment newInstance(String param1, String param2) {
        InicioviajeFragment fragment = new InicioviajeFragment();
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        PrincipalView = inflater.inflate(R.layout.fragment_inicioviaje,container,false);
        preferences =  this.getActivity().getSharedPreferences("idcarta", Context.MODE_PRIVATE);
        sr = ""+preferences.getInt("idnum",0);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

        date = new Date();
        hourFormat = new SimpleDateFormat("HH:mm");
        System.out.println("Hora: " + hourFormat.format(date));
        dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        incio = PrincipalView.findViewById(R.id.request_location_updates_button);
        isLocationServiceRunning();
        incio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_CODE_LOCATION_PERMISSION);
                } else {
                    startLocationService();
                    incio.setVisibility(View.INVISIBLE);

                    Calendar calendario = Calendar.getInstance();
                    hora = calendario.get(Calendar.HOUR_OF_DAY);
                    minutos = calendario.get(Calendar.MINUTE);
                    segundos = calendario.get(Calendar.SECOND);
                    if (hora == 23) {
                        hora = 00;
                    } else {
                        hora = hora + 1;
                    }
                    Query query = FirebaseDatabase.getInstance().getReference("Viajes").child(""+preferences.getInt("idnum",0));
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int k, i;
                            k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                            System.out.println("el numero es" + k);
                            //   costos = new String[k];
                            if (k != 0) {
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("status").setValue(false);

                            } else {

                                FirebaseDatabase.getInstance().getReference().child("Gastos").child("pagados").child(""+preferences.getInt("idnum",0)).setValue(false);
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("status").setValue(false);
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("entrega").setValue(false);
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("llegadacliente").setValue(false);

                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("fechasalida").setValue(dateFormat.format(date));
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("auxt").setValue(hora + ":" + minutos);

                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("horasalida").setValue(hourFormat.format(date));
                                FirebaseDatabase.getInstance().getReference().child("Viajes").child(""+preferences.getInt("idnum",0)).child("operador").setValue(user_id);

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        return PrincipalView;
    }
    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) this.getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null){
            for (ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)){
                System.out.println("la compa"+ LocationService.class.getName()+" con"+service.service.getClassName());

                if (LocationService.class.getName().equals(service.service.getClassName())){
                    if (service.foreground){
                       incio.setVisibility(View.INVISIBLE);
                        return true;
                    }
                }
            }
            return false;
        }
        return false;
    }
    private void startLocationService(){
        // if (isLocationServiceRunning()){
        Intent intent = new Intent(getActivity().getApplicationContext(),LocationService.class);
        intent.putExtra("sr",sr);
        intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
        getActivity().startService(intent);
    }

}