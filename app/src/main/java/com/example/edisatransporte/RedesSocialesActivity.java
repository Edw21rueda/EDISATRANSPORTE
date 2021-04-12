package com.example.edisatransporte;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class RedesSocialesActivity extends AppCompatActivity {

    String url,url2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redes_sociales);
        url="https://www.facebook.com/EDISAMEXICO/";
        url2="https://www.instagram.com/edisamexico/?hl=es-la";
    }
    public void facebook(View v){
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }
    public void insta(View v){
        Uri uri = Uri.parse(url2);
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        startActivity(intent);
    }
}
