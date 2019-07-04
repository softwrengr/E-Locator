package com.techease.elocator.utilities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.techease.elocator.activities.MainActivity;
import com.techease.elocator.activities.NavigationDrawerActivity;

import static android.content.Context.LOCATION_SERVICE;

public class GetLocation {


    String strLatitude, strLongitude;
    public static double lattitude, longitude;
    LocationManager locationManager;
    private static final int REQUEST_LOCATION = 100;
    private static final long LOCATION_REFRESH_TIME = 1;
    private static final float LOCATION_REFRESH_DISTANCE = 1;


    //getting current location
    public void getLocation(Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((NavigationDrawerActivity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);

            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();

                lattitude = latti;
                longitude = longi;

            } else if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();

                lattitude = latti;
                longitude = longi;

            } else if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = latti;
                longitude = longi;

            } else {
                Toast.makeText(context, "Unble to Trace your location", Toast.LENGTH_SHORT).show();
            }

            strLatitude = String.valueOf(lattitude);
            strLongitude = String.valueOf(longitude);
            GeneralUtils.putStringValueInEditor(context, "latitude", strLatitude);
            GeneralUtils.putStringValueInEditor(context, "longitude", strLongitude);
        }
    }


    protected void buildAlertMessageNoGps(final Context context) {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {


        }
    };
}
