package com.broeders.festifyapp;

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
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class LogInActivity extends AppCompatActivity {
    // UI Declarations
    Button btnSignIn, btnLogin;
    TextView txtError;
    EditText txtEmail, txtPassword;
    ProgressDialog progressDialog;
    // Variables Declarations
    String email, password;
    //
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    //  Importing shared preferences

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // UI Initialization
        progressDialog = new ProgressDialog(this);
        //Hiding status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //Hiding action bar
        //TODO: Fix this shiiiiiiiiii
        //getSupportActionBar().hide();
        txtEmail = findViewById(R.id.txtEmailLogin);
        txtPassword = findViewById(R.id.txtPasswordLogIn);
        txtError = findViewById(R.id.errorTextView);
        btnLogin = findViewById(R.id.btnLogIn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
        btnSignIn = findViewById(R.id.btnSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSignin();
            }
        });
        // End UI Initialization
        pref = getApplicationContext().getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
        //initialising shared preferences
    }

    @Override
    protected void onDestroy() {
        //code uit te voeren wanneer de activity beeindigt word.
        editor.commit();
        super.onDestroy();
    }

    public void displayProgressDialog(int title, int message) {
        progressDialog.setTitle(title);
        progressDialog.setMessage(getApplicationContext().getString(message));
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(true);
    }

    public void logIn() {
        displayProgressDialog(R.string.Logging_In, R.string.Please_Wait);
        setUserValues();
        if (verifyData()) {
            checkLogin(email, password);
            //TODO: Verwijder
            //authLogin();
        }
    }

    public void setUserValues() {
        email = txtEmail.getText().toString().trim().toLowerCase();
        password = txtPassword.getText().toString().trim();
    }

    protected boolean verifyData() {
        if (email.isEmpty()) {
            txtError.setText(R.string.Enter_your_email);
            progressDialog.dismiss();
            return false;
        }
        if (!email.contains("@") && !email.contains(".")) {
            txtError.setText(R.string.no_valid_email);
            progressDialog.dismiss();
            return false;
        }
        if (password.isEmpty()) {
            txtError.setText(R.string.Enter_your_password);
            progressDialog.dismiss();
            return false;
        }
        return true;
    }

    //TODO: functionaliteiten vanuit deze functie in andere steken
    protected void authLogin() {
        txtError.setTextColor(getResources().getColor(R.color.colorError));

        if (isNetworkAvailable() == false) {
            txtError.setText(R.string.noNetwork);
            progressDialog.dismiss();
        } else {
            RequestQueue loginRequestQueue = Volley.newRequestQueue(this);

            String url = String.format("http://ineke.broeders.be/1920festify/webservice.aspx?actie=checkLogin&Email=%s&Password=%s", email, password);
            StringRequest loginRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //TODO: voor debuggen, verwijder!!!!!
                    txtError.setText(response);
                    if (response.contains("true") || response.contains("True")) {
                        String userID = response.substring(response.indexOf(",") + 1, response.indexOf(";"));

                        //initialising user
                        //TODO:change
                        //getUser(userID);
                        //end requesting user

                        editor.putBoolean("isNew", false);
                        editor.putString("userID",userID);
                        editor.commit();

                        goToHome();
                    } else {
                        txtError.setText(R.string.wrongCreds);
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
            loginRequestQueue.add(loginRequest);
        }
    }

    private void checkLogin(String emailString, String wachtwoordString) {
        if (isNetworkAvailable() == false) {
            txtError.setText(R.string.noNetwork);
            progressDialog.dismiss();
        } else {
            RequestQueue userRequestQueue = Volley.newRequestQueue(this);
            String userURL = String.format("http://ineke.broeders.be/1920festify/webservice.aspx?actie=checkLogin&Email=%s&Password=%s", emailString, wachtwoordString);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, userURL, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                System.out.println(response);
                                //TODO:change to int
                                int accountID = response.getInt("AccountID");
                                String username = response.getString("accountNaam");
                                String password = response.getString("password");
                                String email = response.getString("Mailadres");
                                String location = response.getString("Locatie");

                                editor.putInt("accountID", accountID);
                                editor.putString("email", email);
                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.putString("location", location);
                                editor.commit();
                                progressDialog.dismiss();

                                goToHome();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            txtError.setText(R.string.logInError);
                            //txtError.setText(error.getMessage());
                            progressDialog.dismiss();
                        }
                    });
            userRequestQueue.add(jsonObjectRequest);
        }
    }

    //Deze 2 methodes worden gebruikt om naar HomeActivity of SignInActivity te gaan.
    public void goToHome() {
        Intent home = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(home);
        finish();
    }

    public void goToSignin() {
        Intent signin = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(signin);
    }
}
