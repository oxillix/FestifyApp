package com.broeders.festifyapp;

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
import com.android.volley.toolbox.Volley;
import com.broeders.festifyapp.Adapter.RoomAdapter;
import com.broeders.festifyapp.HelperClasses.NetworkCheckingClass;
import com.broeders.festifyapp.models.RoomItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RoomsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private RoomAdapter mRoomAdapter;
    private ArrayList<RoomItem> mRoomsList;
    private RequestQueue mRequestQueue;

    private TextView roomNameText;
    public FloatingActionButton addButton;
    private TextView errorText;
    private Button retryButton;
    ProgressBar progressBar;
    final Handler handler = new Handler();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);
        progressBar = rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Rooms");
        //initialising
        mRecyclerView = rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRoomsList = new ArrayList<>();

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

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddRoomFragment()).commit();
            }
        });

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                parseJSON();
            }
        }, 1000);

        return rootView;
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

                            mRoomsList.add(new RoomItem(roomID, accountID, Locatie,roomName));

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
