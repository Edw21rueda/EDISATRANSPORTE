package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.content.pm.ActivityInfo;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.android.volley.RequestQueue;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class CustomerMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    String sucursal="";
    RequestQueue requestQueue;
    private GoogleMap nMap;
    GoogleApiClient nGoogleApiClient;
    Location nLastLocation;
    private Location locMarker = new Location("marcador_act");
    LocationRequest nLocationRequest;
    private Button nRequest;
    String names,cadena,address;
    String userId;
    double lon,lat;

    int cont=0,d=0,aux,ex=0;
    FirebaseDatabase mibase;
    DatabaseReference mireferencia;
    private Marker pacien;
    private Marker pacien2;

    private Float dROMaP,dGUAaP, dPEDaP, dINTERaP, dLOMASaP, dSANTaP, dPOLAaP, dCOAPAaP, dSATEaP, dECHEaP, dESMEaP, dVALLEaP, dBOSQaP;
    //GUADALAJARA
    private static final LatLng GUA = new LatLng(20.738733,-103.400339);
    private static Location GUAL = new Location("GUADALAJARA");

    //Variables para guardar ubicacion de sucursales
    private static final LatLng ROMA = new LatLng(19.415177,-99.164279);
    private static Location ROMAL = new Location("ROMA");
    private static Location MIXES = new Location("MIXES");
    private static Location finalP2 = new Location("PACIENTE");
    private static Location finalP = new Location("PACIENTE");
    private static final LatLng PEDREGAL = new LatLng(19.334907,-99.198691);
    private static Location PEDREL = new Location("PEDREGAL");
    private  static final LatLng INTERLOMAS = new LatLng(19.399269,-99.274032);
    private static Location INTERL = new Location("INTERLOMAS");
    private  static final LatLng LOMAS = new LatLng(19.416316,-99.225483);
    private static Location LOMASL = new Location("LOMAS");
    private static final LatLng SANTAFE = new LatLng(19.361660,-99.278107);
    private static Location SANTAL = new Location("SANTA FE");
    private static final LatLng POLANCO = new LatLng(19.441507,-99.199051);
    private static Location POLANL = new Location("POLANCO");
    private static final LatLng COAPA = new LatLng(19.299552,-99.125370);
    private static Location COAPAL = new Location("COAPA");
    private static final LatLng SATELITE = new LatLng(19.507259,-99.237282);
    private static Location SATEL = new Location("SATELITE");
    private static final LatLng ECHEGARAY = new LatLng(19.494086,-99.226611);
    private static Location ECHEL = new Location("ECHEGARAY");
    private static final LatLng ZONAESMERALDA = new LatLng(19.548812,-99.284935);
    private static Location ESMEL = new Location("ZONA ESMERALDA");
    private static final LatLng DELVALLE = new LatLng(19.370444,-99.174419);
    private static Location VALLEL = new Location("DEL VALLE");
    private static final LatLng BOSQUEDURAZ = new LatLng(19.407287,-99.239525);
    private static Location DURAZL = new Location("BOSQUE DE DURAZNOS");

    private double rango = 5000;

    private Marker mROMA,mGUADALAJARA, mPEDREGAL, mINTERLOMAS, mLOMAS, mSANTAFE, mPOLANCO, mCOAPA, mSATELITE, mECHEGARAY, mZONAESMERALDA, mDELVALLE, mBOSQUEDURAZ;

    private LatLng pickupLocation;
    private LatLng pickupLocation2;
    private  LatLng locationactual;

    //Array para guardar distancias.
    ArrayList<Float> distancia = new ArrayList<Float>();
    ArrayList<String> sucursales = new ArrayList<String>();
    //Arrays donde se van a guardar la sucursal mas cercana que encontro, junto con la ubicación del paciente
    ArrayList<String> sucursAsig = new ArrayList<String>();
    PlacesClient placesClient;
    LinearLayout l1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_map);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        nRequest=(Button) findViewById(R.id.request);
        l1 = (LinearLayout)findViewById(R.id.l1);
        String apikey ="AIzaSyDhhI8zgNyObowE5ewAbCKjzEh3DpoDCgc";
        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(),apikey);
        }
        placesClient = Places.createClient(CustomerMapActivity.this);
        AutocompleteSupportFragment autocompleteSupportFragment =(AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {

                LatLng latLng = place.getLatLng();
                BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);

                //       Toast.makeText(CustomerMapActivity.this,"las coordenadas son"+latLng.latitude+" mas"+latLng.longitude,Toast.LENGTH_SHORT).show();
                pickupLocation2 = new LatLng (latLng.latitude, latLng.longitude);

                cont =1;
                pacien2 = nMap.addMarker(new MarkerOptions()
                        .icon(icon)
                        .position(pickupLocation2).title("Ubicacion Servicio")
                        .draggable(true));
                nMap.moveCamera(CameraUpdateFactory.newLatLng(pickupLocation2));
                locMarker.setLongitude(latLng.longitude);
                locMarker.setLatitude(latLng.latitude);
                System.out.println("este es el lock"+locMarker);

                dGUAaP = GUAL.distanceTo(locMarker);
                dROMaP = ROMAL.distanceTo(locMarker);
                dPEDaP = PEDREL.distanceTo(locMarker);
                dINTERaP = INTERL.distanceTo(locMarker);
                dLOMASaP = LOMASL.distanceTo(locMarker);
                dSANTaP = SANTAL.distanceTo(locMarker);
                dPOLAaP = POLANL.distanceTo(locMarker);
                dCOAPAaP = COAPAL.distanceTo(locMarker);
                dSATEaP = SATEL.distanceTo(locMarker);
                dECHEaP = ECHEL.distanceTo(locMarker);
                dESMEaP = ESMEL.distanceTo(locMarker);
                dVALLEaP = VALLEL.distanceTo(locMarker);
                dBOSQaP = DURAZL.distanceTo(locMarker);
                distancia.set(0,dROMaP);
                distancia.set(1,dPEDaP);
                distancia.set(2,dINTERaP);
                distancia.set(3,dLOMASaP);
                distancia.set(4,dSANTaP);
                distancia.set(5,dPOLAaP);
                distancia.set(6,dCOAPAaP);
                distancia.set(7,dSATEaP);
                distancia.set(8,dECHEaP);
                distancia.set(9,dESMEaP);
                distancia.set(10,dVALLEaP);
                distancia.set(11,dBOSQaP);
                distancia.set(11,dGUAaP);

                System.out.println(distancia);
                Float distmetro;
                distmetro=0.0f;
                sucursal="";
                distmetro = distancia.get(0);
                System.out.println("TENERMOS A DISTMETRO"+ distmetro);
                if (sucursal == "A") {
                    names = "Roma";
                }
                else if (sucursal == "D") {
                    names = "SANTO DOMINGO GUADALAJARA";
                }
                else if (sucursal == "N") {
                    names = "Polanco";
                }
                else if (sucursal == "E") {
                    names = "Echagaray";
                }
                else if (sucursal == "F") {
                    names = "Santa Fe";
                }
                else if (sucursal == "G") {
                    names = "Interlomas";
                }
                else if (sucursal == "L") {
                    names = "Lomas";
                }
                else if (sucursal == "O") {
                    names = "Bosques";
                }
                else if (sucursal == "P") {
                    names = "Pedregal";
                }
                else if (sucursal == "S") {
                    names = "Satelite";
                }
                else if (sucursal == "T") {
                    names = "Coapa";
                }
                else if (sucursal == "V") {
                    names = "Del Valle";
                }
                else if (sucursal == "Z") {
                    names = "Zona Esmeralda";
                }
                System.out.println(sucursal+"SUCURSAL LAB");
                for (int x = 0; x < distancia.size() && x < sucursales.size(); x++) {


                    if (distancia.get(x) <= distmetro) {
                        distmetro = distancia.get(x);

                        if (distmetro < rango) {
                            sucursal = sucursales.get(x);

                        }
                    } else {

                    }
                }
                System.out.println("YA TENEMOS LA SUCURSAL MAS CERCANA YA TENEMOS LA SUCURSAL MAS CERCANA CON EVENTO DRAGEND" + distmetro +"VAMOS VAMOS DRAGEND"+ sucursal);
                // sucursAsig.set(0,sucursal);

                if ((sucursal == "") && (aux!=0)) {
                    LayoutInflater myLayout = LayoutInflater.from(CustomerMapActivity.this);
                    final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                    //dialog.setTitle("Sin servicio en tu zona");
                    builder.setView(dialogView);
                    final String finalSucursal = sucursal;
                    TextView txt =(TextView)dialogView.findViewById(R.id.username);
                    txt.setText("Lo sentimos, aún no contamos con servicio a domicilio en tu zona");
                    builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else if (aux!=0){
                    final String finalSucursal = sucursal;

                    if (sucursal == "A") {
                        names = "Roma";
                    }
                    else if (sucursal == "D") {
                        names = "SANTO DOMINGO GUADALAJARA";
                    }
                    else if (sucursal == "N") {
                        names = "Polanco";
                    }
                    else if (sucursal == "E") {
                        names = "Echagaray";
                    }
                    else if (sucursal == "F") {
                        names = "Santa Fe";
                    }
                    else if (sucursal == "G") {
                        names = "Interlomas";
                    }
                    else if (sucursal == "L") {
                        names = "Lomas";
                    }
                    else if (sucursal == "O") {
                        names = "Bosques";
                    }
                    else if (sucursal == "P") {
                        names = "Pedregal";
                    }
                    else if (sucursal == "S") {
                        names = "Satelite";
                    }
                    else if (sucursal == "T") {
                        names = "Coapa";
                    }
                    else if (sucursal == "V") {
                        names = "Del Valle";
                    }
                    else if (sucursal == "Z") {
                        names = "Zona Esmeralda";
                    }

                    ex=1;
                }
                Geocoder geocoder= new Geocoder (getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> direccion= geocoder.getFromLocation(locMarker.getLatitude(),locMarker.getLongitude(),5);

                    //Pasar location/
                    address = direccion.get(0).getAddressLine(0);//creo se debe de poner est variable global cva

                    //Calle String Feature= direccion.get(0).getFeatureName();//String Phone=direccion.get(0).getPhone();//String Url=direccion.get(0).getUrl();//String SubCalle=direccion.get(0).getSubThoroughfare();//String Premisas=direccion.get(0).getPremises();//String CodigoPais = direccion.get(0).getCountryCode();
                    System.out.println("Aquí podemos observar la direccion completa sin hjdjiasjsadj"+address);

                    LayoutInflater myLayout = LayoutInflater.from(CustomerMapActivity.this);
                    final View dialogView = myLayout.inflate(R.layout.dialogdes, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                    //dialog.setTitle("Sin servicio en tu zona");
                    builder.setView(dialogView);
                    TextView txt =(TextView)dialogView.findViewById(R.id.username);
                    txt.setText(address);
                    builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            aux =1;
                            if (ex==1) {
                                cadena = cadena + "|" + address + "|" + sucursal;
                                // FIRE ZE MISSILES!
                                mibase = FirebaseDatabase.getInstance();
                                mireferencia = mibase.getReference();
                                mireferencia.child("Users").child(userId).child("domicilios").child(address).setValue(true);

                                Intent intent = new Intent(CustomerMapActivity.this, IndexActivity.class);
                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CustomerMapActivity.this).toBundle());
                                finish();
                                return;
                                // User cancelled the dialog
                            }  }
                    })
                            .setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }



                System.out.println("las coordenadas son"+latLng.latitude+" mas"+latLng.longitude);
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });


    }

    public void Busqueda(double longi,double lati){
         userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//    System.out.println("Nombre usuario " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
       // GeoFire geoFire = new GeoFire(ref);
       // geoFire.setLocation(userId, new GeoLocation(nLastLocation.getLatitude(), nLastLocation.getLongitude()));

        //SUCURSALES/
        GUAL.setLatitude(20.738733);
        GUAL.setLongitude(-103.400339);

        ROMAL.setLatitude(19.415177);
        ROMAL.setLongitude(-99.164279);
        PEDREL.setLatitude(19.334907);
        PEDREL.setLongitude(-99.198691);
        INTERL.setLatitude(19.399269);
        INTERL.setLongitude(-99.274032);
        LOMASL.setLatitude(19.361660);
        LOMASL.setLongitude(-99.278107);
        POLANL.setLatitude(19.441507);
        POLANL.setLongitude(-99.199051);
        COAPAL.setLatitude(19.299552);
        COAPAL.setLongitude(-99.125370);
        SATEL.setLatitude(19.507259);
        SATEL.setLongitude(-99.237282);
        ECHEL.setLatitude(19.494086);
        ECHEL.setLongitude(-99.226611);
        ESMEL.setLatitude(19.548812);
        ESMEL.setLongitude(-99.284935);
        VALLEL.setLatitude(19.370444);
        VALLEL.setLongitude(-99.174419);
        DURAZL.setLatitude(19.407287);
        DURAZL.setLongitude(-99.239525);
        SANTAL.setLongitude(-99.278161);
        SANTAL.setLatitude(19.361478);
        //FIN SUCU/

        if (cont == 0) {
            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            pickupLocation = new LatLng(lati, longi);
            pacien = nMap.addMarker(new MarkerOptions()
                    .icon(icon)
                    .position(pickupLocation).title("Ubicacion Servicio")
                    .draggable(true));



            finalP.setLongitude(longi);
            finalP.setLatitude(lati);


            dGUAaP = GUAL.distanceTo(finalP);
            dROMaP = ROMAL.distanceTo(finalP);
            dPEDaP = PEDREL.distanceTo(finalP);
            dINTERaP = INTERL.distanceTo(finalP);
            dLOMASaP = LOMASL.distanceTo(finalP);
            dSANTaP = SANTAL.distanceTo(finalP);
            dPOLAaP = POLANL.distanceTo(finalP);
            dCOAPAaP = COAPAL.distanceTo(finalP);
            dSATEaP = SATEL.distanceTo(finalP);
            dECHEaP = ECHEL.distanceTo(finalP);
            dESMEaP = ESMEL.distanceTo(finalP);
            dVALLEaP = VALLEL.distanceTo(finalP);
            dBOSQaP = DURAZL.distanceTo(finalP);
            distancia.add(dGUAaP);
            sucursales.add("D");
            distancia.add(dROMaP);
            sucursales.add("A");
            distancia.add(dPEDaP);
            sucursales.add("P");
            distancia.add(dINTERaP);
            sucursales.add("G");
            distancia.add(dLOMASaP);
            sucursales.add("L");
            distancia.add(dSANTaP);
            sucursales.add("F");
            //INVIERTE ORDEN
            sucursales.add("N");
            distancia.add(dPOLAaP);
            sucursales.add("T");
            distancia.add(dCOAPAaP);
            sucursales.add("S");
            distancia.add(dSATEaP);
            sucursales.add("E");
            distancia.add(dECHEaP);
            sucursales.add("Z");
            distancia.add(dESMEaP);
            sucursales.add("V");
            distancia.add(dVALLEaP);
            sucursales.add("BOSQUES DE DURAZNOS");
            distancia.add(dBOSQaP);
            System.out.println("Esta es la distacia"+distancia);

            Float distmetr;

            distmetr = distancia.get(0);

            for (int x = 0; x < distancia.size() && x < sucursales.size(); x++) {


                if (distancia.get(x) <= distmetr) {
                    distmetr = distancia.get(x);
                    if (distmetr < rango) {

                        sucursal = sucursales.get(x);
                    }
                } else {

                }
            }
            System.out.println("YA TENEMOS LA SUCURSAL MAS CERCANA YA TENEMOS LA SUCURSAL MAS CERCANA" + distmetr + sucursal);
            sucursAsig.add(sucursal);

            if (sucursAsig.get(0) == "" || aux!=0) {
                LayoutInflater myLayout = LayoutInflater.from(CustomerMapActivity.this);
                final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                //dialog.setTitle("Sin servicio en tu zona");
                builder.setView(dialogView);
                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                txt.setText("Lo sentimos, aún no contamos con servicio a domicilio en tu zona");

                builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        // User cancelled the dialog
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();



            } else if(aux!=0) {
                final String finalSucursal = sucursal;

                if (sucursal == "A") {
                    names = "Roma";
                }
                else if (sucursal == "D") {
                    names = "SANTO DOMINGO GUADALAJARA";
                }
                else if (sucursal == "N") {
                    names = "Polanco";
                }
                else if (sucursal == "E") {
                    names = "Echagaray";
                }
                else if (sucursal == "F") {
                    names = "Santa Fe";
                }
                else if (sucursal == "G") {
                    names = "Interlomas";
                }
                else if (sucursal == "L") {
                    names = "Lomas";
                }
                else if (sucursal == "O") {
                    names = "Bosques";
                }
                else if (sucursal == "P") {
                    names = "Pedregal";
                }
                else if (sucursal == "S") {
                    names = "Satelite";
                }
                else if (sucursal == "T") {
                    names = "Coapa";
                }
                else if (sucursal == "V") {
                    names = "Del Valle";
                }
                else if (sucursal == "Z") {
                    names = "Zona Esmeralda";
                }

                cadena = cadena + "|"+ address+ "|"+ sucursal;
                // FIRE ZE MISSILES!
                mibase = FirebaseDatabase.getInstance();
                mireferencia = mibase.getReference();
                mireferencia.child("Users").child(userId).child("domicilios").child(address).setValue(true);

                Intent intent = new Intent(CustomerMapActivity.this, IndexActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CustomerMapActivity.this).toBundle());
                finish();
                return; }
            //Aqui el codigo pro/
            nRequest.setText("Solicitando Servicio");

        }
    }


    public void Busqueda2( ) {
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        System.out.println("Nombre usuario " + FirebaseAuth.getInstance().getCurrentUser().getEmail());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("customerRequest");
     //   GeoFire geoFire = new GeoFire(ref);
      //  geoFire.setLocation(userId, new GeoLocation(nLastLocation.getLatitude(), nLastLocation.getLongitude()));

        //SUCURSALES/
        GUAL.setLatitude(20.738733);
        GUAL.setLongitude(-103.400339);
        ROMAL.setLatitude(19.415177);
        ROMAL.setLongitude(-99.164279);
        PEDREL.setLatitude(19.334907);
        PEDREL.setLongitude(-99.198691);
        INTERL.setLatitude(19.399269);
        INTERL.setLongitude(-99.274032);
        LOMASL.setLatitude(19.361660);
        LOMASL.setLongitude(-99.278107);
        POLANL.setLatitude(19.441507);
        POLANL.setLongitude(-99.199051);
        COAPAL.setLatitude(19.299552);
        COAPAL.setLongitude(-99.125370);
        SATEL.setLatitude(19.507259);
        SATEL.setLongitude(-99.237282);
        ECHEL.setLatitude(19.494086);
        ECHEL.setLongitude(-99.226611);
        ESMEL.setLatitude(19.548812);
        ESMEL.setLongitude(-99.284935);
        VALLEL.setLatitude(19.370444);
        VALLEL.setLongitude(-99.174419);
        DURAZL.setLatitude(19.407287);
        DURAZL.setLongitude(-99.239525);
        SANTAL.setLongitude(-99.278161);
        SANTAL.setLatitude(19.361478);
        //FIN SUCU/

        if (cont == 0) {
            BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
            pickupLocation = new LatLng(nLastLocation.getLatitude(), nLastLocation.getLongitude());
            pacien = nMap.addMarker(new MarkerOptions()
                    .icon(icon)
                    .position(pickupLocation).title("Ubicacion Servicio")
                    .draggable(true));


            finalP.setLongitude(nLastLocation.getLongitude());
            finalP.setLatitude(nLastLocation.getLatitude());

            dGUAaP = GUAL.distanceTo(finalP);
            dROMaP = ROMAL.distanceTo(finalP);
            dPEDaP = PEDREL.distanceTo(finalP);
            dINTERaP = INTERL.distanceTo(finalP);
            dLOMASaP = LOMASL.distanceTo(finalP);
            dSANTaP = SANTAL.distanceTo(finalP);
            dPOLAaP = POLANL.distanceTo(finalP);
            dCOAPAaP = COAPAL.distanceTo(finalP);
            dSATEaP = SATEL.distanceTo(finalP);
            dECHEaP = ECHEL.distanceTo(finalP);
            dESMEaP = ESMEL.distanceTo(finalP);
            dVALLEaP = VALLEL.distanceTo(finalP);
            dBOSQaP = DURAZL.distanceTo(finalP);
            distancia.add(dGUAaP);
            sucursales.add("D");
            distancia.add(dROMaP);
            sucursales.add("A");
            distancia.add(dPEDaP);
            sucursales.add("P");
            distancia.add(dINTERaP);
            sucursales.add("G");
            distancia.add(dLOMASaP);
            sucursales.add("L");
            distancia.add(dSANTaP);
            sucursales.add("F");
            //INVIERTE ORDEN
            sucursales.add("N");
            distancia.add(dPOLAaP);
            sucursales.add("T");
            distancia.add(dCOAPAaP);
            sucursales.add("S");
            distancia.add(dSATEaP);
            sucursales.add("E");
            distancia.add(dECHEaP);
            sucursales.add("Z");
            distancia.add(dESMEaP);
            sucursales.add("V");
            distancia.add(dVALLEaP);
            sucursales.add("BOSQUES DE DURAZNOS");
            distancia.add(dBOSQaP);
            System.out.println("Esta es la distacia" + distancia);

            Float distmetr;

            distmetr = distancia.get(0);

            for (int x = 0; x < distancia.size() && x < sucursales.size(); x++) {


                if (distancia.get(x) <= distmetr) {
                    distmetr = distancia.get(x);
                    if (distmetr < rango) {

                        sucursal = sucursales.get(x);
                    }
                } else {

                }
            }
            System.out.println("YA TENEMOS LA SUCURSAL MAS CERCANA YA TENEMOS LA SUCURSAL MAS CERCANA" + distmetr + sucursal);
            sucursAsig.add(sucursal);
            //Aqui el codigo pro/
            nRequest.setText("Solicitando Servicio");

        }
    }
    //LISTO EL MAPA
    @Override
    public void onMapReady(GoogleMap googleMap) {

        nMap = googleMap;

        mROMA = nMap.addMarker(new MarkerOptions()
                .position(ROMA)
                .title("LABORATORIOS OLARTE Y AKLE ROMA"));
        mROMA.setTag(0);

        mPEDREGAL = nMap.addMarker(new MarkerOptions()
                .position(PEDREGAL)
                .title("LABORATORIOS OLARTE Y AKLE PEDREGAL"));
        mPEDREGAL.setTag(0);

        mINTERLOMAS = nMap.addMarker(new MarkerOptions()
                .position(INTERLOMAS)
                .title("LABORATORIOS OLARTE Y AKLE INTERLOMAS"));
        mINTERLOMAS.setTag(0);

        mLOMAS = nMap.addMarker(new MarkerOptions()
                .position(LOMAS)
                .title("LABORATORIOS OLARTE Y AKLE LOMAS"));
        mLOMAS.setTag(0);

        mSANTAFE = nMap.addMarker(new MarkerOptions()
                .position(SANTAFE)
                .title("LABORATORIOS OLARTE Y AKLE SANTAFE"));
        mSANTAFE.setTag(0);

        mPOLANCO = nMap.addMarker(new MarkerOptions()
                .position(POLANCO)
                .title("LABORATORIOS OLARTE Y AKLE POLANCO"));
        mPOLANCO.setTag(0);

        mCOAPA = nMap.addMarker(new MarkerOptions()
                .position(COAPA)
                .title("LABORATORIOS OLARTE Y AKLE COAPA"));
        mCOAPA.setTag(0);

        mSATELITE = nMap.addMarker(new MarkerOptions()
                .position(SATELITE)
                .title("LABORATORIOS OLARTE Y AKLE SATELITE"));
        mSATELITE.setTag(0);

        mECHEGARAY = nMap.addMarker(new MarkerOptions()
                .position(ECHEGARAY)
                .title("LABORATORIOS OLARTE Y AKLE ECHEGARAY"));
        mECHEGARAY.setTag(0);

        mGUADALAJARA = nMap.addMarker(new MarkerOptions()
                .position(GUA)
                .title("LABORATORIOS SANTO DOMINGO GUADALAJARA"));
        mGUADALAJARA.setTag(0);

        mZONAESMERALDA = nMap.addMarker(new MarkerOptions()
                .position(ZONAESMERALDA)
                .title("LABORATORIOS OLARTE Y AKLE ZONA ESMERALDA"));
        mZONAESMERALDA.setTag(0);

        mDELVALLE = nMap.addMarker(new MarkerOptions()
                .position(DELVALLE)
                .title("LABORATORIOS OLARTE Y AKLE DEL VALLE"));
        mDELVALLE.setTag(0);

        mBOSQUEDURAZ = nMap.addMarker(new MarkerOptions()
                .position(BOSQUEDURAZ)
                .title("LABORATORIOS OLARTE Y AKLE BOSQUES DE DURAZNOS"));
        mBOSQUEDURAZ.setTag(0);



        nMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                // Toast.makeText(CustomerMapActivity.this,"aqui es"+marker.getPosition(),Toast.LENGTH_SHORT).show();

                sucursAsig = null;

                System.out.println("PORPORPORPORPORPORPORPORPORPORPORPORPORPORPORPORPOR"+marker.getPosition().latitude);
                locMarker.setLatitude(marker.getPosition().latitude);
                locMarker.setLongitude(marker.getPosition().longitude);
                LatLng actual = marker.getPosition();
                cont = cont + 1;

                nMap.moveCamera(CameraUpdateFactory.newLatLng(actual));

                dROMaP = ROMAL.distanceTo(locMarker);
                dPEDaP = PEDREL.distanceTo(locMarker);
                dINTERaP = INTERL.distanceTo(locMarker);
                dLOMASaP = LOMASL.distanceTo(locMarker);
                dSANTaP = SANTAL.distanceTo(locMarker);
                dPOLAaP = POLANL.distanceTo(locMarker);
                dCOAPAaP = COAPAL.distanceTo(locMarker);
                dSATEaP = SATEL.distanceTo(locMarker);
                dECHEaP = ECHEL.distanceTo(locMarker);
                dESMEaP = ESMEL.distanceTo(locMarker);
                dVALLEaP = VALLEL.distanceTo(locMarker);
                dBOSQaP = DURAZL.distanceTo(locMarker);
                distancia.set(0,dROMaP);
                distancia.set(1,dPEDaP);
                distancia.set(2,dINTERaP);
                distancia.set(3,dLOMASaP);
                distancia.set(4,dSANTaP);
                distancia.set(5,dPOLAaP);
                distancia.set(6,dCOAPAaP);
                distancia.set(7,dSATEaP);
                distancia.set(8,dECHEaP);
                distancia.set(9,dESMEaP);
                distancia.set(10,dVALLEaP);
                distancia.set(11,dBOSQaP);

                System.out.println(distancia);
                Float distmetro;
                distmetro=0.0f;
                sucursal="";
                distmetro = distancia.get(0);
                System.out.println("TENERMOS A DISTMETRO"+ distmetro);
                if (sucursal == "A") {
                    names = "Roma";
                }
                else if (sucursal == "N") {
                    names = "Polanco";
                }
                else if (sucursal == "E") {
                    names = "Echagaray";
                }
                else if (sucursal == "F") {
                    names = "Santa Fe";
                }
                else if (sucursal == "G") {
                    names = "Interlomas";
                }
                else if (sucursal == "L") {
                    names = "Lomas";
                }
                else if (sucursal == "O") {
                    names = "Bosques";
                }
                else if (sucursal == "P") {
                    names = "Pedregal";
                }
                else if (sucursal == "S") {
                    names = "Satelite";
                }
                else if (sucursal == "T") {
                    names = "Coapa";
                }
                else if (sucursal == "V") {
                    names = "Del Valle";
                }
                else if (sucursal == "Z") {
                    names = "Zona Esmeralda";
                }
                System.out.println(sucursal+"SUCURSAL LAB");
                for (int x = 0; x < distancia.size() && x < sucursales.size(); x++) {


                    if (distancia.get(x) <= distmetro) {
                        distmetro = distancia.get(x);

                        if (distmetro < rango) {
                            sucursal = sucursales.get(x);

                        }
                    } else {

                    }
                }
                System.out.println("YA TENEMOS LA SUCURSAL MAS CERCANA YA TENEMOS LA SUCURSAL MAS CERCANA CON EVENTO DRAGEND" + distmetro +"VAMOS VAMOS DRAGEND"+ sucursal);
                // sucursAsig.set(0,sucursal);

                if (sucursal == "") {
                    LayoutInflater myLayout = LayoutInflater.from(CustomerMapActivity.this);
                    final View dialogView = myLayout.inflate(R.layout.canceldes, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                    //dialog.setTitle("Sin servicio en tu zona");
                    builder.setView(dialogView);
                    final String finalSucursal = sucursal;
                    TextView txt =(TextView)dialogView.findViewById(R.id.username);
                    txt.setText("Lo sentimos, aún no contamos con servicio a domicilio en tu zona");
                    builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                } else {
                    final String finalSucursal = sucursal;

                    if (sucursal == "A") {
                        names = "Roma";
                    }
                    else if (sucursal == "N") {
                        names = "Polanco";
                    }
                    else if (sucursal == "E") {
                        names = "Echagaray";
                    }
                    else if (sucursal == "F") {
                        names = "Santa Fe";
                    }
                    else if (sucursal == "G") {
                        names = "Interlomas";
                    }
                    else if (sucursal == "L") {
                        names = "Lomas";
                    }
                    else if (sucursal == "O") {
                        names = "Bosques";
                    }
                    else if (sucursal == "P") {
                        names = "Pedregal";
                    }
                    else if (sucursal == "S") {
                        names = "Satelite";
                    }
                    else if (sucursal == "T") {
                        names = "Coapa";
                    }
                    else if (sucursal == "V") {
                        names = "Del Valle";
                    }
                    else if (sucursal == "Z") {
                        names = "Zona Esmeralda";
                    }
                    ex=1;
                }


                Geocoder geocoder= new Geocoder (getApplicationContext(), Locale.getDefault());

                try {
                    List<Address> direccion= geocoder.getFromLocation(locMarker.getLatitude(),locMarker.getLongitude(),5);

                    //Pasar location/
                    address = direccion.get(0).getAddressLine(0);//creo se debe de poner est variable global cva

                    //Calle String Feature= direccion.get(0).getFeatureName();//String Phone=direccion.get(0).getPhone();//String Url=direccion.get(0).getUrl();//String SubCalle=direccion.get(0).getSubThoroughfare();//String Premisas=direccion.get(0).getPremises();//String CodigoPais = direccion.get(0).getCountryCode();
                    System.out.println("Aquí podemos observar la direccion completa sin hjdjiasjsadj"+address);

                    LayoutInflater myLayout = LayoutInflater.from(CustomerMapActivity.this);
                    final View dialogView = myLayout.inflate(R.layout.dialogdes, null);
                    AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                    //dialog.setTitle("Sin servicio en tu zona");
                    builder.setView(dialogView);
                    TextView txt =(TextView)dialogView.findViewById(R.id.username);
                    txt.setText(address);
                    builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            if (ex==1){
                                cadena = cadena + "|"+ address+ "|"+ sucursal;
                                // FIRE ZE MISSILES!
                                mibase = FirebaseDatabase.getInstance();
                                mireferencia = mibase.getReference();
                                mireferencia.child("Users").child(userId).child("domicilios").child(address).setValue(true);

                                Intent intent = new Intent(CustomerMapActivity.this, IndexActivity.class);
                                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CustomerMapActivity.this).toBundle());
                                finish();
                                return;
                            }}
                    })
                            .setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("estamos conectados");
            return;
        }
        System.out.println("estamos conectados");

        buildGoogleApiClient();
        nMap.setMyLocationEnabled(true);
    }
    protected synchronized void buildGoogleApiClient(){
        System.out.println("ESTAMOS CONECTADOS");
        nGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        nGoogleApiClient.connect();
        System.out.println("ESTAMOS CONECTADOS");
    }

    @Override
    public void onLocationChanged(Location location) {

        nLastLocation = location;

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        nMap.setMinZoomPreference(15);
        nMap.setMaxZoomPreference(20);
        d= d + 1;
        if (d==1) {
            LayoutInflater myLayout = LayoutInflater.from(CustomerMapActivity.this);
            final View dialogView = myLayout.inflate(R.layout.dialogdes, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
            //dialog.setTitle("Sin servicio en tu zona");
            builder.setView(dialogView);
            final String finalSucursal = sucursal;
            TextView txt =(TextView)dialogView.findViewById(R.id.username);
            txt.setText("En caso de que requiera otra ubicación ingrese la dirección. ");
            builder  .setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // FIRE ZE MISSILES!
                    Intent intent = new Intent(CustomerMapActivity.this, IndexActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(CustomerMapActivity.this).toBundle());
                    finish();
                    return;
                }
            })
                    .setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

                            try {
                                List<Address> direccion = geocoder.getFromLocation(nLastLocation.getLatitude(), nLastLocation.getLongitude(), 5);


                                address = direccion.get(0).getAddressLine(0);

                                //Calle String Feature= direccion.get(0).getFeatureName();//String Phone=direccion.get(0).getPhone();//String Url=direccion.get(0).getUrl();//String SubCalle=direccion.get(0).getSubThoroughfare();//String Premisas=direccion.get(0).getPremises();//String CodigoPais = direccion.get(0).getCountryCode();
                                System.out.println("Aquí podemos observar la direccion completa sin hjdjiasjsadj" + address);
                                LayoutInflater myLayout = LayoutInflater.from(CustomerMapActivity.this);
                                final View dialogView = myLayout.inflate(R.layout.dialogdes, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(CustomerMapActivity.this);
                                //dialog.setTitle("Sin servicio en tu zona");
                                builder.setView(dialogView);
                                final String finalSucursal = sucursal;
                                TextView txt =(TextView)dialogView.findViewById(R.id.username);
                                txt.setText("Ubicación Actual: \n"+address);
                                builder.setNegativeButton("Aceptar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // User cancelled the dialog
                                        aux = 1;
                                        lat = nLastLocation.getLatitude();
                                        lon = nLastLocation.getLongitude();
                                        Busqueda(lat,lon);
                                        l1.setVisibility(View.VISIBLE);

                                    }
                                })
                                        .setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                lat = nLastLocation.getLatitude();
                                                lon = nLastLocation.getLongitude();
                                                Busqueda(lat,lon);

                                                l1.setVisibility(View.VISIBLE);

                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog dialog2 = builder.create();
                                dialog2.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // User cancelled the dialog
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        if(cont==0){
            nMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // nMap.animateCamera(CameraUpdateFactory.zoomTo(17));

        }

    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        nLocationRequest = new LocationRequest();
        nLocationRequest.setInterval(1000);
        nLocationRequest.setFastestInterval(1000);
        nLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

//minuto 8:18 if
        LocationServices.FusedLocationApi.requestLocationUpdates(nGoogleApiClient, nLocationRequest, this);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}









