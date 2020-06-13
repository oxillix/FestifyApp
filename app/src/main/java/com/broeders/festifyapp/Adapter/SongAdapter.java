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


import com.broeders.festifyapp.models.RoomItem;
import com.broeders.festifyapp.R;
import com.broeders.festifyapp.models.SongItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.RoutesViewHolder> {
    private Context mContext;
    private ArrayList<SongItem> mSongsList;

    public SongAdapter(Context context, ArrayList<SongItem> roomsList) {
        mContext = context;
        mSongsList = roomsList;
    }

    @Override
    public RoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.data_single_item, parent, false);
        return new RoutesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RoutesViewHolder holder, int position) {
        SongItem currentItem = mSongsList.get(position);

        //get
        int songID = currentItem.getSongID();
        String songTitle = currentItem.getSongTitle();
        String songArtist = currentItem.getSongArtist();
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

        holder.txtSong.setText(String.format("%s",songTitle));
        holder.txtArtist.setText(String.format("%s",songArtist));
    }

    @Override
    public int getItemCount() {
        return mSongsList.size();
    }

    public class RoutesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtSong;
        public TextView txtArtist;
        public Button likeButton;
        private Integer clickCounter = 1;

        CardView cardView;

        SharedPreferences pref;
        SharedPreferences.Editor editor;

        public RoutesViewHolder(View itemView) {
            super(itemView);
            //songs
            txtSong = itemView.findViewById(R.id.songTextView);
            txtArtist = itemView.findViewById(R.id.artistTextView);
            //button
            likeButton = itemView.findViewById(R.id.startRouteButton);

            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        int clickedPosition = getAdapterPosition();

                        editor.putString("currentRouteID", mSongsList.get(clickedPosition).getSongArtist());
                        editor.putString("currentRouteName", mSongsList.get(clickedPosition).getSongTitle());
                        editor.putBoolean("isDoingRoute", true);
                        editor.commit();

                        //TODO: fix
                        //AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        //Fragment myFragment = new mapRouteFragment();
                        //activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).commit();
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
                likeButton.setVisibility(View.VISIBLE);
            } else {
                likeButton.setVisibility(View.GONE);
                //breng naar routeactivity
            }
        }
    }
}