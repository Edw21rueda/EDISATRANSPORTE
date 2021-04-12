package com.example.edisatransporte.Class;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.example.edisatransporte.R;

public class LoadingDialog {

    private AlertDialog alertDialog;
    private Activity activity;

    public LoadingDialog(Activity myactivity){
        activity = myactivity;
    }

    public void startLoadingDialog(){
        AlertDialog.Builder builder= new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

    }

    public void dismissDialog(){
        alertDialog.dismiss();
    }
}
