package com.example.gpsapi;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    private TextView tvLon, tvLat, tvCount, tvSpeed;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest; //位置請求物件
    private LocationCallback locationCallback; //位置更新時的傾聽物件

    private Location lastLocation;//紀錄最新位置

    private int updateCount = 0;//位置更新次數

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        tvLon = findViewById(R.id.tvLon);
        tvLat = findViewById(R.id.tvLat);
        tvCount = findViewById(R.id.tvCount);
        tvSpeed = findViewById(R.id.tvSpeed);
        //
        initFusedLocationClient();
        initLocationRequest();
        setLocationCallback();
    }

    @SuppressLint("MissingPermission")
    public void initFusedLocationClient() { //初始化
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    lastLocation = location;//紀錄最新位置
                }
            }
        });
    }

    private void initLocationRequest() { //位置請求
        locationRequest = LocationRequest.create(); //創造實體
        locationRequest.setInterval(1 * 1000); //多久更新一次
        locationRequest.setFastestInterval(1 * 1000); //最快更新區間
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    Location startPoint = new Location("LocationA");
    Location endPoint = new Location("LocationB");
    double distance;
    private void setLocationCallback() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //
                updateCount++;
                for (Location location : locationResult.getLocations()) {
                    Log.i(TAG, location.toString());
                    lastLocation = location;
                    //
                    tvLon.setText("經度 : " + location.getLongitude());
                    tvLat.setText("緯度 : " + location.getLatitude());
                    tvCount.setText("更新次數 : " + updateCount);
                    if(updateCount %2 != 0){
                        startPoint.setLatitude(location.getLongitude());
                        startPoint.setLongitude(location.getLongitude());
                    }else{
                        endPoint.setLatitude(location.getLongitude());
                        endPoint.setLongitude(location.getLongitude());
                        distance = startPoint.distanceTo(endPoint);
                        tvSpeed.setText("速度: "+distance);
                    }

                }
            }
        };
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------

    public Location getLastLocation() {
        return lastLocation;
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    public void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        //
        startLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //
        stopLocationUpdates();
    }

}