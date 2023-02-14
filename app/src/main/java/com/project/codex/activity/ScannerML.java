package com.project.codex.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.project.codex.R;
import com.project.codex.data.local.MyDatabaseHelper;

import com.project.codex.ml.Model;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

public class ScannerML extends AppCompatActivity {
    Button validation;
    FloatingActionButton camera;
    ImageView imageView;
    TextView result, classified;
    int imageSize = 200;
    String book_id_2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scanner_ml);

        getSupportActionBar().setElevation(0);
        camera = findViewById(R.id.button);
        validation = findViewById(R.id.validation);
        classified = findViewById(R.id.classified);
        classified.setVisibility(View.GONE);
        result = findViewById(R.id.result);
        result.setVisibility(View.GONE);
        validation.setVisibility(View.GONE);
        validation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScannerML.this, UpdateNoteActivity.class);
                MyDatabaseHelper myDB = new MyDatabaseHelper(ScannerML.this);
                String result_txt = (String) result.getText();
                Cursor cursor = myDB.readNote(result_txt);
                while (cursor.moveToNext()) {
                    // intent.putExtra("id", cursor.getString(0));
                    intent.putExtra("id", cursor.getString(0));
                    intent.putExtra("ml_id", cursor.getString(2));
                    intent.putExtra("mongo_id", cursor.getString(1));
                    intent.putExtra("book_id", cursor.getString(3));
                    intent.putExtra("title", cursor.getString(4));
                    intent.putExtra("content", cursor.getString(5));
                    intent.putExtra("page", cursor.getString(6));
                }
                startActivity(intent);
                finish();
            }
        });

        imageView = findViewById(R.id.imageView);

        camera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Intent intent = new Intent(ScannerML.this,
                    CustomCameraActivity.class);
                    if (getIntent().hasExtra("book_id")) {
                        intent.putExtra("book_id", getIntent().getStringExtra("book_id"));
                    }
                    startActivityForResult(intent, 3);
                } else {
                    requestPermissions(new String[] { Manifest.permission.CAMERA }, 100);
                }
            }
        });
        getAndSetIntentData();
    }

    public void classifyImage(Bitmap image) {
        try {

            Model model = Model.newInstance(getApplicationContext());

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[] { 1, 200, 200, 3 }, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            int[] intValues = new int[imageSize * imageSize];
            image.getPixels(intValues, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
            int pixel = 0;
            // iterate over each pixel and extract R, G, and B values. Add those values
            // individually to the byte buffer.
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 1));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 1));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = { "1", "10", "2", "3", "4", "5", "6", "7", "8", "9" };
            result.setVisibility(View.VISIBLE);
            classified.setVisibility(View.VISIBLE);
            result.setText(classes[maxPos]);

            MyDatabaseHelper myDB = new MyDatabaseHelper(ScannerML.this);
            Boolean noteAssociated = myDB.checkNoteId(classes[maxPos], book_id_2);
            if (noteAssociated) {
                validation.setVisibility(View.VISIBLE);
            }
            model.close();

            // ICI
        } catch (IOException e) {
            Log.e("error", e.toString());
        }
    }

    void cameraReturnedData(String cameraData) {

                File imgFile = new  File(cameraData);
                Bitmap image = (Bitmap) BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image = ThumbnailUtils.extractThumbnail(image, 200, 200);
                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false);
                classifyImage(image);
                // validation on CLick listener
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("book_id")) {
            book_id_2 = getIntent().getStringExtra("book_id");
        }


        String data2 = getIntent().getExtras().getString("data");
        if (data2 == null || data2.length() == 0) {
        } else {
            cameraReturnedData(data2);
        }
    }
}
