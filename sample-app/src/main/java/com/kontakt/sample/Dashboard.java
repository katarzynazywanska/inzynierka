package com.kontakt.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Dashboard extends AppCompatActivity {
RecyclerView recyclerView;
RecyclerAdapter adapter;

String rooms[] = {"235, 236, 237, 238, 239, 240"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();

        recyclerView = findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerAdapter(this,rooms);
        recyclerView.setAdapter(adapter);

    }
}