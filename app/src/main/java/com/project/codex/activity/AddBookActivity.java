package com.project.codex.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.codex.R;
import com.project.codex.data.remote.FetchBook;
import com.project.codex.data.remote.MyApiHelper;

public class AddBookActivity extends AppCompatActivity {
    FloatingActionButton scan_button;
    EditText title_input, author_input, pages_input, img_input, desc_input, isbn_input;
    Button add_button;
    // API Implementation
    private static final String TAG = AddBookActivity.class.getSimpleName();
    private EditText mBookInput;
    private TextView mAuthorText, mTitleText, mDescriptionText, mPageNumberText, mImgText, mISBNText;

    // ENDS HERE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        // API Implementation
        mAuthorText = findViewById(R.id.author_input);
        mTitleText = findViewById(R.id.title_input);
        mDescriptionText = findViewById(R.id.description_input);
        mImgText = findViewById(R.id.img_input);
        mPageNumberText = findViewById(R.id.pages_input);
        mISBNText = findViewById(R.id.isbn_input);

        getSupportActionBar().setElevation(0);

        // ENDS HERE
        title_input = findViewById(R.id.title_input);
        title_input.setFocusable(false);

        author_input = findViewById(R.id.author_input);
        author_input.setFocusable(false);

        img_input = findViewById(R.id.img_input);
        img_input.setVisibility(View.GONE);

        pages_input = findViewById(R.id.pages_input);
        pages_input.setFocusable(false);

        desc_input = findViewById(R.id.description_input);
        desc_input.setVisibility(View.GONE);

        isbn_input = findViewById(R.id.isbn_input);
        isbn_input.setVisibility(View.GONE);

        add_button = findViewById(R.id.add_button);
        add_button.setVisibility(View.GONE);

        scan_button = findViewById(R.id.scanButton);
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddBookActivity.this, ScannerActivity.class);
                int LAUNCH_SECOND_ACTIVITY = 1;
                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
            }
        });

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyApiHelper myAPI = new MyApiHelper(AddBookActivity.this);
                int bookMark = 0;

                try {
                    myAPI.addBook(title_input.getText().toString().trim(),
                            author_input.getText().toString().trim(),
                            img_input.getText().toString().trim(),
                            Integer.valueOf(pages_input.getText().toString().trim()),
                            desc_input.getText().toString().trim(),
                            bookMark, isbn_input.getText().toString().trim());
                } catch (Error e) {
                    Log.d("response", e.toString());
                }
            }
        });
    }

    public void searchBooks(View view) {
        String queryString = mBookInput.getText().toString();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() && queryString.length() != 0) {
            new FetchBook(mTitleText, mAuthorText, mDescriptionText, mImgText, mPageNumberText, mISBNText, add_button).execute(queryString);
            mAuthorText.setText("");
            mPageNumberText.setText("");
            mDescriptionText.setText("");
            mImgText.setText("");
            mISBNText.setText("");
            mTitleText.setText("Loading..");
        } else {
            if (queryString.length() == 0) {
                mAuthorText.setText("");
                mPageNumberText.setText("");
                mDescriptionText.setText("");
                mImgText.setText("");
                mISBNText.setText("");
                mTitleText.setText("Please enter a title");
            } else {
                mAuthorText.setText("");
                mPageNumberText.setText("");
                mDescriptionText.setText("");
                mImgText.setText("");
                mISBNText.setText("");
                mTitleText.setText("Please check your network");
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (data.hasExtra("decodedBarcode")) {
                Toast.makeText(
                        this,
                        "book imported",
                        Toast.LENGTH_SHORT).show();
                String queryString = data.getExtras().getString("decodedBarcode");
                new FetchBook(mTitleText, mAuthorText, mDescriptionText, mImgText, mPageNumberText, mISBNText, add_button)
                        .execute(queryString);
            }
        }
    }
}