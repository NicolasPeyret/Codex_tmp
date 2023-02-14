package com.project.codex.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.codex.R;
import com.project.codex.adapter.BookCustomAdapter;
import com.project.codex.data.local.MyDatabaseHelper;
import com.project.codex.data.remote.MyApiHelper;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    FloatingActionButton add_button;
    MyDatabaseHelper myDB;
    ArrayList<String> book_id, mongo_id, book_title, book_author, book_pages, book_img, book_noteNb, book_desc, bookMark;
    BookCustomAdapter bookCustomAdapter;
    ImageView empty_imageview;
    TextView no_data;

    private static final int PERMISSION_REQUEST_CODE = 1;

    /**
     * onCreate method is called when the activity is created.
     * 
     * @param savedInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        myDB = new MyDatabaseHelper(MainActivity.this);
        // myDB.updateData();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {
        }
        recyclerView = findViewById(R.id.bookRecyclerView);
        add_button = findViewById(R.id.add_button);
        empty_imageview = findViewById(R.id.empty_imageview);
        no_data = findViewById(R.id.no_data);

        add_button.setOnClickListener(new View.OnClickListener() {
            /**
             * onClick method is called when the add button is clicked.
             *
             * @param view
             */
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                startActivity(intent);
            }
        });

        // remove app name from action bar
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);

        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater custInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View custView = custInflater.inflate(R.layout.custom_image, null);
        actionBar.setCustomView(custView);

        book_id = new ArrayList<>();
        mongo_id = new ArrayList<>();
        book_title = new ArrayList<>();
        book_author = new ArrayList<>();
        book_img = new ArrayList<>();
        book_pages = new ArrayList<>();
        book_noteNb = new ArrayList<>();
        book_desc = new ArrayList<>();
        bookMark = new ArrayList<>();
        storeDataInArrays();
        bookCustomAdapter = new BookCustomAdapter(MainActivity.this, this, book_id, mongo_id, book_title, book_author,
                book_img,
                book_pages, book_noteNb, book_desc, bookMark);
        recyclerView.setAdapter(bookCustomAdapter);

        // Use GridLayoutManager to make 3 items in a row
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));

        String tokenValidation = myDB.getAccessToken(MainActivity.this);
        if (tokenValidation == "" && tokenValidation.length() == 0) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }

    void storeDataInArrays() {
        Cursor cursor = myDB.readAllData("books");
        if (cursor.getCount() == 0) {
            empty_imageview.setVisibility(View.VISIBLE);
            no_data.setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()) {
                book_id.add(cursor.getString(0));
                mongo_id.add(cursor.getString(1));
                book_title.add(cursor.getString(2));
                book_author.add(cursor.getString(3));
                book_desc.add(cursor.getString(4));
                book_img.add(cursor.getString(5));
                book_pages.add(cursor.getString(6));
                bookMark.add(cursor.getString(7));
                // book_noteNb.add(cursor.getString(5));
            }
            empty_imageview.setVisibility(View.GONE);
            no_data.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete_all) {
            confirmDialog();
        } else if (item.getItemId() == R.id.disconnect) {
            disconnectDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete all ?");
        builder.setMessage("Are you sure you want to delete all ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyApiHelper myAPI = new MyApiHelper(MainActivity.this);
                MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);
                myAPI.deleteAllData();
                myDB.deleteAllData();
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    void disconnectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Disconnect ?");
        builder.setMessage("Are you sure you want to disconnect ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                MyDatabaseHelper myDB = new MyDatabaseHelper(MainActivity.this);
                myDB.setAccessToken(MainActivity.this, "");
                myDB.deleteForLogin();
                // go to login activity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
    
    private void requestPermission() {
    
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

}
