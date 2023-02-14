package com.project.codex.data.DAO;

import android.content.Context;
import android.os.AsyncTask;
import com.project.codex.data.local.MyDatabaseHelper;
import com.project.codex.utils.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class Book extends AsyncTask<String, Void, String> {

    String id;
    int bookMark;
    Context context;

    public Book(Context context, String id, int bookMark) {
        this.context = context;
        this.id = id;
        this.bookMark = bookMark;
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    protected void onPostExecute(String s) {
        try {
            String[] parts = s.split("@");
            JSONObject jsonObject = new JSONObject(parts[1]);
            JSONObject obj2 = jsonObject.getJSONObject("ISBN:" + parts[0]);

            String title = obj2.getString("title");

            // JSONArray authors = obj2.getJSONArray("excerpts");
            JSONArray authors = obj2.getJSONArray("authors");
            // get first author "name" in authors array
            JSONObject author = authors.getJSONObject(0);
            String author_name = author.getString("name");
            String page = obj2.getString("number_of_pages");

            // if obj2 has cover
            String cover = "";
            if (obj2.has("cover")) {
                JSONObject img = obj2.getJSONObject("cover");
                cover = img.getString("medium");
            } else {
                cover = "https://via.placeholder.com/300/353535/353535?Text=image";
            }
            String description = "Unfortunately, this free version of the openlibrary API does not include data related to the description of a book, thank you for your understanding.";

            MyDatabaseHelper myDB = new MyDatabaseHelper(context);
            myDB.addBook(id, title, author_name, cover, Integer.parseInt(page), description, bookMark);


        } catch (Exception e) {


            e.printStackTrace();
        }
    }
}
