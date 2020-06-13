package com.broeders.festifyapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.broeders.festifyapp.Adapter.SongAdapter;
import com.broeders.festifyapp.HelperClasses.NetworkCheckingClass;
import com.broeders.festifyapp.models.SongItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SongsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private SongAdapter mSongAdapter;
    private ArrayList<SongItem> mSongsList;
    private RequestQueue mRequestQueue;

    private TextView songText;
    private TextView artistText;

    private TextView errorText;
    private Button retryButton;
    ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        progressBar = rootView.findViewById(R.id.routes_progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Public Routes");
        //initialising
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mSongsList = new ArrayList<>();

        mRequestQueue = Volley.newRequestQueue(getContext());

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

        parseJSON();

        return rootView;
    }

    private void parseJSON() {
        progressBar.setVisibility(View.VISIBLE);
        errorText.setVisibility(View.GONE);
        retryButton.setVisibility(View.GONE);

        String url = "http://ineke.broeders.be/1920festify/webservice.aspx?actie=getSongs";

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