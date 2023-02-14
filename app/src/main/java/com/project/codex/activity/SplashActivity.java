package com.project.codex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.project.codex.R;
import com.project.codex.data.remote.MyApiHelper;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //if get extra loading data
                if (getIntent().hasExtra("loadingData")) {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    MyApiHelper myAPI = new MyApiHelper(SplashActivity.this);
                    myAPI.readAllNotes();
                    startActivity(i);
                } else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                }
                finish();
            }
        }, 3500);

        // hide bar
        getSupportActionBar().hide();
    }
}
