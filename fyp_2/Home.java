package com.example.simon.fyp_2;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class Home extends Activity {

    String user, lng, lat;
    TextView usernameTV;
    Context cont = this;
    LocationManager locationManager;
    LocationListener locationListener;
    double LNG, LAT;
    Button inputExp, logout, locationBtn;
    ArrayList<Double> locationArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkIfUserIsLoggedIn();

        setNotification();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);

        usernameTV = (TextView) findViewById(R.id.home_name);
        usernameTV.setText("Welcome " + user);

        inputExp = (Button) findViewById(R.id.inputExp);
        logout = (Button) findViewById(R.id.logoutBtn);
        locationBtn = (Button) findViewById(R.id.locBtn);

        locationArr = getLocation();
    }

    public void checkIfUserIsLoggedIn(){
        user = SaveSharedPreference.getUserName(cont);
        //check if user is logged in, if not, send to login page
        if(user.isEmpty())
            user="";
        if(user.length() == 0)
        {
            Intent login = new Intent(cont, Login.class);
            startActivity(login);
        }
    }

    private void onClickInp() {
        inputExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // location updates every 5 seconds (5000ms) or if location changes by 5m

                Intent input = new Intent(cont, InputExpenditure.class);
                lng = Double.toString(locationArr.get(1));
                lat = Double.toString(locationArr.get(0));
                input.putExtra("latitude", lat);
                input.putExtra("longitude", lng);
                startActivity(input);
            }
        });
    }

    public void onClickView(View v) {
        Intent goToSelect = new Intent(cont, SelectView.class);
        goToSelect.putExtra("username", user);
        startActivity(goToSelect);

    }

    public void onClickLogout(View v){
        SaveSharedPreference.logout(cont);
        Intent login = new Intent(cont, Login.class);
        startActivity(login);
    }

    public void onClickLocation(View v){
        Intent goToSelectLoc = new Intent(cont, ExpenditureLocationSelect.class);
        goToSelectLoc.putExtra("username", user);
        startActivity(goToSelectLoc);
    }

    public void setNotification(){
        // Notification a certain time after user logs in
        Calendar notifTime = Calendar.getInstance();
        int noOfDays = notifTime.getActualMaximum(Calendar.MONTH); // get num of days in the month
        int tomorrow;
        // Check if this is the last day of the month, if not, add 1 day to get tomorrow, if so, next day is the first
        // The below is commented out for demonstation purposes
        //if(Calendar.DAY_OF_MONTH != noOfDays)
            tomorrow = notifTime.get(Calendar.DAY_OF_MONTH);//+1;
/*        else
            tomorrow = 1;*/
        int hour = notifTime.get(Calendar.HOUR_OF_DAY);
        int minute = notifTime.get(Calendar.MINUTE)+3;
        notifTime.set(Calendar.DAY_OF_MONTH, tomorrow);
        notifTime.set(Calendar.HOUR_OF_DAY, hour);
        notifTime.set(Calendar.MINUTE, minute);
        Intent notification = new Intent(getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, notification, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notifTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    @Override
    // method to give check if the permission to use location has been granted
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode){
            case 10:
                // check if permission is granted, if so, send user to InputExpenditure
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    onClickInp();
                return;
        }
    }

    public ArrayList<Double> getLocation(){
        ArrayList<Double> Location = new ArrayList<>();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LNG = location.getLongitude();
                LAT = location.getLatitude();
                lng = Double.toString(LNG);
                lat = Double.toString(LAT);
                locationManager.removeUpdates(this); // Should stop location from constantly updating
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent sendUserToSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(sendUserToSettings);
            }
        };
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
                        , Manifest.permission.INTERNET
                }, 10); //10 is a request code, is an indicator for the permission
                //return;
            } else {
                onClickInp();
            }
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 5, locationListener);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, locationListener);

        // if location hasn't changed (location listener), we use last known location
        if(lat==null) {
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            // Or use LocationManager.GPS_PROVIDER

            Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            LNG = lastKnownLocation.getLongitude();
            LAT = lastKnownLocation.getLatitude();
            Location.add(LAT);
            Location.add(LNG);
        }
        return Location;
    }
}