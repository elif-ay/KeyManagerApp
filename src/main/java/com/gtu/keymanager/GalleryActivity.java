package com.gtu.keymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

public class GalleryActivity extends AppCompatActivity {

    ImageView galleryImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        galleryImage = findViewById(R.id.gallery_image);
     //   galleryImage.setImageURI(MainActivity.res);

    }
}