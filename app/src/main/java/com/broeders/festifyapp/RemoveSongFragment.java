package com.broeders.festifyapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.broeders.festifyapp.Adapter.RemoveSongAdapter;
import com.broeders.festifyapp.Adapter.SongAdapter;
import com.broeders.festifyapp.HelperClasses.NetworkCheckingClass;
import com.broeders.festifyapp.models.SongItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RemoveSongFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RemoveSongAdapter mRemoveSongAdapter;
    private ArrayList<SongItem> mSongsList;
    private RequestQueue mRequestQueue;

    private TextView songText;
    private TextView artistText;
    private int roomID;
    private  int songID;
    private  int accountID;
    private boolean remove;
     TextView errorText;
     Button retryButton;
    ProgressBar progressBar;
    public FloatingActionButton removeRoomButton;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String roomName;
    final Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        remove = pref.getBoolean("removeSongID",false);
        View rootView = inflater.inflate(R.layout.fragment_remove_room, container, false);
        roomName = pref.getString("currentRoomName","geen room name");
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(roomName);

        //initialising
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSongsList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(getContext());
        removeRoomButton = rootView.findViewById(R.id.removeRoomButton);
        songText = rootView.findViewById(R.id.songTextView);
        artistText = rootView.findViewById(R.id.artistTextView);
        errorText = rootView.findViewById(R.id.routes_error_textView);
        retryButton = rootView.findViewById(R.id.button_retry_routes);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseJSON();
            }
        });
        //end initialising

        removeRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeRoom();
            }
        });

        System.err.println("remove: " + remove);
if(remove == true){
    removeSong();
}

        parseJSON();

        return rootView;
    }

    protected void removeRoom(){
       roomID = pref.getInt("currentRoomID",0);
       accountID = pref.getInt("accountID",0);
        if (NetworkCheckingClass.isNetworkAvailable(getContext()) == false) {

        } else {
            RequestQueue signinRequestQueue = Volley.newRequestQueue(getContext());

            String url = String.format("http://ineke.broeders.be/1920festify/webservice.aspx?actie=removeRoomByAccountID&accountID=%s&roomID=%s", accountID, roomID);
            StringRequest signInRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //voor te debuggen, mag weg

                    if (response == null) {
                        //TODO: fix errorhandelimg
                        errorText.setText("IZJEN PROBLEEM");
                    } else {
                        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyRoomsFragment()).commit();
                    }
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            signinRequestQueue.add(signInRequest);
        }
    }
    protected void removeSong(){
        roomID = pref.getInt("currentRoomID",0);
        songID = pref.getInt("currentSongID",0);
        if (NetworkCheckingClass.isNetworkAvailable(getContext()) == false) {

        } else {
            RequestQueue signinRequestQueue = Volley.newRequestQueue(getContext());

            String url = String.format("http://ineke.broeders.be/1920festify/webservice.aspx?actie=removeSongInRoom&songID=%s&roomID=%s", songID, roomID);
            StringRequest signInRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //voor te debuggen, mag weg

                    if (response == null) {

                    } else {

                    }
                }
            }, new Response.ErrorListener() {
                //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            signinRequestQueue.add(signInRequest);
        }
    }

    private void parseJSON() {
        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);



        int roomID =  pref.getInt("currentRoomID",0);
        int songID = pref.getInt("currentSongID",0);

        String url = String.format("http://ineke.broeders.be/1920festify/webservice.aspx?actie=getSongsInRoom&roomID=%s", roomID);
        System.out.println(url);
        if (NetworkCheckingClass.isNetworkAvailable(getContext())) {
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            //Get JSONObject route
                            JSONObject song = response.getJSONObject(i);

                            //get data
                            String songTitle = song.getString("songTitle");
                            String songArtist = song.getString("songArtist");
                            int songID = song.getInt("SongID");

                            mSongsList.add(new SongItem(songID,songTitle,songArtist));
                        }

                        mRemoveSongAdapter = new RemoveSongAdapter(getContext(), mSongsList);
                        mRecyclerView.setAdapter(mRemoveSongAdapter);
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
