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


import com.broeders.festifyapp.SongsFragment;
import com.broeders.festifyapp.models.RoomItem;
import com.broeders.festifyapp.R;
import com.broeders.festifyapp.models.SongItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoutesViewHolder> {
    private Context mContext;
    private ArrayList<RoomItem> mRoomsList;

    public RoomAdapter(Context context, ArrayList<RoomItem> roomsList) {
        mContext = context;
        mRoomsList = roomsList;
    }

    @Override
    public RoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.data_single_item_room, parent, false);
        return new RoutesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RoutesViewHolder holder, int position) {
        RoomItem currentItem = mRoomsList.get(position);

        //get
        int roomID = currentItem.getRoomID();
        String roomName = currentItem.getRoomName();
       // String songArtist = currentItem.getSongArtist();
        //String routeTitle = currentItem.getRouteTitle();
        //String creatorName = currentItem.getCreator();
        //String routeDescription = currentItem.getDescription();
        //info
        //String location = currentItem.getLocation();
        //String routeLength = currentItem.getRouteLength();

        //set
        /*
        Picasso.get().load(imageUrl).fit().centerInside().into(holder.bigImageView);
        if (!profileImageUrl.contentEquals("")){
            Picasso.get().load(profileImageUrl).fit().centerInside().transform(new CircleTransform()).into(holder.ProfileImageView);
        }
        holder.TextViewTitle.setText(routeTitle);
        holder.TextViewCreator.setText(creatorName);
        holder.TextViewDescription.setText(routeDescription);
        //info
        holder.TextViewInfo.setText(location + " - " + routeLength + " km");
        */

        holder.txtRoomName.setText(String.format("%s",roomName));
     //   holder.txtArtist.setText(String.format("%s",songArtist));
    }

    @Override
    public int getItemCount() {
        return mRoomsList.size();
    }

    public class RoutesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtRoomName;
      //  public TextView txtArtist;
        public Button joinRoomButton;
        private Integer clickCounter = 1;

        CardView cardView;

        SharedPreferences pref;
        SharedPreferences.Editor editor;

        public RoutesViewHolder(View itemView) {
            super(itemView);
            //songs
            txtRoomName = itemView.findViewById(R.id.roomNameTextView);
           // txtArtist = itemView.findViewById(R.id.artistTextView);
            //button
            joinRoomButton = itemView.findViewById(R.id.joinRoomButton);

            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

            joinRoomButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        int clickedPosition = getAdapterPosition();

                        editor.putInt("currentRoomID", mRoomsList.get(clickedPosition).getRoomID());
                        editor.putString("currentRoomName", mRoomsList.get(clickedPosition).getRoomName());

                      //  editor.putBoolean("isDoingRoute", true);

                        editor.commit();
                        //TODO: fix
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Fragment myFragment = new SongsFragment();
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
                joinRoomButton.setVisibility(View.VISIBLE);
            } else {
                joinRoomButton.setVisibility(View.GONE);
                //breng naar routeactivity
            }
        }
    }
}
