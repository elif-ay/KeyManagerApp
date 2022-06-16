package com.gtu.keymanager;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CompareImages {
    private Bitmap img;
    public ArrayList<String> imagelist;

    // constructors
    public CompareImages() {
        img = null;
        imagelist=new ArrayList<>();
    }

    public CompareImages(Bitmap image) {
        img = image;
        imagelist=new ArrayList<>();
    }

    // getters-setters
    public Bitmap getImg() {
        return img;
    }
    public void setImg1(Bitmap image) {
        img = image;
    }

    public void getImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://keymanager-2cf3e.appspot.com/CapturedPhotos").child("IMG20220610182552.jpg");
        final long ONE_MEGABYTE = 1024 * 1024 * 5;

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.i("Download3", uri.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        //download file as a byte array
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                Log.i("Download", bitmap.toString());
            }
        });
    }
    // Compare 2 images
    public ArrayList<String> Compare(ArrayList<Bitmap> bitmaps) {
        Activity context = new Activity();
        if (getImg() != null) {
            StorageReference listRef = FirebaseStorage.getInstance().getReference().child("CapturedPhotos");
            listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for(StorageReference file:listResult.getItems()){
                        String name = file.getName();
                        String path = file.getPath();
                        file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                            //    imagelist.add(uri.toString());
                                byte[] data = null;

                                Log.e("Itemvalue",uri.toString());
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                            }
                        });

                //        Task<Uri> uri = file.getDownloadUrl();
                        Log.i("Name", name.toString());
                        Log.i("Name", path.toString());


                        /*
                        file.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                imagelist.add(uri.toString());
                                /*
                                Bitmap bitmap = null;
                                try {
                                    bitmap = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                bitmaps.add(bitmap);

                                Log.e("Itemvalue",uri.toString());
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                Log.e("FINISH","FINISH POINT");
                            }
                        });

                         */
                    }
                    Log.i("Ret", imagelist.toString());
                }
            });
        }
        Log.i("Ret", imagelist.toString());
        return imagelist;
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

}
