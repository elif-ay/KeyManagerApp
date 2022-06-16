package com.gtu.keymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class SaveActivity extends AppCompatActivity {

    ImageView capturedImage, pythonret_image;
    private static String LOG1 = "Log";
    private StorageReference storageReference, mstorage;
    ProgressDialog progressDialog;
    Button py_button;
    String resim1 = "";
    String resim2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        capturedImage = findViewById(R.id.downloaded_image);
        pythonret_image = findViewById(R.id.pythonret_image);
        py_button = findViewById(R.id.python_button);
        mstorage = FirebaseStorage.getInstance().getReference();

        progressDialog = new ProgressDialog(SaveActivity.this);
        progressDialog.setMessage("Fetching Image");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String name = OpencvCamera.photoName;

        storageReference = FirebaseStorage.getInstance().getReference().child("CapturedPhotos/" + name + ".jpg");
        try {
            final File file = File.createTempFile("tempfile", "jpg");
            storageReference.getFile(file)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(SaveActivity.this, "picture retrieved", Toast.LENGTH_SHORT).show();
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            capturedImage.setImageBitmap(bitmap);
                            if (!Python.isStarted())
                                Python.start(new AndroidPlatform(SaveActivity.this));

                            final Python py = Python.getInstance();

                            py_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    resim1 = getStrImage(bitmap);       // firebase den gelen resim
                                //    resim2 = getStrImage(OpencvCamera.photo);   // kameradan gelen resim

                                    PyObject pyObject = py.getModule("CannyDetector");
                                    PyObject obj = pyObject.callAttr("main", resim1);
                                    String str = obj.toString();
                                    byte data[] = android.util.Base64.decode(str, Base64.DEFAULT);
                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    pythonret_image.setImageBitmap(bmp);
                                    Log.i(LOG1, str);

                                    Uri uri = getImageUri(getApplicationContext(), bmp);
                                    if (uri != null && name != null) {
                                        progressDialog.setMessage("Uploading Canny image");
                                        progressDialog.show();
                                        StorageReference filepath = mstorage.child("CannyPhotos/"+ name + ".jpg");

                                        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                progressDialog.dismiss();
                                                Toast.makeText(SaveActivity.this, "Uploading canny finished", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                    else {
                                        Toast.makeText(SaveActivity.this, "Data couldn't uploaded", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SaveActivity.this, "picture doesn't retrieved", Toast.LENGTH_SHORT).show();

                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStrImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imgbytes = byteArrayOutputStream.toByteArray();
        String encImage = android.util.Base64.encodeToString(imgbytes, Base64.DEFAULT);
        return encImage;
    }
    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                inImage, "Title", null);
        return Uri.parse(path);
    }
}