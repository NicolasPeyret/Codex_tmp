package com.project.codex.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.codex.R;
import com.project.codex.adapter.NoteCustomAdapter;
import com.project.codex.data.local.MyDatabaseHelper;
import com.project.codex.data.remote.MyApiHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import io.apptik.widget.MultiSlider;
import java.util.ArrayList;
import org.checkerframework.checker.units.qual.A;

public class UpdateBookActivity extends AppCompatActivity {

    TextView mongo_id_input, title_input, author_input, pages_input, img_input, desc_input, bookMark_input;
    ImageView book_img_view, bg_image;
    MultiSlider multiSlider5;

    MyDatabaseHelper myDB;
    RecyclerView recyclerView;
    ArrayList<String> note_id, ml_id, mongo_id_2, note_title, note_content, book_id, note_page;
    NoteCustomAdapter noteCustomAdapter;

    FloatingActionButton scan_note_button;

    String id, mongo_id, title, author, pages, img, desc, bookMark;
    Boolean max_notes = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateEvent();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        onCreateEvent();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            recreate();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu_2, menu);
        return super.onCreateOptionsMenu(menu);
    }


    void getAndSetIntentData() {
        if (getIntent().hasExtra("id")
                && getIntent().hasExtra("mongo_id")
                && getIntent().hasExtra("title")
                && getIntent().hasExtra("author")
                && getIntent().hasExtra("image")
                && getIntent().hasExtra("desc")
                && getIntent().hasExtra("bookMark")
                && getIntent().hasExtra("pages")) {
            // GET
            id = getIntent().getStringExtra("id");
            mongo_id = getIntent().getStringExtra("mongo_id");
            title = getIntent().getStringExtra("title");
            author = getIntent().getStringExtra("author");
            img = getIntent().getStringExtra("image");
            desc = getIntent().getStringExtra("desc");
            pages = getIntent().getStringExtra("pages");
            bookMark = getIntent().getStringExtra("bookMark");

            // SET
            mongo_id_input.setText(mongo_id);
            title_input.setText(title);
            author_input.setText(author);
            img_input.setText(img);
            desc_input.setText(desc);
            pages_input.setText(pages);
            bookMark_input.setText(bookMark);
            Picasso.with(this)
                    .load(String.valueOf(img))
                    .into(book_img_view, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
            Picasso.with(this)
                    .load(String.valueOf(img))
                    .into(bg_image, new Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onError() {
                        }
                    });
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
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateBookActivity.this);
                MyApiHelper myAPI = new MyApiHelper(UpdateBookActivity.this);
                myAPI.deleteOneRow("books", mongo_id);
                myDB.deleteOneRow("books", id);
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

    void storeDataInArrays() {
        if (getIntent().hasExtra("id")) {
            id = getIntent().getStringExtra("id");
            Cursor cursor = myDB.readNotesByBookId(Integer.parseInt(id));
            Integer count = 0;
            while (cursor.moveToNext()) {
                note_id.add(cursor.getString(0));
                ml_id.add(cursor.getString(2));
                mongo_id_2.add(cursor.getString(1));
                book_id.add(cursor.getString(3));
                note_title.add(cursor.getString(4));
                note_content.add(cursor.getString(5));
                note_page.add(cursor.getString(6));
                count++;
            }
            // count cursor length
            if (count == 10) {
                max_notes = true;
            }
            if (count > 0) {
                scan_note_button.setVisibility(View.VISIBLE);
            }
        }
    }

    void onCreateEvent() {
        setContentView(R.layout.activity_update_book);

        recyclerView = findViewById(R.id.noteRecyclerView);
        myDB = new MyDatabaseHelper(UpdateBookActivity.this);
        note_id = new ArrayList<>();
        ml_id = new ArrayList<>();
        mongo_id_2 = new ArrayList<>();
        note_title = new ArrayList<>();
        note_content = new ArrayList<>();
        book_id = new ArrayList<>();
        note_page = new ArrayList<>();

        scan_note_button = findViewById(R.id.scan_note_button);
        scan_note_button.setVisibility(View.INVISIBLE);
        storeDataInArrays();

        noteCustomAdapter = new NoteCustomAdapter(UpdateBookActivity.this, this, note_id, ml_id, mongo_id_2, note_title,
                note_content,
                book_id, note_page);
        recyclerView.setAdapter(noteCustomAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(UpdateBookActivity.this, 3));

        scan_note_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UpdateBookActivity.this, CustomCameraActivity.class);
                intent.putExtra("book_id", id);
                int LAUNCH_SECOND_ACTIVITY = 1;
                startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
            }
        });

        book_img_view = findViewById(R.id.book_img_2);
        bg_image = findViewById(R.id.bg_image);
        title_input = findViewById(R.id.title_input2);
        author_input = findViewById(R.id.author_input2);
        img_input = findViewById(R.id.img_input2);
        img_input.setVisibility(View.GONE);
        mongo_id_input = findViewById(R.id.mongo_id_input2);
        mongo_id_input.setVisibility(View.GONE);
        desc_input = findViewById(R.id.description_input2);
        pages_input = findViewById(R.id.pages_input2);
        // pages_input.setVisibility(View.GONE);

        bookMark_input = findViewById(R.id.bookMark_input2);
        getAndSetIntentData();

        //set multislider5 thumb 1 to 5

        multiSlider5 = findViewById(R.id.range_slider5);
        multiSlider5.setMax(Integer.parseInt(pages_input.getText().toString().trim()));
        multiSlider5.setStepsThumbsApart(1);
        multiSlider5.setMin(0, true, false);
        multiSlider5.getThumb(0).setInvisibleThumb(true);

        
        multiSlider5.getThumb(1).setValue(Integer.parseInt(bookMark_input.getText().toString().trim()));

        
        multiSlider5.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                bookMark_input.setText(String.valueOf(multiSlider5.getThumb(1).getValue()));
            }
        });
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(title);
        }

        // remove app name from action bar
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setElevation(0);
    }

    public void addNote() {
        Intent intent = new Intent(UpdateBookActivity.this, AddNoteActivity.class);
        intent.putExtra("book_id", id);
        intent.putExtra("book_mongo_id", mongo_id);
        startActivity(intent);
    }

    public void saveDialog() {
        MyApiHelper myAPI = new MyApiHelper(UpdateBookActivity.this);
        mongo_id = mongo_id_input.getText().toString().trim();
        title = title_input.getText().toString().trim();
        author = author_input.getText().toString().trim();
        img = img_input.getText().toString().trim();
        pages = pages_input.getText().toString().trim();
        desc = desc_input.getText().toString().trim();
        bookMark = bookMark_input.getText().toString().trim();
        myAPI.updateBook(id, mongo_id, title, author, img, pages, desc, Integer.parseInt(bookMark));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.delete) {
            confirmDialog();
        } else if (item.getItemId() == R.id.save) {
            saveDialog();
        } else if (item.getItemId() == R.id.add_button_note) {
            if (max_notes == false) {
                addNote();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}