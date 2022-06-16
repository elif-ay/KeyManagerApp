package com.gtu.keymanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import java.io.FileOutputStream;

public class OpencvCamera extends AppCompatActivity {
    public static Bitmap photo;
    public static String photoName;
    private static String LOG = "Log";
    private StorageReference mstorage;
    private ProgressDialog progressDialog;
    private static final int CAM_REQ_CODE = 1;
    EditText editText;
    ImageView imageView;
    Button saveButton;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv_camera);

        editText = (EditText) findViewById(R.id.editTextTextPersonName);
        imageView = (ImageView) findViewById(R.id.imageofkey);
        saveButton = (Button) findViewById(R.id.saveButton);
        mstorage = FirebaseStorage.getInstance().getReference();
        progressDialog = new  ProgressDialog(this);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAM_REQ_CODE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editText.getText().toString();
                photoName = name;
                Log.i(LOG, name);
                Uri uri = getImageUri(getApplicationContext(), photo);

                if (uri != null && name != null) {
                    progressDialog.setMessage("Uploading image");
                    progressDialog.show();
                    StorageReference filepath = mstorage.child("CapturedPhotos/"+ name + ".jpg");

                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(OpencvCamera.this, "Uploading finished", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                            startActivity(intent);
                        }
                    });
                }
                else {
                    Toast.makeText(OpencvCamera.this, "Data couldn't uploaded", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==CAM_REQ_CODE && resultCode == RESULT_OK){
            photo = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }
}