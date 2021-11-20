package com.kontakt.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.kontakt.sample.samples.BeaconEddystoneScanActivity;
import com.kontakt.sample.samples.BeaconProScanActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

  public static final int REQUEST_CODE_PERMISSIONS = 100;

  private LinearLayout buttonsLayout;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    setupButtons();
    checkPermissions();
  }

  //Setting up buttons and listeners.
  private void setupButtons() {
    buttonsLayout = findViewById(R.id.buttons_layout);

    final Button beaconsScanningButton = findViewById(R.id.button_scan_beacons);
    final Button beaconsProScanningButton = findViewById(R.id.button_scan_beacons_pro);
    final Button dashboardButton = findViewById(R.id.dashboard);
    final Button entryButton = findViewById(R.id.entryButton);

    beaconsScanningButton.setOnClickListener(this);
    beaconsProScanningButton.setOnClickListener(this);
    dashboardButton.setOnClickListener(this);
    entryButton.setOnClickListener(this);
  }

  //Since Android Marshmallow starting a Bluetooth Low Energy scan requires permission from location group.
  private void checkPermissions() {
    int checkSelfPermissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    if (PackageManager.PERMISSION_GRANTED != checkSelfPermissionResult) {
      //Permission not granted so we ask for it. Results are handled in onRequestPermissionsResult() callback.
      ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSIONS);
    }
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      if (REQUEST_CODE_PERMISSIONS == requestCode) {
        Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show();
      }
    } else {
      disableButtons();
      Toast.makeText(this, "Location permissions are mandatory to use BLE features on Android 6.0 or higher", Toast.LENGTH_LONG).show();
    }
  }

  @Override
  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.button_scan_beacons:
        startActivity(BeaconEddystoneScanActivity.createIntent(this));
        break;
      case R.id.button_scan_beacons_pro:
        startActivity(BeaconProScanActivity.createIntent(this));
        break;
      case R.id.dashboard:
        Intent i = new Intent(MainActivity.this, Dashboard.class);
        startActivity(i);
        break;
      case R.id.entryButton:
        Intent intent = new Intent(MainActivity.this, Entry.class);
        startActivity(intent);
        break;
    }
  }

  private void disableButtons() {
    for (int i = 0; i < buttonsLayout.getChildCount(); i++) {
      buttonsLayout.getChildAt(i).setEnabled(false);
    }
  }

}
