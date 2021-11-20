package com.kontakt.sample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Dashboard extends AppCompatActivity implements View.OnClickListener {
RecyclerView recyclerView;
RecyclerAdapter adapter;
LinearLayout linearLayout;
ImageView imageView;

String rooms[] = {"Pokój 235", "Pokój 236", "Pokój 237", "Pokój 238", "Pokój 239", "Pokój 240", "Pokój 241", "Pokój 241", "Pokój 241", "Pokój 241", "Pokój 241", "Pokój 241", "Pokój 241"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(this,rooms);
        recyclerView.setAdapter(adapter);
        //imageView = findViewById(R.id.settingImgView);
        //imageView.setOnClickListener((View.OnClickListener) this);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener((View.OnClickListener) this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Dashboard.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}