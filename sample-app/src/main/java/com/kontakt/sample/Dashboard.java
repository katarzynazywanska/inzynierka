package com.kontakt.sample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;


public class Dashboard extends AppCompatActivity implements View.OnClickListener  {
RecyclerView recyclerView;
RecyclerAdapter adapter;
LinearLayout linearLayout;
//ImageView imageView;
TextView chooseDestination;
TextView settings;
SharedPreferences sharedPreferences;
String fontSize;
//BeaconList beaconList;
public HashMap<String, Map<String,String>> beaconRoomsDataBase;
public String[] roomNames;
public int howManyRooms = 0;

public static final int REQ_CODE = 1;
public BluetoothAdapter bluetoothAdapter;
public LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        askToTurnOnBluetooth();
        askToTurnOnLocation();


        recyclerView = findViewById(R.id.recView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Read from the database
        readRoomsFromDataBase();

        linearLayout = findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener((View.OnClickListener) this);

        chooseDestination = findViewById(R.id.chooseDestinationText);
        settings= findViewById(R.id.settingsCardViewText);

        //change text size
        changeFontSize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            Intent intent = new Intent(Dashboard.this, Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        //Log.i("click", "siema " + sharedPreferences.getString("fontSize", "1"));
        Intent intent = new Intent(Dashboard.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void changeFontSize(){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        fontSize = sharedPreferences.getString("fontSize", "1");
        double fontSizeVal = Double.parseDouble(fontSize);
        //int fontSizeVal = Integer.parseInt(fontSize);
        settings.setTextSize(TypedValue.COMPLEX_UNIT_SP,(float) Math.ceil(fontSizeVal*20));
        chooseDestination.setTextSize(TypedValue.COMPLEX_UNIT_SP, (float) Math.ceil(fontSizeVal*22));
    }

    public void assignRoomsNames(){
        for (Map.Entry<String, Map<String, String>> keyAndValue : beaconRoomsDataBase.entrySet()) {
            for (Map.Entry<String, String> roomAndDir : keyAndValue.getValue().entrySet()) {
                howManyRooms++;
            }
        }
        int i = 0;
        roomNames = new String[howManyRooms];
        for (Map.Entry<String, Map<String, String>> keyAndValue : beaconRoomsDataBase.entrySet()) {
            for (Map.Entry<String, String> roomAndDir : keyAndValue.getValue().entrySet()) {
                roomNames[i] = roomAndDir.getKey();
                i++;
            }
        }
        sortArray(roomNames);
    }

    public void sortArray(String myArray[]) {
        int size = myArray.length;
        for(int i = 0; i<size-1; i++) {
            for (int j = i+1; j<myArray.length; j++) {
                if(myArray[i].contains(" ")){
                    if(Integer.parseInt(myArray[i].split(" ")[1]) - Integer.parseInt(myArray[j].split(" ")[1]) > 0) {
                        String temp = myArray[i];
                        myArray[i] = myArray[j];
                        myArray[j] = temp;
                    }
                } else if(myArray[i].matches("[0-9]+")){
                    if(Integer.parseInt(myArray[i]) - Integer.parseInt(myArray[j]) > 0) {
                        String temp = myArray[i];
                        myArray[i] = myArray[j];
                        myArray[j] = temp;
                    }
                } else {
                    break;
                }
            }
        }
    }

    public void askToTurnOnBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null) {
            Toast.makeText(Dashboard.this, "To urządzenie nie wspiera Bluetooth", Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled()) {
            Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent, REQ_CODE);
        }

    }

    public void askToTurnOnLocation(){
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException e) {
                    switch (e.getStatusCode()){
                        case LocationSettingsStatusCodes
                                .RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(Dashboard.this, REQ_CODE );
                            } catch (IntentSender.SendIntentException sendIntentException) {
                                sendIntentException.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });
    }

    public void readRoomsFromDataBase(){
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://nawigacja-w-budynku-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        //rooms database key
        //klucz baza danych
        myRef.child("beaconRoomsDataBase1").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(!task.isSuccessful()){
                    Log.e("firebase", "error getting data");
                } else {
                    beaconRoomsDataBase = (HashMap<String, Map<String, String>>) task.getResult().getValue();
                    Log.d("firebase", "Done ");

                    assignRoomsNames();

                    adapter = new RecyclerAdapter(Dashboard.this, roomNames);
                    recyclerView.setAdapter(adapter);
                }
            }

        });
    }
}