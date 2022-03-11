package com.kontakt.sample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.kontakt.sample.beacons.BeaconList;
import com.kontakt.sdk.android.ble.configuration.ScanMode;
import com.kontakt.sdk.android.ble.configuration.ScanPeriod;
import com.kontakt.sdk.android.ble.connection.OnServiceReadyListener;
import com.kontakt.sdk.android.ble.device.BeaconDevice;
import com.kontakt.sdk.android.ble.manager.ProximityManager;
import com.kontakt.sdk.android.ble.manager.ProximityManagerFactory;
import com.kontakt.sdk.android.ble.manager.listeners.IBeaconListener;
import com.kontakt.sdk.android.common.profile.IBeaconDevice;
import com.kontakt.sdk.android.common.profile.IBeaconRegion;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Directions extends AppCompatActivity {

    public static Intent createIntent(@NonNull Context context) {
        return new Intent(context, Directions.class);
    }

    public static final String TAG = "ProximityManager";
    private ProximityManager proximityManager;

    //TextView myTextView;
    TextView directionsTextView;
    ImageView imageView;
    String roomName;
    BeaconList beaconList;
    Boolean rememberPathFlag = true;
    String myPath = "";
    String startPathBeacon = "";
    LinkedList<String> path;
    LinkedList<String> pathLive;
    Map<String, Boolean> proximityMap = new HashMap<>();
    //TextView myTextView;
    String firstFoundBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions);

        imageView = findViewById(R.id.arrow);
        //myTextView =  (TextView) findViewById(R.id.textView);
        directionsTextView =  (TextView) findViewById(R.id.directionsTextView);
        directionsTextView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);

        setupProximityManager();

        if (turnOnOffAnimation()) {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.blink_anim);
            //blink
            imageView.startAnimation(animation);
        }
        startScanning();
        roomName =  getIntent().getStringExtra("cel");
        beaconList = new BeaconList();
        pathLive = new LinkedList<String>();
        //change text size
        changeFontSize();
    }

    private void setupProximityManager() {
        proximityManager = ProximityManagerFactory.create(this);

        //Configure proximity manager basic options
        proximityManager.configuration()
                //Using ranging for continuous scanning or MONITORING for scanning with intervals
                .scanPeriod(ScanPeriod.create(TimeUnit.MINUTES.toMillis(10), TimeUnit.SECONDS.toMillis(2)))
                //Using BALANCED for best performance/battery ratio
                .scanMode(ScanMode.BALANCED)
                //OnDeviceUpdate callback will be received with 5 seconds interval
                .deviceUpdateCallbackInterval(TimeUnit.SECONDS.toMillis(1));

        //Setting up iBeacon and Eddystone listeners
        proximityManager.setIBeaconListener(createIBeaconListener());
    }

    private IBeaconListener createIBeaconListener() {
        return new IBeaconListener() {
            @Override
            public void onIBeaconDiscovered(IBeaconDevice iBeacon, IBeaconRegion region) {
                Log.i(TAG, "onIBeaconDiscovered: " + /*iBeacon.toString()*/ " "+iBeacon.getProximity());
            }

            @Override
            public void onIBeaconsUpdated(List<IBeaconDevice> iBeacons, IBeaconRegion region) {

                if (iBeacons.size() >= 1) {

                //remember Path
                    if (rememberPathFlag) {
                        rememberPathFlag = false;

                        //pathLive.add(firstFoundBeacon.toString());
                        pathLive.add(getNearestBeacon(iBeacons));
                        Log.d("pathLive", pathLive.toString());

                        path = beaconList.findPath(getNearestBeacon(iBeacons), beaconList.getKey(roomName));
                        startPathBeacon = getBeaconRoomName(getNearestBeacon(iBeacons));
                        Log.i("getKeyFun", "" + beaconList.getKey(roomName));


                        setProximityMapFalse();
                        //jak to niżej usunięte będzie to odkomentować linie powyżej
                        /*for (int i = path.size() - 1; i >= 0; i--) {
                            //   fill proximityMap, if it value of proximity was immediate give true
                            proximityMap.put(path.get(i), false);
                            myPath += getBeaconRoomName(path.get(i)) + "\n";
                            Log.i("findPath", i + " " + path.get(i).toString());
                        }*/
                    }

                    /*//beacon info display
                    String beaconsDistancesPrev = "";
                    String beaconsDistancesCur = "";
                    String ifCloser = "";
                    String ifCloser2 = "";

                    //get prev and curr distance
                    for (IBeaconDevice iBeacon : iBeacons) {
                        beaconList.beaconPreviousDistance.put(iBeacon.getMinor(), beaconList.beaconCurrentDistance.get(iBeacon.getMinor()));
                    }

                    // remember current distance
                    for (IBeaconDevice iBeacon : iBeacons) {
                        beaconList.beaconCurrentDistance.put(iBeacon.getMinor(), iBeacon.getDistance());
                        //text view strings
                        //beaconsDistancesCur += iBeacon.getMinor() + "  " + beaconList.beaconCurrentDistance.get(iBeacon.getMinor()) + "\n";
                        //beaconsDistancesPrev += iBeacon.getMinor() + "  " + beaconList.beaconPreviousDistance.get(iBeacon.getMinor()) + "\n";
                        //ifCloser = "6823 " + beaconList.whetherUserApproached(6823).toString();
                        //ifCloser2 = "12880 " + beaconList.whetherUserApproached(12880).toString();
                    }*/

                    giveDirection(iBeacons);
                }
                    /*//text view info
                    myTextView.setText("Idę z " + startPathBeacon + " do " + roomName + "\n\nDroga :\n " + myPath + "\n" + path + "\n"
                            //*+ "\n" + ifCloser + "\n" + ifCloser2 + "\n\nCurrent\n " + beaconsDistancesCur + "\n\n" + "Previous\n" + beaconsDistancesPrev*//*
                            + "\nProximity: \n" + returnProximity(iBeacons) +"\nCurrent: "+ getCurrentDirectionOfMovement()+"| Correct: "+ getCorrectDirectionOfMovement(path) + "\npathLive: \n" + pathLive.toString());
                    */
            }
            
            @Override
            public void onIBeaconLost(IBeaconDevice iBeacon, IBeaconRegion region) {
                Log.e(TAG, "onIBeaconLost: " + iBeacon.toString());
            }
        };
    }

    /*public String returnProximity(List<IBeaconDevice> iBeacons){
        String prox = "";
        for (IBeaconDevice iBeacon: iBeacons) {
            prox += iBeacon.getMinor()+ " " + iBeacon.getProximity().toString() + " | dist: "+ (new DecimalFormat("##.##").format(iBeacon.getDistance())) + " | RSSI: " + iBeacon.getRssi()+ "\n";
        }
        return prox;
    }*/

    public String getNearestBeacon(List<IBeaconDevice> iBeacons){
        Integer nearestBeacon = iBeacons.get(0).getMinor();
        double nearestDistance = iBeacons.get(0).getDistance();

        for (IBeaconDevice iBeacon: iBeacons) {
            if (iBeacon.getDistance() < nearestDistance){
                nearestBeacon = iBeacon.getMinor();
                nearestDistance = iBeacon.getDistance();
            }
        }
        return nearestBeacon.toString();
    }

    public String getBeaconRoomName(String nearestBeaconMinor){
        String roomsNames = "(";
        for(Map.Entry map: beaconList.beaconRoomsDataBase.get(nearestBeaconMinor).entrySet()){
            roomsNames+= map.getKey()+"  ";
        }
        return roomsNames+")";
    }

    private void startScanning() {
        //Connect to scanning service and start scanning when ready
        proximityManager.connect(new OnServiceReadyListener() {
            @Override
            public void onServiceReady() {
                //Check if proximity manager is already scanning
                if (proximityManager.isScanning()) {
                    //Toast.makeText(Directions.this, "Already scanning", Toast.LENGTH_SHORT).show();
                    return;
                }
                proximityManager.startScanning();
            }
        });
    }

    private void stopScanning() {
        //Stop scanning if scanning is in progress
        if (proximityManager.isScanning()) {
            proximityManager.stopScanning();
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

    public void directionsInImgViewAndTxtView(String direction){
        switch (direction){
            case "prosto":
                imageView.setImageResource(R.drawable.arrow_top_200);
                imageView.setContentDescription("Strzałka skierowana w górę");
                directionsTextView.setText("Idź prosto");
                //directionsTextView.setContentDescription("Idź prosto");
                break;
            case "prawo":
                imageView.setImageResource(R.drawable.arrow_right_200);
                directionsTextView.setText(roomName+"\nWybrany cel jest po prawej stronie");
                imageView.setContentDescription("Strzałka skierowana w prawo");
                //directionsTextView.setContentDescription("Wybrany cel jest po prawej stronie");
                stopScanning();
                //waitAndGoBack();
                break;
            case "lewo":
                imageView.setImageResource(R.drawable.arrow_left_200);
                directionsTextView.setText(roomName+"\nWybrany cel jest po lewej stronie");
                imageView.setContentDescription("Strzałka skierowana w lewo");
                //directionsTextView.setContentDescription("Wybrany cel jest po lewej stronie");
                stopScanning();
                //waitAndGoBack();
                break;
            case "zawróć":
                imageView.setImageResource(R.drawable.arrow_down_200);
                directionsTextView.setText("Zawróć");
                imageView.setContentDescription("Strzałka skierowana w dół");
                //directionsTextView.setContentDescription("Zawróć");
                break;
        }
    }

    public IBeaconDevice returnBeaconByMinor(List<IBeaconDevice> iBeacons) {
        for (int i = path.size()-1; i >= 0; i--) {
            for (IBeaconDevice iBeacon : iBeacons) {
                if (path.get(i).equals( ((Integer)iBeacon.getMinor()).toString() )) {
                    return iBeacon;
                }
            }
        }
        return null;
    }

    public void giveDirection (List<IBeaconDevice> iBeacons){
        for(int i=path.size()-1; i>=0; i--){
            for(IBeaconDevice iBeacon : iBeacons) {
                if(((Integer)iBeacon.getMinor()).toString().equals(path.get(i)) && iBeacon.getProximity().toString().equals("IMMEDIATE")){

                    if(!(getCurrentDirectionOfMovement().equals(getCorrectDirectionOfMovement(path))) && (pathLive.size() > 1)) {
                        directionsInImgViewAndTxtView("zawróć");
                        setProximityMapFalse();
                    } else {

                        if (proximityMap.get(((Integer) iBeacon.getMinor()).toString()) == false) {
                            proximityMap.put(((Integer) iBeacon.getMinor()).toString(), true);
                            if (((Integer) iBeacon.getMinor()).toString().equals(path.get(0))) {
                                Log.i("6823", "hello " + iBeacon.getMinor() + " " + path.get(0));
                                //funkcja do wyznaczania po której stronie jest pokój
                                giveMorePreciseClue();
                            } else if (i == path.size() - 1) {
                                String fromDirection = beaconList.beaconNeighboursMapDataBase.get(path.get(i)).get(path.get(i - 1));
                                String toDirection = beaconList.beaconNeighboursMapDataBase.get(path.get(i - 1)).get(path.get(i));
                                directionsInImgViewAndTxtView(beaconList.giveDirections(fromDirection, toDirection));
                            } else {
                                String fromDirection = beaconList.beaconNeighboursMapDataBase.get(path.get(i)).get(path.get(i - 1));
                                String toDirection = beaconList.beaconNeighboursMapDataBase.get(path.get(i)).get(path.get(i + 1));
                                directionsInImgViewAndTxtView(beaconList.giveDirections(fromDirection, toDirection));
                            }
                        }

                    }
                } else if (iBeacon.getProximity().toString().equals("IMMEDIATE")){
                    if(!(getCurrentDirectionOfMovement().equals(getCorrectDirectionOfMovement(path))) && (pathLive.size() > 1)) {
                        directionsInImgViewAndTxtView("zawróć");
                        setProximityMapFalse();
                    } else {
                        directionsInImgViewAndTxtView("prosto");
                    }
                }
                if(iBeacon.getProximity().toString().equals("IMMEDIATE") && !pathLive.get(pathLive.size()-1).equals( ((Integer)iBeacon.getMinor()).toString() ) ) {
                    pathLive.add( ((Integer)iBeacon.getMinor()).toString() );
                }
            }
        }
    }

    //When you arrive to the destination
    public void giveMorePreciseClue(){
        if(path.size() == 1){
            directionsTextView.setText(roomName+"\nDotarłeś do celu!");
            imageView.setImageResource(R.drawable.nowy_projekt);
            imageView.setContentDescription("Symbol punkt na mapie");
            stopScanning();
            //waitAndGoBack();
        } else {
            String toDirection = beaconList.beaconRoomsDataBase.get(path.get(0)).get(roomName);
            String fromDirection = beaconList.beaconNeighboursMapDataBase.get(path.get(1)).get(path.get(0));
            //directionsInImgViewAndTxtViewWhenYouReachedDirection(beaconList.giveDirections(fromDirection, toDirection));
            directionsInImgViewAndTxtView(beaconList.giveDirections(fromDirection, toDirection));
        }
    }

    public void waitAndGoBack(){
        stopScanning();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();
            }
        }, 5000);
    }

    public void changeFontSize(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String fontSize = sharedPreferences.getString("fontSize", "1");
        double fontSizeVal = Double.parseDouble(fontSize);
        //int fontSizeVal = Integer.parseInt(fontSize);
        directionsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP,(float) Math.ceil(fontSizeVal*20));
    }

    public Boolean turnOnOffAnimation(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean animationBoolean = sharedPreferences.getBoolean("animation", true);
        return animationBoolean;
    }

    public String getCorrectDirectionOfMovement(LinkedList<String> path){
        if(path.size()>1) {
            String startOfPath = path.get(0);
            String oneAfterStartOfPath = path.get(1);
            return beaconList.beaconNeighboursMapDataBase.get(startOfPath).get(oneAfterStartOfPath);
        }
        return "0";
    }

    public String getCurrentDirectionOfMovement(){
        if(pathLive.size()>1) {
            String endOfPath = pathLive.get(pathLive.size()-1);
            String oneBeforeEndOfPath = pathLive.get(pathLive.size() - 2);
            if(beaconList.beaconNeighboursMapDataBase.get(endOfPath).get(oneBeforeEndOfPath)!=null) {
                return beaconList.beaconNeighboursMapDataBase.get(endOfPath).get(oneBeforeEndOfPath);
            } else {
                return getCorrectDirectionOfMovement(beaconList.findPath(oneBeforeEndOfPath, endOfPath));
            }
        }
        return "0";
    }

    public void setProximityMapFalse(){
        for (int i = path.size() - 1; i >= 0; i--) {
            //   fill proximityMap, if it value of proximity was immediate give true
            proximityMap.put(path.get(i), false);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        directionsTextView.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_FOCUSED);
    }
}