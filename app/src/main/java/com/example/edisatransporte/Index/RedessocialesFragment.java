package com.example.edisatransporte.Index;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.edisatransporte.R;

public class RedessocialesFragment extends Fragment {
    String url,url2;
    private View PrincipalView;
    CardView card1,card2;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
 private String mParam1;
    private String mParam2;
    public RedessocialesFragment() {
        // Required empty public constructor
    }

    public static RedessocialesFragment newInstance(String param1, String param2) {
        RedessocialesFragment fragment = new RedessocialesFragment();
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
        PrincipalView = inflater.inflate(R.layout.fragment_redessociales,container,false);
        url="https://www.facebook.com/EDISAMEXICO/";
        url2="https://www.instagram.com/edisamexico/?hl=es-la";
        card1 =PrincipalView.findViewById(R.id.card1);
        card2 =PrincipalView.findViewById(R.id.card2);
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(url2);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
            }
        });


        return PrincipalView;
    }

}