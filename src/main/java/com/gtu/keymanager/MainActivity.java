package com.gtu.keymanager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import org.jetbrains.annotations.Nullable;
import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    public static Bitmap image, bitmap;
    public static Bitmap mainBitmap;
    public static String FirebaseImageName="", name, capturedImageStr;
    Integer size = 0;
    Integer i = 0;
    Boolean found = false;
    public long start, end, execution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button cameraButton = findViewById(R.id.camera_button);
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {
                            Manifest.permission.CAMERA
                    }, 100);
        }

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, 100);
            }

        });
    }

    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);
        if (req == 100 && data != null) {
            image = (Bitmap) data.getExtras().get("data");
            capturedImageStr = getStrImage(image);

            start = System.nanoTime();
            Compare(capturedImageStr);

        }
    }

    public void Compare(String capturedImageStr) {
        final String[] result = {""};
        if (!Python.isStarted())
            Python.start(new AndroidPlatform(MainActivity.this));

        final Python py = Python.getInstance();
        PyObject pyObject = py.getModule("image_similarity");

        if (image != null) {
            StorageReference listRef = FirebaseStorage.getInstance().getReference().child("CapturedPhotos");
            listRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    size = listResult.getItems().size();
                    for(StorageReference file:listResult.getItems()){
                    //    String path = file.getPath();
                        file.getBytes(1024*1024*5)
                                .addOnSuccessListener(new OnSuccessListener<byte[]>(){
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        BitmapFactory.Options options = new
                                                BitmapFactory.Options();
                                        options.inMutable = true;
                                        if (i < size && found == false)
                                        {
                                            name = file.getName();
                                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                                            //    String FirebaseImageStr = getStrImage(bitmap);
                                            String encImage = Base64.encodeToString(bytes, Base64.DEFAULT);
                                            PyObject obj = pyObject.callAttr("main", capturedImageStr, encImage);

                                            Log.d("RESULT", obj.toString() + " -- " + name);
                                            if (obj.toFloat() > 10) {
                                                mainBitmap = bitmap;
                                                FirebaseImageName = name;
                                                found = true;
                                                openShowActivity();
                                            }

                                        }

                                        i++;
                                        openSaveActivity();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        int errorCode = ((StorageException) exception).getErrorCode();
                                        String errorMessage = exception.getMessage();
                                        Log.d("FAIL", errorMessage + errorCode);
                                    }
                                });
                    }
                }

            });

        }

    }

    private void openSaveActivity() {
        if (i >= size && found == false)
        {
            end = System.nanoTime();
            execution = end - start;
            Log.d("EXEC_TIME:", String.valueOf(execution));
            Intent intent = new Intent(getApplicationContext(), SaveImage.class);
            startActivity(intent);
        }

    }

    private void openShowActivity() {
        end = System.nanoTime();
        execution = end - start;
        Log.d("EXEC_TIME:", String.valueOf(execution));
        Intent intent = new Intent(getApplicationContext(), ShowImage.class);
        startActivity(intent);


    }

    private String getStrImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] imgbytes = byteArrayOutputStream.toByteArray();
        String encImage = android.util.Base64.encodeToString(imgbytes, Base64.DEFAULT);
        return encImage;
    }
    @Override
    public void onRestart() {
        super.onRestart();
        //When BACK BUTTON is pressed, the activity on the stack is restarted
        //Do what you want on the refresh procedure here
        super.onRestart();
        finish();
        startActivity(getIntent());

    }

}
