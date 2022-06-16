package com.gtu.keymanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class SaveImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);

        Bitmap image = MainActivity.image;

        ImageView imageView = findViewById(R.id.SaveImageView);
        imageView.setImageBitmap(image);
        EditText editText = findViewById(R.id.editTextKeyName);
        Button button = findViewById(R.id.saveImageButton);
        ProgressDialog progressDialog = new ProgressDialog(SaveImage.this);
        StorageReference mstorage = FirebaseStorage.getInstance().getReference();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editText.getText().toString();
                Uri uri = getImageUri(getApplicationContext(), image);

                if (uri != null && name != null) {
                    progressDialog.setMessage("Uploading image");
                    progressDialog.show();

                    StorageReference filepath = mstorage.child("CapturedPhotos/"+ name + ".jpg");

                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(SaveImage.this.getApplicationContext(), "Uploading finished", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    Toast.makeText(SaveImage.this, "Data couldn't uploaded", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }
}