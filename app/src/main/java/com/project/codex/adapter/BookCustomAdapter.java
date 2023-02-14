package com.project.codex.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.project.codex.R;
import com.project.codex.activity.UpdateBookActivity;
import com.project.codex.data.local.MyDatabaseHelper;
import com.project.codex.data.remote.MyApiHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class BookCustomAdapter extends RecyclerView.Adapter<BookCustomAdapter.MyViewHolder> {

    Context context;
    Activity activity;

    private ArrayList book_id, mongo_id, book_title, book_author, book_img, book_pages, book_desc, book_noteNb, bookMark;

    Animation translate_anim;

    public BookCustomAdapter(Activity activity,
            Context context,
            ArrayList book_id,
            ArrayList mongo_id,
            ArrayList book_title,
            ArrayList book_author,
            ArrayList book_img,
            ArrayList book_pages,
            ArrayList book_noteNb,
            ArrayList book_desc,
            ArrayList bookMark) {
        this.activity = activity;
        this.context = context;
        this.book_id = book_id;
        this.mongo_id = mongo_id;
        this.book_title = book_title;
        this.book_author = book_author;
        this.book_img = book_img;
        this.book_pages = book_pages;
        this.book_noteNb = book_noteNb;
        this.book_desc = book_desc;
        this.bookMark = bookMark;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.my_books_row, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.book_id_txt.setText(String.valueOf(book_id.get(position)));
        holder.mongo_id_txt.setText(String.valueOf(mongo_id.get(position)));
        holder.book_title_txt.setText(String.valueOf(book_title.get(position)));
        holder.book_mark_txt.setText(String.valueOf(bookMark.get(position)));
        holder.book_author_txt.setText(String.valueOf(book_author.get(position)));
        Picasso.with(context)
                .load(String.valueOf(book_img.get(position)))
                .into(holder.book_img_txt, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                    }
                });

        holder.book_pages_txt.setText(String.valueOf(book_pages.get(position)));
        holder.book_desc_txt.setText(String.valueOf(book_desc.get(position)));
        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
        Integer count = myDB.countNotesperBooks(String.valueOf(book_id.get(position)));
        if (count == 0) {
            holder.mark_txt.setText(String.valueOf(0));
            holder.mark_txt.setVisibility(View.GONE);
        } else {
            holder.mark_txt.setText(String.valueOf(count));
        }
        holder.mark_txt.setText(String.valueOf(count));
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateBookActivity.class);
                intent.putExtra("id", String.valueOf(book_id.get(position)));
                intent.putExtra("mongo_id", String.valueOf(mongo_id.get(position)));
                intent.putExtra("title", String.valueOf(book_title.get(position)));
                intent.putExtra("author", String.valueOf(book_author.get(position)));
                intent.putExtra("image", String.valueOf(book_img.get(position)));
                intent.putExtra("desc", String.valueOf(book_desc.get(position)));
                intent.putExtra("pages", String.valueOf(book_pages.get(position)));
                intent.putExtra("bookMark", String.valueOf(bookMark.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }


    @Override
    public int getItemCount() {
        return book_id.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView book_id_txt, mongo_id_txt, book_title_txt, book_desc_txt, book_author_txt, book_pages_txt, mark_txt, book_mark_txt;
        ImageView book_img_txt;
        LinearLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            book_id_txt = itemView.findViewById(R.id.book_id_txt);
            mongo_id_txt = itemView.findViewById(R.id.mongo_id_txt);
            book_title_txt = itemView.findViewById(R.id.book_title_txt);
            book_author_txt = itemView.findViewById(R.id.book_author_txt);
            book_desc_txt = itemView.findViewById(R.id.book_desc_txt);
            book_img_txt = itemView.findViewById(R.id.book_img_txt);
            book_pages_txt = itemView.findViewById(R.id.book_pages_txt);
            book_mark_txt = itemView.findViewById(R.id.book_mark_txt);
            mark_txt = itemView.findViewById(R.id.mark);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);

        }
    }
}