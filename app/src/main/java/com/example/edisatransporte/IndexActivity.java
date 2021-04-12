package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.edisatransporte.HomeAdapter.FeaturedAdapter;
import com.example.edisatransporte.HomeAdapter.FeaturedHelperClass;
import com.example.edisatransporte.Index.MenufragActivity;
import com.example.edisatransporte.viaje.MisViajesActivity;

import com.example.edisatransporte.viaje.QrlActivity;
import com.example.edisatransporte.viaje.ViajesDListActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class IndexActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    RecyclerView featuredRecycler, mostViewedRecycler, categoriesRecycler;
    RecyclerView.Adapter adapter;
    private GradientDrawable gradient1, gradient2, gradient3, gradient4;
    //Variables
    LinearLayout cards;
    static final float END_SCALE = 0.7f;
    TextView appname;
    RequestQueue requestQueue;
    int numv = 0,kk=0;
    ImageView menuIcon,fotoope;
    ImageView logoi;
    TextView nombreope;
    ArrayList<FeaturedHelperClass> featuredLocations = new ArrayList<>();

    String user_id;
    private FirebaseAuth nAuth;

    LinearLayout contentView;
    LottieAnimationView lottie;
    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    View view;

    DateFormat hourFormat;
    DateFormat dateFormat;
    Date date;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Query query = FirebaseDatabase.getInstance().getReference("Viajes").orderByChild("operador").equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                System.out.println("el num de k es"+k);

                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    System.out.println("el sta es"+snap.child("status").getValue(boolean.class));
                    if (snap.child("status").getValue(boolean.class)==true) {
                        kk = kk + 1;
                    }
                }
                numv=kk;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        super.onCreate(savedInstanceState);
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_index);

//ANIMACIONES
        cards = findViewById(R.id.cards);
        lottie = findViewById(R.id.lottie);
        logoi = findViewById(R.id.logoi);
        nombreope = findViewById(R.id.nope);
        fotoope = findViewById(R.id.fotoope);

        YoYo.with(Techniques.FlipInX).duration(2500).playOn(logoi);
        YoYo.with(Techniques.BounceInLeft).duration(2500).playOn(nombreope);
        YoYo.with(Techniques.BounceInRight).duration(2500).playOn(fotoope);
        YoYo.with(Techniques.BounceInUp).duration(2500).playOn(cards);

        YoYo.with(Techniques.BounceInUp).duration(2500).playOn(lottie);

        // featuredRecycler = findViewById(R.id.featured_recycler);
        date = new Date();
//Caso 1: obtener la hora y salida por pantalla con formato:
        hourFormat = new SimpleDateFormat("HH:mm");
        System.out.println("Hora: "+hourFormat.format(date));
        //Hooks
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();
        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        Query query1 = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        //FIREBASE
        query1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    System.out.println("Mi nombre"+dataSnapshot.child("nombre").getValue(String.class));
                    nombreope.setText("¡Buen Día! "+dataSnapshot.child("nombre").getValue(String.class));
                    //          tomadoresn2[i]=dataSnapshot.child("nombre").getValue(String.class)+" "+dataSnapshot.child("apellidos").getValue(String.class);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        requestQueue= Volley.newRequestQueue(getApplicationContext());


        String url= "https://sistemavaltons.com.mx/operadores/images/"+user_id+"/"+user_id+".jpeg";
        url=url.replace(" ","%20");
        ImageRequest imageRequest=new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                fotoope.setImageBitmap(response);
            }
        },0,0, ImageView.ScaleType.CENTER,null,new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                error.printStackTrace();
//                Toast.makeText(error.printStackTrace(),getApplication(),Toast.LENGTH_LONG).show();
            }
        });
        requestQueue.add(imageRequest);

        naviagtionDrawer();

        //Functions will be executed automatically when this activity will be created
       // featuredRecycler();
        //    mostViewedRecycler();
        //     categoriesRecycler();
    }

    //Navigation Drawer Functions
    private void naviagtionDrawer() {

        //Naviagtion Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();

    }
    private void animateNavigationDrawer() {

        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        drawerLayout.setScrimColor(getResources().getColor(R.color.darkblue));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
          /*  case R.id.nav_citas:
                Intent intent = new Intent(IndexActivity.this,ListviaActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
                break;*/
            case R.id.nav_agendar:
                Intent intent2 = new Intent(IndexActivity.this, QrlActivity.class);
                startActivity(intent2, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
                break;
            case R.id.nav_exit:
                cerrarsesion();
                break;
            case R.id.nav_redes:
//                Intent intent3 = new Intent(IndexActivity.this, RedesSocialesActivity.class);
                Intent intent3 = new Intent(IndexActivity.this, MenufragActivity.class);
                startActivity(intent3, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
                break;
            case R.id.nav_home:
                Intent intent4 = new Intent(IndexActivity.this, IndexActivity.class);
                startActivity(intent4, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
                break;
            case R.id.nav_viajes:
                Intent intent6 = new Intent(IndexActivity.this, MisViajesActivity.class);
                startActivity(intent6, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
                break;
            case R.id.nav_viajesp:
                Intent intent9 = new Intent(IndexActivity.this, ViajesDListActivity.class);
                startActivity(intent9, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
                break;
            case R.id.nav_profile:
                Intent intent7 = new Intent(IndexActivity.this, MiPerfilActivity.class);
                startActivity(intent7, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
                break;
        }
        return true;
    }
    private void cerrarsesion(){
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(IndexActivity.this,LoginActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
        finish();
        return;

    }


    private void featuredRecycler() {



        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        featuredLocations.add(new FeaturedHelperClass(R.drawable.trailer2, "VIAJES REALIZADOS:", ""+kk+" viajes"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.trailer4, "PUERTO DE LÁZARO CÁRDENAS", ""));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.trailer1, "PUERTO DE MANZANILLO", ""));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.trailer3, "PUERTO DE VERACRUZ", ""));


        adapter = new FeaturedAdapter(featuredLocations);

        featuredRecycler.setAdapter(adapter);

        GradientDrawable gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xffeff400, 0xffaff600});

    }

    public void servicios(View view) {
        Intent intent = new Intent(IndexActivity.this,QrlActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
        return;

    }
    public void viajesfin(View view) {
        Intent intent = new Intent(IndexActivity.this,MisViajesActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
        return;

    } public void viajesp(View view) {
        Intent intent = new Intent(IndexActivity.this,ViajesDListActivity.class);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(IndexActivity.this).toBundle());
        return;

    }

}
