package com.broeders.festifyapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.broeders.festifyapp.Adapter.RoomAdapter;
import com.broeders.festifyapp.HelperClasses.NetworkCheckingClass;
import com.broeders.festifyapp.models.RoomItem;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public class RoomsByLocationFragment extends Fragment {
    ProgressBar progressBar;

    private RecyclerView mRecyclerView;
    private RoomAdapter mRoomAdapter;
    private ArrayList<RoomItem> mRoomsList;
    private RequestQueue mRequestQueue;

    private TextView roomNameText;
    public FloatingActionButton addButton;
    private TextView errorText;
    private Button retryButton;
    final Handler handler = new Handler();

    private SeekBar locatieSlider;
    private TextView txtLocatie;

    //locatie opvragen
    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    LocationManager mLocationManager;
    Location huidigeLocatie;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rooms_location, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Rooms by location");

        //lijsten met kamers initialiseren
        mRoomsList = new ArrayList<>();

        //progressbar initialiseren
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        //initialising
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //gps opvragen en initialiseren
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                huidigeLocatie = locationResult.getLastLocation();
            }
        };
        mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        checkLocationPermission();
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

        //txtlocatie initialiseren
        txtLocatie = rootView.findViewById(R.id.txtLocatie);

        //locatieslider init
        locatieSlider = rootView.findViewById(R.id.locatieSlider);
        locatieSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                // TODO Auto-generated method stub
                txtLocatie.setText(String.format("Binnen een straal van: %dkm", progress));
                filterRoomsListOpAfstand(progress * 1000);

            }
        });
        //locatieslider einde
        //andere initialisaties
        mRequestQueue = Volley.newRequestQueue(getContext());

        roomNameText = rootView.findViewById(R.id.roomNameTextView);
        addButton = rootView.findViewById(R.id.addButton);
        errorText = rootView.findViewById(R.id.error_textView);
        retryButton = rootView.findViewById(R.id.button_retry);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJSON();
            }
        });
        //end initialising
        parseJSON();


        return rootView;
    }

    private void filterRoomsListOpAfstand(int afstandInM) {
        ArrayList<RoomItem> mRoomsListFiltered = new ArrayList<>();


        for (int i=0; i < mRoomsList.size(); i++) {
            String[] locatieString = new String[2];
            Location locatie;
            locatieString = mRoomsList.get(i).getLocatie().split(",");
            locatie = new Location("");
            locatie.setLatitude(Double.parseDouble(locatieString[0]));
            locatie.setLongitude(Double.parseDouble(locatieString[1]));

            if(berekenAfstand(huidigeLocatie, locatie) <= afstandInM) {
                mRoomsListFiltered.add(mRoomsList.get(i));
            }
        }


        mRoomAdapter = new RoomAdapter(getContext(), mRoomsListFiltered);
        mRecyclerView.setAdapter(mRoomAdapter);
    }

    private int berekenAfstand(Location huidigeLocatie, Location teControlerenLocatie) {
        System.out.println("Huidig lat: " + huidigeLocatie.getLatitude() + " long:" + huidigeLocatie.getLongitude());
        System.out.println("tocheck lat: " + teControlerenLocatie.getLatitude() + " long:" + teControlerenLocatie.getLongitude());
        System.out.println(String.valueOf(huidigeLocatie.distanceTo(teControlerenLocatie)));
        return (int) huidigeLocatie.distanceTo(teControlerenLocatie);
    }

    private boolean isLocationEnabled(Context context) {
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

    private void checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Location Permssion Required")
                        .setMessage("Please enable location permissions")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            }
            else{
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void parseJSON() {
        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);

        String url = "http://ineke.broeders.be/1920festify/webservice.aspx?actie=getRooms";

        if (NetworkCheckingClass.isNetworkAvailable(getContext())) {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {

                            JSONObject room = response.getJSONObject(i);

                            //get data
                            String roomName = room.getString("roomName");
                            String Locatie = room.getString("Locatie");
                            int roomID = room.getInt("RoomID");
                            int accountID = room.getInt("accountID");

                            //lijst van rooms invullen
                            mRoomsList.add(new RoomItem(roomID, accountID, Locatie, roomName));
                        }

                        mRoomAdapter = new RoomAdapter(getContext(), mRoomsList);
                        mRecyclerView.setAdapter(mRoomAdapter);
                        progressBar.setVisibility(View.GONE);
                    } catch (JSONException e) {
                        errorText.setText(e.toString());

                        errorText.setVisibility(View.VISIBLE);
                        retryButton.setVisibility(View.VISIBLE);

                        progressBar.setVisibility(View.GONE);
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            errorText.setText(error.getMessage());

                            errorText.setVisibility(View.VISIBLE);
                            retryButton.setVisibility(View.VISIBLE);

                            progressBar.setVisibility(View.GONE);
                        }
                    }
            );

            mRequestQueue.add(jsonArrayRequest);
        } else {
            errorText.setText(getContext().getResources().getString(R.string.noNetwork));

            errorText.setVisibility(View.VISIBLE);
            retryButton.setVisibility(View.VISIBLE);

            progressBar.setVisibility(View.GONE);
        }
    }
}