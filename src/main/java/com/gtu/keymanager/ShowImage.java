package com.gtu.keymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        Bitmap image = MainActivity.mainBitmap;
        String name = MainActivity.FirebaseImageName;
        name = name.substring(0, name.lastIndexOf('.'));

        ImageView imageView = findViewById(R.id.ShowImageView);
        imageView.setImageBitmap(image);

        TextView textView = findViewById(R.id.textViewKeyName);
        textView.setText(name);

    }
}