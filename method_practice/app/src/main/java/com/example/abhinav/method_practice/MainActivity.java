package com.example.abhinav.method_practice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView textView =new TextView(this);
        textView.setText("Wow");
        textView.append("hellow");
        setContentView(textView);
    }
}
