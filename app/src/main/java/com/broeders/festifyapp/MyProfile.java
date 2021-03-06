package com.broeders.festifyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.USAGE_STATS_SERVICE;

public class MyProfile extends Fragment {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Button logout;
    TextView Email;
    TextView Username;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Home");

        pref = getContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        Email = rootView.findViewById(R.id.txtEmailProfile);
        Username = rootView.findViewById(R.id.txtUsernameProfile);

        String user_Email =  pref.getString("email", "no email");
        String user_Username =  pref.getString("username", "no username");

        Email.setText("Email: " + user_Email);
        Username.setText("Username: " + user_Username);
        //TODO: verander alles in deze torrie
        logout = rootView.findViewById(R.id.logoutButton);
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
goToLogIn();
            }
        });



        return rootView;
    }
    public void goToLogIn() {
        Intent signin = new Intent(getActivity().getApplicationContext(), LogInActivity.class);
        startActivity(signin);
    }
}
