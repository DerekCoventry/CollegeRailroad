package com.example.derek.collegerailroad;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CameraActivity extends BaseAppCompatActivity {

    public static File _file;
    public static File _dir;
    public static Bitmap bitmap;
    ImageView imageView;
    private String pictureImagePath = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.WRITE_EXTERNAL_STORAGE  },
                    11 );
        }
        Button cameraButton = (Button) findViewById(R.id.camera_but2);
        imageView = (ImageView) findViewById(R.id.imageView);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                _dir = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), "CameraActivity");

                _file = new File(_dir, "bookPhoto/" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(_file));

                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(_file);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);
            int height = imageView.getWidth() / 2;
            int width = imageView.getHeight() / 2;
            bitmap = LoadAndResizeBitmap(_file.getAbsolutePath(), width, height);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                bitmap = null;
            }
        }catch(Exception e){
            Toast.makeText(CameraActivity.this, "Error uploading photo", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap LoadAndResizeBitmap(String fileName, int width, int height)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(fileName, options);
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int inSampleSize = 1;
        if (outHeight > height || outWidth > width)
        {
            inSampleSize = outWidth > outHeight ? outHeight / height : outWidth / width;
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap resizedBitmap = BitmapFactory.decodeFile(fileName, options);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(), matrix, true);
}

}
