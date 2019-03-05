package com.example.simon.fyp_2;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class ExpenditureLocations extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    ArrayList<String> expenseArr, dateArr;
    ArrayList<Double> latArr, longArr;
    ArrayList<Float> expenditureArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure_locations);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        latArr = (ArrayList<Double>) getIntent().getSerializableExtra("latArr");
        longArr = (ArrayList<Double>) getIntent().getSerializableExtra("longArr");
        expenseArr = getIntent().getStringArrayListExtra("expenseArr");
        dateArr = getIntent().getStringArrayListExtra("dateArr");
        expenditureArr = (ArrayList<Float>) getIntent().getSerializableExtra("expenditureArr");

        setUpMap();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set camera to be facing Ireland
        LatLng ireland = new LatLng(53.5, -7.6);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ireland));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(6), 2000, null);
    }

    private void addMarkersToMap() {
        mMap.clear();
        for (int i = 0; i < expenditureArr.size(); i++) {
            LatLng latLng = new LatLng(latArr.get(i), longArr.get(i));
            BitmapDescriptor bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);;
            switch (expenseArr.get(i)) {
                case "Accomodation":
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    break;
                case "Shopping":
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                    break;
                case "Meals":
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                    break;
                case "Drink":
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    break;
                case "Transport":
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                    break;
                case "Excursions":
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                    break;
                case "Other":
                    bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
                    break;
        }
            //mMap.addMarker(new MarkerOptions().position(latLng).title(expenseArr.get(i)));
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(expenseArr.get(i))
                    .snippet("€" + expenditureArr.get(i) + " on " + dateArr.get(i).substring(8, 10) + "/" + dateArr.get(i).substring(5, 7))
                    .icon(bitmapMarker));
        }
    }

    private void setUpMap() {
        // Do a null check to confirm that we have not already instantiated the
        // map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // Hide the zoom controls as the button panel will cover it.
                mMap.getUiSettings().setZoomControlsEnabled(false);
                // Add markers to the map.
                addMarkersToMap();
            }
        }
    }
}
