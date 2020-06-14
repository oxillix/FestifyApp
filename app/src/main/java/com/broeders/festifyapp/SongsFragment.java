package com.broeders.festifyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.android.volley.toolbox.Volley;
import com.broeders.festifyapp.Adapter.SongAdapter;
import com.broeders.festifyapp.models.SongItem;
import com.broeders.festifyapp.HelperClasses.NetworkCheckingClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SongsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private SongAdapter mSongAdapter;
    private ArrayList<SongItem> mSongsList;
    private RequestQueue mRequestQueue;


    private TextView songText;
    private TextView artistText;
    private int roomID;

    private TextView errorText;
    private Button retryButton;
    ProgressBar progressBar;
    public FloatingActionButton addButton;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String roomName;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
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
        addButton = rootView.findViewById(R.id.addButton);
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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO:Fix voor naar add pagina

              //getActivity().beginTransaction().replace(R.id.fragment_container, new AddSongFragment()).commit();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddSongFragment()).commit();
                //Intent login = new Intent(getContext(), AddSongFragment.class);
                // login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
               // startActivity(login);

            }
        });

        parseJSON();

        return rootView;
    }

    private void parseJSON() {
        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);



        int roomID =  pref.getInt("currentRoomID",0);

        System.out.println(roomID);
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

                        mSongAdapter = new SongAdapter(getContext(), mSongsList);
                        mRecyclerView.setAdapter(mSongAdapter);
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