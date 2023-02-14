package com.project.codex.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.project.codex.R;
import com.project.codex.data.local.MyDatabaseHelper;
import com.project.codex.data.remote.MyApiHelper;

public class AddNoteActivity extends AppCompatActivity {
    EditText title_input, page_input, book_id_input, content_input, note_id_ml_input;
    String id, book_mongo_id;
    Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        title_input = findViewById(R.id.title_input);
        page_input = findViewById(R.id.page_input);
        note_id_ml_input = findViewById(R.id.note_id_ml_input);
        book_id_input = findViewById(R.id.book_id_input);
        book_id_input.setVisibility(View.GONE);
        content_input = findViewById(R.id.content_input);
        getAndSetIntentData();

        add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(AddNoteActivity.this);
                Integer count = myDB.countNotesperBooks(book_id_input.getText().toString().trim());
                // if count is less than 10 then add the note
                if (count < 10) {
                    MyApiHelper myAPI = new MyApiHelper(AddNoteActivity.this);
                    if (!myDB.checkNoteId(note_id_ml_input.getText().toString().trim(),id)) {
                        if (Integer.valueOf(note_id_ml_input.getText().toString().trim()) < 11 && Integer.valueOf(note_id_ml_input.getText().toString().trim()) > 0) {
                            myAPI.addNote(
                                    book_mongo_id,
                                    Integer.valueOf(note_id_ml_input.getText().toString().trim()),
                                    Integer.parseInt(id),
                                    title_input.getText().toString().trim(),
                                    content_input.getText().toString().trim(),
                                    Integer.valueOf(page_input.getText().toString().trim()));
                        } else {
                            Toast.makeText(AddNoteActivity.this, "note must be between 1 and 10", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(AddNoteActivity.this, "note already exists", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("book_id") && getIntent().hasExtra("book_mongo_id")) {
            id = getIntent().getStringExtra("book_id");
            book_mongo_id = getIntent().getStringExtra("book_mongo_id");
            book_id_input.setText(id);
        }
    }
}