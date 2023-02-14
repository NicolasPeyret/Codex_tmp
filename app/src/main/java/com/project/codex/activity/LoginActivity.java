package com.project.codex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.project.codex.R;
import com.project.codex.data.local.MyDatabaseHelper;
import com.project.codex.data.remote.MyApiHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String API_RESSOURCE = "http://153.92.208.103:8080/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // remove app name from action bar
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);

        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater custInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View custView = custInflater.inflate(R.layout.custom_image, null);
        actionBar.setCustomView(custView);

        MyDatabaseHelper myDB = new MyDatabaseHelper(LoginActivity.this);
        String tokenValidation = myDB.getAccessToken(LoginActivity.this);

        if (tokenValidation != null && !tokenValidation.isEmpty()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            TextView username = (TextView) findViewById(R.id.username);
            TextView password = (TextView) findViewById(R.id.password);
            Button loginbtn = findViewById(R.id.loginbtn);
            Button signupbtn = findViewById(R.id.signupbtn);
            TextView not_found = findViewById(R.id.not_found);
            not_found.setVisibility(View.GONE);

            loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (username.getText().toString().equals("") || password.getText().toString().trim().equals("")) {
                        Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("email", username.getText().toString());
                            jsonObject.put("password", password.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        MyDatabaseHelper myDB = new MyDatabaseHelper(LoginActivity.this);
                        AndroidNetworking.post(API_RESSOURCE + "auth/login")
                                .addJSONObjectBody(jsonObject)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("response", response.toString());
                                        try {
                                            String token = response.getString("token");
                                            MyDatabaseHelper myDB = new MyDatabaseHelper(LoginActivity.this);
                                            myDB.setAccessToken(LoginActivity.this, token);
                                            //Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                            MyApiHelper myAPI = new MyApiHelper(LoginActivity.this);
                                            myAPI.readAllData();
                                            Intent intent = new Intent(LoginActivity.this, SplashActivity.class);
                                            intent.putExtra("loadingData", true);
                                            startActivity(intent);
                                            finish();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Log.e("Error", anError.getErrorBody());
                                        not_found.setVisibility(View.VISIBLE);
                                        not_found.postDelayed(new Runnable() {
                                            public void run() {
                                                not_found.setVisibility(View.GONE);
                                            }
                                        }, 5000);
                                    }
                                });
                    }
                }
            });

            signupbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
}
