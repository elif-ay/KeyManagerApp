package com.gtu.keymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class CompareWithNameActivity extends AppCompatActivity {

    ImageView capturedImage;
    EditText compareName;
    Button compareButton;
    String retrievedImageStr="", capturedImageStr="";
    private ProgressDialog progressDialog;
    private StorageReference mStorage;
    public  static Bitmap CompareBitmap;
    public static Float SimilarityRatio;
    public static String nameToCompare = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_with_name);

        capturedImage = findViewById(R.id.capturedImageToCompare);
        compareName = findViewById(R.id.compareNameEditText);
        compareButton = findViewById(R.id.compareButton);
        capturedImage.setImageBitmap(MainActivity.image);
        mStorage = FirebaseStorage.getInstance().getReference();
        capturedImageStr = getStrImage(MainActivity.image);
        progressDialog = new  ProgressDialog(this);

        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameToCompare = compareName.getText().toString();
                if (nameToCompare != null) {

                    StorageReference storageReference = mStorage.child("CapturedPhotos/"+ nameToCompare + ".jpg");
                    try {
                        final File file = File.createTempFile("tempFile", "jpg");
                        storageReference.getFile(file)
                                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                               //         Toast.makeText(CompareWithNameActivity.this, "picture retrieved", Toast.LENGTH_SHORT).show();
                                        progressDialog.setMessage("Comparing images");
                                        progressDialog.show();
                                        CompareBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                        if (!Python.isStarted())
                                            Python.start(new AndroidPlatform(CompareWithNameActivity.this));

                                        final Python py = Python.getInstance();
                                        retrievedImageStr = getStrImage(CompareBitmap);       // Image from FireBase
                                        PyObject pyObject = py.getModule("image_similarity");
                                        PyObject obj = pyObject.callAttr("main", capturedImageStr, retrievedImageStr);
                                        progressDialog.dismiss();
                                        SimilarityRatio = obj.toFloat();
                                        Log.i("Result", SimilarityRatio.toString());
                                        Toast.makeText(CompareWithNameActivity.this, "Similarity: %" + SimilarityRatio.toString(), Toast.LENGTH_SHORT).show();
                                        if (SimilarityRatio > 15) {
                                            // TODO: 12.06.2022 Show Image Page
                                            Intent intent = new Intent(getApplicationContext(), ShowImage.class);
                                            startActivity(intent);

                                        } else {
                                            // TODO: 12.06.2022 Save Image Page
                                            Intent intent = new Intent(getApplicationContext(), SaveImage.class);
                                            startActivity(intent);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CompareWithNameActivity.this, "Couldn't retrieve image", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), SaveImage.class);
                                        startActivity(intent);
                                    }
                                });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else {
                    Toast.makeText(CompareWithNameActivity.this, "Missing info", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
    private String getStrImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        String encImage = android.util.Base64.encodeToString(imgBytes, Base64.DEFAULT);
        return encImage;
    }
}