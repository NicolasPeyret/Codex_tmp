package com.project.codex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.project.codex.R;
import com.project.codex.data.local.MyDatabaseHelper;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {

    private static final String API_RESSOURCE = "http://153.92.208.103:8080/api/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // remove app name from action bar
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();

        LayoutInflater custInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View custView = custInflater.inflate(R.layout.custom_image, null);
        actionBar.setCustomView(custView);

        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setElevation(0);
        actionBar.setDisplayShowCustomEnabled(true);

        TextView username = (TextView) findViewById(R.id.username);
        TextView password = (TextView) findViewById(R.id.password);
        TextView ConfirmPassword = (TextView) findViewById(R.id.confirm_password);

        Button signupbtn = findViewById(R.id.signupbtn);

        signupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (password.getText().toString().trim().equals(ConfirmPassword.getText().toString().trim()) == false) {
                    Toast.makeText(SignupActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else if (username.getText().toString().equals("")  || password.getText().toString().trim().equals("") || ConfirmPassword.getText().toString().trim().equals("")) {
                    Toast.makeText(SignupActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("email", username.getText().toString());
                        jsonObject.put("password", password.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    MyDatabaseHelper myDB = new MyDatabaseHelper(SignupActivity.this);
                    AndroidNetworking.post(API_RESSOURCE + "auth/signup")
                            .addJSONObjectBody(jsonObject)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("response", response.toString());
                                    Toast.makeText(SignupActivity.this, "Signup Success", Toast.LENGTH_SHORT)
                                            .show();
                                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                                    startActivity(intent);

                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("Error", anError.getErrorBody());
                                    Toast.makeText(SignupActivity.this, "Signup Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }
}
