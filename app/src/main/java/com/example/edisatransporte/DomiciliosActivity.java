package com.example.edisatransporte;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

public class DomiciliosActivity extends AppCompatActivity implements UsersAdapter.SelectedUser {
    Toolbar toolbar;
    TextView dir;
    private Button buttonMapa,buttonServicios;
    RecyclerView recyclerView;
    List<UserModel> userModelList = new ArrayList<>();
    String[] names ;
    UsersAdapter usersAdapter;
    int i,k;
    String user_id,uactual,unueva;
    DatabaseReference databaseReference;
    private FirebaseAuth nAuth;
    FirebaseDatabase mibase;
    DatabaseReference mireferencia;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_domicilios);
        nAuth = FirebaseAuth.getInstance();
        user_id = nAuth.getCurrentUser().getUid();
        mibase = FirebaseDatabase.getInstance();
        mireferencia = mibase.getReference();
        dir = (TextView) findViewById(R.id.dir);
        recyclerView = findViewById(R.id.recyclerview);
        toolbar = findViewById(R.id.toolbar);
        buttonMapa = (Button) findViewById(R.id.btnubi);

        //  this.getSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("DOMICILIOS");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        buttonMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DomiciliosActivity.this, CustomerMapActivity.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(DomiciliosActivity.this).toBundle());
                return;

            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("domicilios");

        //FIREBASE
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModelList.clear();
                k = Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                names = new String[k];
                i=0;
                for(DataSnapshot snap:dataSnapshot.getChildren()){
                    names[i]=snap.getRef().getKey();
                    i++;
                }
                for (String s:names){
                    UserModel userModel = new UserModel(s,"","");
                    userModelList.add(userModel);
                }
                usersAdapter.notifyDataSetChanged();
                Query query = FirebaseDatabase.getInstance().getReference("Users").child(user_id).child("domicilios");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long auxtot;
                        int auxt;
                        for(DataSnapshot snap:dataSnapshot.getChildren()){
                            if (snap.getValue(Boolean.class)==true){
                                dir.setText("Ubicación Actual: "+snap.getRef().getKey());
                                uactual = snap.getRef().getKey();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        usersAdapter = new UsersAdapter(userModelList,this);
        recyclerView.setAdapter(usersAdapter);

    }

    @Override
    public void selectedUser(UserModel userModel) {
        System.out.println("LA ELECCION ES"+userModel.getUsername());
        unueva = userModel.getUsername();
        LayoutInflater myLayout = LayoutInflater.from(DomiciliosActivity.this);
        final View dialogView = myLayout.inflate(R.layout.wardes, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(DomiciliosActivity.this);
        //dialog.setTitle("Sin servicio en tu zona");
        builder.setView(dialogView);
        TextView txt =(TextView)dialogView.findViewById(R.id.username);
        txt.setText("¿Deseas que sea tu ubicación actual?");
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                mireferencia.child("Users").child(user_id).child("domicilios").child(uactual).setValue(false);
                mireferencia.child("Users").child(user_id).child("domicilios").child(unueva).setValue(true);
                dir.setText("Ubicación Actual: "+unueva);
                dialogo1.cancel();
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                dialogo1.cancel();
            }
        });
        AlertDialog titulo= builder.create();
        titulo.show();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menurecy,menu);
        MenuItem menuItem = menu.findItem(R.id.search_view);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
