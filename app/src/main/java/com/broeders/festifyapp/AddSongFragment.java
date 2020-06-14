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


public class AddSongFragment extends Fragment {

    Button addSongButton;
    EditText txtArtist,txtSong;
    String artist,song;
    TextView txtError;
    int roomID;
    int accountID;
    SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        accountID = pref.getInt("accountID",0);
        roomID = pref.getInt("currentRoomID",0);
        View rootView = inflater.inflate(R.layout.activity_addsong, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Add song");

        txtError = rootView.findViewById(R.id.errorTextView);
        txtArtist = rootView.findViewById(R.id.txtAddArtist);
        txtSong = rootView.findViewById(R.id.txtAddSong);
        addSongButton = rootView.findViewById(R.id.addSongButton);
        addSongButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserValues();
                addSong();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new SongsFragment()).commit();
            }
        });

        return rootView;
    }

    public void setUserValues(){
        artist = txtArtist.getText().toString().trim();
        song = txtSong.getText().toString().trim();
    }

    protected void addSong(){
        txtError.setTextColor(getResources().getColor(R.color.colorError));

        if (NetworkCheckingClass.isNetworkAvailable(getContext()) == false) {
            txtError.setText(R.string.noNetwork);
        } else {
            RequestQueue signinRequestQueue = Volley.newRequestQueue(getContext());

            String url = String.format("http://ineke.broeders.be/1920festify/Webservice.aspx?actie=addSong&songTitle=%s&songArtist=%s&roomID=%s&accountID=%s", song, artist, roomID, accountID);
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
