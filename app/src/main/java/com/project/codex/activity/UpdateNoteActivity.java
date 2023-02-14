package com.project.codex.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.project.codex.R;
import com.project.codex.data.local.MyDatabaseHelper;
import com.project.codex.data.remote.MyApiHelper;

public class UpdateNoteActivity extends AppCompatActivity {

    EditText mongo_id_input_2, ml_id_input_2, title_input_2, content_input_2, book_id_input_2, page_input_2;
    MyDatabaseHelper myDB;
    String id;
    String ml_id;
    String mongo_id;
    String title;
    String content;
    String book_id;
    String page;
    String tmpId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);
        myDB = new MyDatabaseHelper(UpdateNoteActivity.this);
        title_input_2 = findViewById(R.id.title_input_2);
        mongo_id_input_2 = findViewById(R.id.mongo_id_input_2);
        mongo_id_input_2.setVisibility(View.GONE);
        ml_id_input_2 = findViewById(R.id.ml_id_input_2);
        content_input_2 = findViewById(R.id.content_input_2);
        book_id_input_2 = findViewById(R.id.book_id_input_2);
        book_id_input_2.setVisibility(View.GONE);
        page_input_2 = findViewById(R.id.page_input_2);
        getSupportActionBar().setElevation(0);

        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu_3, menu);
        return super.onCreateOptionsMenu(menu);
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("id")
                && getIntent().hasExtra("mongo_id")
                && getIntent().hasExtra("ml_id")
                && getIntent().hasExtra("title")
                && getIntent().hasExtra("content")
                && getIntent().hasExtra("book_id")
                && getIntent().hasExtra("page")) {
            // GET
            id = getIntent().getStringExtra("id");
            mongo_id = getIntent().getStringExtra("mongo_id");
            ml_id = getIntent().getStringExtra("ml_id");
            tmpId = getIntent().getStringExtra("ml_id");
            title = getIntent().getStringExtra("title");
            content = getIntent().getStringExtra("content");
            book_id = getIntent().getStringExtra("book_id");
            page = getIntent().getStringExtra("page");

            // SET
            mongo_id_input_2.setText(mongo_id);
            ml_id_input_2.setText(ml_id);
            title_input_2.setText(title);
            content_input_2.setText(content);
            book_id_input_2.setText(book_id);
            page_input_2.setText(page);
        } else {
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + title + " ?");
        builder.setMessage("Are you sure you want to delete " + title + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateNoteActivity.this);
                MyApiHelper myAPI = new MyApiHelper(UpdateNoteActivity.this);
                myAPI.deleteOneRow("notes", mongo_id);
                myDB.deleteOneRow("notes", id);
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

    public void saveDialog() {
        MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateNoteActivity.this);
        MyApiHelper myAPI = new MyApiHelper(UpdateNoteActivity.this);
        mongo_id = mongo_id_input_2.getText().toString();
        ml_id = ml_id_input_2.getText().toString();
        title = title_input_2.getText().toString().trim();
        content = content_input_2.getText().toString().trim();
        book_id = book_id_input_2.getText().toString().trim();
        page = page_input_2.getText().toString().trim();

        if (!myDB.checkNoteId(ml_id,book_id) || Integer.parseInt(ml_id) == Integer.parseInt(tmpId)) {
            if (Integer.valueOf(ml_id) < 11 && Integer.valueOf(ml_id) > 0) {
                myAPI.updateNote(id, ml_id, mongo_id, book_id, title, content, page);
                finish();
            } else {
                Toast.makeText(UpdateNoteActivity.this, "note must be between 1 and 10", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(UpdateNoteActivity.this, "note already exists", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            confirmDialog();
        } else if (item.getItemId() == R.id.save) {
            saveDialog();
        }
        return super.onOptionsItemSelected(item);
    }
}