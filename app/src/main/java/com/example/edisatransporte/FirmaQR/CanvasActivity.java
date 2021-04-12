package com.example.edisatransporte.FirmaQR;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CanvasActivity extends AppCompatActivity {

    MyCanvas myCanvas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myCanvas = new MyCanvas(this,null);

        setContentView(myCanvas);
    }
}
