package com.broeders.festifyapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //navdrawer
    private DrawerLayout drawer;
    SharedPreferences pref;
    NavigationView navigationView;

    //locatie opvragen
    private FusedLocationProviderClient mFusedLocationClient;
    LocationManager lm;
    boolean gps_enabled;
    boolean network_enabled;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //balkje bovenaan
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        // Navigatiemenu
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navEmail= headerView.findViewById(R.id.emailTxtDrawer);
        TextView navName =  headerView.findViewById(R.id.nameTxtDrawer);

        //Waarden (Strings) ophalen uit het geheugen (lokaal)
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        String user_Email =  pref.getString("email", "no email");
        String user_Username =  pref.getString("username", "no username");

        navEmail.setText(user_Email);
        navName.setText(user_Username);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // einde navigatiemenu

        //Vragen voor locatiepermissie
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //
        mLocationRequest = new LocationRequest();
        //locatieInit
        locatieInit();
        if (isLocationEnabled(this)){
            //Toast toast = Toast.makeText(this, "Locatie is enabled!", Toast.LENGTH_LONG);
            //toast.show();
        } else {
            Toast toast = Toast.makeText(this, "Locatie is NIET ENABLED", Toast.LENGTH_LONG);
            toast.show();
        }

        //locatierequest
        checkLocationPermission();

        LocationCallback mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                System.out.println("LAT: " + locationResult.getLastLocation().getLatitude() + " LONG: " + locationResult.getLastLocation().getLongitude());
            }};

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Location Permssion Required")
                        .setMessage("Please enable location permissions")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
               getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                break;
            case R.id.nav_all_rooms:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomsFragment()).commit();
                break;
            case R.id.nav_room_by_location:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomsByLocationFragment()).commit();
                break;
            case R.id.nav_MyProfile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProfile()).commit();
                break;
            case R.id.nav_MyRooms:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyRoomsFragment()).commit();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //init
    public void locatieInit() {
        lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        gps_enabled = false;
        network_enabled = false;
    }

    //locatiecheck
    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case 1:{
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                       // mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }
                } else{
                    Toast.makeText(this, "Please provide the permission", Toast.LENGTH_LONG).show();
                    System.out.println("Please provide the permission");
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}