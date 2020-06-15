package com.broeders.festifyapp.Adapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.broeders.festifyapp.HelperClasses.NetworkCheckingClass;
import com.broeders.festifyapp.MyRoomsFragment;
import com.broeders.festifyapp.RemoveSongFragment;
import com.broeders.festifyapp.SongsFragment;
import com.broeders.festifyapp.models.RoomItem;
import com.broeders.festifyapp.R;
import com.broeders.festifyapp.models.SongItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class MyRoomsAdapter extends RecyclerView.Adapter<MyRoomsAdapter.RoomViewHolder> {
    private Context mContext;
    private ArrayList<RoomItem> mRoomsList;

    public MyRoomsAdapter(Context context, ArrayList<RoomItem> roomsList) {
        mContext = context;
        mRoomsList = roomsList;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.data_single_item_my_rooms, parent, false);
        return new RoomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        RoomItem currentItem = mRoomsList.get(position);

        int roomID = currentItem.getRoomID();
        String roomName = currentItem.getRoomName();

        holder.txtRoomName.setText(String.format("%s",roomName));

    }

    @Override
    public int getItemCount() {
        return mRoomsList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtRoomName;
        //  public TextView txtArtist;
        public Button selectRoomButton;
        private Integer clickCounter = 1;

        CardView cardView;

        SharedPreferences pref;
        SharedPreferences.Editor editor;
        int roomID;

        public RoomViewHolder(View itemView) {
            super(itemView);
            //songs
            txtRoomName = itemView.findViewById(R.id.roomNameTextView);
            //button
            selectRoomButton = itemView.findViewById(R.id.selectRoomButton);
            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

            selectRoomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        int clickedPosition = getAdapterPosition();

                        editor.putInt("currentRoomID", mRoomsList.get(clickedPosition).getRoomID());
                        editor.putString("currentRoomName", mRoomsList.get(clickedPosition).getRoomName());
                        //  editor.putBoolean("isDoingRoute", true);

                        editor.commit();
                        roomID = pref.getInt("currentRoomID",0);

                        //TODO: fix
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Fragment myFragment = new RemoveSongFragment();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).commit();
                    }catch (Exception e){
                        //TODO: fix
                        //AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        //Fragment myFragment = new mapRouteFragment();
                        //activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).commit();
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            clickCounter += 1;

            pref = mContext.getSharedPreferences("pref", MODE_PRIVATE);
            editor = pref.edit();

            if (clickCounter % 2 == 0) {
                selectRoomButton.setVisibility(View.VISIBLE);
            } else {
                selectRoomButton.setVisibility(View.GONE);
            }
        }
    }

}
