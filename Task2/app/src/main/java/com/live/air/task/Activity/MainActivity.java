package com.live.air.task.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.live.air.task.Fragments.MapFrag;
import com.live.air.task.Fragments.WeatherFrag;
import com.live.air.task.R;
import com.live.air.task.Utils.FullScreenTransparentLoading;
import com.live.air.task.Utils.SharePref;

import java.util.List;

/**
 * Created by JASPINDER on 11/4/2017.
 */

public class MainActivity extends Activity {


    public static int PERMISSION_LOCATION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        FullScreenTransparentLoading.INSTANCE.init(this);
        if (checkPermission(this)) {
            Location loc = getLastKnownLocation();
            if (loc != null) {
                openMap(loc.getLatitude(), loc.getLongitude());
            }
        } else {
            showPermissionDialog();
        }

    }


    private void showPermissionDialog() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_LOCATION_REQUEST_CODE);
    }


    private Location getLastKnownLocation() {
        Location bestLocation = null;
        if (checkPermission(MainActivity.this) ) {
            LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            List<String> providers = mLocationManager.getProviders(true);

            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    // Found best last known location: %s", l);
                    bestLocation = l;
                }
            }
        }
        return bestLocation;
    }


    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() -1 == 0){
            finish();
        }else{
            super.onBackPressed();
        }
    }

    public void openMap(double lat, double lng) {
        MapFrag frag = MapFrag.newInstance(lat, lng);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.add(R.id.mapframe, frag);
        transaction.addToBackStack(MapFrag.class.getName());
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Location loc = getLastKnownLocation();
                if (loc != null) {
                    openMap(loc.getLatitude(), loc.getLongitude());
                }
            } else {
                Toast.makeText(MainActivity.this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
                openMap(0, 0);
            }
        }
    }
}
