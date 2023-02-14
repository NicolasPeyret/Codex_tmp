package com.project.codex.data.remote;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.project.codex.utils.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class FetchBook extends AsyncTask<String, Void, String> {

    private TextView mAuthorText, mTitleText, mDescriptionText, mPageNumberText, mImgText, mISBNText;
    private Button add_button;

    public FetchBook(TextView mTitleText, TextView mAuthorText, TextView mDescriptionText, TextView mImgText,
                     TextView mPageNumberText, TextView mISBNText, Button add_button) {
        this.mTitleText = mTitleText;
        this.mAuthorText = mAuthorText;
        this.mDescriptionText = mDescriptionText;
        this.mImgText = mImgText;
        this.mPageNumberText = mPageNumberText;
        this.mISBNText = mISBNText;
        this.add_button = add_button;
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetworkUtils.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            String[] parts = s.split("@");
            JSONObject jsonObject = new JSONObject(parts[1]);
            JSONObject obj2 = jsonObject.getJSONObject("ISBN:" + parts[0]);

            mTitleText.setText(obj2.getString("title"));
            mAuthorText.setText("");

            // JSONArray authors = obj2.getJSONArray("excerpts");
            JSONArray authors = obj2.getJSONArray("authors");
            // get first author "name" in authors array
            JSONObject author = authors.getJSONObject(0);
            mAuthorText.setText(author.getString("name"));
            mPageNumberText.setText(obj2.getString("number_of_pages"));
            mISBNText.setText(parts[0]);

            // if obj2 has cover
            if (obj2.has("cover")) {
                JSONObject img = obj2.getJSONObject("cover");
                mImgText.setText(img.getString("medium"));
            } else {
                mImgText.setText("https://via.placeholder.com/300/353535/353535?Text=image");
            }

            mDescriptionText.setText(
                    "Unfortunately, this free version of the openlibrary API does not include data related to the description of a book, thank you for your understanding.");

            add_button.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            mTitleText.setText("Nothing found");
            mAuthorText.setText("");
            mDescriptionText.setText("");
            mISBNText.setText("");
            mImgText.setText("");
            mPageNumberText.setText("");
            e.printStackTrace();
        }
    }
}
