package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.edisatransporte.RecyclerD.UserModel;
import com.example.edisatransporte.RecyclerD.UsersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ProductosActivity extends AppCompatActivity implements UsersAdapter.SelectedUser{
    int k,i,ii;
    //Estudio Array
//    private ArrayList<Estudio> listEstudios;
    private Button buttonMapa,buttonServicios;
    //SERVICIOS VAR
    String user_id,pos;
    String[] tomadoresn,tomadoresn2,costos;
    DatabaseReference databaseReference;
    private FirebaseAuth nAuth;
    Toolbar toolbar;
    RecyclerView recyclerView;
    List<UserModel> userModelList = new ArrayList<>();
    String[] names ;
    UsersAdapter usersAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        this.getSupportActionBar().setTitle("PRODUCTOS");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();

        buttonMapa = (Button) findViewById(R.id.btnubi);
        usersAdapter = new UsersAdapter(userModelList,ProductosActivity.this);
        recyclerView.setAdapter(usersAdapter);


        Query query = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("carrito");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int k,i;
                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                //   costos = new String[k];
                if (k!=0){
                buttonMapa.setText("Ver carrito ("+k+")");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("Productos");

        //FIREBASE
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModelList.clear();
                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                tomadoresn = new String[k];
                tomadoresn2 = new String[k];
                names = new String[k];
                i=0;ii=0;
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    tomadoresn[ii]=snap.getRef().getKey();
                    System.out.println("LA CONSUL"+tomadoresn[ii]+"pos"+ii);
                    Query query = FirebaseDatabase.getInstance().getReference("Productos").child(tomadoresn[ii]);
                    ii++;

                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            tomadoresn2[i]=dataSnapshot.child("nombre").getValue(String.class);
                            names[i]=tomadoresn2[i];
                            System.out.println("LA CONSUL2"+tomadoresn2[i]+"pos"+i);
                            UserModel userModel = new UserModel(names[i],"","");
                            userModelList.add(userModel);
                            usersAdapter.notifyDataSetChanged();
                            i++;

                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }

                    });
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
//BUSCADOR

        buttonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(ProductosActivity.this, CarritoActivity.class);
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ProductosActivity.this).toBundle());
                    return;

            }
        });


    }

    @Override
    public void selectedUser(UserModel userModel) {
        int aux,posa;
        posa=0;

        for (int l=0;l<tomadoresn2.length;l++){
            if (tomadoresn2[l].compareTo(userModel.getUsername())==0){
                posa=l;
            }

        }

        System.out.println("LA ELECCION ES"+userModel.getUsername()+"con posicion"+posa);
        pos = tomadoresn[posa];
        System.out.println("El producto es"+pos);
        Intent intent = new Intent(ProductosActivity.this, DetallesActivity.class);
        intent.putExtra("producto",pos);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(ProductosActivity.this).toBundle());
        return;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menurecy,menu);
        MenuItem menuItem = menu.findItem(R.id.search_view);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                usersAdapter.getFilter().filter(newText);
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        int id=item.getItemId();
        if (id == R.id.search_view){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
