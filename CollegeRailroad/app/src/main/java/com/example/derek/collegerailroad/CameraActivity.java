package com.example.derek.collegerailroad;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class CameraActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("CameraActivity", "onCreate() method triggered");
        setContentView(R.layout.activity_camera2);
        Button cameraButton = (Button) findViewById(R.id.camera_but2);
        imageView = (ImageView)findViewById(R.id.imageView);

        cameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.i("CameraActivity", "onStart() method triggered");
    }


    @Override
    protected void onResume(){
        super.onResume();
        Log.i("CameraActivity", "onResume() method triggered");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap)data.getExtras().get("data");
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
        Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        imageView.setImageBitmap(rotatedBitmap);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i("CameraActivity", "onPause() method triggered");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.i("CameraActivity", "onStop() method triggered");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.i("CameraActivity", "onDestroy() method triggered");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.i("CameraActivity", "onRestart() method triggered");
    }

}
