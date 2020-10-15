package com.example.abhinav.bluetooth_srthi_discover;


import android.content.Intent;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.net.Uri;

import android.widget.Button;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;


public class MainActivity extends AppCompatActivity {

    Button btnbluetoothinfo,btnsendbluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnbluetoothinfo = (Button) findViewById(R.id.bluetooth_info);
        btnsendbluetooth = (Button) findViewById(R.id.bluetooth_send);

        btnbluetoothinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent direct_intent = new Intent(MainActivity.this,blue_discover.class);
                startActivity(direct_intent);
            }
        });
        btnsendbluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("*/*");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "MyCsvFile.csv");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                share.setPackage("com.android.bluetooth");
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:////storage/self/primary/abhinav2.csv"));
                startActivity(Intent.createChooser(share, "Share Csv file"));
            }
        });
    }
}
