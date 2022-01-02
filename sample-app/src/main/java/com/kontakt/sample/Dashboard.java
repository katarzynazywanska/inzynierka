package com.kontakt.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Dashboard extends AppCompatActivity implements View.OnClickListener {
RecyclerView recyclerView;
RecyclerAdapter adapter;
LinearLayout linearLayout;
ImageView imageView;
TextView chooseDestination;
TextView settings;
SharedPreferences sharedPreferences;
String fontSize;

String rooms[] = {"Pokój 1", "Pokój 2", "Pokój 3", "Pokój 4", "Pokój 5", "Pokój 6", "Pokój 7", "Pokój 8", "Pokój 9", "Pokój 10"};

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

        chooseDestination = findViewById(R.id.chooseDestinationText);
        settings= findViewById(R.id.settingsCardViewText);

        //change text size
        changeFontSize();
    }

    @Override
    public void onClick(View v) {
        Log.i("click", "siema " + sharedPreferences.getString("fontSize", "1"));
        Intent intent = new Intent(Dashboard.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void changeFontSize(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        fontSize = sharedPreferences.getString("fontSize", "1");
        int fontSizeVal = Integer.parseInt(fontSize);
        settings.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSizeVal*17);
        chooseDestination.setTextSize(TypedValue.COMPLEX_UNIT_SP,fontSizeVal*18);
    }

}