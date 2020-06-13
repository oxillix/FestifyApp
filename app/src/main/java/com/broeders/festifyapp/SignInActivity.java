package com.broeders.festifyapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class SignInActivity extends AppCompatActivity {
    Button btnLogIn, btnSignIn;
    EditText txtemail,txtpassword,txtpasswordconfirm,txtusername;
    String email,username,password,passwordconfirm;
    TextView txtError;
    ProgressDialog progressDialog;
    // Variables Declarations
    //sharedPreferences init
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    //end sharedPreferences init

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // UI Initialization
        progressDialog = new ProgressDialog(this);
        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        //TODO:FIx this shiii
        //getSupportActionBar().hide();
        txtError = findViewById(R.id.errorTextView);
        txtusername = findViewById(R.id.txtUserSignIn);
        txtemail = findViewById(R.id.txtEmailSignIn);
        txtpassword = findViewById(R.id.txtPasswordSignIn);
        txtpasswordconfirm = findViewById(R.id.txtPasswordConfirmSignIn);
        btnLogIn = findViewById(R.id.btnLogIn_SignIn);
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
        btnSignIn = findViewById(R.id.btnSignIn_Signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        // End UI Initialization
        //sharedPref init
        pref = getApplicationContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        //end sharedPref init
    }

    public void displayProgressDialog(int title, int message){
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
    }

    public void goToLogin(){
        Intent login = new Intent(getApplicationContext(),LogInActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(login);
        finish();
    }

    public void signIn(){
        displayProgressDialog(R.string.Creating_Account, R.string.Please_Wait);
        setUserValues();
        if (verifyData()){
            registerUser();
        }
    }

    public void setUserValues(){
        email = txtemail.getText().toString().trim();
        username = txtusername.getText().toString().trim();
        password = txtpassword.getText().toString().trim();
        passwordconfirm = txtpasswordconfirm.getText().toString().trim();
    }

    protected boolean verifyData() {
        if (email.isEmpty()) {
            txtError.setText(R.string.Enter_your_email);
            Toast.makeText(this, R.string.Enter_your_email, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (username.isEmpty()) {
            txtError.setText(R.string.Enter_your_username);
            Toast.makeText(this, R.string.Enter_your_email, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (password.isEmpty()) {
            txtError.setText(R.string.Enter_your_password);
            Toast.makeText(this, R.string.Enter_your_password, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (passwordconfirm.isEmpty()) {
            txtError.setText(R.string.Confirm_your_password);
            Toast.makeText(this, R.string.Confirm_your_password, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if(password.length() < 5  || passwordconfirm.length() < 5 ){
            txtError.setText(R.string.Password_length);
            Toast.makeText(this, R.string.Password_length, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        if (!password.equals(passwordconfirm)) {
            txtError.setText(R.string.Password_match);
            Toast.makeText(this, R.string.Password_match, Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            return false;
        }
        return true;
    }

    protected void registerUser(){
        txtError.setTextColor(getResources().getColor(R.color.colorError));

        if (isNetworkAvailable() == false) {
            txtError.setText(R.string.noNetwork);
            progressDialog.dismiss();
        } else {
            RequestQueue signinRequestQueue = Volley.newRequestQueue(this);

            String url = String.format("http://ineke.broeders.be/1920festify/webservice.aspx?actie=addAccount&Accountnaam=%s&Mailadress=%s&Password=%s", username, email, password);
            StringRequest signInRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //voor te debuggen, mag weg
                    txtError.setText(response);
                    if (response == null) {
                        txtError.setText(R.string.signin_Error);
                    } else {
                        editor.putString("username",username);
                        editor.putString("email",email);
                        editor.putString("password",password);
                        editor.commit();

                        goToHome();
                    }
                    progressDialog.dismiss();
                }
            }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
                @Override
                public void onErrorResponse(VolleyError error) {
                    txtError.setText(R.string.logInError);
                    progressDialog.dismiss();
                }
            });

            signinRequestQueue.add(signInRequest);
        }
    }
    public void goToHome() {
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
        finish();
    }
}
