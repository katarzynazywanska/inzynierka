package com.kontakt.sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kontakt.sample.R;
import com.kontakt.sample.beacons.BeaconList;
import com.kontakt.sample.samples.BeaconEddystoneScanActivity;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Directions extends AppCompatActivity {

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, Directions.class);
    }

    public static final String TAG = "ProximityManager";

    private ProximityManager proximityManager;

    ////////////////////////////////////////////////////////////////////////////
    TextView myTextView;
    TextView directionsTextView;
    ImageView imageView;
    String roomName;
    BeaconList beaconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        imageView = findViewById(R.id.arrow);
        myTextView =  (TextView) findViewById(R.id.textView);
        directionsTextView =  (TextView) findViewById(R.id.directionsTextView);
        setupProximityManager();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
        //blink
        imageView.startAnimation(animation);
        startScanning();
        roomName =  getIntent().getStringExtra("cel");
        beaconList = new BeaconList();
    }

    private void setupProximityManager() {
        proximityManager = ProximityManagerFactory.create(this);

        //Configure proximity manager basic options
        proximityManager.configuration()
                //Using ranging for continuous scanning or MONITORING for scanning with intervals
                .scanPeriod(ScanPeriod.RANGING)
                //Using BALANCED for best performance/battery ratio
                .scanMode(ScanMode.BALANCED)
                //OnDeviceUpdate callback will be received with 5 seconds interval
                .deviceUpdateCallbackInterval(TimeUnit.SECONDS.toMillis(2));

        //Setting up iBeacon and Eddystone listeners
        proximityManager.setIBeaconListener(createIBeaconListener());
    }

    private IBeaconListener createIBeaconListener() {

        return new IBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice iBeacon, IBeaconRegion region) {

                Log.i(TAG, "onIBeaconDiscovered: " + iBeacon.toString());
                myTextView.setText("1 onIBeaconDiscovered: " + iBeacon.toString());
            }

            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> iBeacons, IBeaconRegion region) {
                double rssi0 = iBeacons.get(0).getRssi();
                double power0 = iBeacons.get(0).getTxPower();
                double n = 2;
                double distance0 = Math.pow(10,((power0 - rssi0)/(10*n)));
                //myTextView.setText("2 onIBeaconsUpdated: "+ iBeacons.get(0).getProximity() + "\ndistance = " + distance+ " meters");

                /*myTextView.setText(iBeacons.get(0).getMinor() +" 0  RSSI:"+iBeacons.get(0).getRssi()+"  TxPower:"+iBeacons.get(0).getTxPower()
                        + "\ndistance = " + distance0+ " meters" + "\ntimestamp " + iBeacons.get(0).getTimestamp() + "\n distanece " + iBeacons.get(0).getDistance()
                        + "\nname " + iBeacons.get(0).getName() + "\n\n"+ roomName);
                */
                myTextView.setText(" " + getBeaconRoomName(getNearestBeacon(iBeacons)) + "\ngdzie idę " +roomName + "\n" + beaconList.getKey(roomName) );

                if (iBeacons.size() >= 2){
                    Log.i("beaconList" , iBeacons.get(1).getMinor() + "   RSSI:"+iBeacons.get(1).getRssi());
                    Log.i("nearestBeacon" , " " + getNearestBeacon(iBeacons));
                }


                if (iBeacons.size() >= 1){
                    LinkedList<Integer> path =  beaconList.findPath(getNearestBeacon(iBeacons), beaconList.getKey(roomName));
                    for (int i = 0; i < path.size(); i++){
                        Log.i("findPath", i+" "+ path.get(i).toString());
                    }
                }

                Log.i(TAG, "onIBeaconsUpdated: " + iBeacons.size());
            }
            
            public Integer getNearestBeacon(List<IBeaconDevice> iBeacons){
                Integer nearestBeacon = iBeacons.get(0).getMinor();
                double nearestDistance = iBeacons.get(0).getDistance();

                for (IBeaconDevice iBeacon: iBeacons) {
                    if (iBeacon.getDistance() < nearestDistance){
                        nearestBeacon = iBeacon.getMinor();
                        nearestDistance = iBeacon.getDistance();
                    }
                }
                return nearestBeacon;
            }

            public String getBeaconRoomName(int nearestBeaconMinor){
                return beaconList.beaconRooms.get(nearestBeaconMinor);
            }
            
            @Override
            public void onIBeaconLost(IBeaconDevice iBeacon, IBeaconRegion region) {
                myTextView.setText("3 onIBeaconLost: " + iBeacon.toString());

                Log.e(TAG, "onIBeaconLost: " + iBeacon.toString());
            }
        };
    }

    private void startScanning() {
        //Connect to scanning service and start scanning when ready
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                //Check if proximity manager is already scanning
                if (proximityManager.isScanning()) {
                    Toast.makeText(Directions.this, "Already scanning", Toast.LENGTH_SHORT).show();
                    return;
                }
                proximityManager.startScanning();

                Toast.makeText(Directions.this, "Scanning started", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void stopScanning() {
        //Stop scanning if scanning is in progress
        if (proximityManager.isScanning()) {
            proximityManager.stopScanning();
            Toast.makeText(this, "Scanning stopped", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        //Stop scanning when leaving screen.
        stopScanning();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //Remember to disconnect when finished.
        proximityManager.disconnect();
        super.onDestroy();
    }
}