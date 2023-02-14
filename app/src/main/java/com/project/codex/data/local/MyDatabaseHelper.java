package com.project.codex.data.local;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private Context context;

    private static final String API_RESSOURCE = "http://153.92.208.103:8080/api/";

    private static final String DATABASE_NAME = "BookLibrary.db";
    private static final int DATABASE_VERSION = 3;

    private static final String TABLE_NAME_1 = "my_library";
    private static final String COLUMN_ID_1 = "_id";
    private static final String COLUMN_MONGO_ID_1 = "mongo_id";
    private static final String COLUMN_TITLE_1 = "book_title";
    private static final String COLUMN_AUTHOR_1 = "book_author";
    private static final String COLUMN_IMG_1 = "book_img";
    private static final String COLUMN_DESC_1 = "book_description";
    private static final String COLUMN_PAGES_1 = "book_pages";
    private static final String COLUMN_BOOKMARK_1 = "book_bookmark";

    private static final String TABLE_NAME_2 = "my_notes";
    private static final String COLUMN_ID_2 = "_id";
    private static final String COLUMN_MONGO_ID_2 = "mongo_id";
    private static final String BOOK_ID_2 = "book_id";
    private static final String COLUMN_TITLE_2 = "note_title";
    private static final String COLUMN_CONTENT_2 = "note_content";
    private static final String COLUMN_PAGE_2 = "book_page";
    private static final String ML_ID = "ML_note_id";

    // API
    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE "
                + TABLE_NAME_1
                + " ("
                + COLUMN_ID_1
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MONGO_ID_1
                + " TEXT, "
                + COLUMN_TITLE_1
                + " TEXT, "
                + COLUMN_AUTHOR_1
                + " TEXT, "
                + COLUMN_DESC_1
                + " TEXT, "
                + COLUMN_IMG_1
                + " TEXT, "
                + COLUMN_PAGES_1
                + " INTEGER, "
                + COLUMN_BOOKMARK_1
                + " INTEGER);";

        String query_2 = "CREATE TABLE "
                + TABLE_NAME_2
                + " ("
                + COLUMN_ID_2
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_MONGO_ID_2
                + " TEXT, "
                + ML_ID
                + " INTEGER, "
                + BOOK_ID_2
                + " INTEGER, "
                + COLUMN_TITLE_2
                + " TEXT, "
                + COLUMN_CONTENT_2
                + " TEXT, "
                + COLUMN_PAGE_2
                + " INTEGER, "
                + "FOREIGN KEY("
                + BOOK_ID_2
                + ") references "
                + TABLE_NAME_1
                + "("
                + COLUMN_ID_1
                + "))";

        db.execSQL(query);
        db.execSQL(query_2);
    }

    // API
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String accessToken = getAccessToken(context);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        onCreate(db);

    }

    // API
    public void addBook(String mongo_id, String title, String author, String img, int pages, String desc,
            int bookMark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_MONGO_ID_1, mongo_id);
        cv.put(COLUMN_TITLE_1, title);
        cv.put(COLUMN_AUTHOR_1, author);
        cv.put(COLUMN_DESC_1, desc);
        cv.put(COLUMN_IMG_1, img);
        cv.put(COLUMN_PAGES_1, pages);
        cv.put(COLUMN_BOOKMARK_1, bookMark);

        long result = db.insert(TABLE_NAME_1, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        }

    }

    public void addNote(String mongo_id, int MLId, int bookId, String title, String content, int page) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_MONGO_ID_2, mongo_id);
        cv.put(ML_ID, MLId);
        cv.put(BOOK_ID_2, bookId);
        cv.put(COLUMN_TITLE_2, title);
        cv.put(COLUMN_CONTENT_2, content);
        cv.put(COLUMN_PAGE_2, page);

        long result = db.insert(TABLE_NAME_2, null, cv);
        if (result == -1) {
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Added Successfully", Toast.LENGTH_LONG).show();
        }

    }

    public Cursor readAllData(String database) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if (database == "books") {
            query = "SELECT * FROM " + TABLE_NAME_1;

        } else if (database == "notes") {
            query = "SELECT * FROM " + TABLE_NAME_2;
        }

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public Cursor readNotesByBookId(int book_id) {
        String query = "SELECT * FROM " + TABLE_NAME_2 + " WHERE " + BOOK_ID_2 + " = " + book_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateBook(String row_id, String mongo_id, String title, String author, String img, String pages,
            String desc, int bookMark) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE_1, title);
        cv.put(COLUMN_MONGO_ID_1, mongo_id);
        cv.put(COLUMN_AUTHOR_1, author);
        cv.put(COLUMN_IMG_1, img);
        cv.put(COLUMN_PAGES_1, pages);
        cv.put(COLUMN_DESC_1, desc);
        cv.put(COLUMN_BOOKMARK_1, bookMark);

        long result = db.update(TABLE_NAME_1, cv, "_id=?", new String[] { row_id });
        if (result == -1) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Updated!", Toast.LENGTH_SHORT).show();
        }

    }

    public Integer countNotesperBooks(String book_id) {
        String query = "SELECT COUNT(*) FROM " + TABLE_NAME_2 + " WHERE " + BOOK_ID_2 + " = " + book_id;
        // count the number of rows in the table
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null; // cursor to hold the result of the query
        int count = 0;
        if (db != null) {
            cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }
        return count;
    }

    public void updateNote(String row_id, String MLId, String mongo_id, String bookId, String title, String content,
            String page) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_MONGO_ID_2, mongo_id);
        cv.put(ML_ID, MLId);
        cv.put(BOOK_ID_2, bookId);
        cv.put(COLUMN_TITLE_2, title);
        cv.put(COLUMN_CONTENT_2, content);
        cv.put(COLUMN_PAGE_2, page);

        long result = db.update(TABLE_NAME_2, cv, "_id=?", new String[] { row_id });
        if (result == -1) {
            Toast.makeText(context, "Failed to update.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Updated!", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("Range")
    public void deleteOneRow(String database, String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        long result = -1;
        if (database == "books") {
            result = db.delete(TABLE_NAME_1, "_id=?", new String[] { row_id });
        } else if (database == "notes") {
            result = db.delete(TABLE_NAME_2, "_id=?", new String[] { row_id });
        }
        if (result == -1) {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
        }

    }

    public Cursor readNote(String note_id) {
        String query = "SELECT * FROM " + TABLE_NAME_2 + " WHERE " + ML_ID + " = " + note_id;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_1);
    }

    public void deleteForLogin() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME_1);
        db.execSQL("DELETE FROM " + TABLE_NAME_2);
    }

    public String rowIdToMongoId(String row_id, String database) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "";
        if (database == "books") {
            query = "SELECT " + COLUMN_MONGO_ID_1 + " FROM " + TABLE_NAME_1 + " WHERE _id = " + row_id;
        } else if (database == "notes") {
            query = "SELECT " + COLUMN_MONGO_ID_2 + " FROM " + TABLE_NAME_2 + " WHERE _id = " + row_id;
        }
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        String mongo_id = "";
        if (cursor.moveToFirst()) {
            mongo_id = cursor.getString(0);
        }
        return mongo_id;
    }

    public int MongoIdToRowId(String mongo_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID_1 +  " FROM " + TABLE_NAME_1 + " WHERE " + COLUMN_MONGO_ID_1 + " = '" + mongo_id + "'";
        Log.d("query",query);
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        String row_id = "";
        if (cursor.moveToFirst()) {
            row_id = cursor.getString(0);
        }
        return Integer.parseInt(row_id);
    }

    // check if the note id exists in the database
    public boolean checkNoteId(String note_id, String book_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        // String query = "SELECT * FROM " + TABLE_NAME_2 + " WHERE " + COLUMN_ID_2 + "
        // = " + note_id;
        String query = "SELECT * FROM " + TABLE_NAME_2 + " WHERE " + ML_ID + " = " + note_id + " AND "
                + BOOK_ID_2 + " = " + book_id;
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }


    public void setAccessToken(@NonNull Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ACCESSTOKEN", token);
        editor.apply();
    }

    public String getAccessToken(@NonNull Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("ACCESSTOKEN", null);
    }
}
