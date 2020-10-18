package com.ashmitaryan.weatherapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.ashmitaryan.weatherapplication.Adapter.ViewPagerAdapter;
import com.ashmitaryan.weatherapplication.Fragment.CurrentWeatherFragment;
import com.ashmitaryan.weatherapplication.Fragment.HourlyForecast;
import com.ashmitaryan.weatherapplication.Fragment.SearchForecast;
import com.ashmitaryan.weatherapplication.Model.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private TabLayout tabs;
    private RelativeLayout relativeLayout;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        relativeLayout = findViewById(R.id.root_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Get permissions for Location
        getPermissions();


    }

    private void getPermissions() {
        Dexter.withContext(getApplicationContext())
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            buildLocationRequest();
                            buildLocationCallback();

                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, final PermissionToken permissionToken) {
                        Snackbar.make(relativeLayout, "Permission Denied", Snackbar.LENGTH_LONG).setAction("Retry", v -> {
                            permissionToken.continuePermissionRequest();
                        }).show();
                    }
                }).check();
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Common.current_location = locationResult.getLastLocation();
                viewPager = findViewById(R.id.viewPager);
                setUpViewPager(viewPager);
                tabs = findViewById(R.id.tabs);
                tabs.setupWithViewPager(viewPager);
            }
        };
    }

    private void setUpViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(CurrentWeatherFragment.getInstance(),"Today's Weather");
        adapter.addFragment(HourlyForecast.getInstance(),"5 Day's Forecast");
        adapter.addFragment(SearchForecast.getInstance(),"Search City");
        viewPager.setAdapter(adapter);
    }


    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setSmallestDisplacement(500.0f);
    }
}