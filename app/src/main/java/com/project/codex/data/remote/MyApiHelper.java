package com.project.codex.data.remote;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.project.codex.R;
import com.project.codex.data.DAO.Book;
import com.project.codex.data.local.MyDatabaseHelper;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyApiHelper {

    private Context context;
    private static final String API_RESSOURCE = "http://153.92.208.103:8080/api/";

    public MyApiHelper(@Nullable Context context) {
        this.context = context;
    }

    public void addBook(String title, String author, String img, int pages, String desc, int bookMark, String isbn) {
        String accessToken = getAccessToken(context);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("isbn", isbn);
            jsonObject.put("bookMark", bookMark);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(API_RESSOURCE + "books")
                .addHeaders("Authorization",
                        "Bearer " + accessToken)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String id = null;
                        try {
                            id = response.getString("_id");
                            MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                            myDB.addBook(id, title, author, img, pages, desc, bookMark);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.getErrorBody());
                    }
                });
    }

    public void addNote(String bookMongoId, int MLId, int bookId, String title, String content, int page) {
        String accessToken = getAccessToken(context);
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("content", content);
            jsonObject.put("pageNumber", page);
            jsonObject.put("noteId", MLId);
            jsonObject.put("bookId", bookMongoId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.post(API_RESSOURCE + "notes")
                .addHeaders("Authorization",
                        "Bearer " + accessToken)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String noteId = null;
                        try {
                            noteId = response.getString("_id");
                        } catch (JSONException e) {
                            // e.printStackTrace();
                        } finally {
                            myDB.addNote(noteId, MLId, bookId, title, content, page);
                            Toast.makeText(context, "Success API", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.getErrorBody());
                    }
                });

    }

    public void updateBook(String row_id, String mongo_id, String title, String author, String img, String pages,
            String desc, int bookMark) {
        String accessToken = getAccessToken(context);
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("bookMark", bookMark);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.put(API_RESSOURCE + "books" + "/" + mongo_id)
                .addHeaders("Authorization",
                        "Bearer " + accessToken)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        myDB.updateBook(row_id, mongo_id, title, author, img, pages, desc, bookMark);
                        Toast.makeText(context, "Success API", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.getErrorBody());
                    }
                });
    }

    public void updateNote(String row_id, String ml_id, String mongo_id, String bookId, String title, String content,
            String page) {
        String accessToken = getAccessToken(context);
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("content", content);
            jsonObject.put("pageNumber", page);
            jsonObject.put("noteId", ml_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AndroidNetworking.put(API_RESSOURCE + "notes" + "/" + mongo_id)
                .addHeaders("Authorization",
                        "Bearer " + accessToken)
                .addJSONObjectBody(jsonObject)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        myDB.updateNote(row_id, ml_id, mongo_id, bookId, title, content, page);
                        Toast.makeText(context, "Success API", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.getErrorBody());
                    }
                });
    }

    public void readAllData() {
        String accessToken = getAccessToken(context);
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);

        AndroidNetworking.get(API_RESSOURCE + "books")
                .addHeaders("Authorization",
                        "Bearer " + accessToken)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("response", response.toString());
                        // foreach element of JSON array store and add book to database
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = response.getJSONObject(i);

                                String id = jsonObject.getString("_id");
                                String isbn = jsonObject.getString("isbn");
                                int bookMark = jsonObject.getInt("bookMark");

                                new Book(context, id, bookMark).execute(isbn);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    public void readAllNotes() {
        String accessToken = getAccessToken(context);
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);

        // read all notes
        AndroidNetworking.get(API_RESSOURCE + "notes")
                .addHeaders("Authorization",
                        "Bearer " + accessToken)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("response", response.toString());
                        // foreach element of JSON array store and add book to database
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = response.getJSONObject(i);

                                String id = jsonObject.getString("_id");
                                String noteId = jsonObject.getString("noteId");
                                String mongoId = jsonObject.getString("bookId");
                                int bookId = myDB.MongoIdToRowId(mongoId);
                                Log.d("bookId", String.valueOf(bookId));
                                String title = jsonObject.getString("title");
                                String content = jsonObject.getString("content");
                                String page = jsonObject.getString("pageNumber");
                                myDB.addNote(id, Integer.parseInt(noteId), bookId, title, content,
                                        Integer.parseInt(page));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    public void deleteOneRow(String database, String mongo_id) {
        String accessToken = getAccessToken(context);
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);

        AndroidNetworking.delete(API_RESSOURCE + database + "/" + mongo_id)
                .addHeaders("Authorization",
                        "Bearer " + accessToken)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, R.string.api_success, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.getErrorBody());
                    }
                });
    }

    public void deleteAllData() {
        String accessToken = getAccessToken(context);
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        Cursor cursor = myDB.readAllData("books");
        // store all the ids in an array
        ArrayList<String> ids = new ArrayList<>();
        while (cursor.moveToNext()) {
            ids.add(cursor.getString(0));
        }
        // delete all the rows
        for (int i = 0; i < ids.size(); i++) {

            String apiUrl = API_RESSOURCE + "books" + "/" + myDB.rowIdToMongoId(String.valueOf(ids.get(i)),"books");
            AndroidNetworking
                    .delete(apiUrl)
                    .addHeaders("Authorization",
                            "Bearer " + accessToken)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Toast.makeText(context, "Success API", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("Error", anError.getErrorBody());
                        }
                    });
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
