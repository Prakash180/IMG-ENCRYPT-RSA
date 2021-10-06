package com.example.image_encryption;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;

public class MainActivity extends AppCompatActivity {

    Button BSelectImage;
    Button EncryptImage;
    Button DecryptImage;
    Button btndownload;
    private Bitmap bmp;
    private Bitmap operation;
    int pk, prk;
    ImageView IVPreviewImage;
    TextView tv;
    EditText e;
    int SELECT_PICTURE = 200;
    private static final int STORAGE_PERMISSION_CODE=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BSelectImage = (Button) findViewById(R.id.BSelectImage);
        IVPreviewImage = (ImageView) findViewById(R.id.IVPreviewImage);
        EncryptImage = (Button) findViewById(R.id.EncryptImage);
        DecryptImage = (Button) findViewById(R.id.DecryptImage);
        btndownload = (Button) findViewById(R.id.btndownload);
        e = (EditText) findViewById(R.id.editTextTextPersonName);
        tv = (TextView) findViewById(R.id.textView);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        btndownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery();
            }
        });


        BSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,STORAGE_PERMISSION_CODE);
                imageChooser();
            }

        });
    }

    public void checkPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] { permission }, requestCode);
        }
        else {
            Toast.makeText(MainActivity.this, "Permission already granted", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveToGallery() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) IVPreviewImage.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        FileOutputStream outputStream = null;
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/Mypics");
        dir.mkdirs();

        String filename = String.format("%d.jpeg", System.currentTimeMillis());
        File outFile = new File(dir, filename);
        try {
            outputStream = new FileOutputStream(outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        try {
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);


         if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    void imageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    public void encryptonClick(View v) {
        IVPreviewImage.setDrawingCacheEnabled(true);
        IVPreviewImage.buildDrawingCache(true);
        bmp = IVPreviewImage.getDrawingCache();
        pk = Integer.valueOf((e.getText().toString()));
        for (int i = 0; i < bmp.getWidth(); i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                int p = bmp.getPixel(i, j);
                int r = Color.red(p);
                int g = Color.green(p);
                int b = Color.blue(p);
                BigInteger red = new BigInteger(String.valueOf(r));
                BigInteger green = new BigInteger(String.valueOf(g));
                BigInteger blue = new BigInteger(String.valueOf(b));
                red = red.modPow(BigInteger.valueOf(pk), BigInteger.valueOf(253));
                green = green.modPow(BigInteger.valueOf(pk), BigInteger.valueOf(253));
                blue = blue.modPow(BigInteger.valueOf(pk), BigInteger.valueOf(253));
                r = red.intValue();
                g = green.intValue();
                b = blue.intValue();
                bmp.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
            }
        }
        IVPreviewImage.setImageBitmap(bmp);
    }

    public void decryptonClick(View v) {
        IVPreviewImage.setDrawingCacheEnabled(true);
        IVPreviewImage.buildDrawingCache(true);
        bmp = IVPreviewImage.getDrawingCache();
        int pk2 = Integer.valueOf((e.getText().toString()));
        for (int i = 1; i <= 219; i++) {
            if ((i * pk2) % 220 == 1) {
                prk = i;
                break;
            }
        }

        for (int i = 0; i < bmp.getWidth(); i++) {
            for (int j = 0; j < bmp.getHeight(); j++) {
                int p = bmp.getPixel(i, j);
                int r = Color.red(p);
                int g = Color.green(p);
                int b = Color.blue(p);

                BigInteger red = new BigInteger(String.valueOf(r));
                BigInteger green = new BigInteger(String.valueOf(g));
                BigInteger blue = new BigInteger(String.valueOf(b));
                red = red.modPow(BigInteger.valueOf(prk), BigInteger.valueOf(253));
                green = green.modPow(BigInteger.valueOf(prk), BigInteger.valueOf(253));
                blue = blue.modPow(BigInteger.valueOf(prk), BigInteger.valueOf(253));
                r = red.intValue();
                g = green.intValue();
                b = blue.intValue();
                bmp.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
            }
        }
        IVPreviewImage.setImageBitmap(bmp);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    IVPreviewImage.setImageURI(selectedImageUri);
                    IVPreviewImage.setDrawingCacheEnabled(true);
                    IVPreviewImage.buildDrawingCache(true);
                    bmp = IVPreviewImage.getDrawingCache();
                    for (int i = 0; i < bmp.getWidth(); i++) {
                        for (int j = 0; j < bmp.getHeight(); j++) {
                            int p = bmp.getPixel(i, j);
                            int r = Color.red(p);
                            int g = Color.green(p);
                            int b = Color.blue(p);
                            r = Math.min(252, r);
                            g = Math.min(252, g);
                            b = Math.min(252, b);
                            bmp.setPixel(i, j, Color.argb(Color.alpha(p), r, g, b));
                        }
                    }
                    IVPreviewImage.setImageBitmap(bmp);
                }
            }
        }
    }
}