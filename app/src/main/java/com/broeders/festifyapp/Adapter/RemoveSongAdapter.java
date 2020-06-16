package com.broeders.festifyapp.Adapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaExtractor;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.broeders.festifyapp.R;
import com.broeders.festifyapp.RemoveSongFragment;
import com.broeders.festifyapp.models.SongItem;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class RemoveSongAdapter extends RecyclerView.Adapter<RemoveSongAdapter.RoomViewHolder> {
    private Context mContext;
    private ArrayList<SongItem> mSongsList;

    public RemoveSongAdapter(Context context, ArrayList<SongItem> songsList) {
        mContext = context;
        mSongsList = songsList;
    }

    @Override
    public RoomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.data_single_item_remove_song, parent, false);
        return new RoomViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RoomViewHolder holder, int position) {
        SongItem currentItem = mSongsList.get(position);

        //get
        int songID = currentItem.getSongID();
        String songTitle = currentItem.getSongTitle();
        String songArtist = currentItem.getSongArtist();

        holder.txtSong.setText(String.format("%s",songTitle));
        holder.txtArtist.setText(String.format("%s",songArtist));
    }

    @Override
    public int getItemCount() {
        return mSongsList.size();
    }

    public class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtSong;
        public TextView txtArtist;
        public Button removeSongButton;
        private Integer clickCounter = 1;

        CardView cardView;

        SharedPreferences pref;
        SharedPreferences.Editor editor;

        public RoomViewHolder(View itemView) {
            super(itemView);
            //songs
            txtSong = itemView.findViewById(R.id.songTextView);
            txtArtist = itemView.findViewById(R.id.artistTextView);
            //button
            removeSongButton = itemView.findViewById(R.id.removeSongButton);

            cardView = itemView.findViewById(R.id.card_view);
            cardView.setOnClickListener(this);


            removeSongButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        int clickedPosition = getAdapterPosition();
                        editor.putBoolean("removeSongID",true);
                        editor.putInt("currentSongID", mSongsList.get(clickedPosition).getSongID());
                        editor.putString("currentSongArtist", mSongsList.get(clickedPosition).getSongArtist());
                        editor.putString("currentSongTitle", mSongsList.get(clickedPosition).getSongTitle());
                        editor.commit();



                        //TODO: fix
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Fragment myFragment = new RemoveSongFragment();
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).commit();
                    }catch (Exception e){
                        //TODO: fix
                        //AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        //Fragment myFragment = new RemoveSongFragment();
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
                removeSongButton.setVisibility(View.VISIBLE);
            } else {
                removeSongButton.setVisibility(View.GONE);
            }
        }
    }

}
