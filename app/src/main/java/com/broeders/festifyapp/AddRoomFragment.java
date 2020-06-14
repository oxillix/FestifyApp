package com.broeders.festifyapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.broeders.festifyapp.HelperClasses.NetworkCheckingClass;

import static android.content.Context.MODE_PRIVATE;


public class AddRoomFragment extends Fragment {

    Button addRoomButton;
    EditText txtRoomName;
    String roomName,Location;
    int accountID;
    TextView txtError;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        accountID = pref.getInt("accountID",0);
        View rootView = inflater.inflate(R.layout.activity_addroom, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add room");

        txtError = rootView.findViewById(R.id.errorTextView);
        txtRoomName = rootView.findViewById(R.id.txtAddRoomName);

        addRoomButton = rootView.findViewById(R.id.addRoomButton);
        addRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserValues();
                addRoom();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomsFragment()).commit();
            }
        });

        return rootView;
    }

    public void setUserValues(){
        roomName = txtRoomName.getText().toString().trim();
    }

    protected void addRoom(){
        txtError.setTextColor(getResources().getColor(R.color.colorError));

        if (NetworkCheckingClass.isNetworkAvailable(getContext()) == false) {
            txtError.setText(R.string.noNetwork);
        } else {
            RequestQueue signinRequestQueue = Volley.newRequestQueue(getContext());

            String url = String.format("http://ineke.broeders.be/1920festify/webservice.aspx?actie=addRoom&accountID=%s&roomname=%s&Location=%s", accountID, roomName, Location);
            StringRequest signInRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //voor te debuggen, mag weg
                    txtError.setText(response);
                    if (response == null) {
                        txtError.setText(R.string.signin_Error);
                    } else {

                    }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    txtError.setText(R.string.logInError);
                }
            });

            signinRequestQueue.add(signInRequest);
        }
    }
}
